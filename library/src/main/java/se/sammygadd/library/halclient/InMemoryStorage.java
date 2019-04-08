package se.sammygadd.library.halclient;

import java.util.HashMap;

public class InMemoryStorage implements Storage {
    private HashMap<String, ResourceWrapper> mStorage;

    public InMemoryStorage() {
        mStorage = new HashMap<>();
    }
    @Override
    public boolean hasResource(String uri) {
        return mStorage.containsKey(uri);
    }

    @Override
    public ResourceWrapper get(String uri) {
        return mStorage.get(uri);
    }

    @Override
    public void put(String uri, ResourceWrapper resource) {
        mStorage.put(uri, resource);
    }
}
