package se.sammygadd.library.halclient;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class HalLayout extends LinearLayout {
    private final int MARGIN_LEFT = 36;
    private final int MARGIN_TOP = 36;
    private final int MARGIN_RIGHT = 36;
    private final int MARGIN_BOTTON = 0;

    private Resource mResource;
    private HalContainer.OnNavigateToListener mOnNavigateToListener;

    protected HalLayout(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    public HalLayout(Context context, Resource resource) {
        super(context);
        mResource = resource;
        setOrientation(VERTICAL);
        addAttributes();
        addButtons();
    }

    public Resource getResource() {
        return mResource;
    }

    public void setOnNavigateToListener(HalContainer.OnNavigateToListener listener) {
        mOnNavigateToListener = listener;
    }

    protected void addAttributes() {
        HashMap<String, String> map = mResource.getAttributes();

        for (String key : map.keySet()) {
            String value = mResource.getAttribute(key, "");
            addAttribute(key, value);
        }
    }

    protected void addButtons() {
        HashMap<String, String> map = mResource.getLinks();

        for (String rel : map.keySet()) {
            if (rel.equals("self")) continue;
            String value = mResource.getLinkHref(rel);
            // String title = mResource.getLinkTitle(rel, value);
            addButton(rel, value);
        }
    }

    protected void addAttribute(String key, String value) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);
        layout.setDividerDrawable(getDivider());

        TextView textViewLabel = new TextView(getContext());
        textViewLabel.setText(key);
        textViewLabel.setTypeface(null, Typeface.BOLD);
        LayoutParams labelParams = generateDefaultLayoutParams();
        labelParams.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTON);
        textViewLabel.setLayoutParams(labelParams);

        TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        LayoutParams params = generateDefaultLayoutParams();
        params.setMarginStart(MARGIN_LEFT);
        textView.setLayoutParams(params);

        layout.addView(textViewLabel);
        layout.addView(textView);
        addView(layout);
    }

    protected Drawable getDivider() {
        return new ColorDrawable(android.graphics.Color.rgb(40, 40, 150));
    }

    private void addButton(String title, String href) {
        Button button = new Button(getContext());
        LayoutParams params = generateDefaultLayoutParams();
        params.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTON);
        button.setLayoutParams(params);
        button.setText(title);
        button.setOnClickListener(btn -> {
            navigateTo(href);
        });
        addView(button);
    }

    protected void navigateTo(String href) {
        if (mOnNavigateToListener != null) {
            Log.i(Constants.TAG, "navigera till: " + href);
            mOnNavigateToListener.onNavigateTo(href);
        } else {
            Log.i(Constants.TAG, "har ingen lyssnare..");
        }
    }
}
