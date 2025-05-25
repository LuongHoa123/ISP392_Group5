package com.ISP392.demo.utils;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class ImageUtils {
    private static final Path IMAGE_DIR = Paths.get(ConstantUtils.IMAGE_PATH);

    public static Optional<String> upload(MultipartFile file) {
        Optional<String> imageName = Optional.empty();
        try {
            if (!file.isEmpty() && file.getContentType() != null && file.getContentType().startsWith("image")) {
                if (!Files.exists(IMAGE_DIR)) {
                    Files.createDirectories(IMAGE_DIR);
                }

                String timestamp = new SimpleDateFormat("yyyyMMddHH").format(new Date());
                String targetFileName = "img-" + timestamp + ".jpg";

                Path targetLocation = IMAGE_DIR.resolve(targetFileName);

                try (InputStream fileContent = file.getInputStream()) {
                    Files.copy(fileContent, targetLocation, StandardCopyOption.REPLACE_EXISTING);
                }

                imageName = Optional.of(targetFileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageName;
    }

    public static void delete(@NotNull String imageName) {
        Path imagePath = IMAGE_DIR.resolve(imageName).normalize();
        try {
            boolean result = Files.deleteIfExists(imagePath);
            if (result) {
                System.out.println("File is deleted: " + imageName);
            } else {
                System.out.println("Sorry, unable to delete the file: " + imageName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
