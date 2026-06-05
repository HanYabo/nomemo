package com.han.nomemo;

import androidx.annotation.Nullable;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;

final class AiCloudFailureClassifier {
    private static final int SAFE_MESSAGE_LIMIT = 240;
    private static final long DEFAULT_RETRY_DELAY_MS = 1_500L;
    private static final long TRANSIENT_CONNECTION_DELAY_MS = 4_000L;
    private static final long DNS_NEGATIVE_CACHE_DELAY_MS = 12_000L;

    private AiCloudFailureClassifier() {
    }

    static boolean shouldRetry(
            @Nullable AiFailureStage failureStage,
            int httpStatus,
            @Nullable String providerErrorCode,
            @Nullable String message,
            @Nullable Throwable cause,
            int attempt,
            int attemptLimit
    ) {
        if (attempt >= attemptLimit) {
            return false;
        }
        if (failureStage == AiFailureStage.JSON_PARSE
                || failureStage == AiFailureStage.SCHEMA_VALIDATE
                || failureStage == AiFailureStage.JSON_REPAIR
                || failureStage == AiFailureStage.TOKEN_EXHAUSTED) {
            return true;
        }
        if (failureStage == AiFailureStage.CONFIGURATION
                || failureStage == AiFailureStage.IMAGE_INPUT
                || failureStage == AiFailureStage.MODEL_CAPABILITY
                || failureStage == AiFailureStage.LOCAL_FALLBACK) {
            return false;
        }
        if (failureStage != AiFailureStage.CLOUD_REQUEST) {
            return false;
        }
        if (httpStatus == 408
                || httpStatus == 409
                || httpStatus == 425
                || httpStatus == 429
                || httpStatus >= 500) {
            return true;
        }
        if (httpStatus >= 400) {
            return false;
        }
        if (containsCertificateFailure(cause)) {
            return false;
        }
        if (containsTransientNetworkFailure(cause)) {
            return true;
        }
        if (looksTransient(
                nullToEmpty(providerErrorCode)
                        + " "
                        + nullToEmpty(message)
                        + " "
                        + safeCauseMessage(cause)
        )) {
            return true;
        }

        // A provider-less status-0 failure can hide a platform connection error.
        // Certificate and deterministic failures have already been excluded above,
        // so use the remaining budget instead of persisting a premature failure.
        return httpStatus == 0;
    }

    static boolean containsTransientNetworkFailure(@Nullable Throwable throwable) {
        for (Throwable current : causeChain(throwable)) {
            if (current instanceof SocketTimeoutException
                    || current instanceof ConnectException
                    || current instanceof UnknownHostException
                    || current instanceof EOFException
                    || current instanceof SocketException
                    || current instanceof IOException) {
                return true;
            }
        }
        return false;
    }

    static boolean containsDnsFailure(@Nullable Throwable throwable) {
        for (Throwable current : causeChain(throwable)) {
            String type = current.getClass().getSimpleName();
            String message = nullToEmpty(current.getMessage()).toLowerCase(Locale.ROOT);
            if (current instanceof UnknownHostException
                    || "GaiException".equals(type)
                    || message.contains("eai_nodata")
                    || message.contains("eai_again")
                    || message.contains("no address associated with hostname")
                    || message.contains("unable to resolve host")) {
                return true;
            }
        }
        return false;
    }

    static long retryDelayMillis(
            @Nullable AiFailureStage failureStage,
            @Nullable Throwable throwable,
            int attempt
    ) {
        if (failureStage == AiFailureStage.CLOUD_REQUEST && containsDnsFailure(throwable)) {
            return DNS_NEGATIVE_CACHE_DELAY_MS;
        }
        if (failureStage == AiFailureStage.CLOUD_REQUEST
                && containsTransientNetworkFailure(throwable)) {
            return TRANSIENT_CONNECTION_DELAY_MS;
        }
        return DEFAULT_RETRY_DELAY_MS * Math.max(1, attempt);
    }

    static boolean containsCertificateFailure(@Nullable Throwable throwable) {
        for (Throwable current : causeChain(throwable)) {
            if (current instanceof SSLHandshakeException
                    || current instanceof SSLPeerUnverifiedException
                    || current instanceof CertificateException) {
                return true;
            }
        }
        return false;
    }

    static String causeType(@Nullable Throwable throwable) {
        Throwable root = rootCause(throwable);
        return root == null ? "" : root.getClass().getSimpleName();
    }

    static String safeCauseMessage(@Nullable Throwable throwable) {
        Throwable root = rootCause(throwable);
        if (root == null) {
            return "";
        }
        return sanitizeMessage(root.getMessage());
    }

    static String sanitizeMessage(@Nullable String rawMessage) {
        if (rawMessage == null) {
            return "";
        }
        String sanitized = rawMessage
                .replaceAll("(?i)bearer\\s+[^\\s,;]+", "Bearer [redacted]")
                .replaceAll("(?i)(api[_-]?key\\s*[=:]\\s*)[^\\s,;&]+", "$1[redacted]")
                .replace('\r', ' ')
                .replace('\n', ' ')
                .replaceAll("\\s+", " ")
                .trim();
        if (sanitized.length() <= SAFE_MESSAGE_LIMIT) {
            return sanitized;
        }
        return sanitized.substring(0, SAFE_MESSAGE_LIMIT);
    }

    private static boolean looksTransient(String rawMessage) {
        String message = rawMessage.toLowerCase(Locale.ROOT);
        return message.contains("timeout")
                || message.contains("timed out")
                || message.contains("temporary")
                || message.contains("temporarily")
                || message.contains("unavailable")
                || message.contains("overload")
                || message.contains("rate limit")
                || message.contains("rate_limit")
                || message.contains("too many requests")
                || message.contains("connection reset")
                || message.contains("connection refused")
                || message.contains("network is unreachable")
                || message.contains("no route to host")
                || message.contains("unexpected end of stream")
                || message.contains("end of file");
    }

    private static Iterable<Throwable> causeChain(@Nullable Throwable throwable) {
        Set<Throwable> result = new HashSet<>();
        Throwable current = throwable;
        while (current != null && result.add(current)) {
            current = current.getCause();
        }
        return result;
    }

    @Nullable
    private static Throwable rootCause(@Nullable Throwable throwable) {
        Throwable current = throwable;
        Throwable root = null;
        Set<Throwable> visited = new HashSet<>();
        while (current != null && visited.add(current)) {
            root = current;
            current = current.getCause();
        }
        return root;
    }

    private static String nullToEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }
}
