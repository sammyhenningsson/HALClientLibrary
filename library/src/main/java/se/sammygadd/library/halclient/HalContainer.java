package se.sammygadd.library.halclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

public class HalContainer {

    private AppCompatActivity mActivity;
    private ResourceViewModel mViewModel;
    private MutableLiveData<HalLayout> mLayout;
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
        mLayout = new MutableLiveData<>();
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
                mLayout.setValue(getLayout(result));
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

    public LiveData<HalLayout> showResource(String uri) {
        mViewModel.getResource(uri).observe(mActivity, result -> {
            mLayout.setValue(getLayout(result));
        });
        return mLayout;
    }

    private HalLayout getLayout(ResourceWrapper result) {
        HalLayout layout;
        if (result.isSuccessful()) {
            if (result.isForm()) {
                FormLayout form = new FormLayout(mActivity, (Form) result.getResource());
                form.setOnSubmitFormListener(mOnSubmitFormListener);
                layout = form;
            } else {
                layout = new HalLayout(mActivity, result.getResource());
            }
        } else {
            layout = getErrorLayout(result);
        }
        layout.setOnNavigateToListener(mOnNavigateToListener);
        return layout;
    }

    private HalLayout getErrorLayout(ResourceWrapper result) {
        // FIXME
        return new HalLayout(mActivity, result.getResource());
    }
}

