package com.vijay.jsonwizard.utils;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import java.lang.reflect.Field;

import timber.log.Timber;

/**
 * Initializes a safer Gson instance early in app startup so dependency code
 * uses strict DateTime parsing instead of Joda's permissive fallback parser.
 */
public class JsonFormGsonInitializer extends ContentProvider {

    @Override
    public boolean onCreate() {
        try {
            Class<?> jsonFormUtilsClass = Class.forName("org.smartregister.util.JsonFormUtils");
            Field gsonField = jsonFormUtilsClass.getDeclaredField("gson");
            gsonField.setAccessible(true);
            gsonField.set(null, new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .registerTypeAdapter(DateTime.class, new SafeDateTimeTypeConverter())
                    .create());
        } catch (Exception e) {
            Timber.e(e, "Unable to install safe Gson instance");
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
