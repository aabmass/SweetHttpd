package cx.it.aabmass.httpd.util;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import cx.it.aabmass.httpd.*;

public class IO {
    /* uninstantiable */
    protected IO() {

    }

    /* static io methods */
    public static String fileToString(File f) {
        String endResult = new String("");
        try {
            Scanner s = new Scanner(f);
            
            while (s.hasNextLine()) {
                endResult += s.nextLine() + "\n";
            }
            s.close();
        }
        catch (FileNotFoundException e) {
            Log.err("Cound not find file \"" + f.toPath() + "\" requested by" +
                    " the client in the server directory.");
        }
        return endResult;
    }

    public static void writeReaderToWriterChar(Reader in, Writer out) {
        BufferedReader bIn = null;
        BufferedWriter bOut = null;

        try {
            bIn = new BufferedReader(in);
            bOut = new BufferedWriter(out);

            String line = null;
            while ((line = bIn.readLine()) != null) {
                bOut.write(line);
                bOut.newLine();
            }
            bOut.flush();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (bOut != null)
                    bOut.close();
                if (bIn != null)
                    bIn.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public static void writeInputStreamToSocket(Socket client, InputStream in) {
        BufferedReader bIn = null;
        BufferedWriter bOut = null;

        try {
            bIn = new BufferedReader(new InputStreamReader(in));
            bOut = new BufferedWriter
                (new OutputStreamWriter(client.getOutputStream()));

            String line = null;
            while ((line = bIn.readLine()) != null) {
                bOut.write(line);
                bOut.newLine();
            }
            bOut.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bOut.close();
                bIn.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void writeText(Socket client, File file) {
        BufferedWriter out = null;
        Scanner fileScan = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            fileScan = new Scanner(file);
            
            while (fileScan.hasNextLine()) {
                out.write(fileScan.nextLine());
                out.newLine();
            }
            out.flush();
        }
        catch (FileNotFoundException e) {
            Log.err("Cound not find file \"" + file.toPath() + "\" requested by" +
                    " the client in the server directory.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                out.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeDataStream(Socket client, File file) {
        //needs to be implemented!
    }
}
