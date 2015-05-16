package hello;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class FilesInDirectoryItemReader implements ItemReader<File>, ItemStream {
        
        private File [] files;
        
        private int currentCount;
        
        private String key = "file.count";

        public FilesInDirectoryItemReader(File directory) {

                FileFilter fileFilter = new FileFilter(){
                  public boolean accept(File pathname) {
                    return pathname.isFile();
                  }                
                };
                Comparator<File> nameFileNameComparator = new Comparator<File>() {
                   	public int compare(File f1, File f2){
                   	    return f1.getName().compareTo(f2.getName());
                   	}
                 	};
                this.files = directory.listFiles( fileFilter );
                Arrays.sort(files, nameFileNameComparator);
        }

        @Override
        public void open(ExecutionContext executionContext)
                        throws ItemStreamException {
                currentCount = executionContext.getInt(key, 0);
        }

        @Override
        public void update(ExecutionContext executionContext)
                        throws ItemStreamException {
                executionContext.putInt(key, currentCount);
        }

        @Override
        public void close() throws ItemStreamException { }

        @Override
        public File read() throws Exception, UnexpectedInputException,
                        ParseException, NonTransientResourceException {
                int index = currentCount++;
                if (index == files.length) {
                        return null;
                }
                return files[index];
        }

}

