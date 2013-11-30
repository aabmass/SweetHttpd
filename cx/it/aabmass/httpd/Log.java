package cx.it.aabmass.httpd;

import java.util.List;
import java.util.ArrayList;

/**
 * The logging class used for writing all sorts
 * of log message in this project. It has a singleton!
**/
public class Log {
    private static Log instance = null;
    private boolean debugging;
    
    private List<String> messages;

    /* Stop Instantiation */
    protected Log() {
        messages = new ArrayList<String>();
        debugging = false;
    }

    public static Log getInstance() {
        if (instance == null)
            instance = new Log();
        return instance;
    }

    protected void errInstance(String err) {
        String errFull = "-x-> ERROR: " + err;
        System.err.println(errFull);
        messages.add(errFull);
    }

    protected void infoInstance(String info) {
        String infoFull = "---> INFO: " + info;
        System.out.println(infoFull);
        messages.add(infoFull);        
    }

    protected void debugInstance(String msg) {
        String msgFull = "-@-> DEBUG: " + msg;
        System.out.println(msgFull);
        messages.add(msgFull);
    }
    
    public static void setDebugging(boolean b) {
        getInstance().debugging = b;
    }

    public static void err(String errorMessage) {
        getInstance().errInstance(errorMessage);
    }

    public static void info(String info) {
        getInstance().infoInstance(info);
    }

    public static void debug(String msg) {
        if (getInstance().debugging)
            getInstance().debugInstance(msg);
    }
}
