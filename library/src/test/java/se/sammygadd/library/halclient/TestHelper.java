package se.sammygadd.library.halclient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TestHelper {
    public static String getResource(Class clazz, String name) {
        InputStream is = clazz.getClassLoader().getResourceAsStream(name);
        if (is == null ) return null;
        return new BufferedReader(new InputStreamReader(is))
                .lines().collect(Collectors.joining("\n"));
    }
}
