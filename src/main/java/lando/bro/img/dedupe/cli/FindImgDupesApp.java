package lando.bro.img.dedupe.cli;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lando.bro.img.dedupe.DHashComputer;
import lando.bro.img.dedupe.HtmlReportWriter;
import lando.bro.img.dedupe.Img;
import lando.bro.img.dedupe.ImgLoader;
import lando.bro.img.dedupe.ImgSortEntry;
import lando.bro.img.dedupe.MatchGroup;
import lando.bro.img.dedupe.PathUtil;
import lando.bro.img.dedupe.SimilarMatchingPair;

public final class FindImgDupesApp {
    
    private static final List<String> IMG_EXTS = Arrays.asList(
            "bmp", "gif", "png", "jpg", "jpeg");
    
    @Option(name="-imgDir", required=true)
    private String imgDir;
    
    @Option(name="-reportDir", required=true)
    private String reportDir;

    public static void main(String [] args) throws Exception {
                
        FindImgDupesApp app = new FindImgDupesApp();
        CmdLineParser parser = new CmdLineParser(app);
        
        try {
            parser.parseArgument(args);
        } catch(CmdLineException e) {
            //this doesn't print anything... who knows why
            //parser.printUsage(System.err);
            System.err.println("Usage: -imgDir <path> -reportDir <path>");
            System.exit(1);
        }
        
        app.exec();
    }
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private FindImgDupesApp() {
        
    }
    
    private void exec() throws Exception {
        
        Path imgDirPath = Paths.get(imgDir),
             reportDirPath = Paths.get(reportDir);
        
        List<Img> imgs = loadImgs(imgDirPath);
        
        Map<String, List<Img>> exactMatches = imgs.stream()
                .collect(groupingBy(Img::getDigest));
        
        logger.info("Found {} matching hash groups", 
                exactMatches.values()
                    .stream()
                    .filter(l -> l.size() > 1)
                    .count());
        
        List<Img> searchImgs = exactMatches.values()
                .stream()
                .map(l -> l.get(0)) //only grab 1 from each group
                .collect(toList());
        
        logger.info("Searching in {} images", searchImgs.size());
        
        Set<SimilarMatchingPair> similarMatchPairs = findAllSimilar(searchImgs);
        
        logger.info("Found {} similar matches", similarMatchPairs.size());
        
        Map<String, List<Img>> similarMatches = groupMatches(similarMatchPairs);
        
        List<MatchGroup> matchGroups = buildMatchGroups(imgs, exactMatches, similarMatches);
                
        logger.info("match-groups {}", matchGroups.size());
        logger.info("left over digest-groups {}", exactMatches.size());
        logger.info("left over similar-matches {}", similarMatches.size());
        
        writeReport(reportDirPath, matchGroups);
        
        logger.info("done");
    }
    
    private void writeReport(Path reportDir, List<MatchGroup> matches) throws Exception {
        Path reportPath = reportDir.resolve("img-matches.html");
        
        logger.info("writing report to {}", reportPath);
        
        try(BufferedWriter bout = Files.newBufferedWriter(reportPath, StandardCharsets.UTF_8);
            PrintWriter pout = new PrintWriter(bout)) {
            
            new HtmlReportWriter(pout, matches)
                .write();
        }
    }
    
    private List<MatchGroup> buildMatchGroups(
            List<Img> imgs, 
            Map<String, List<Img>> allExactMatches, 
            Map<String, List<Img>> allSimilarMatches) {
        
        Set<String> digestsAlreadySeen = new HashSet<>();
        
        //The largest image in each group is considered the "best".
        //So we sort by size in descending order. In the case
        //of the sizes being the same we take the older file.
        
        return imgs.stream()
            .sorted((a, b) -> {
                int cmp = -Integer.compare(a.getSize(), b.getSize());
                
                if( cmp != 0 ) return cmp;
                
                cmp = Long.compare(a.getMTime(), b.getMTime());
                
                if( cmp != 0 ) return cmp;
                
                //last resort
                return a.getPath().compareTo(b.getPath());
                
            })
            .map(img -> {
                
                if( digestsAlreadySeen.contains(img.getDigest())) {
                    return Optional.<MatchGroup>empty();
                }
                
                digestsAlreadySeen.add(img.getDigest());
                
                List<Img> exactMatches = allExactMatches.remove(img.getDigest())
                        .stream()
                        .filter(o -> o != img)
                        .collect(toList());
                
                List<Img> similarMatches = allSimilarMatches.remove(img.getDigest());
                
                if( similarMatches == null ) {
                    similarMatches = emptyList();
                }
                
                exactMatches.stream().map(Img::getDigest).forEach(digestsAlreadySeen::add);
                similarMatches.stream().map(Img::getDigest).forEach(digestsAlreadySeen::add);
                
                return Optional.of(new MatchGroup(img, exactMatches, similarMatches));
            })
            .filter(o -> o.isPresent())
            .map(o -> o.get())
            .filter(m -> m.hasAnyMatches())
            .collect(toList());
    }
    
