package nodes;

import java.io.*;
import java.net.*;


public class ChunkServer {

    // We then construct our Chunk Server and connect to the master server
    public static void main(String [] args) {

        int myPort = Integer.parseInt(args[0]);
        String myAddress = "localhost:" + myPort;
        long free = new File(".").getUsableSpace();

        try (Socket call = new Socket("localhost", 9000); BufferedReader in = new BufferedReader(new InputStreamReader(call.getInputStream())); PrintWriter out = new PrintWriter(call.getOutputStream(), true)) {

            // Send a REGISTER command to the master server
            out.println("REGISTER " + myAddress + " " + free);

            // Read the response from the master server
            String response = in.readLine();
            System.out.println("Response from master: " + response);

        } catch (IOException e) {
            System.out.println("Error connecting to master server: " + e.getMessage());
        }
    }
}
