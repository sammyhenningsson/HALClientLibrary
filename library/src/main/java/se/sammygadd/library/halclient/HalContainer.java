package se.sammygadd.library.halclient;

import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
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
        mViewModel.getResource(uri).observe(mActivity, result -> {
            mView.setValue(getView(result));
        });
        return mView;
    }

    private View getView(ResourceWrapper result) {
        return getView(result, null);
    }

    private View getView(ResourceWrapper result, Form form) {
        HalLayout layout;

        if (form != null && result.isValidationError()) {
            layout = getFormLayout(form, result.getValidationError());
        } else if (result.isFailure()) {
            layout = getErrorLayout(result.getResource());
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

    private HalLayout getErrorLayout(Resource resource) {
        // Currently there is no special view for errors
        return new HalLayout(mActivity, resource, mOnNavigateToListener);
    }

    private FormLayout getFormLayout(Form form) {
        return new FormLayout(mActivity, form, mOnNavigateToListener, mOnSubmitFormListener);
    }

    private FormLayout getFormLayout(Form form, ValidationError validationError) {
        return new FormLayout(mActivity, form, validationError, mOnNavigateToListener, mOnSubmitFormListener);
    }
}

