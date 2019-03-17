package lando.bro.img.dedupe;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Validate;

public final class ImgLoader {
    
    private final DHashComputer dhasher;
    
    public ImgLoader(DHashComputer dhasher) {
        this.dhasher = Objects.requireNonNull(dhasher);
    }

    public Img load(Path path) throws Exception {
        Validate.notNull(path);
        
        long longSize = Files.size(path);
        
        if( longSize > Integer.MAX_VALUE ) {
            throw new IllegalArgumentException(path + " too large");
        }
        
        int size = (int)longSize;
        long mtime = Files.getLastModifiedTime(path).toMillis();
        byte[] bytes = Files.readAllBytes(path);
        String digest = DigestUtils.md5Hex(bytes);
        
        ImgInfo imgInfo = dhasher.forImg(bytes); 
        
        return new Img(path, mtime, 
                imgInfo.width, imgInfo.height, 
                size, digest, imgInfo.dhash);
    }
    
    static long packBytesIntoLong(byte[] bytes, int start) {
        long l = 0;
        
        for(int i = 0; i < 8; i++) {
            l <<= 8;
            l |= bytes[start + i] & 255;
        }
        
        return l;
    }
}
