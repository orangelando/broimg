package lando.bro.img.dedupe;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.MultiStepRescaleOp;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * http://www.hackerfactor.com/blog/?/archives/529-Kind-of-Like-That.html
 */
public final class DHashComputer {
    
    public static String dhashASCII(long dhash) {
        StringBuilder sb = new StringBuilder();
        String nl = String.format("%n");
        
        for(int y = 0; y < 8; y++) {
            
            for(int x = 0; x < 8; x++) {
                String c =  (dhash & (1L<<(y*8L + x))) != 0 ? "* " : ". ";
                
                sb.append(c);
            }
            
            sb.append(nl);
        }

        return sb.toString();
    }
    
    private static final int W = 9;
    private static final int H = 8;
    
    private final ThreadLocal<ResampleOp> resizeOps = new ThreadLocal<ResampleOp>() {
        @Override
        public ResampleOp initialValue() {
            return new ResampleOp(W, H);
        }
    };
    
    private final ThreadLocal<MultiStepRescaleOp> fallbackRezizeOps = new ThreadLocal<MultiStepRescaleOp>() {
        @Override
        public MultiStepRescaleOp initialValue() {
            return new MultiStepRescaleOp(W, H);
        }
    };
    
    public ImgInfo forImg(byte[] imgBytes) throws Exception {
        
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgBytes));
        BufferedImage thumb = createThumb(img);
        
        int[] pixels = getPixels(thumb);
        
        convertToGrayscale(pixels);
        
        long dhash = 0;
        
        for(int y = 0; y < H; y++) {
            int yOffset = y*W;
            
            for(int x = 0; x < W - 1; x++) {
                int xOffset = yOffset + x;
                
                if(pixels[xOffset] > pixels[xOffset + 1]) {
                    dhash |= 1L<<(y*8 + x);
                }
            }
        }
        
        return new ImgInfo(img.getWidth(), img.getHeight(), dhash);
    }
    
    private BufferedImage createThumb(BufferedImage img) {
        if( img.getWidth() > W && img.getHeight() > H ) {
            return resizeOps.get().filter(img, null);
        }
        
        return fallbackRezizeOps.get().filter(img, null);
    }
    
    private void convertToGrayscale(int[] pixels) {
        
        for(int i = 0; i < pixels.length; i++) {
            
            int rgb = pixels[i],
                r   = (rgb >> 16)&255,
                g   = (rgb >>  8)&255,
                b   = (rgb >>  0)&255;
            
            pixels[i] = 
                    (int)(0.2126*r) + 
                    (int)(0.7152*g) + 
                    (int)(0.0722*b);
        }
    }
    
    private int[] getPixels(BufferedImage img) {
        
        int w = img.getWidth();
        int h = img.getHeight();
        
        return img.getRGB(
                0, 0, 
                w, h, 
                null, 0, w);
    }
}
