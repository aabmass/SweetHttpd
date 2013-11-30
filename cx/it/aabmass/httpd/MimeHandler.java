package cx.it.aabmass.httpd;

import java.net.Socket;
import java.io.File;

/** Provides the default operation for a given mime type **/
public interface MimeHandler {
    /**
     * This is where the work is done! Note: the socket is only
     * supplied in case the handler wants to get information 
     * about the client. No connection work should be done with
     * the given Socket, just write to it.
     **/
    public void handleClientConnection(final Socket client, File fileToServe);
    
    /**
     * Returns the mime type name that this handler handles.
     **/
    public String getMimeTypeName();

    public String getFileExtension();
    
}
