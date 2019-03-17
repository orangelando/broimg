package lando.bro.img.dedupe;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Img {

    private final Path path;
    private final long mtime;
    private final int size;
    private final int width;
    private final int height;
    private final String digest;
    private final long dhash;
    
    /**
     * I couldn't get any of the Jackson custom serialization/deserialization
     * to work. I kept getting a "No default constructor" despite my classes
     * very clearly having a default constructor. Fuck all that nonsense.
     */
    @JsonCreator
    public Img(
            @JsonProperty("path"  ) String path,
            @JsonProperty("mtime" ) String mtime,
            @JsonProperty("width" ) int width, 
            @JsonProperty("height") int height, 
            @JsonProperty("size"  ) int size, 
            @JsonProperty("digest") String digest,
            @JsonProperty("dhash" ) String dhash) {
        
        this(Paths.get(path), 
                Long.parseLong(mtime, 10),
                width,
                height,
                size,
                digest,
                Long.parseLong(dhash, 10));
    }
    
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

    @JsonIgnore
    public Path getPath() {
        return path;
    }
    
    @JsonProperty("path")
    public String getPathStr() {
        return path.toString();
    }
    
    @JsonIgnore
    public long getMTime() {
        return mtime;
    }
    
    @JsonProperty("mtime")
    public String getMTimeStr() {
        return Long.toString(mtime, 10);
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
    
    @JsonIgnore
    public long getDhash() {
        return dhash;
    }
    
    @JsonProperty("dhash")
    public String getDhashStr() {
        return Long.toString(dhash, 10);
    }
}