    private Map<String, List<Img>> groupMatches(Set<SimilarMatchingPair> matches) {
        //I'm sure I can do this with some stream expression
        //but I have a 101 fever and terrible right now
        Map<String, List<Img>> g = new HashMap<>();
        
        for(SimilarMatchingPair m: matches) {
            addToGroup(g, m.getImg1(), m.getImg2());
            addToGroup(g, m.getImg2(), m.getImg1());
        }
        
        return g;
    }
    
    private void addToGroup(Map<String, List<Img>> g, Img img1, Img img2) {
        List<Img> l = g.get(img1.getDigest());
        
        if( l == null ) {
            l = new ArrayList<>();
            g.put(img1.getDigest(), l);
        }
        
        l.add(img2);
    }

    private Set<SimilarMatchingPair> findAllSimilar(List<Img> searchImgs) {
        Set<SimilarMatchingPair> allMatches = new HashSet<>();

        IntStream.range(0, 64).parallel().forEach(rotateDistance -> {
            logger.info("starting pass {} out of 64", (rotateDistance + 1));
            
            ImgSortEntry[] entries = searchImgs.stream()
                    .map(img -> new ImgSortEntry(img, 
                            Long.rotateLeft(img.getDhash(), rotateDistance)))
                    .sorted( (a, b) -> Long.compare(a.dhash, b.dhash))
                    .toArray(len -> new ImgSortEntry[len]);
                    
                    
            synchronized(allMatches) {
                allMatches.addAll(findSimilar(entries));
            }
            
            logger.info("all matches so far {}", allMatches.size());
        });
        
        return allMatches;
    }
    
    private Set<SimilarMatchingPair> findSimilar(ImgSortEntry[] entries) {
        //finds images where the dhash hamming distance is 0 or 1.
        final int N = 100;
        final int k = 2;
        final Set<SimilarMatchingPair> matches = new HashSet<>();
        
        for(int i = 0; i < entries.length; i++) {
            ImgSortEntry a = entries[i];
            
            for(int j = i + 1; j < Math.min(entries.length, i + N); j++) {
                ImgSortEntry b = entries[j];
                                
                if( a.img != b.img && Long.bitCount(a.dhash ^ b.dhash) <= k ) {
                    matches.add(new SimilarMatchingPair(a.img, b.img));
                }
            }
        }
        
        return matches;
    }
    
    private List<Img> loadImgs(Path imgDirPath) throws Exception {
        
        logger.info("reading images from {}", imgDirPath);
        
        List<Path> imgPaths =  Files.walk(imgDirPath)
            .filter(p -> Files.isRegularFile(p) && IMG_EXTS.contains(PathUtil.getExt(p)))
            //.limit(2000)
            .collect(toList());
        
        long totalSize = imgPaths.stream()
            .mapToLong(this::size)
            .sum();
        
        DecimalFormat nfmt = new DecimalFormat("#,##0");
        
        logger.info("found {} images for a total of {} MiB",
                nfmt.format(imgPaths.size()), 
                nfmt.format(totalSize/1024/1024));
        
        logger.info("loading images...");
        
        DHashComputer dhasher = new DHashComputer();
        ImgLoader loader = new ImgLoader(dhasher);
        
        long start = System.currentTimeMillis();
        
        AtomicInteger numLoaded = new AtomicInteger(0);
        
        List<Img> imgs = imgPaths.parallelStream()
                .map(p -> loadImg(loader, p))
                .peek(o ->  {
                    if( numLoaded.incrementAndGet()%100 == 0 ) {
                        logger.info("loaded {} so far...", numLoaded);
                    }
                })
                .filter(o -> o.isPresent())
                .map(o -> o.get())
                .collect(toList());
        
        long end = System.currentTimeMillis();
        
        logger.info("loaded {} images in {}", 
                imgs.size(),
                Duration.ofMillis(end - start)
                    .toString().toLowerCase());
        
        return imgs;
    }
    
    private Optional<Img> loadImg(ImgLoader loader, Path path) {
        try {
            return Optional.of(loader.load(path));
        } catch(Exception e) {
            logger.info("Unable to load {} because {}", path, e.getMessage());
            return Optional.empty();
        }
    }
    
    private long size(Path p) {
        try {
            return Files.size(p);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
