package lando.bro.img.dedupe;

import java.io.PrintStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FSTestApp {

    public static void main(String [] args) throws Exception {
        PrintStream out = System.out;
        Path p = Paths.get("/Users/oroman/tmp/av/pv4/ecg/ecg-ali-1820-hd.mp4");
        FileStore fs = Files.getFileStore(p);
        
        out.println("name : " + fs.name());
        out.println("type : " + fs.type());
        out.println("blockSize        : " + fs.getBlockSize());
        out.println("totalSpace       : " + fs.getTotalSpace()/1024/1024/1024);
        out.println("unallocatedSpace : " + fs.getUnallocatedSpace()/1024/1024/1024);
        out.println("usableSpace      : " + fs.getUsableSpace()/1024/1024/1024);
        
        
        
        out.println(fs);
    }
}
