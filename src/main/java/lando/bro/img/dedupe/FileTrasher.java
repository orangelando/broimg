package lando.bro.img.dedupe;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FileTrasher {
    
    public static Path addNumToFileName(Path path, int num) {
        String origFileName = path.getFileName().toString();
        int lastDot = origFileName.lastIndexOf('.');
        String numPart = "(" + num + ")";
        
        String newFileName;
        
        if( lastDot == -1 || lastDot == 0 || lastDot == origFileName.length() - 1) {
            newFileName = origFileName + numPart;
        } else {
            newFileName = origFileName.substring(0, lastDot) + numPart + 
                          origFileName.substring(lastDot);
        }
        
        return path.getParent().resolve(newFileName);
    }
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Path trashDir;
    private final FastDateFormat dateFormat =  DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT;
    
    public FileTrasher(Path trashDir) {
        Validate.notNull(trashDir);
        Validate.isTrue(Files.isDirectory(trashDir), trashDir + " is not a dir");
        
        this.trashDir = trashDir;
    }
    
    public void trashFile(Path file) throws Exception {
        Validate.notNull(file);
        Validate.isTrue(Files.exists(file));
        Validate.isTrue(Files.isRegularFile(file));
        
        Path destFile = getDestFile(file);
        Validate.isTrue(! Files.exists(destFile));
        
        logger.info("Moving " + file + " to " + destFile);
        Files.move(file, destFile);
    }
    
    private Path getDestFile(Path file) throws Exception {
        long mtime = Files.getLastModifiedTime(file).toMillis();
        String dateStr = "exact-" + dateFormat.format(mtime);
        Path trashSubDir = trashDir.resolve(dateStr);
        
        if( ! Files.exists(trashSubDir) ) {
            Files.createDirectories(trashSubDir);
        }
        
        Validate.isTrue(Files.isDirectory(trashSubDir));
        
        String origFileName = file.getFileName().toString();
        
        Path destFile = trashSubDir.resolve(origFileName);
        
        int i = 2;
        
        while( Files.exists(destFile) )  {
            destFile = FileTrasher.addNumToFileName(destFile, i++);
        }
        
        return destFile;
    }    
}
