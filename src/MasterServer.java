import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import components.ChunkServerInfo;
import components.Commands;
import components.FileMeta;

import java.util.logging.Level;

import java.io.*;
import java.net.*;

public class MasterServer {
    
    private final Map<String, FileMeta> fileMetaMap = new ConcurrentHashMap<>(); 
    private final Map<Long, Set<String>> chunkLocations = new ConcurrentHashMap<>();
    private final Map<String, ChunkServerInfo> chunkServerInfoMap = new ConcurrentHashMap<>();
    private final AtomicLong nextChunkId = new AtomicLong(1); // Starting chunk ID from 1
    private final Commands commands;

    // Logger
    private static final Logger logger = Logger.getLogger(MasterServer.class.getName());

    // Specify a thread limit size
    private static final int THREAD_POOL_SIZE = 8;

    public MasterServer(Commands commands) {
        this.commands = commands;
    }

    // Start the master server
    private void start() {

        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(9000)) {
            System.out.println("Master server is running on port 9000...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            System.out.println("Error starting master server: " + e.getMessage());
        }
    }

    private void handleClient(Socket call) {
        try (call; BufferedReader in = new BufferedReader(new InputStreamReader(call.getInputStream())); PrintWriter out = new PrintWriter(call.getOutputStream(), true)) {

            // Will need to keep reading commands from the chunk server
            String command;
            while((command = in.readLine()) != null) {
                logger.log(Level.INFO, "Received command: " + command);
                // Might need to put this in a try-catch of some sort 
                // For now let's ignore it 
                String response = processCommand(command); 
                out.println(response);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException: " + e.getMessage(), e);
            System.out.println("Call went wrong: " + e.getMessage());
        }
    }


    private String processCommand(String command) {

        String[] parts = command.split(" ");
        String main_command = parts[0];

        switch(main_command) {
            case "register":

                String address = parts[1];
                long freeSpace = Long.parseLong(parts[2]);
                ChunkServerInfo info = commands.registerCommand(address, freeSpace);
                chunkServerInfoMap.put(address, info);
                return "Registered chunk server: " + address + " with free space: " + freeSpace;

            case "ls":
                commands.lsCommand();
                return "ls command received";
            case "mkdir":
                commands.mkdirCommand();
                return "mkdir command received";
            case "rm":
                commands.rmCommand();
                return "rm command received";
            case "get":
                commands.getCommand();
                return "get command received";
            case "put":
                commands.putCommand();
                return "put command received";
            default:
                return "Unknown command: " + main_command;
        }
    }

    public static void main(String[] args) {
        Commands commands = new Commands();
        MasterServer masterServer = new MasterServer(commands);
        masterServer.start();
    }
}