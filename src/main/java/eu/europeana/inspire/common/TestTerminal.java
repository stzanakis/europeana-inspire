package eu.europeana.inspire.common;

import java.io.*;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-22
 */
public class TestTerminal {

    public static void executeCommands() throws IOException, InterruptedException {

        File tempScript = createTempScript();

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } finally {
            tempScript.delete();
        }
    }

    public static File createTempScript() throws IOException {
        File tempScript = File.createTempFile("script", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
                tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println("cd bin");
        printWriter.println("ls");

        printWriter.close();

        return tempScript;
    }
}
