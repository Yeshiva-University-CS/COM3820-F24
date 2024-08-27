package edu.yu.parallel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ImageLoader {

    public static ImageIcon loadImageFromResources(String resourceFilePath) {
        URL imageURL = ImageLoader.class.getResource(resourceFilePath);
        if (imageURL == null) {
            throw new IllegalArgumentException("Image not found: " + resourceFilePath);
        }

        try {
            return new ImageIcon(ImageIO.read(imageURL));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ImageIcon> loadImagesFromResources(String resourcePath) {
        List<ImageIcon> imageIcons = new ArrayList<>();
        URL resourceURL = ImageLoader.class.getResource(resourcePath);

        if (resourceURL != null) {
            try {
                if (resourceURL.getProtocol().equals("file")) {
                    loadImagesFromFileSystem(resourceURL, imageIcons);
                } else if (resourceURL.getProtocol().equals("jar")) {
                    loadImagesFromJar(resourceURL, resourcePath, imageIcons);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return imageIcons;
    }

    private static void loadImagesFromFileSystem(URL resourceURL, List<ImageIcon> imageIcons)
            throws URISyntaxException, IOException {
        Path resourceDirectory = Paths.get(resourceURL.toURI());
        Files.list(resourceDirectory)
                .filter(path -> path.toString().toLowerCase().endsWith(".png"))
                .forEach(path -> {
                    try {
                        imageIcons.add(new ImageIcon(ImageIO.read(path.toFile())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    private static void loadImagesFromJar(URL resourceURL, String resourcePath, List<ImageIcon> imageIcons)
            throws IOException, URISyntaxException {
        URI uri = resourceURL.toURI();
        try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
            Path resourceDirectory = fs.getPath(resourcePath);
            Files.walk(resourceDirectory)
                    .filter(path -> path.toString().toLowerCase().endsWith(".png"))
                    .forEach(path -> {
                        try (InputStream is = Files.newInputStream(path)) {
                            imageIcons.add(new ImageIcon(ImageIO.read(is)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

}
