package cx.it.aabmass.httpd.defaultplugins;

import cx.it.aabmass.httpd.util.IO;
import cx.it.aabmass.httpd.*;

import java.net.Socket;
import java.io.*;

public class PlainTextHandler implements MimeHandler {
    public static final String fileNotFound = 
        "<html><body><title>404 File Not Found</title><h1>404 File Not Found</h1>" +
        "The requested file could not be found on this webserver!</body></html>";
    
    public PlainTextHandler() {

    }
    
    public void handleClientConnection(final Socket client, File fileToServe) {
        Reader in = null;
        Writer out = null;
        try {
            out = new OutputStreamWriter(client.getOutputStream());
            in = new FileReader(fileToServe);
            IO.writeReaderToWriterChar(in, out);
        }
        catch (FileNotFoundException e) {
            //do a 404. Note: make a better 404 page in the future!
            in = new StringReader(PlainTextHandler.fileNotFound);
            IO.writeReaderToWriterChar(in, out);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getMimeTypeName() {
        return PlainTextPlugin.mimeType;
    }

    public String getFileExtension() {
        return PlainTextPlugin.fileExt;
    }
}
