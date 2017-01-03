package com.simples.simplewificonnections.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.simples.simplewificonnections.MainActivity;

import java.util.List;

/**
 * Represents WiFi list adapter with connection functionality.
 */
public class WifiListAdapter extends SpecsListAdapter {

    // ... instance of main activity ...
    protected MainActivity activity;
    // ... reference on button ...
    protected int buttonId;

    /**
     * Creates new WiFi adapter.
     *
     * @param context ... of view.
     * @param resourceId ... of layout.
     * @param scanId ... of scan textView.
     * @param specsId ... of specs textView.
     * @param layoutId ... of specs layout which shows or hides.
     * @param scans ... of founded WiFi.
     * @param specs  ... of WiFi.
     */
    public WifiListAdapter(Context context, int resourceId, int scanId,
                           int specsId, int layoutId, List<String> scans,
                           List<String> specs, int buttonId, MainActivity activity) {
        super(context, resourceId, scanId, specsId, layoutId, scans, specs);
        this.buttonId = buttonId;
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View res = super.getView(position, convertView, parent);

        if(convertView == null) {
            // ... adding connection functionality ...
            Button button = (Button) res.findViewById(buttonId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView title = (TextView) res.findViewById(scanId);
                    // ... connecting ...
                    activity.connect(String.valueOf(title.getText()));
                }
            });
        }

        return res;
    }
}
