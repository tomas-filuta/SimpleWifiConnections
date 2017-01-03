package com.simples.simplewificonnections;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Represents WiFi handler which contains info about them. Also manages connections.
 */
public class WifiHandler {

    /** Contains all used WiFi security types. */
    public enum WifiSecurity { NONE, WEP, WPA }

    private WifiManager wifiManager;
    private List<ScanResult> scanResults;
    private Context context;

    private BroadcastReceiver receiver;
    /** Releases broadcast receiver. */
    public void releaseReceiver() {
        context.unregisterReceiver(receiver);
    }

    /**
     * Creates new WiFi handler.
     * @param context ... of view.
     */
    public WifiHandler(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        // ... adding new broadcast receiver ...
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanResults = wifiManager.getScanResults();
            }
        };
        context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // ... scanning ...
        scan();
    }

    /**
     * Starts scan of WiFi networks.
     */
    public void scan() {
        wifiManager.startScan();
    }

    /**
     * Returns list of scan results.
     * @return ... list of ScanResult
     */
    public List<ScanResult> getScanResult() {
        return scanResults;
    }

    /**
     * Tries to connects into required WiFi.
     * @param wifiId ... SSID of WiFi.
     * @param password ... of WiFi.
     * @return ... TRUE means that connection was successfully realised.
     */
    public boolean connectToWifi(String wifiId, String password) {
        // ... getting WiFi configuration ...
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + wifiId + "\"";

        // ... getting WiFi security ...
        switch (getWiFiSecurity(wifiId)) {
            // ... there is WEB security ...
            case WEP:
                conf.wepKeys[0] = password;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                break;
            // ... there is WPA security ...
            case WPA:
                conf.preSharedKey = "\"" + password + "\"";
                break;
            // ... there isn't security ...
            case NONE:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        // ... checking network ...
        int networkId = wifiManager.addNetwork(conf);
        if (networkId != -1) {
            // ... disabling all already connected WiFi networks ...
            for (WifiConfiguration con : wifiManager.getConfiguredNetworks()) {
                wifiManager.disableNetwork(con.networkId);
            }
            // ... establishing connection ...
            wifiManager.disconnect();
            wifiManager.enableNetwork(networkId, true);
            wifiManager.reconnect();
            return true;
        }
        // ... something's wrong ...
        return false;
    }

    /**
     * Returns WiFi security type. Can be 'WEB', 'WPA' or 'NONE' (default).
     * @param wifiId - SSID of WiFi
     * @return ... type of security
     */
    public WifiSecurity getWiFiSecurity(String wifiId) {
        // ... iterating over all scan results ...
        for (ScanResult result : scanResults) {
            if (result.SSID.equals(wifiId)) {
                // ... there is WEB security ...
                if (result.capabilities.contains("WEP")) {
                    return WifiSecurity.WEP;
                }
                // ... there is WPA security ...
                if (result.capabilities.contains("WPA")) {
                    return WifiSecurity.WPA;
                }
            }
        }
        // ... there isn't security ...
        return WifiSecurity.NONE;
    }
}
