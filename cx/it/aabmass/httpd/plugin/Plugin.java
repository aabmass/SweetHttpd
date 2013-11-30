package cx.it.aabmass.httpd.plugin;

/** plugins should implement this interface **/
public interface Plugin {
    public void onLoad();
    public void onClose();
    public String getPluginName();
}
