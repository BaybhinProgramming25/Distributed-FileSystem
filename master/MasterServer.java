package master;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import java.io.*;
import java.net.*;

import master.components.ChunkServerInfo;
import master.components.FileMeta;

public class MasterServer {

    Map<String, FileMeta> fileMetaMap = new ConcurrentHashMap<>();
    Map<Long, Set<String>> chunkLocations = new ConcurrentHashMap<>();
    Map<String, ChunkServerInfo> chunkServerInfoMap = new ConcurrentHashMap<>();
    AtomicLong nextChunkId = new AtomicLong(1);


    void registerServer(String address, long freeSpace) {

        ChunkServerInfo info = new ChunkServerInfo();
        info.setAddress(address);
        info.setFreeSpace(freeSpace);
        info.setLastHeartBeat(System.currentTimeMillis());
        chunkServerInfoMap.put(address, info);

    }

    
    void handle(Socket call) {
        try (call; BufferedReader in = new BufferedReader(new InputStreamReader(call.getInputStream())); PrintWriter out = new PrintWriter(call.getOutputStream(), true)) {

            String line = in.readLine();
            if (line == null) {
                return; // Nothing from the end 
            }
            String[] parts = line.split(" ");

            if (parts[0].equals("REGISTER")) {
                registerServer(parts[1], Long.parseLong(parts[2]));
                out.println("OK");
                System.out.println("Registered chunk servers: " + chunkServerInfoMap.keySet());
            } else {
                out.println("ERROR: Unknown command");
            }
        } catch (IOException e) {
            System.out.println("Call went wrong: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        // Start the master server
        MasterServer masterServer = new MasterServer();

        // Wait for connections and then once we have a connection, we handle it in a new thread
        try (ServerSocket phone = new ServerSocket(9000)) {
            System.out.println("Master server is running on port 9000...");
            while (true) {
                Socket call = phone.accept();
                new Thread(() -> masterServer.handle(call)).start();
            }
        } catch (IOException e) {
            System.out.println("Error starting master server: " + e.getMessage());
        }
    }
}