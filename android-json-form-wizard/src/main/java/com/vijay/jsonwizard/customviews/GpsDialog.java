package com.vijay.jsonwizard.customviews;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.rey.material.widget.TextView;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.widgets.GpsFactory;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Dialog that acquires a high-accuracy GPS fix before committing a value back to the form.
 */
public class GpsDialog extends Dialog {

    public static final double DEFAULT_ACCURACY_THRESHOLD_METERS = 10d;
    public static final long DEFAULT_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(15);

    private final View dataView;
    private final TextView latitudeTV;
    private final TextView longitudeTV;
    private final TextView altitudeTV;
    private final TextView accuracyTV;
    private final Context context;
    private final double accuracyThresholdMeters;
    private final long timeoutMillis;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView dialogAccuracyTV;
    private TextView statusMessageTV;
    private ProgressBar progressBar;
    private Button okButton;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private Location lastLocation;
    private Location bestLocation;
    private boolean hasCommitted;

    private Runnable timeoutRunnable;

    public GpsDialog(@NonNull Context context,
                     @NonNull View dataView,
                     @NonNull TextView latitudeTV,
                     @NonNull TextView longitudeTV,
                     @NonNull TextView altitudeTV,
                     @NonNull TextView accuracyTV,
                     double accuracyThresholdMeters,
                     long timeoutMillis) {
        super(context);
        this.context = context;
        this.dataView = dataView;
        this.latitudeTV = latitudeTV;
        this.longitudeTV = longitudeTV;
        this.altitudeTV = altitudeTV;
        this.accuracyTV = accuracyTV;
        this.accuracyThresholdMeters = accuracyThresholdMeters;
        this.timeoutMillis = timeoutMillis;
        init();
    }

    public GpsDialog(@NonNull Context context,
                     @NonNull View dataView,
                     @NonNull TextView latitudeTV,
                     @NonNull TextView longitudeTV,
                     @NonNull TextView altitudeTV,
                     @NonNull TextView accuracyTV) {
        this(context, dataView, latitudeTV, longitudeTV, altitudeTV, accuracyTV,
                DEFAULT_ACCURACY_THRESHOLD_METERS, DEFAULT_TIMEOUT_MILLIS);
    }

