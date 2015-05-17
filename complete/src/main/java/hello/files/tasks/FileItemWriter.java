package hello.files.tasks;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.FileFilter;
import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class FileItemWriter implements ItemWriter<File> {
        
    protected File dirOut;
    
    public FileItemWriter(File dirOut){
      this.dirOut = dirOut;
    }
    
    @Override 
    public void write(List<? extends File> files) throws IOException {
      System.out.println("Writer is receiving " + files.size() + " files");
      for (File file : files) {
        Path dest = new File (this.dirOut, file.getName()).toPath();
        Path destinationPath = Files.move(file.toPath(), dest);
        System.out.println("    Moved file to " + destinationPath);
      }
    }
    
}

