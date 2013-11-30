package cx.it.aabmass.httpd.exception;

import java.net.Socket;

public class NoMimeHandlerException extends RuntimeException {
    public NoMimeHandlerException() {
        super();
    }

    public NoMimeHandlerException(String mimeType, Socket client) {
        super("The mime type \"" + mimeType + "\" has no handler connection " + 
              "with " + client.getInetAddress().getHostAddress() + ".");
    }

    public NoMimeHandlerException(String message) {
        super(message);
    }
}
