package cx.it.aabmass.httpd.defaultplugins;

import cx.it.aabmass.httpd.plugin.*;
import cx.it.aabmass.httpd.Registrar;

public class ImagePlugin extends AbstractPlugin {
    public static final String pluginName = "ImagePlugin";
    public static final String mimeType = "image";
    public static final String fileExt = "jpg"; //could be many
    
    private ImageHandler handler;

    public ImagePlugin() {
        super();
        this.handler = new ImageHandler();
    }

    public void onLoad() {
        //register the handler
        Registrar.registerMimeType(ImagePlugin.mimeType, ImagePlugin.fileExt,
                                   true, handler);
    }

    public String getPluginName() {
        return ImagePlugin.pluginName;
    }

    public String toString() {
        return getPluginName();
    }
}
