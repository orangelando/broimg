package lando.bro.img.dedupe.html;

import java.io.PrintWriter;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;

final class IndexWriter {

    private final PrintWriter out;
    private final int numPages;
    private final Function<Integer, String> pageNumToFileNameFn;
    
    IndexWriter(PrintWriter out, int numPages, Function<Integer, String> pageNumToFileNameFn) {
        Validate.notNull(out);
        Validate.isTrue(numPages > 0);
        Validate.notNull(pageNumToFileNameFn);
        
        this.out = out;
        this.numPages = numPages;
        this.pageNumToFileNameFn = pageNumToFileNameFn;
    }

    void write() throws Exception {
        o("<html>");
        o("<!doctype html>");
        
        o("<head>");
        o("<meta charset='UTF-8'>");
        o("<title>Image Matches</title>");
        
        o("<styles>");
        o("</styles>");
        
        o("<script>");
        o("</script>");
        
        o("</head>");
        
        o("<body>");
        
        o("<ul>");
        for(int i = 0; i < numPages; i++) {
            o("<li>");
            o("<a href='"+pageNumToFileNameFn.apply(i)+"'>page - " + i + "</a>");
            o("</li>");
        }
        o("</ul>");
        
        o("</body>");
        
        o("</html>");
    }
    
    private void o(String s) {
        out.println(s);
    }
}
