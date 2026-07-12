import java.io.*;
import java.net.*;
import java.nio.file.*;


public class Client {


    public static void main(String[] args) {

        if (args.length < 2) {

            System.out.println("Usage");
            System.out.println(" java Client store <filepath> ");
            System.out.println(" java Client get <filename> ");
            return;
        }

        String host = "localhost";
        int port = 8001;
        String action = args[0]; 

        // Create a socket
        Socket socket = new Socket(host, port);
        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        // If the action is "store"
        if(action.equals("store")) {

            Path file = Paths.get(args[1]);
            String fileName = filePath.getFileName().toString();
            long size = Files.size(file);

            // Write it to the other machine 
            out.writeUTF("STORE");
            out.writeUTF(fileName);
            out.writeUTF(size);

            try(InputStream fileIn = Files.newInputStream(file)) {
                // Add copyBytes() logic here later on but we are basically writing from the fileIn (file from our machine) to the outer machine (outside our network)
            }

            // Flush all the bytes from the bucket down to the pipe 
            out.flush(); 

            // Capture reply
            String reply = in.readUTF();
            System.out.println("Server said: " + reply);
        
        } else if (action.equals("get")) {

            String fileName = args[1];
            out.writeUTF("GET");
            out.writeUTF(fileName);
            out.flush();

            String status = in.readUTF();
            if (status.equals("FOUND")) {
                long size = in.readLong();
                Path outFile = Paths.get("downloaded_" + fileName);
                try (OutputStream fileOut = Files.newOutputStream(outFile)) {
                    // We are essentially taking in what we read from the input (in.readLong()) and putting it into fileOut which is now the file on our machine 
                }
                System.out.println("Got the file, saved as: " + outFile.toAbsolutePath());
            } else {
                System.out.println("Server said: File Not Found");
            }

        } else {
            System.out.println("Unknown action: " + action);
        }

        socket.close();
    }

    static void copyBytes(InputStream in, OutputStream out, long count) {

        byte[] buffer = new byte[4096];
        long remaining = count;
        while (remaining > 0) {
            int bytesToRead = (int)Math.min(buffer.length, remaining);
            int read = in.read(buffer, 0, bytesToRead);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            reamining -= read;
        }
    }
}