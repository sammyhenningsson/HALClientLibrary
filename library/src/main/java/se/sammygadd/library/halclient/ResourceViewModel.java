package se.sammygadd.library.halclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class ResourceViewModel extends ViewModel {

    private ResourceRepository repository() {
        return ResourceRepository.get();
    }

    public LiveData<ResourceWrapper> getResource(String uri) {
        return repository().getResource(uri);
    }

    public LiveData<ResourceWrapper> submitForm(Form form) {
        return repository().submitForm(form);
    }
}
