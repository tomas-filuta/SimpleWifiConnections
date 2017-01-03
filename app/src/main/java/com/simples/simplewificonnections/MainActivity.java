package com.simples.simplewificonnections;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ListView;

import com.simples.simplewificonnections.adapter.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents main activity of APP.
 */
public class MainActivity extends AppCompatActivity {

    private WifiHandler wifiHandler;
    private WifiListAdapter adapter;
    private WifiSearch searcher;

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermission(String permission) {
        requestPermissions(new String[]{permission},1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ... permissions for newer SDKs ...
        if (Build.VERSION.SDK_INT >= 23) requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        // ... creating new WiFi handler ...
        wifiHandler = new WifiHandler(this);

        // ... creating new searcher thread ...
        searcher = new WifiSearch(this);
        Thread t = new Thread(searcher);
        t.start();

        // ... initializing list of results ...
        initResultsList();
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        // ... terminating searcher thread ...
        searcher.stop();
        // ... releasing WiFi receiver ...
        wifiHandler.releaseReceiver();
    }

    /**
     * Initializes result list.
     */
    void initResultsList() {
        // ... creating new adapter ...
        adapter = new WifiListAdapter(this, R.layout.wifi_list_node, R.id.wifi_name,
                R.id.wifi_specs, R.id.specs_layout, new ArrayList<String>(), new ArrayList<String>(),
                R.id.button_connect,this);

        // ... setting WiFi list view adapter ...
        ListView wifiList = (ListView) findViewById(R.id.wifi_list_view);
        wifiList.setAdapter(adapter);
    }

    /**
     * Refreshes WiFi list.
     */
    public void refreshWifiList() {
        // ... getting new list of WiFi result ..
        wifiHandler.scan();
        // ... checking result => nothing to do ...
        if (wifiHandler.getScanResult() == null) return;

        List<String> scans = new ArrayList<>();
        List<String> specs = new ArrayList<>();
        // ... iterating over all scan results ...
        for (ScanResult r : wifiHandler.getScanResult()) {
//            Log.i("## WiFi scan result >> ", String.valueOf(r));
            // ... getting SSID ...
            scans.add(r.SSID);
            // ... getting WiFi specifications ...
            String info = " # SIGNAL LEVEL: " + r.level + "\n" +
                          " # FREQUENCY: " + r.frequency + "\n" +
                          " # SECURITY: " + r.capabilities + "\n";
            specs.add(info);
        }
        // ... setting new values into
        adapter.setScans(scans);
        adapter.setSpecs(specs);
    }

    /**
     * Connects into required WIFi.
     * @param wifiId ... SSID of WiFi
     */
    public void connect(final String wifiId) {
        // ... password is not required ...
        if (wifiHandler.getWiFiSecurity(wifiId) == WifiHandler.WifiSecurity.NONE) {
            // ... connecting into required WiFi ...
            wifiHandler.connectToWifi(wifiId, "");
        } else { // ... there is password ...
            // ... creating new alert ...
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            // ... setting title ...
            alert.setTitle("---  ENTER PASSWORD  ---");
            // ... creating password text line ...
            final EditText password = new EditText(this);
//            password.setHint("...");
            password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            alert.setView(password);
            // ... creating OK button ... with click listener ..
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // ... connecting into required WiFi ...
                    wifiHandler.connectToWifi(wifiId,password.getText().toString());
                }
            });
            // ... showing alert ...
            alert.create().show();
        }
    }
}
