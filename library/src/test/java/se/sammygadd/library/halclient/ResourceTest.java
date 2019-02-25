package se.sammygadd.library.halclient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ResourceTest {

    private String getTestResource(String name) {
        // FIXME: How to put resources on the classpath and skip this ugly workaround????
        String path = getClass().getResource(".").getPath();
        name = path + name;
        System.out.println("File: " + name);
        InputStream is = getClass().getResourceAsStream(name);
        if (is == null ) {
            System.out.println("InputStream is null");
            return "";
        }
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    Resource post;

    @Before
    public void setup() throws JSONException {
        String str = getTestResource("post.json");
        System.out.println("Str: " + str);
        JSONObject json = new JSONObject(str);
        post = new Resource(json);
    }

    @Test
    public void resource_contructor() throws JSONException {
        assertEquals("hello", post.getAttribute("title"));
        assertEquals("Lorem ipsum", post.getAttribute("message"));
    }

    @Test
    public void getAttributes() {
    }

    @Test
    public void getLinks() {
    }

    @Test
    public void getAttribute() {
    }

    @Test
    public void getAttribute1() {
    }

    @Test
    public void getLinkHref() {
    }

    @Test
    public void resource_toString() {
    }

    @Test
    public void actions() {
    }
}