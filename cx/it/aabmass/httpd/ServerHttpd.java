package cx.it.aabmass.httpd;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.jar.*;

import cx.it.aabmass.httpd.util.*;
import cx.it.aabmass.httpd.plugin.*;

public class ServerHttpd {
    private File rootDir; //becomes the message
    private File pluginDir;
    private int port;
    private Registrar registrar;
    private List<Class> pluginClasses;

    public ServerHttpd(int port, File rootDir, File pluginDir) throws IOException {
        this.port = port;
        this.rootDir = rootDir;
        this.pluginDir = pluginDir;
        this.registrar = Registrar.getInstance(); //instantiate the Registrar singleton
        
        // not sure if this is the best place to load plugins
        this.pluginClasses = loadPlugins(pluginDir);
        addDefaultPlugins();
        registerPlugins();

        Thread cct = new ClientConnThread(new ServerSocket(this.port), this.rootDir);
        Registrar.startThread(cct);
    }

    private void addDefaultPlugins() {
        for (String className : PluginUtils.defaultPluginFQNs) {
            try {
                pluginClasses.add(Class.forName(className));
            }
            catch (ClassNotFoundException e) {
                Log.err("Could not find default plugin \"" + className + "\".");
                e.printStackTrace();
            }
        }
    }

    private void registerPlugins() {
        for (Class c : this.pluginClasses) {
            try {
                Registrar.registerPlugin((Plugin) c.newInstance());
            }
            catch (ReflectiveOperationException e) {
                Log.err("Could not load plugin \"" + c.getSimpleName() + "\".");
                e.printStackTrace();
            }
        }
    }

    private List<Class> loadPlugins(File pluginDir) {
        List<Class> classes = new ArrayList<Class>();
        
        File[] jarFilesAsFile = pluginDir.listFiles();
        JarFile[] jarFiles = new JarFile[jarFilesAsFile.length];
        URL[] urls = new URL[jarFilesAsFile.length];
        
        try {
            for (int i = 0; i < jarFilesAsFile.length; ++i) {
                jarFiles[i] = new JarFile(jarFilesAsFile[i]);
                urls[i] = new URL("jar:file:" + jarFilesAsFile[i].getCanonicalPath() + "!/");
            }
        
            for (JarFile jarFile : jarFiles) {
                Enumeration e = jarFile.entries();
                URLClassLoader cl = URLClassLoader.newInstance(urls);
            
                while (e.hasMoreElements()) {
                    JarEntry je = (JarEntry) e.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(".class"))
                        continue;
                
                    String className = je.getName().substring(0,je.getName().length()-6);
                    className = className.replace('/', '.');
                
                    Class c = cl.loadClass(className);

                    //check if they extend AbstractPlugin
                    Class superclass = c.getSuperclass();

                    if (superclass.getName().equals(PluginUtils.abstractPluginFQN))
                        classes.add(c);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }
}
