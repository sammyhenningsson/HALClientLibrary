package se.sammygadd.library.halclient;

import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;
import se.sammygadd.library.halclient.layouts.FormLayout;
import se.sammygadd.library.halclient.layouts.HalLayout;

public class HalContainer {

    private AppCompatActivity mActivity;
    private ResourceViewModel mViewModel;
    private MutableLiveData<View> mView;
    private OnNavigateToListener mOnNavigateToListener;
    private OnSubmitFormListener mOnSubmitFormListener;
    private List<String> mBackStack;

    public interface OnNavigateToListener {
        void onNavigateTo(String uri);
    }

    public interface OnSubmitFormListener {
        void onSubmitForm(Form form);
    }

    public HalContainer(AppCompatActivity activity) {
        mActivity = activity;
        mViewModel = ViewModelProviders.of(activity).get(ResourceViewModel.class);
        mView = new MutableLiveData<>();
        mOnNavigateToListener = defaultOnNavigateToListener();
        mOnSubmitFormListener = defaultOnSubmitFormListener();
        mBackStack = new ArrayList<>();
        ApiService.create(activity.getApplicationContext());
    }

    private OnNavigateToListener defaultOnNavigateToListener() {
        return this::showResource;
    }

    private OnSubmitFormListener defaultOnSubmitFormListener() {
        return (form) -> {
            mViewModel.submitForm(form).observe(mActivity, result -> {
                mView.setValue(getView(result, form));
            });
        };
    }

    public void setOnNavigateToListener(OnNavigateToListener listener) {
        if (listener == null) {
            mOnNavigateToListener = defaultOnNavigateToListener();
        } else {
            mOnNavigateToListener = listener;
        }
    }

    public void setOnSubmitFormListener(OnSubmitFormListener listener) {
        if (listener == null) {
            mOnSubmitFormListener = defaultOnSubmitFormListener();
        } else {
            mOnSubmitFormListener = listener;
        }
    }

    public LiveData<View> showResource(String uri) {
        mBackStack.add(uri);
        mViewModel.getResource(uri).observe(mActivity, result -> {
            mView.setValue(getView(result));
        });
        return mView;
    }

    public boolean canGoBack() {
        return mBackStack.size() > 1;
    }

    public LiveData<View> goBack() {
        if (mBackStack.size() > 1) {
            mBackStack.remove(mBackStack.size() - 1); // remove current
            String uri = mBackStack.get(mBackStack.size() - 1);
            mBackStack.remove(mBackStack.size() - 1); // remove last (will be readded in showResource)
            return showResource(uri);
        } else {
            return mView;
        }
    }

    private View getView(ResourceWrapper result) {
        return getView(result, null);
    }

    private View getView(ResourceWrapper result, Form form) {
        HalLayout layout;

        if (result == null) {
            // FIXME: How to handle this? 
            // Perhaps we should pass in the uri and send out an intent to
            // open the uri in browser instead???)
        }

        if (form != null && result.isValidationError()) {
            layout = getFormLayout(form, result.getValidationError());
        } else if (result.isError()) {
            layout = getErrorLayout(result.getError());
        } else if (result.isForm()) {
            layout = getFormLayout(result.getForm());
        } else {
            layout = getHALLayout((result.getResource()));
        }
        ScrollView view = new ScrollView(mActivity.getApplicationContext());
        view.addView(layout);
        return view;
    }

    private HalLayout getHALLayout(Resource resource) {
        return new HalLayout(mActivity, resource, mOnNavigateToListener);
    }

    private HalLayout getErrorLayout(Error error) {
        // Currently there is no special view for errors
        return new HalLayout(mActivity, error, mOnNavigateToListener);
    }

    private FormLayout getFormLayout(Form form) {
        return new FormLayout(mActivity, form, mOnNavigateToListener, mOnSubmitFormListener);
    }

    private FormLayout getFormLayout(Form form, ValidationError validationError) {
        return new FormLayout(mActivity, form, validationError, mOnNavigateToListener, mOnSubmitFormListener);
    }
}

