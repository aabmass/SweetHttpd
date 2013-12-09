package cx.it.aabmass.httpd.defaultplugins;

import cx.it.aabmass.httpd.plugin.*;
import cx.it.aabmass.httpd.Registrar;

public class PlainTextPlugin extends AbstractPlugin {
    public static final String pluginName = "PlainTextPlugin";
    public static final String mimeType = "text";
    public static final String fileExt = "html"; //could be many
    
    private PlainTextHandler handler;

    public PlainTextPlugin() {
        super();
        this.handler = new PlainTextHandler();
    }

    public void onLoad() {
        //register the handler
        Registrar.registerMimeType(PlainTextPlugin.mimeType, PlainTextPlugin.fileExt,
                                   true, handler);
    }

    public String getPluginName() {
        return PlainTextPlugin.pluginName;
    }

    public String toString() {
        return getPluginName();
    }
}
