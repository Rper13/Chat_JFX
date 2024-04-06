package GlobalScope;

import Controllers.HomePageController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Functions {

    public static byte[] fileToByteArray(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }

            return bos.toByteArray();
        }catch(Exception e){
            System.err.println("could not convert file to byte array");
        }
        return null;
    }

    public static byte[] processPicture(byte[] picture){
        if (picture == null){
            try {
                Path path = Paths.get(HomePageController.class.getResource("/Images/profile_default.jpg").toURI());
                picture = Files.readAllBytes(path);
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        return picture;
    }

}
