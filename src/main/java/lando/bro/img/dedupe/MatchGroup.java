package lando.bro.img.dedupe;

import java.util.List;
import java.util.Objects;

public final class MatchGroup {

    private final Img img;
    private final List<Img> exactMatches;
    private final List<Img> similarMatches;
    
    public MatchGroup(Img img, List<Img> exactMatches, List<Img> similarMatches) {
        this.img = Objects.requireNonNull(img);
        this.exactMatches = Objects.requireNonNull(exactMatches);
        this.similarMatches = Objects.requireNonNull(similarMatches);
    }

    public Img getImg() {
        return img;
    }

    public List<Img> getExactMatches() {
        return exactMatches;
    }

    public List<Img> getSimilarMatches() {
        return similarMatches;
    }
    
    public boolean hasAnyMatches() {
        return exactMatches.size() > 0 || similarMatches.size() > 0;
    }
}
