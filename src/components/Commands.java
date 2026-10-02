package components;

public class Commands {

    public ChunkServerInfo registerCommand(String address, long freeSpace) {

        // Register the chunk server and return the ChunkServerInfo object
        ChunkServerInfo info = new ChunkServerInfo();
        info.setAddress(address);
        info.setFreeSpace(freeSpace);
        info.setLastHeartBeat(System.currentTimeMillis());
        return info;

    }

    public void lsCommand() {
        System.out.println("To be Implemented");
    }

    public void mkdirCommand() {
        System.out.println("To be Implemented");
    }

    public void rmCommand() {
        System.out.println("To be Implemented");
    }

    public void getCommand() {
        System.out.println("To be Implemented");
    }

    public void putCommand() {
        System.out.println("To be Implemented");
    }
}
