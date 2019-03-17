package lando.bro.img.dedupe;

final class ImgInfo {

    final int width;
    final int height;
    final long dhash;
    
    ImgInfo(int width, int height, long dhash) {
        this.width = width;
        this.height = height;
        this.dhash = dhash;
    }
}
