package it.vitalegi.cosucce.util;

import java.io.File;
import java.net.URISyntaxException;

public class FileUtil {
    public static File getResourceAsFile(String resource) {
        try {
            return new File(FileUtil.class.getClassLoader().getResource(resource).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
