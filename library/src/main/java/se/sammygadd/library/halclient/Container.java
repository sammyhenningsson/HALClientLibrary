package se.sammygadd.library.halclient;

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

public abstract class Container {

    protected AppCompatActivity mActivity;
    protected ResourceViewModel mViewModel;
    protected MutableLiveData<View> mView;
    protected List<String> mBackStack;

    public Container(AppCompatActivity activity) {
        mActivity = activity;
        mViewModel = ViewModelProviders.of(activity).get(ResourceViewModel.class);
        mView = new MutableLiveData<>();
        mBackStack = new ArrayList<>();
        ApiService.create(activity.getApplicationContext());
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public LiveData<View> showResource(String uri) {
        mBackStack.add(uri);
        mViewModel.getResource(uri).observe(getActivity(), result -> {
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

    public View getView(ResourceWrapper result) {
        return getView(result, null);
    }

    public View getView(ResourceWrapper result, Form form) {
        View layout;

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
        ScrollView view = new ScrollView(getActivity().getApplicationContext());
        view.addView(layout);
        return view;
    }

    abstract View getHALLayout(Resource resource);
    abstract View getErrorLayout(Error error);
    abstract View getFormLayout(Form form);
    abstract View getFormLayout(Form form, ValidationError validationError);
}
