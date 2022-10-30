package th.ac.chula.fims.utils;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;

public class FileServiceUtils {
    public static Resource downloadFileAsResource(String path) throws MalformedURLException {
        Path targetLocation = Path.of(path).normalize();
        return new UrlResource(targetLocation.toUri());
    }
}
