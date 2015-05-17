package hello.files.tasks;

import java.io.*;
import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.batch.item.*;

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
				  if (br != null)br.close();
			  } catch (IOException ex) {
			  }
		  }
      return file;
    }

  protected void treat(String line){
    System.out.println ("HEY, line is journalized!  " + line);
  }

}
