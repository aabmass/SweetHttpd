package cx.it.aabmass.httpd.defaultplugins;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.imageio.ImageIO;

import cx.it.aabmass.httpd.Log;
import cx.it.aabmass.httpd.MimeHandler;

public class ImageHandler implements MimeHandler {
    // public static final String fileNotFound = 
    //     "<html><body><title>404 File Not Found</title><h1>404 File Not Found</h1>" +
    //     "The requested file could not be found on this webserver!</body></html>";
    
    public ImageHandler() {
        
    }
    
    public void handleClientConnection(final Socket client, String fullCommand, File fileToServe) {
        if (!fileToServe.exists()) {
            Log.debug("Could not find image \"" + fileToServe.getName() + "\"");
            return;
        }

        try {
            String[] split = fileToServe.getName().split("\\.");
            String format = split[split.length - 1];
            BufferedImage bi = ImageIO.read(fileToServe);
            
            //java doesn't support ico, pretend its a png and hope it works!
            if (format.equals("ico")) format = "png";
            ImageIO.write(bi, format, client.getOutputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }   
    }

    public String getMimeTypeName() {
        return ImagePlugin.mimeType;
    }

    public String getFileExtension() {
        return ImagePlugin.fileExt;
    }
}
