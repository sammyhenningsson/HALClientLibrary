package se.sammygadd.library.halclient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ResourceTest {

    private String getTestResource(String name) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(name);
        if (is == null ) return null;
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }

    Resource post;

    @Before
    public void setup() {
        String str = getTestResource("post.json");
        assertNotNull("Failed to load test resource \"post.json\"", str);
        try {
            post = new Resource(new JSONObject(str));
        } catch (JSONException e) {
            System.out.println("Caught json exception: " + e.getMessage());
        }
        assertNotNull("Could not parse test resource \"post.json\"", post);
    }

    @Test
    public void resource_contructor() throws JSONException {
        assertNotNull(post.toString());
    }

    @Test
    public void getAttributes() {
        HashMap<String, String> attributes = post.getAttributes();
        assertEquals(2, attributes.size());
    }

    @Test
    public void getLinks() {
        HashMap<String, Link> links = post.getLinks();
        assertEquals(4, links.size());
    }

    @Test
    public void getAttribute() {
        assertEquals("Hello", post.getAttribute("title"));
        assertEquals("Lorem ipsum", post.getAttribute("message"));
    }

    @Test
    public void link_hrefs() {
        assertEquals("http://localhost:3000/posts/1", post.getLink("self").href());
        assertEquals("/posts/1/edit", post.getLink("doc:edit-form").href());
        assertEquals("/posts/1", post.getLink("doc:delete").href());
        assertEquals("/posts/1/comments", post.getLink("comments").href());
    }

    @Test
    public void getCurie() {
        Curie curie = post.getCurie("doc");
        assertNotNull(curie);
        assertEquals("doc", curie.name());
        assertEquals("/doc/post/rels/{rel}", curie.href());
        assertEquals(true, curie.isTemplated());
        assertEquals("/doc/post/rels/comments", curie.resolve("comments"));
    }

    @Test
    public void resource_toString() {
        String json = post.toString();
        assertNotNull(json);
        assertTrue(json.length() > 20);
        assertTrue(json.contains("_links"));
    }

    @Test
    public void actions() {
        Set<String> actions = post.actions();
        assertEquals(4, actions.size());

        Collection<String> expected = new ArrayList<String>();
        expected.add("self");
        expected.add("doc:edit-form");
        expected.add("doc:delete");
        expected.add("comments");

        assertTrue(actions.containsAll(expected));
    }
}