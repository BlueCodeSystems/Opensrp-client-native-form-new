package com.vijay.jsonwizard.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

import timber.log.Timber;

/**
 * Strict DateTime adapter that rejects malformed strings instead of falling back to Joda's
 * permissive parser. This prevents sync-time OOMs on corrupt date values.
 */
public class SafeDateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

    private static final DateTimeFormatter DEFAULT_OUTPUT_FORMAT =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC();
    private static final DateTimeFormatter[] DEFAULT_INPUT_FORMATS = new DateTimeFormatter[]{
            DEFAULT_OUTPUT_FORMAT,
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC(),
            DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC()
    };

    private final DateTimeFormatter dateTimeFormatter;

    public SafeDateTimeTypeConverter() {
        this(null);
    }

    public SafeDateTimeTypeConverter(String dateTimeFormat) {
        this.dateTimeFormatter = dateTimeFormat == null ? null : DateTimeFormat.forPattern(dateTimeFormat);
    }

    @Override
    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) {
            return null;
        }

        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement millisElement = jsonObject.get("iMillis");
            if (millisElement != null && millisElement.isJsonPrimitive()) {
                try {
                    return new DateTime(millisElement.getAsLong());
                } catch (Exception e) {
                    Timber.w(e, "Ignoring malformed DateTime millis payload");
                    return null;
                }
            }
            return null;
        }

        if (!json.isJsonPrimitive()) {
            return null;
        }

        String value = json.getAsString();
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return null;
        }

        try {
            if (dateTimeFormatter != null) {
                return dateTimeFormatter.parseDateTime(value);
            }

            for (DateTimeFormatter inputFormat : DEFAULT_INPUT_FORMATS) {
                try {
                    return inputFormat.parseDateTime(value);
                } catch (IllegalArgumentException ignored) {
                    // Keep trying the next strict formatter.
                }
            }
        } catch (Exception e) {
            Timber.w(e, "Ignoring malformed DateTime value: %s", value);
            return null;
        }

        Timber.w("Ignoring unsupported DateTime value: %s", value);
        return null;
    }

    @Override
    public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }

        if (dateTimeFormatter == null) {
            return new JsonPrimitive(src.toString(DEFAULT_OUTPUT_FORMAT));
        }

        return new JsonPrimitive(src.toString(dateTimeFormatter));
    }
}
