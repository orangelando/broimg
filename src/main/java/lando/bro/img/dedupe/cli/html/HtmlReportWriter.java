package lando.bro.img.dedupe.cli.html;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lando.bro.img.dedupe.Img;
import lando.bro.img.dedupe.MatchGroup;

/**
 * I should probably be using a templating library...
 */
public final class HtmlReportWriter {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DecimalFormat fmt = new DecimalFormat("00");
    
    public void write(
            Path reportDir,
            int matchesPerPage, 
            List<MatchGroup> matches) throws Exception {
        
        Validate.notNull(reportDir);
        Validate.isTrue(Files.isDirectory(reportDir));
        Validate.isTrue(matchesPerPage > 0);
        Validate.notNull(matches);
        Validate.noNullElements(matches);
        
        List<List<MatchGroup>> pages = 
                splitList(
                        matchesPerPage,         
                        matches.stream()
                          .sorted((a, b) -> a.getImg().getPath().compareTo(b.getImg().getPath()))
                          .collect(Collectors.toList()));
        
        writeIndex(reportDir, pages.size());
        
        int index = 0;
        
        for(List<MatchGroup> page: pages) {
            writePage(reportDir, index++, page);
        }
    }
    
    private String pageFileName(int pageNum) {
        return "img-matches-page-" + fmt.format(pageNum) + ".html";
    }
    
    private void writeIndex(Path reportDir, int numPages) throws Exception {
        Path indexPath = reportDir.resolve("img-matches-index.html");
        
        logger.info("writing index to {}", indexPath);
        
        try(BufferedWriter bout = Files.newBufferedWriter(indexPath, UTF_8);
            PrintWriter pout = new PrintWriter(bout)) {
            
            new IndexWriter(pout, numPages, this::pageFileName)
                .write();
        }
    }
    
    private void writePage(Path reportDir, int pageNum, List<MatchGroup> matches) throws Exception {
        Path pagePath = reportDir.resolve(pageFileName(pageNum));
        
        logger.info("writing page to {}", pagePath);
        
        try(BufferedWriter bout = Files.newBufferedWriter(pagePath, UTF_8);
            PrintWriter pout = new PrintWriter(bout)) {
            
            new PageWriter(pout, matches)
                .write();
        }
    }
    
    public <T> List<List<T>> splitList(int size, List<T> list) {
        List<List<T>> subLists = new ArrayList<>();
        
        for(int i = 0; i < list.size(); i += size) {
            subLists.add(list.subList(i, Math.min(i + size, list.size())));
        }
        
        return subLists;
    }
}
