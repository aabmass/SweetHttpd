package cx.it.aabmass.httpd.util;

public class PluginUtils {
    private static final String plugPrefix = 
        "cx.it.aabmass.httpd.defaultplugins.";
    
    public static final String abstractPluginFQN = 
        "cx.it.aabmass.httpd.plugin.AbstractPlugin";

    public static final String[] defaultPluginFQNs = {
        plugPrefix + "PlainTextPlugin",
        plugPrefix + "ImagePlugin"
    };
}
