package lando.bro.img.dedupe.html;

import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

import lando.bro.img.dedupe.Img;
import lando.bro.img.dedupe.MatchGroup;

final class PageWriter {
    
    private final PrintWriter out;
    private final List<MatchGroup> matches;

    PageWriter(PrintWriter out, List<MatchGroup> matches) {
        this.out = Objects.requireNonNull(out);
        this.matches = Objects.requireNonNull(matches);
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
        
        matches.stream()
            .sorted((a, b) -> a.getImg().getPath().compareTo(b.getImg().getPath()))
            .forEach(this::printMatchGroup);
        
        o("</body>");
        
        o("</html>");
    }
    
    private void printMatchGroup(MatchGroup m) {
        
        o("<hr>");
        
        printImg(m.getImg(), 250);
        printImgSection(m.getExactMatches(), "Exact Matches", 100);
        printImgSection(m.getSimilarMatches(), "Similar Matches", 100);
    }
    
    private void printImgSection(List<Img> images, String sectionName, int imgLongSideSize) {
        if( ! images.isEmpty() ) {
            o("<h3>"+sectionName+"</h3>");
            
            images.forEach(img -> {
                printImg(img, imgLongSideSize);
            });
        }
    }
    
    private void printImg(Img img, int longSize) {
        
        o("<div>" + img.getPath().toString() + "</div>");
        out.printf("<div>%d x %d %.3fMiB</div>%n", 
                img.getWidth(), img.getHeight(), img.getSize()/1024.0/1024.0);
        
        String size = img.getWidth() > img.getHeight() ?
                " width='" + longSize + "'" :
                " height='" + longSize + "'";
        
        //i'm sure there's some standard url encoding thing i should be using...
        String fileUrl = img.getPath().toString().replaceAll("'", "%27");
        
        o("<a href='"+fileUrl+"'><img src='"+fileUrl+"'"+size+"></a>");
    }
    
    private void o(String s) {
        out.println(s);
    }
}
