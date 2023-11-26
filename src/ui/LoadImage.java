package ui;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.io.File;


public class LoadImage {

  public static String base64Encode() throws IOException {
    String filePath = "src/icon.png";
    File file = new File(filePath);
    return encodeFileToBase64Binary(file);
  }

  private static String encodeFileToBase64Binary(File file) throws IOException {
    String encodedFile;
    FileInputStream fileInputStreamReader = new FileInputStream(file);
    byte[] bytes = new byte[(int) file.length()];
    fileInputStreamReader.read(bytes);
    encodedFile = Base64.getEncoder().encodeToString(bytes);
    fileInputStreamReader.close();
    return encodedFile;
  }

  public static void base64Decode(String base64Text) throws IOException {
    BufferedImage image;
    byte[] imageByte = Base64.getDecoder().decode(base64Text);
    ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
    image = ImageIO.read(bis);
    bis.close();

    File outputfile = new File("src/decrypted.png");
    ImageIO.write(image, "png", outputfile);
  }
}
