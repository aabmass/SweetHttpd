package cx.it.aabmass.httpd;

import java.net.Socket;
import java.io.File;

import cx.it.aabmass.httpd.plugin.*;
import cx.it.aabmass.httpd.exception.*;
import cx.it.aabmass.httpd.error.*;

import java.util.*;

/**
 * This class will have a singleton. It takes care of starting
 * threads, registering plugins, potentially events, etc.
 **/
public class Registrar {
    /** singleton **/
    public static Registrar instance;

    private List<Plugin> registeredPlugins;

    /**
     * Maps MIME types (as strings) to another map. The second
     * map maps file extensions (as strings) to MimeHandlers
     */
    private Map<String, Map<String, MimeHandler>> mimeTypeToHandler;
    private List<Thread> runningThreads;

    private Registrar() {
        this.registeredPlugins = new ArrayList<Plugin>();
        this.mimeTypeToHandler = new HashMap<String, Map<String, MimeHandler>>();
        initMimeTypes();
        this.runningThreads = new ArrayList<Thread>();
    }
    
    private void initMimeTypes() {
        //needs implementation. Will load from a config file
        //and also some defaults from inside the getResourceAsStream
    }
    
    /** Methods called on the instance from their respective
        static methods to maintain the instance **/
    private void startThreadInst(Thread t) throws IllegalThreadStateException {
        if (runningThreads.contains(t)) {
            Log.err("Thread \"" + t.toString() + "\" already started!");
            return;
        }
        
        t.start();
        runningThreads.add(t);
    }

    /* for now, plugin loading is also done from here;
       it should probably be threaded eventually. */
    private void registerPluginInst(Plugin p) {
        if (registeredPlugins.contains(p)) {
            Log.err("Plugin \"" + p.toString() + "\" was already registered!");
            return;
        }
        
        Log.info("Loaded plugin \"" + p.getPluginName() + "\".");
        registeredPlugins.add(p);
        p.onLoad();
    }

    /** Is default arg is described at registerMimeType(...) **/
    private void registerMimeTypeInst
        (String typeName, String fileExt, boolean isDefault, MimeHandler handler) {

        Log.debug("Attempting to register handler \"" + handler.toString() + "\".");

        Map<String, MimeHandler> fileExtToHandler = mimeTypeToHandler.get(typeName);
        if (fileExtToHandler == null) {
            fileExtToHandler = new HashMap<String, MimeHandler>();
            mimeTypeToHandler.put(typeName, fileExtToHandler);
        }
        
        if (isDefault)
            fileExtToHandler.put("default", handler);
        fileExtToHandler.put(fileExt, handler);
    }

    private void handleClientConnectionInst
        (String mimeType, String fullCommand, Socket client, File fileToServe) {

        //get file extension. Make sure we support filenames
        //like foo.tar -- future -- support bar.tar.gz
        
        Log.debug(fileToServe.toString());
        String[] splitByDot = fileToServe.getName().split("\\.");
        String fileExt = splitByDot[splitByDot.length - 1];
        String superficialType = mimeType.split("/")[0];

        if (fileToServe.getName().split("\\.")[0].equals("favicon"))
            mimeType = "image";
        
        //a primary and secondary attempt
        Map<String, MimeHandler> stringToHandlerPri = mimeTypeToHandler.get(mimeType);
        Map<String, MimeHandler> stringToHandlerSec = mimeTypeToHandler.get(superficialType);
        MimeHandler[] handlersInPriority = new MimeHandler[4];
        
        handlersInPriority[0] = (stringToHandlerPri == null ? null : stringToHandlerPri.get(fileExt));
        handlersInPriority[1] = (stringToHandlerSec == null ? null : stringToHandlerSec.get(fileExt));
        handlersInPriority[2] = (stringToHandlerPri == null ? null : stringToHandlerPri.get("default"));
        handlersInPriority[3] = (stringToHandlerSec == null ? null : stringToHandlerSec.get("default"));
        MimeHandler handler = null;
        
        //exhaust all possible handlers before an exception
        for (MimeHandler m : handlersInPriority) {
            if (m == null) continue;
            handler = m;
            break;
        }
        
        if (handler == null) //give up and throw exception
            throw new NoMimeHandlerException(mimeType, client);
        
        Log.debug(handler.toString());
        handler.handleClientConnection(client, fullCommand, fileToServe);
    }

    /** 
     * Static methods that should be called to control instance.
     * These provide a wrapper for the instance.
     **/
    public static Registrar getInstance() {
        if (instance == null)
            instance = new Registrar();
        return instance;
    }

    public static void startThread(Thread t) throws IllegalThreadStateException {
        getInstance().startThreadInst(t);
    }

    public static void registerPlugin(Plugin p) {
        getInstance().registerPluginInst(p);
    }

    /* returns the mapping int that identifies the mimetype. Is default 
       is used if this handler should be used for this mime type if no
       handler can be found for a specfic file extension. You still need
       to register it with a file extension, however.

       ie. if a client requested an image and there was no handler for the
       filetype, we use the default image handler.
    */
    public static void registerMimeType
        (String typeName, String fileExt, boolean isDefault, MimeHandler handler) {
        getInstance().registerMimeTypeInst(typeName, fileExt, isDefault, handler);
    }
    
    /* this is called from a ClientConnThread exclusively */
    public static void handleClientConnection
        (String mimeType, String fullCommand, Socket client, File fileToServe) {
        Log.debug("Requesting MimeHandler for type \"" + mimeType
                  + "\".");
        getInstance().handleClientConnectionInst(mimeType, fullCommand, client, fileToServe);
    }
}
