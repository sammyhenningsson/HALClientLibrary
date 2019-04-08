package se.sammygadd.library.halclient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import se.sammygadd.library.halclient.resources.Form;

public class ResourceViewModel extends ViewModel {

    private ResourceRepository repository() {
        Storage storage = new InMemoryStorage();
        return ResourceRepository.get(storage);
    }

    public LiveData<ResourceWrapper> getResource(String uri) {
        return repository().getResource(uri);
    }

    public LiveData<ResourceWrapper> submitForm(Form form) {
        return repository().submitForm(form);
    }
}
