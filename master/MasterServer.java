import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import components.FileMeta;
import nodes.components.ChunkServerInfo;

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

    
}

public static void main(String[] args) {

    // Initialize the master server 
    MasterServer master = new MasterServer();

   // L
}
