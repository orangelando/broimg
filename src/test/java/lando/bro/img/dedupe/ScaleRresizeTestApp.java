package lando.bro.img.dedupe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.MultiStepRescaleOp;

public class ScaleRresizeTestApp {

    public static void main(String [] args) throws Exception {
       
        
        List<Path> paths = Arrays.asList(
                "/Users/oroman/Downloads/20170506_195352181_iOS.jpg",
                "/Users/oroman/Downloads/Christian Cross.jpg"
                ).stream().map(s -> Paths.get(s)).collect(Collectors.toList());
        
        int index = 0;
        
        for(Path p: paths) {
        
            System.out.println(p);
            System.out.println("exists: " + Files.exists(p));
            System.out.println("regular-file: " + Files.isRegularFile(p));
            System.out.println("readable: " + Files.isReadable(p));
            System.out.println("size: " + Files.size(p));
            
            BufferedImage img = ImageIO.read(p.toFile());
            
            int newWidth = 9;
            int newHeight = 8;
            
            MultiStepRescaleOp resizeOp = new MultiStepRescaleOp(newWidth, newHeight);
            long start = System.currentTimeMillis();
            BufferedImage resizedImage = resizeOp.filter(img, null);
            long end = System.currentTimeMillis();
            
            System.out.println("resize: " + Duration.ofMillis(end - start));
            
            int[] pixels = getPixels(resizedImage);
            
            System.out.println("pixels: " + pixels.length);
    
                    
            ImageIO.write(
                    resizedImage, 
                    "png", 
                    new File("/Users/oroman/Desktop/thumb"+(index++)+".png"));
            
            byte[] bytes = Files.readAllBytes(p);
            
            DHashComputer dhasher = new DHashComputer();
            
            long dhash = dhasher.forImg(bytes).dhash;
            
            System.out.println(Long.toBinaryString(dhash));
            
            System.out.println(DHashComputer.dhashASCII(dhash));
        }
        
        System.out.println("done");
    }
    
    private static int[] getPixels(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        
        return img.getRGB(
                0, 0, 
                w, h, 
                null, 0, w);
    }
}
