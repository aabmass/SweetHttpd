package cx.it.aabmass.httpd;

import java.io.File;
import java.io.IOException;

class Main {
    public static void main(String args[]) throws IOException {
        int port = 0;
        File rootDir = null;
        try {
            port = Integer.parseInt(args[0]);
            rootDir = new File(args[1]);
            
            if (args.length == 3 &&
                args[2].equals("-debug")) // then debug!
                Log.setDebugging(true);
        }
        catch (Exception e) {
            System.out.println(Main.usage());
            System.exit(1);
        }
        
        File pluginDir = new File("plugins");
        pluginDir.mkdir();
        Log.info("Using plugin directory " + pluginDir.getCanonicalPath().toString());

        new ServerHttpd(port, rootDir, pluginDir);
    }

    public static String usage() {
        return "java -jar httpd.jar <port> <root directory> [-debug]";
    }
}
