package se.sammygadd.library.halclient.layouts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import androidx.cardview.widget.CardView;
import se.sammygadd.library.halclient.Constants;
import se.sammygadd.library.halclient.HalContainer;
import se.sammygadd.library.halclient.R;
import se.sammygadd.library.halclient.resources.Link;
import se.sammygadd.library.halclient.resources.Resource;

public class HalLayout extends LinearLayout {
    protected final int MARGIN_LEFT = 36;
    protected final int MARGIN_TOP = 36;
    protected final int MARGIN_RIGHT = 36;
    protected final int MARGIN_BOTTOM = 0;
    protected final float TITLE_TEXT_SIZE = 20;
    protected final int DEFAULT_BACKGROND_COLOR = Color.WHITE;
    protected final int DEFAULT_PRIMARY_COLOR = 0xFF4DB6AC; // TEAL 50 (300)
    protected final int DEFAULT_SECONDARY_COLOR = 0xFF4CAF50; // GREEN 50 (500)
    protected final int DIVIDER_COLOR = 0xFFE0E0E0; // GRAY 50 (300)
    protected final int BUTTON_BACKGROUND_COLOR = 0;



    protected Resource mResource;
    protected HalContainer.OnNavigateToListener mOnNavigateToListener;
    protected Boolean mIsEmbedded;

    protected HalLayout(Context context) {
        super(context);
        mIsEmbedded = false;
        setOrientation(VERTICAL);
    }

    public HalLayout(Context context, Resource resource, HalContainer.OnNavigateToListener listener) {
        this(context, resource, listener,false);
    }

    public HalLayout(
            Context context,
            Resource resource,
            HalContainer.OnNavigateToListener listener,
            boolean isEmbedded
    ) {
        super(context);
        mResource = resource;
        mOnNavigateToListener = listener;
        mIsEmbedded = isEmbedded;
        setOrientation(VERTICAL);

        addCard();
        addEmbeddedResources();
    }

    private void addCard() {
        Context context = getContext();

        CardView cardView = new CardView(context);
        LayoutParams cardParams = generateDefaultLayoutParams();
        cardParams.setMargins(MARGIN_LEFT, MARGIN_TOP , MARGIN_RIGHT, MARGIN_BOTTOM);
        cardView.setLayoutParams(cardParams);
        addView(cardView);

        LinearLayout cardLayout = new LinearLayout(context);
        cardLayout.setOrientation(VERTICAL);
        cardView.addView(cardLayout);

        LinearLayout attributes = new LinearLayout(context);
        attributes.setOrientation(VERTICAL);
        cardLayout.addView(attributes);
        addAttributes(attributes);

        if (mResource.getAttributes().size() > 0) {
            addDivider(cardLayout);
        }

        LinearLayout actions = new LinearLayout(context);
        LayoutParams actionParams = generateDefaultLayoutParams();
        actionParams.setMarginEnd(MARGIN_TOP);
        actionParams.height = LayoutParams.WRAP_CONTENT;
        actions.setLayoutParams(actionParams);
        actions.setOrientation(VERTICAL);
        actions.setDividerDrawable(getDivider());
        actions.setShowDividers(SHOW_DIVIDER_BEGINNING);
        cardLayout.addView(actions);
        addButtons(actions);
    }

    public Resource getResource() {
        return mResource;
    }

    protected void navigateTo(String rel, String href) {
        if (mOnNavigateToListener != null) {
            Log.i(Constants.TAG, "navigera till: " + href);
            mOnNavigateToListener.onNavigateTo(rel, href);
        } else {
            Log.d(Constants.TAG, "Button was clicked but it has no OnNavigateToListener");
        }
    }

    protected void addDivider(ViewGroup view) {
        LayoutParams dividerParams = generateDefaultLayoutParams();
        dividerParams.height = 1;
        dividerParams.width = LayoutParams.MATCH_PARENT;
        dividerParams.setMargins(MARGIN_LEFT * 2, MARGIN_TOP, MARGIN_RIGHT * 2, MARGIN_BOTTOM);
        View divider = new View(getContext());
        divider.setLayoutParams(dividerParams);
        divider.setBackgroundColor(DIVIDER_COLOR);
        view.addView(divider);
    }

    protected void addAttributes(ViewGroup card) {
        HashMap<String, String> map = mResource.getAttributes();

        for (String key : map.keySet()) {
            String value = mResource.getAttribute(key, "");
            addAttribute(card, key, value);
        }
    }

    protected void addButtons(ViewGroup card) {
        HashMap<String, Link> map = mResource.getLinks();

        for (String rel : map.keySet()) {
            Link link = mResource.getLink(rel);
            String title = link.title();
            if (rel.equals("self")) {
                if (mIsEmbedded) {
                    title = "show";
                } else {
                    continue;
                }
            }
            addButton(card, title, link.rel(), link.href());
        }
    }

    protected void addEmbeddedResources() {
        HashMap<String, List<Resource>> map = mResource.getEmbedded();

        for (String name : map.keySet()) {
            List<Resource> list = mResource.getEmbedded(name);
            if (list.isEmpty()) continue;

            addEmbeddedTitle(name); for (Resource embedded : list) {
                addEmbeddedResource(embedded);
            }
        }

    }

    protected void addAttribute(ViewGroup card, String key, String value) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);

        TextView textViewLabel = new TextView(getContext());
        textViewLabel.setText(key);
        textViewLabel.setTypeface(null, Typeface.BOLD);
        LayoutParams labelParams = generateDefaultLayoutParams();
        labelParams.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
        textViewLabel.setLayoutParams(labelParams);

        TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        LayoutParams params = generateDefaultLayoutParams();
        params.setMarginStart(MARGIN_LEFT);
        textView.setLayoutParams(params);

        layout.addView(textViewLabel);
        layout.addView(textView);

        card.addView(layout);
    }

    protected Drawable getDivider() {
        return new ColorDrawable(DIVIDER_COLOR);
    }

    protected void addButton(ViewGroup card, String title, String rel, String href) {
        LayoutParams params = generateDefaultLayoutParams();
        params.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);

        Button button = new Button(getContext());
        button.setLayoutParams(params);
        button.setText(title);
        button.setTextColor(getPrimaryColor());
        button.setBackgroundColor(BUTTON_BACKGROUND_COLOR);
        button.setOnClickListener(btn -> navigateTo(rel, href));

        card.addView(button);
    }

    protected void addEmbeddedTitle(String title) {
        TextView textViewTitle = new TextView(getContext());
        textViewTitle.setText(title);
        textViewTitle.setTextSize(TITLE_TEXT_SIZE);
        textViewTitle.setTypeface(null, Typeface.BOLD);
        LayoutParams labelParams = generateDefaultLayoutParams();
        labelParams.setMargins(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM);
        textViewTitle.setLayoutParams(labelParams);
        addView(textViewTitle);
    }

    protected void addEmbeddedResource(Resource resource) {
        HalLayout embedded = new HalLayout(getContext(), resource, mOnNavigateToListener, true);
        addView(embedded);
    }

    private int getBackgroundColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.background, typedValue, true);
        if (typedValue.data == 0) return DEFAULT_BACKGROND_COLOR;
        return typedValue.data;
    }
    private int getPrimaryColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        if (typedValue.data == 0) return DEFAULT_PRIMARY_COLOR;
        return typedValue.data;
    }

    private int getSecondaryColor() {
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        if (typedValue.data == 0) return DEFAULT_SECONDARY_COLOR;
        return typedValue.data;
    }
}
