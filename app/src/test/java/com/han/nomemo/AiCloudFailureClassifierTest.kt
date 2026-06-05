package com.han.nomemo

import java.io.EOFException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.security.cert.CertificateException
import javax.net.ssl.SSLHandshakeException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiCloudFailureClassifierTest {
    @Test
    fun `transient status zero network failures retry before budget is exhausted`() {
        val failures = listOf(
            SocketTimeoutException("read timed out"),
            ConnectException("connection refused"),
            UnknownHostException("temporary dns failure"),
            SocketException("connection reset"),
            EOFException("unexpected end of stream"),
            IOException("connection closed")
        )

        failures.forEach { failure ->
            assertTrue(
                failure.javaClass.simpleName,
                shouldRetry(cause = failure, attempt = 1, attemptLimit = 3)
            )
        }
    }

    @Test
    fun `first transient failure can be followed by a successful attempt without exhausting budget`() {
        var calls = 0
        var success = false

        while (calls < 3 && !success) {
            calls += 1
            try {
                if (calls == 1) {
                    throw SocketTimeoutException("connect timed out")
                }
                success = true
            } catch (failure: Exception) {
                if (!shouldRetry(cause = failure, attempt = calls, attemptLimit = 3)) {
                    break
                }
            }
        }

        assertTrue(success)
        assertEquals(2, calls)
    }

    @Test
    fun `wrapped transient cause remains retryable`() {
        val wrapped = IllegalStateException(
            "Cloud request failed",
            SocketTimeoutException("read timed out")
        )

        assertTrue(shouldRetry(cause = wrapped, attempt = 1, attemptLimit = 3))
    }

    @Test
    fun `dns failures wait for Android negative cache before retrying`() {
        val failure = UnknownHostException(
            "Unable to resolve host: EAI_NODATA (No address associated with hostname)"
        )

        assertTrue(AiCloudFailureClassifier.containsDnsFailure(failure))
        assertEquals(
            12_000L,
            AiCloudFailureClassifier.retryDelayMillis(
                AiFailureStage.CLOUD_REQUEST,
                failure,
                1
            )
        )
    }

    @Test
    fun `connection abort uses a longer stabilization delay`() {
        val failure = SocketException("Software caused connection abort")

        assertEquals(
            4_000L,
            AiCloudFailureClassifier.retryDelayMillis(
                AiFailureStage.CLOUD_REQUEST,
                failure,
                1
            )
        )
    }

    @Test
    fun `unknown status zero connection failure uses remaining retry budget`() {
        val failure = IllegalStateException("Cloud request failed")

        assertTrue(shouldRetry(cause = failure, attempt = 1, attemptLimit = 3))
        assertTrue(shouldRetry(cause = failure, attempt = 2, attemptLimit = 3))
        assertFalse(shouldRetry(cause = failure, attempt = 3, attemptLimit = 3))
    }

    @Test
    fun `retryable http statuses and structured output failures use remaining budget`() {
        listOf(408, 409, 425, 429, 500, 503).forEach { status ->
            assertTrue(
                "HTTP $status",
                shouldRetry(httpStatus = status, attempt = 1, attemptLimit = 3)
            )
        }
        listOf(
            AiFailureStage.JSON_PARSE,
            AiFailureStage.SCHEMA_VALIDATE,
            AiFailureStage.JSON_REPAIR,
            AiFailureStage.TOKEN_EXHAUSTED
        ).forEach { stage ->
            assertTrue(
                stage.name,
                shouldRetry(stage = stage, attempt = 1, attemptLimit = 3)
            )
        }
    }

    @Test
    fun `deterministic failures do not retry`() {
        listOf(400, 401, 403, 404, 422).forEach { status ->
            assertFalse(
                "HTTP $status",
                shouldRetry(httpStatus = status, attempt = 1, attemptLimit = 3)
            )
        }
        listOf(
            AiFailureStage.CONFIGURATION,
            AiFailureStage.IMAGE_INPUT,
            AiFailureStage.MODEL_CAPABILITY
        ).forEach { stage ->
            assertFalse(
                stage.name,
                shouldRetry(stage = stage, attempt = 1, attemptLimit = 3)
            )
        }
    }

    @Test
    fun `certificate failures do not retry even though they are IO failures`() {
        val certificateFailure = SSLHandshakeException("certificate rejected").apply {
            initCause(CertificateException("expired"))
        }

        assertFalse(shouldRetry(cause = certificateFailure, attempt = 1, attemptLimit = 3))
    }

    @Test
    fun `diagnostics expose safe root cause without bearer secret`() {
        val failure = IOException("Bearer top-secret-token\nconnection reset")

        assertEquals("IOException", AiCloudFailureClassifier.causeType(failure))
        assertFalse(AiCloudFailureClassifier.safeCauseMessage(failure).contains("top-secret-token"))
    }

    private fun shouldRetry(
        stage: AiFailureStage = AiFailureStage.CLOUD_REQUEST,
        httpStatus: Int = 0,
        cause: Throwable? = null,
        attempt: Int,
        attemptLimit: Int
    ): Boolean {
        return AiCloudFailureClassifier.shouldRetry(
            stage,
            httpStatus,
            null,
            cause?.message,
            cause,
            attempt,
            attemptLimit
        )
    }
}
