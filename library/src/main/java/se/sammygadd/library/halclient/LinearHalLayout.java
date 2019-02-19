package se.sammygadd.library.halclient;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LinearHalLayout extends LinearLayout {
    private Resource mResource;
    protected Context mContext; // FIXME: Where is this saved?? It should be inherited somewhere?!?!!

    public LinearHalLayout(Context context, Resource resource) {
        super(context);
        mContext = context;
        mResource = resource;
        setOrientation(VERTICAL);
    }

    protected View createView(String key) {
        TextView textView = new TextView(mContext);
        textView.setText(key + ": " + mResource.getAttribute(key));
        textView.setLayoutParams(generateDefaultLayoutParams());
        return textView;
    }
}
