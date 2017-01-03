package com.simples.simplewificonnections.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Represents list of WiFi specifications.
 */
public class SpecsListAdapter extends ArrayAdapter {

    protected List<String> scans;
    protected List<String> specs;
    protected int specsLayoutId;
    protected int scanId;
    protected int specsId;
    protected int resourceId;

    /**
     * Creates new WiFi specifications adapter.
     *
     * @param context ... of view.
     * @param resourceId ... of layout.
     * @param scanId ... of scan textView.
     * @param specsId ... of specs textView.
     * @param layoutId ... of specs layout which shows or hides.
     * @param scans ... of founded WiFi.
     * @param specs  ... of WiFi.
     */
    public SpecsListAdapter(Context context, int resourceId, int scanId,
                            int specsId, int layoutId,
                            List<String> scans, List<String> specs) {
        super(context, resourceId, scanId, scans);
        this.resourceId = resourceId;
        this.scanId = scanId;
        this.specsId = specsId;
        this.specsLayoutId = layoutId;
        this.scans = scans;
        this.specs = specs;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ... instance of view already exists ...
        if(convertView != null) {
            // ... filling view ...
            fillView(convertView,position);
            return convertView;
        } else {
            // ... creating new instance of view ...
            final View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            // ... filling view ...
            fillView(view, position);
            // ... adding specs show/hide functionality ...
            TextView title = (TextView)view.findViewById(scanId);
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout specs = (LinearLayout) view.findViewById(specsLayoutId);
                    if(specs.getVisibility() == View.GONE) {
                        specs.setVisibility(View.VISIBLE);
                    }
                    else {
                        specs.setVisibility(View.GONE);
                    }
                }
            });
            // ... hiding specs of WiFi ...
            LinearLayout specs = (LinearLayout) view.findViewById(specsLayoutId);
            specs.setVisibility(View.GONE);
            // ... we finished ...
            return view;
        }
    }

    /**
     * Fills required text view.
     * @param view ... to fill.
     * @param index ... of from list.
     */
    private void fillView(View view, int index) {
        // ... setting title ...
        TextView title = (TextView) view.findViewById(scanId);
        title.setText(scans.get(index));
        // ... setting specs ...
        TextView specs = (TextView) view.findViewById(specsId);
        specs.setText(this.specs.get(index));
    }

    public void setScans(List<String> scans) {
        this.scans = scans;
        clear();
        addAll(scans);
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
        super.notifyDataSetChanged();
    }
}
