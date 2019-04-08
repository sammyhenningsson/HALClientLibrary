package se.sammygadd.library.halclient;

public interface Storage {
    boolean hasResource(String uri);
    ResourceWrapper get(String uri);
    void put(String uri, ResourceWrapper resource);
}
