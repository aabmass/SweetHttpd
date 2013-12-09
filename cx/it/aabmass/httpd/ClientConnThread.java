package cx.it.aabmass.httpd;

import java.net.*;
import java.io.*;
import java.util.Scanner;

import cx.it.aabmass.httpd.util.*;

/**
 * This class represents a client connection and runs
 * as its own thread. 
 **/
class ClientConnThread extends Thread {
    private File rootDir;
    private ServerSocket srv;
    private Socket client;

    public ClientConnThread(ServerSocket srv, File rootDir) {
        this.srv = srv;
        this.rootDir = rootDir;
    }
    
    @Override
    public void run() {
        try {
            this.client = srv.accept();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        handleConnection();
        close();
    }

    private void handleConnection() {
        BufferedReader in = null;
        String clientCommand = "";

        /* first, lets get what the client's command */
        try {
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String line = null;
            
            while ((line = in.readLine()) != null) {
                if (line.equalsIgnoreCase(""))
                    break;
                clientCommand += line + "\n";
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
                
        Log.info("Beginning listening on port " + srv.getLocalPort() +
                 " to " + client.getInetAddress());
        Log.debug(clientCommand);
        
        /* now parse it up and handle it */
        String[] splitCommand = clientCommand.split(" ");
        String httpDirective = splitCommand[0].trim();
        String relativeFile = splitCommand[1].trim();          
        
        if (httpDirective.equalsIgnoreCase("GET")) {
            File file = null;
            if (relativeFile.equalsIgnoreCase("/"))
                file = new File(rootDir, "index.html");
            else
                file = new File(rootDir, relativeFile);

            Registrar.handleClientConnection(parseFileType(clientCommand), client, file);
        }
        try {
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Figures out what the client wants. Eventually
     * this should parse mime types specified.
     **/
    private String parseFileType(String clientCommand) {
        return clientCommand.split("Accept:")[1].split(",")[0]
            .split("\n")[0].trim();
    }

    public void close() {
        try {
            client.close();
            srv.close();
            Log.info("Starting new server thread now...");
            new ClientConnThread(new ServerSocket(srv.getLocalPort()), rootDir)
                                 .start(); //start next thread to accept
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