    private void init() {
        setContentView(R.layout.dialog_gps);
        setTitle(R.string.loading_location);
        setCancelable(false);
        setCanceledOnTouchOutside(false);

        dialogAccuracyTV = findViewById(R.id.accuracy);
        statusMessageTV = findViewById(R.id.status_message);
        progressBar = findViewById(R.id.location_progress);
        okButton = findViewById(R.id.ok_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        if (dialogAccuracyTV != null) {
            dialogAccuracyTV.setText(context.getString(R.string.unknown_gps_accuracy));
        }

        if (statusMessageTV != null) {
            statusMessageTV.setText(R.string.gps_searching_for_fix);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if (okButton != null) {
            setOkEnabled(false);
            okButton.setOnClickListener(view -> onOkClicked());
        }

        if (cancelButton != null) {
            cancelButton.setOnClickListener(view -> dismiss());
        }

        setOnShowListener(dialog -> onShowDialog());
    }

    private void onShowDialog() {
        hasCommitted = false;
        lastLocation = null;
        bestLocation = null;

        if (dialogAccuracyTV != null) {
            dialogAccuracyTV.setText(context.getString(R.string.unknown_gps_accuracy));
        }

        if (statusMessageTV != null) {
            statusMessageTV.setText(R.string.gps_searching_for_fix);
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        setOkEnabled(false);
        requestLocationUpdates();
        scheduleTimeout();
    }

    private void onOkClicked() {
        Location locationToCommit = lastLocation != null ? lastLocation : bestLocation;
        if (locationToCommit != null) {
            commitLocation(locationToCommit);
        } else {
            Toast.makeText(context, R.string.could_not_get_your_location, Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationRequest locationRequest = new LocationRequest.Builder(5_000L)
                .setMinUpdateIntervalMillis(2_000L)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setWaitForAccurateLocation(true)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    handleLocationSample(location);
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::handleCachedLocation);
        } catch (SecurityException securityException) {
            Timber.e(securityException, "Location permission missing for GPS dialog");
            Toast.makeText(context, R.string.could_not_get_your_location, Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void handleCachedLocation(Location location) {
        if (location != null && bestLocation == null) {
            bestLocation = location;
            lastLocation = location;
            updateUiForLocation(location, false);
        }
    }

    private void handleLocationSample(Location location) {
        if (location == null || hasCommitted) {
            return;
        }

        lastLocation = location;
        if (bestLocation == null || (location.hasAccuracy() && location.getAccuracy() < bestLocation.getAccuracy())) {
            bestLocation = location;
        }

        updateUiForLocation(location, true);

        if (location.hasAccuracy() && location.getAccuracy() <= accuracyThresholdMeters) {
            commitLocation(location);
        } else {
            setOkEnabled(true);
        }
    }

    private void updateUiForLocation(Location location, boolean fromLiveSample) {
        String accuracyDisplay = location.hasAccuracy()
                ? formatMeters(location.getAccuracy())
                : context.getString(R.string.gps_accuracy_unknown_short);

        if (dialogAccuracyTV != null) {
            dialogAccuracyTV.setText(context.getString(R.string.accuracy, accuracyDisplay));
        }

        if (statusMessageTV != null) {
            if (location.hasAccuracy() && location.getAccuracy() <= accuracyThresholdMeters) {
                statusMessageTV.setText(context.getString(R.string.gps_fix_ready, accuracyDisplay));
            } else if (location.hasAccuracy()) {
                statusMessageTV.setText(context.getString(R.string.gps_accuracy_progress,
                        accuracyDisplay,
                        formatMeters(accuracyThresholdMeters)));
            } else if (fromLiveSample) {
                statusMessageTV.setText(R.string.gps_searching_for_fix);
            }
        }
    }

    private void commitLocation(Location location) {
        if (hasCommitted) {
            return;
        }
        hasCommitted = true;
        clearTimeout();

        updateLocationViews(location);

        if (statusMessageTV != null) {
            statusMessageTV.setText(context.getString(R.string.gps_fix_saved, formatMeters(location.getAccuracy())));
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        dismiss();
    }

    private void updateLocationViews(Location location) {
        if (location == null) {
            return;
        }

        String latitudeText = String.format(Locale.US, "%s", location.getLatitude());
        String longitudeText = String.format(Locale.US, "%s", location.getLongitude());
        String altitudeText = String.format(Locale.US, "%.1f m", location.getAltitude());
        String accuracyText = location.hasAccuracy() ? formatMeters(location.getAccuracy()) : context.getString(R.string.gps_accuracy_unknown_short);

        latitudeTV.setText(String.format(context.getString(R.string.latitude), latitudeText));
        longitudeTV.setText(String.format(context.getString(R.string.longitude), longitudeText));
        altitudeTV.setText(String.format(context.getString(R.string.altitude), altitudeText));
        accuracyTV.setText(String.format(context.getString(R.string.accuracy), accuracyText));
        dataView.setTag(R.id.raw_value, GpsFactory.constructString(location));
    }

    private void scheduleTimeout() {
        clearTimeout();
        timeoutRunnable = () -> {
            if (bestLocation != null) {
                lastLocation = bestLocation;
                updateUiForLocation(bestLocation, false);
                setOkEnabled(true);
                if (statusMessageTV != null) {
                    statusMessageTV.setText(context.getString(R.string.gps_timeout_best_accuracy,
                            formatMeters(bestLocation.getAccuracy())));
                }
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(context, R.string.could_not_get_your_location, Toast.LENGTH_LONG).show();
                dismiss();
            }
        };
        handler.postDelayed(timeoutRunnable, timeoutMillis);
    }

    private void clearTimeout() {
        if (timeoutRunnable != null) {
            handler.removeCallbacks(timeoutRunnable);
            timeoutRunnable = null;
        }
    }

    private void setOkEnabled(boolean enabled) {
        if (okButton != null) {
            okButton.setEnabled(enabled);
            okButton.setAlpha(enabled ? 1f : 0.5f);
        }
    }

    private String formatMeters(double meters) {
        return String.format(Locale.US, "%.1f m", meters);
    }

    @Override
    public void dismiss() {
        cleanupLocationUpdates();
        super.dismiss();
    }

    public View getRecordButtonView() {
        return dataView;
    }

    private void cleanupLocationUpdates() {
        clearTimeout();

        if (fusedLocationClient != null && locationCallback != null) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            } catch (SecurityException securityException) {
                Timber.w(securityException, "Failed to remove location updates");
            }
        }

        Object originalState = dataView.getTag(R.id.gps_record_button_enabled_state);
        if (originalState instanceof Boolean) {
            boolean enabled = (Boolean) originalState;
            dataView.setEnabled(enabled);
            dataView.setAlpha(enabled ? 1f : 0.5f);
            dataView.setTag(R.id.gps_record_button_enabled_state, null);
        } else {
            dataView.setEnabled(true);
            dataView.setAlpha(1f);
        }
    }
}
