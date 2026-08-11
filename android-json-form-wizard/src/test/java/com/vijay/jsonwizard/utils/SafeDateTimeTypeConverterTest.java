package com.vijay.jsonwizard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SafeDateTimeTypeConverterTest {

    private final SafeDateTimeTypeConverter converter = new SafeDateTimeTypeConverter();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(DateTime.class, converter)
            .create();

    @Test
    public void deserializeReturnsNullForMalformedValue() {
        assertNull(converter.deserialize(new JsonPrimitive("not-a-date"), DateTime.class, null));
    }

    @Test
    public void deserializeParsesIsoDateTime() {
        DateTime dateTime = converter.deserialize(new JsonPrimitive("2026-08-11T10:20:30.000Z"), DateTime.class, null);

        assertEquals(2026, dateTime.getYear());
        assertEquals(8, dateTime.getMonthOfYear());
        assertEquals(11, dateTime.getDayOfMonth());
    }

    @Test
    public void serializeUsesIsoUtcFormat() {
        JsonElement jsonElement = gson.toJsonTree(new DateTime(2026, 8, 11, 10, 20, 30, 0));

        assertTrue(jsonElement.isJsonPrimitive());
        assertEquals("2026-08-11T10:20:30.000Z", jsonElement.getAsString());
    }
}
