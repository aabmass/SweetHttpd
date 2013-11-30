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

    /** actual private data members **/
    private boolean existUnloadedPlugins;
    private List<Plugin> registeredPlugins;
    
    /* Maps dataType String from the client commands to
       the an appropriate integer identifier (index) */
    private List<String> mimeTypes;

    /* In conjungtion with mimeTypes data member, this maps each
       unique identifier given to a mime string to a specific
       Map of MimeHandlers that performs the correct action depending on
       the type and file extention requested by the client. In the encapsulated
       map, the String is the file extension. One might do:
       
       MimeHandler h = mimeIdentifierToHandler.get(someIndex)
                       .get(someFileExtension);
       
       Note:
       There may be multiple handlers for one mime type, but only
       one for each mime type and file extention together!
    */
    private Map<Integer, Map<String, MimeHandler>> mimeIdentifierToHandler;

    private List<Thread> runningThreads;
    
    private Registrar() {
        this.existUnloadedPlugins = false;
        this.registeredPlugins = new ArrayList<Plugin>();
        this.mimeTypes = new ArrayList<String>();
        this.mimeIdentifierToHandler = new HashMap<Integer, Map<String, MimeHandler>>();
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

    private int registerMimeTypeInst
        (String typeName, String fileExt, MimeHandler handler) {

        Log.debug("Attempting to register handler \"" + handler.toString() + "\".");

        int index = -1;
        boolean alreadyRegistered = false;
        if ((index = mimeTypes.indexOf(typeName)) != -1) alreadyRegistered = true;

        if (!alreadyRegistered) {
            mimeTypes.add(typeName);
            index = mimeTypes.indexOf(typeName);
            
            Map<String, MimeHandler> m = new HashMap<String, MimeHandler>();
            m.put(fileExt, handler);
            mimeIdentifierToHandler.put(index, m);
        }        
        else {
            Map<String, MimeHandler> m = mimeIdentifierToHandler.get(index);
            
            //make sure there isn't a handler yet
            //for this extension or throw an error
            if (m.containsKey(fileExt)) {
                MimeHandler preexisting = m.get(fileExt);
                Log.err(preexisting.toString());
                throw new MultipleHandlerError(handler, fileExt);
            }
            m.put(fileExt, handler);
        }
        return index;
    }

    private void handleClientConnectionInst
        (String mimeType, Socket client, File fileToServe) {

        //get file extension. Make sure we support filenames
        //like foo.tar -- future -- support bar.tar.gz
        
        Log.debug(fileToServe.toString());
        String[] splitByDot = fileToServe.getName().split("\\.");
        String fileExt = splitByDot[splitByDot.length - 1];
        
        int mIdent = mimeTypes.indexOf(mimeType);
        MimeHandler handler = 
            mimeIdentifierToHandler.get(mIdent).get(fileExt);
        
        if (handler == null)
            throw new NoMimeHandlerException(mimeType, client);
        else
            handler.handleClientConnection(client, fileToServe);
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

    /* returns the mapping int that identifies the mimetype */
    public static int registerMimeType
        (String typeName, String fileExt, MimeHandler handler) {
        return getInstance().registerMimeTypeInst(typeName, fileExt, handler);
    }
    
    /* this is called from a ClientConnThread exclusively */
    public static void handleClientConnection
        (String mimeType, Socket client, File fileToServe) {
        Log.debug("Requesting MimeHandler for type \"" + mimeType
                  + "\".");
        getInstance().handleClientConnectionInst(mimeType, client, fileToServe);
    }
}
