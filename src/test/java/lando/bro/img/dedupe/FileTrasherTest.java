package lando.bro.img.dedupe;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;

public class FileTrasherTest {

    @Test
    public void test_add_num() {
        test("/Foo/bar/.hidden",       3, "/Foo/bar/.hidden(3)");
        test("/Foo/bar/trailing dot.", 3, "/Foo/bar/trailing dot.(3)");
        test("/Foo/bar/normal.jpg",    3, "/Foo/bar/normal(3).jpg");
    }
    
    private void test(String path, int num, String expectedPath) {
        
        Path calculated = FileTrasher.addNumToFileName(Paths.get(path), num);
        Path expected   = Paths.get(expectedPath);
        
        Assert.assertEquals(expected, calculated);
    }
    
    
}
