package lando.bro.img.dedupe;

import java.util.Objects;

public final class ImgSortEntry {

    public final Img img;
    public final long dhash;
    
    public ImgSortEntry(Img img, long dhash) {
        this.img = Objects.requireNonNull(img);
        this.dhash = dhash;
    }
}
