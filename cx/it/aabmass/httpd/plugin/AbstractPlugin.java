package cx.it.aabmass.httpd.plugin;

import cx.it.aabmass.httpd.Registrar;

/** 
 * For now until better fix, register the plugin
 * subclass by making making a singleton :(
**/

public abstract class AbstractPlugin implements Plugin {
    /** we will use this constructor when loading the plugin **/
    public AbstractPlugin() {
        
    }

    public abstract void onLoad();
    public void onClose() {
        return;
    }

    public abstract String getPluginName();
}
