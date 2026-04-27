package com.han.nomemo;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class AiResultValidator {
    private static final Set<String> ALLOWED_CATEGORY_CODES = new HashSet<>(Arrays.asList(
            CategoryCatalog.CODE_WORK_SCHEDULE,
            CategoryCatalog.CODE_WORK_TODO,
            CategoryCatalog.CODE_LIFE_PICKUP,
            CategoryCatalog.CODE_LIFE_DELIVERY,
            CategoryCatalog.CODE_LIFE_CARD,
            CategoryCatalog.CODE_LIFE_TICKET,
            CategoryCatalog.CODE_QUICK_NOTE
    ));

    private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(Arrays.asList(
            "pickup",
            "delivery",
            "ticket",
            "schedule",
            "todo",
            "card",
            "note"
    ));

    private AiResultValidator() {
    }

    public static JSONObject validate(JSONObject resultJson) throws Exception {
        if (resultJson == null) {
            throw new IllegalStateException("AI result JSON is null");
        }
        requireStringField(resultJson, "promptVersion");
        String schemaVersion = requireStringField(resultJson, "schemaVersion");
        if (!AiPromptBuilder.SCHEMA_VERSION.equals(schemaVersion)) {
            throw new IllegalStateException("Unsupported schemaVersion: " + schemaVersion);
        }
        requireStringField(resultJson, "title");
        requireStringField(resultJson, "summary");
        requireStringField(resultJson, "analysis");
        requireStringField(resultJson, "memory");
        String categoryCode = requireStringField(resultJson, "suggestedCategoryCode");
        if (!categoryCode.isEmpty() && !ALLOWED_CATEGORY_CODES.contains(categoryCode)) {
            throw new IllegalStateException("Unsupported suggestedCategoryCode: " + categoryCode);
        }
        Object structuredFactsValue = resultJson.opt("structuredFacts");
        if (!(structuredFactsValue instanceof JSONObject)) {
            throw new IllegalStateException("structuredFacts must be an object");
        }
        validateStructuredFacts((JSONObject) structuredFactsValue);
        return resultJson;
    }

    private static void validateStructuredFacts(JSONObject structuredFacts) throws Exception {
        String domain = requireStringField(structuredFacts, "domain");
        if (!domain.isEmpty() && !ALLOWED_DOMAINS.contains(domain)) {
            throw new IllegalStateException("Unsupported structuredFacts.domain: " + domain);
        }
        validateNullableStringField(structuredFacts, "pickupCode");
        validateNullableStringField(structuredFacts, "pickupCodeType");
        validateConfidenceField(structuredFacts, "pickupCodeConfidence");
        validateNullableStringField(structuredFacts, "pickupCodeEvidence");
        validateNullableStringField(structuredFacts, "location");
        validateConfidenceField(structuredFacts, "locationConfidence");
        validateNullableStringField(structuredFacts, "locationEvidence");
        validateNullableStringField(structuredFacts, "merchantOrCompany");
        validateNullableStringField(structuredFacts, "itemName");
        validateNullableStringField(structuredFacts, "orderNumber");
        validateNullableStringField(structuredFacts, "trackingNumber");
        validateNullableStringField(structuredFacts, "amount");
        validateNullableStringField(structuredFacts, "timeWindow");
        validateNullableStringField(structuredFacts, "rawVisibleText");
    }

    private static String requireStringField(JSONObject jsonObject, String fieldName) throws Exception {
        if (!jsonObject.has(fieldName)) {
            throw new IllegalStateException("Missing required field: " + fieldName);
        }
        Object value = jsonObject.opt(fieldName);
        if (value == null || value == JSONObject.NULL) {
            return "";
        }
        if (!(value instanceof String)) {
            throw new IllegalStateException("Field must be a string: " + fieldName);
        }
        return ((String) value).trim();
    }

    private static void validateNullableStringField(JSONObject jsonObject, String fieldName) throws Exception {
        if (!jsonObject.has(fieldName)) {
            throw new IllegalStateException("Missing required field: " + fieldName);
        }
        Object value = jsonObject.opt(fieldName);
        if (value == null || value == JSONObject.NULL) {
            return;
        }
        if (!(value instanceof String)) {
            throw new IllegalStateException("Field must be a string or null: " + fieldName);
        }
    }

    private static void validateConfidenceField(JSONObject jsonObject, String fieldName) throws Exception {
        if (!jsonObject.has(fieldName)) {
            throw new IllegalStateException("Missing required field: " + fieldName);
        }
        Object value = jsonObject.opt(fieldName);
        if (!(value instanceof Number)) {
            throw new IllegalStateException("Field must be numeric: " + fieldName);
        }
        double confidence = ((Number) value).doubleValue();
        if (confidence < 0.0d || confidence > 1.0d) {
            throw new IllegalStateException("Confidence out of range for " + fieldName);
        }
    }
}
