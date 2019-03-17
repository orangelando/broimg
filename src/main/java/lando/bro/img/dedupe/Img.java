package lando.bro.img.dedupe;

import java.nio.file.Path;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

public final class Img {

    private final Path path;
    private final long mtime;
    private final int size;
    private final int width;
    private final int height;
    private final String digest;
    private final long dhash;
    
    public Img(Path path, long mtime, int width, int height, int size, String digest, long dhash) {
        Validate.isTrue(size >= 0);
        
        this.path = Objects.requireNonNull(path);
        this.mtime = mtime;
        this.width = width;
        this.height = height;
        this.size = size;
        this.digest = digest;
        this.dhash = dhash;
    }

    public Path getPath() {
        return path;
    }
    
    public long getMTime() {
        return mtime;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

    public int getSize() {
        return size;
    }

    public String getDigest() {
        return digest;
    }
    
    public long getDhash() {
        return dhash;
    }
}
