package lando.bro.img.dedupe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ThumbnailRescaleOp;

public class NonDeterministicMatchApp {

    public static void main(String [] args) throws Exception {
        //these images matched sometimes but not others... wth
        
        Path p = Paths.get("/Users/oroman/Desktop/test");
        DHashComputer c = new DHashComputer();
        ImgLoader l = new ImgLoader(c);
        PrintStream out = System.out;
        
        List<Img> images = Files.walk(p).filter(f -> Files.isRegularFile(f)).map(f -> load(l, f)).collect(Collectors.toList());
        
        ThumbnailRescaleOp resizeOp = new ThumbnailRescaleOp(9, 8);
       
        int index = 0;
        
        for(Img img: images) {
            out.println();
            out.println("[" + index++ + "]" + img.getPath());
            out.println(DHashComputer.dhashASCII(img.getDhash()));
            
            BufferedImage img2 = ImageIO.read(img.getPath().toFile());
            BufferedImage thumb = resizeOp.filter(img2, null);

            ImageIO.write(thumb, "PNG", new File("/Users/oroman/Desktop/thumbs/" + img.getPath().getFileName()));
        }
        
        for(int i = 0; i < images.size(); i++) {
            Img img1 = images.get(i);
            
            for(int j = i + 1; j < images.size(); j++) {
                Img img2 = images.get(j);
                
                out.printf("%d ^ %d = %d%n", i, j, 
                        Long.bitCount(img1.getDhash()^img2.getDhash()));
                
            }
        }
        
        out.println("images: " + images.size());
    }
    
    private static Img load(ImgLoader l, Path p) {
        try {
            return l.load(p);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
