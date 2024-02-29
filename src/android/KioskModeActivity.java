package de.amicaldo.cordova.plugin;

import android.app.ActivityManager;
import android.provider.Settings;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import org.apache.cordova.*;


public class KioskModeActivity extends CordovaActivity {
    private static volatile KioskModeActivity instance = null;

    private volatile boolean kioskModeEnabled = true;
    private volatile boolean adbEnabled = false;

    public static KioskModeActivity getInstance() {
        return KioskModeActivity.instance;
    }

    @Override
    protected void onStart() {
        super.onStart();

        KioskModeActivity.instance = this;
    }

    @Override
    protected void onStop() {
        super.onStop();

        KioskModeActivity.instance = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.init();

        if (KioskModeActivity.instance != null) {
            this.finish();
        }

        this.loadUrl(this.launchUrl);

        this.updateDeviceProvisioning();
        this.updateSystemUiVisibility();

        this.getWindow()
            .getDecorView()
            .setOnSystemUiVisibilityChangeListener((int flags) -> {
                this.updateSystemUiVisibility();
            });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (this.kioskModeEnabled) {
            ActivityManager activityManager = (ActivityManager) this.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }

    /**
     * Custom methods
     */
    public void setKioskModeEnabled(boolean enabled) {
        this.kioskModeEnabled = enabled;
    }

    public boolean getKioskModeEnabled() {
        return this.kioskModeEnabled;
    }

    public void updateDeviceProvisioning() {
        try {
            Settings.Global.putInt(
                this.getApplicationContext().getContentResolver(),
                Settings.Global.DEVICE_PROVISIONED,
                (this.kioskModeEnabled) ? 0 : 1
            );
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateSystemUiVisibility() {
        if (this.kioskModeEnabled) {
            this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                    View.FEATURE_ACTION_BAR_OVERLAY |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.STATUS_BAR_VISIBLE |
                    View.SYSTEM_UI_FLAG_VISIBLE
                );
        } else {
            this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }

    public void setAdbEnabled(boolean enabled) {
        this.adbEnabled = enabled;

        this.updateAdb();
    }

    public boolean getAdbEnabled() {
        return this.adbEnabled;
    }

    public void updateAdb() {
        try {
            Settings.Global.putInt(
                this.getApplicationContext().getContentResolver(),
                Settings.Global.ADB_ENABLED,
                (this.adbEnabled) ? 1 : 0
            );
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
