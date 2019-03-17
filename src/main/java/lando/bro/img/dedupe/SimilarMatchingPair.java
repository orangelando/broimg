package lando.bro.img.dedupe;

import org.apache.commons.lang3.Validate;

public final class SimilarMatchingPair {

    private final Img img1;
    private final Img img2;
    
    public SimilarMatchingPair(Img img1, Img img2) {
        Validate.notNull(img1);
        Validate.notNull(img2);
        
        if( img2.getDigest().compareTo(img1.getDigest()) < 0 ) {
            Img tmp = img1;
            img1 = img2;
            img2 = tmp;
        }
        
        this.img1 = img1;
        this.img2 = img2;
    }
    
    public Img getImg1() {
        return img1;
    }
    
    public Img getImg2() {
        return img2;
    }
    
    @Override
    public int hashCode() {
        return 41*(31*img1.getDigest().hashCode() + 17*img2.getDigest().hashCode());
    }
    
    @Override
    public boolean equals(final Object obj) {
        if( obj == this ) return true;
        if( obj == null ) return false;
        
        SimilarMatchingPair that = (SimilarMatchingPair)this;
        
        return this.img1.getDigest().equals(that.img1.getDigest()) && 
               this.img2.getDigest().equals(that.img2.getDigest());
                
    }
}
