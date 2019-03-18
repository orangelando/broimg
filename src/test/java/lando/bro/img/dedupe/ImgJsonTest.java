package lando.bro.img.dedupe;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ImgJsonTest {

    @Test
    public void to_json() throws Exception {
        Img img = new Img(Paths.get("/Users/oroman/tmp/foo.jpg"),
                0xFF_FF_FF_FF_00_00_00_00L,
                100,
                100,
                100,
                "abc123",
                0xFF_FF_FF_FF_00_00_00_00L
                );
        
        ObjectMapper jsonCreator = new ObjectMapper();
        
        jsonCreator.enable(SerializationFeature.INDENT_OUTPUT);
        
        String json = jsonCreator.writeValueAsString(img);
        
        System.out.println(json);
        
        Img img2 = jsonCreator.readValue(json, Img.class);
        
        assertTrue( img.getPath().equals(img2.getPath()));
        assertTrue( img.getMTime() == img2.getMTime() );
        assertTrue( img.getSize() == img2.getSize() );
        assertTrue( img.getWidth() == img2.getWidth() );
        assertTrue( img.getHeight() == img2.getHeight() );
        assertTrue( img.getDigest().equals(img2.getDigest()));
        assertTrue( img.getDhash() == img2.getDhash() );
    }
    
    @Test
    public void to_json_list() throws Exception {
        Img img = new Img(Paths.get("/Users/oroman/tmp/foo.jpg"),
                0xFF_FF_FF_FF_00_00_00_00L,
                100,
                100,
                100,
                "abc123",
                0xFF_FF_FF_FF_00_00_00_00L
                );
        
        List<Img> list = Arrays.asList(img, img, img);
        
        ObjectMapper jsonCreator = new ObjectMapper();
        
        jsonCreator.enable(SerializationFeature.INDENT_OUTPUT);
        
        String json = jsonCreator.writeValueAsString(list);
        
        System.out.println(json);
        
        List<Img> list2 = jsonCreator.readValue(json, new TypeReference<List<Img>>() {});
        System.out.println(list2.getClass().getName());
        
        for(Img imgEntry: list2) {
            System.out.println(imgEntry.getClass().getName());
            assertTrue( imgEntry.getClass() == Img.class);
        }
        
        System.out.println(list2.size());
        
        String json2 = jsonCreator.writeValueAsString(list2);
        
        assertTrue( json.equals(json2));
    }
}
