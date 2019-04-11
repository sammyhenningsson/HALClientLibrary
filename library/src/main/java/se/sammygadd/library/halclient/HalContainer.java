package se.sammygadd.library.halclient;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import se.sammygadd.library.halclient.resources.Error;
import se.sammygadd.library.halclient.resources.Form;
import se.sammygadd.library.halclient.resources.Resource;
import se.sammygadd.library.halclient.resources.ValidationError;
import se.sammygadd.library.halclient.layouts.FormLayout;
import se.sammygadd.library.halclient.layouts.HalLayout;

public class HalContainer extends Container {

    public interface OnNavigateToListener {
        void onNavigateTo(String rel, String uri);
    }

    public interface OnSubmitFormListener {
        void onSubmitForm(Form form);
    }

    protected OnNavigateToListener mOnNavigateToListener;
    protected OnSubmitFormListener mOnSubmitFormListener;

    public HalContainer(AppCompatActivity activity) {
        super(activity);
        mActivity = activity;
        mViewModel = ViewModelProviders.of(activity).get(ResourceViewModel.class);
        mView = new MutableLiveData<>();
        mBackStack = new ArrayList<>();
        setOnNavigateToListener(null);
        setOnSubmitFormListener(null);
        ApiService.create(activity.getApplicationContext());
    }

    private OnNavigateToListener defaultOnNavigateToListener() {
        return (rel, uri) -> showResource(uri);
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
            listener = defaultOnNavigateToListener();
        }
        mOnNavigateToListener = listener;
    }

    public void setOnSubmitFormListener(OnSubmitFormListener listener) {
        if (listener == null) {
            listener = defaultOnSubmitFormListener();
        }
        mOnSubmitFormListener = listener;
    }

    public HalLayout getHALLayout(Resource resource) {
        return new HalLayout(mActivity, resource, mOnNavigateToListener);
    }

    public HalLayout getErrorLayout(Error error) {
        // Currently there is no special view for errors
        return new HalLayout(mActivity, error, mOnNavigateToListener);
    }

    public FormLayout getFormLayout(Form form) {
        return new FormLayout(mActivity, form, mOnNavigateToListener, mOnSubmitFormListener);
    }

    public FormLayout getFormLayout(Form form, ValidationError validationError) {
        return new FormLayout(mActivity, form, validationError, mOnNavigateToListener, mOnSubmitFormListener);
    }
}

