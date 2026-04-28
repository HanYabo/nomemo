package com.han.nomemo;

public enum AiFailureStage {
    CLOUD_REQUEST,
    TOKEN_EXHAUSTED,
    JSON_PARSE,
    SCHEMA_VALIDATE,
    JSON_REPAIR,
    LOCAL_FALLBACK
}
