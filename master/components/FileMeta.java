package components;

import java.util.ArrayList;
import java.util.List;

public class FileMeta {
    String path; 
    public List<Long> chunkIds = new ArrayList<>();
    long size;
}
