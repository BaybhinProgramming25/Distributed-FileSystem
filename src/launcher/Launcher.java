package launcher;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Launcher {

    static final Map<String, Process> running = new LinkedHashMap<>();

    static final int FIRST_PORT = 9001;
    static final int NUM_CHUNK_SERVERS = 3; // Set 3 for now and we can scale it up later

    static void start(String name, String... javaArgs) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(System.getProperty("java.home") + "/bin/java");
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.addAll(List.of(javaArgs));

        new File("logs").mkdirs(); // Create logs directory if it doesn't exist
        File log = new File("logs/" + name + ".log");

        Process p = new ProcessBuilder(command)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.appendTo(log))
                .start();
        
        running.put(name, p);
        System.out.println("Started " + name + " with PID: " + p.pid() + ", logging to: " + log.getAbsolutePath());
    }

    static void startBox(int port) throws IOException {
        start("chunk-" + port, "ChunkServer", String.valueOf(port));
    }

    public static void main(String[] args) throws Exception {

        File logsDir = new File("logs");
        File[] oldLogs = logsDir.listFiles();
        if (oldLogs != null) {
            for (File f : oldLogs) f.delete();
        }
            
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                running.values().forEach(Process::destroy)));

        start("master", "MasterServer");
        Thread.sleep(1000);   // let the Boss open its phone

        for (int port = FIRST_PORT; port < FIRST_PORT + NUM_CHUNK_SERVERS; port++) {
            startBox(port);
        }

        System.out.println("Type: list | kill <name> | start <port> | quit");
        Scanner keyboard = new Scanner(System.in);

        while (keyboard.hasNextLine()) {
            String[] parts = keyboard.nextLine().trim().split("\\s+");
            switch (parts[0]) {
                case "list" -> running.forEach((name, p) ->
                        System.out.println(name + (p.isAlive() ? "  running" : "  stopped")));
                case "kill" -> {
                    if (parts.length < 2) { System.out.println("kill what? e.g. kill chunk-9002"); break; }
                    Process p = running.get(parts[1]);
                    if (p != null) { p.destroy(); System.out.println("Killed " + parts[1]); }
                    else System.out.println("No such thing: " + parts[1]);
                }
                case "start" -> {
                    if (parts.length < 2) { System.out.println("start what? e.g. start 9002"); break; }
                    startBox(Integer.parseInt(parts[1]));
                }
                case "quit" -> System.exit(0);
                case "" -> { }   // just pressed Enter
                default -> System.out.println("Huh? Try: list, kill <name>, start <port>, quit");
            }
        }
    }
    
}
