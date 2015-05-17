package hello.files.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import org.springframework.batch.item.ItemProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileItemProcessor implements ItemProcessor<File, File> {

    private static Log logger = LogFactory.getLog("FileItemProcessor");
     
    public File process(File file) {

		  BufferedReader br = null;
		  try {
   
			  String line;
			     
			  br = new BufferedReader(new FileReader(file));
   
			  while ((line = br.readLine()) != null) {
            treat(line);
			  }
   
		  } catch (IOException e) {
			  logger.error(e);
			  file = null ; // tell Spring Batch a problem occurred
		  } finally {
			  try {
				  if (br != null)
				    br.close();
			  } catch (IOException ex) {
			  }
		  }
      return file;
    }

  protected void treat(String line){
    System.out.println ("HEY, line is journalized!  " + line);
  }

}
