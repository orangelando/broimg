package lando.bro.img.dedupe;

import java.io.PrintStream;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.Validate;

public class BytePackTestApp {

    public static void main(String [] args) {
        Random rnd = new Random();
        
        byte[] data = new byte[100 + rnd.nextInt(100)];
        
        PrintStream out = System.out;
        
        for(int i = 0; i < 4; i++) {
            rnd.nextBytes(data);
            byte[] digest = DigestUtils.md5(data);
            Validate.isTrue(digest.length == 16);
            
            long low = ImgLoader.packBytesIntoLong(digest, 0);
            long high = ImgLoader.packBytesIntoLong(digest, 8);
            
            out.println("______________________________________________");
            out.print("bytes : ");
            for(byte b: digest) {
                out.printf("%2x", b);
            }
            out.println();
            
            out.printf("low  : %16x%n", low);
            
            out.printf("high : %16x%n", high);
        }
    }
}
