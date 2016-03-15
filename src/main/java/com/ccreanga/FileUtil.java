package com.ccreanga;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileUtil {

    public static InputStream classPathResource(String file) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(file);
        if (url == null)
            throw new RuntimeException("cannot find resource " + file + " in classpath");
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException("cannot open resource " + file, e);
        }

    }
}
