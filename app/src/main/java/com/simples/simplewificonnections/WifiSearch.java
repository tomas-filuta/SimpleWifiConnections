package com.simples.simplewificonnections;

import com.simples.simplewificonnections.MainActivity;

/**
 * Represents thread for WIFI searching.
 */
public class WifiSearch implements Runnable {

    // ... instance of main activity ...
    private MainActivity activity;
    // ... indicator of thread activity ...
    private boolean isActive;

    /** Contains scan timeout in 'ms'. */
    private static final int REFRESH_TIMEOUT = 2500;

    /**
     * Creates new thread for WIFI searching.
     * @param activity ... instance of main activity
     */
    public WifiSearch(MainActivity activity) {
        this.activity = activity;
        isActive = true;
    }

    @Override
    public void run() {
        while(isActive) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // ... refreshing wifi list ...
                    activity.refreshWifiList();
                }
            });
            try {
                Thread.sleep(REFRESH_TIMEOUT);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops thread.
     */
    public void stop() {
        isActive = false;
    }
}
