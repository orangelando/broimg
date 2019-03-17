package lando.bro.img.dedupe;

import java.nio.file.Path;

public final class PathUtil {

    public static String getExt(Path p) {
        String n = p.getFileName().toString().toLowerCase();
        
        int lastDot = n.lastIndexOf('.');
        
        return lastDot == -1 || lastDot == 0 || lastDot == n.length() - 1 ? 
                "" : n.substring(lastDot + 1);
    }
    
    private PathUtil() {
        
    }
}
