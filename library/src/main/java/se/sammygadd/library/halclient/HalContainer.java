package se.sammygadd.library.halclient;

import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

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
                mView.setValue(getView(result));
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
        HalLayout layout;
        if (!result.isSuccessful()) {
            layout = getErrorLayout(result);
        } else if (result.isForm()) {
            layout = getFormLayout(result);
        } else {
            layout = new HalLayout(mActivity, result.getResource(), mOnNavigateToListener);
        }
        ScrollView view = new ScrollView(mActivity.getApplicationContext());
        view.addView(layout);
        return view;
    }

    private HalLayout getErrorLayout(ResourceWrapper result) {
        // FIXME
        return new HalLayout(mActivity, result.getResource(), mOnNavigateToListener);
    }

    private FormLayout getFormLayout(ResourceWrapper result) {
        FormLayout form = new FormLayout(mActivity, (Form) result.getResource(), mOnNavigateToListener, mOnSubmitFormListener);
        return form;
    }
}

