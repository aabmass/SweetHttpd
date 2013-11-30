package cx.it.aabmass.httpd.error;

import cx.it.aabmass.httpd.MimeHandler;

/** may be renamed in the future... **/
public class MultipleHandlerError extends Error {
    public MultipleHandlerError() {
        super();
    }

    public MultipleHandlerError(MimeHandler h, String fileExt) {
        super("The handler \"" + h.toString() + "\" cannot be added" +
              " because there is already a handler for file " +
              "extension \"" + fileExt + "\".");
    }
    
    public MultipleHandlerError(String message) {
        super(message);
    }
}
