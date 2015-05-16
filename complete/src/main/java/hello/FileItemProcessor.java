package hello;

import java.io.*;
import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.batch.item.*;

public class FileItemProcessor implements ItemProcessor<File, List<String>> {

    private static Log logger = LogFactory.getLog("FileItemProcessor");
     
    public List<String> process(File file){

		  BufferedReader br = null;
			List<String> lines = new ArrayList<String>();
		  try {
   
			  String line;
			     
			  br = new BufferedReader(new FileReader(file));
   
			  while ((line = br.readLine()) != null) {
				  lines.add(line);
			  }
   
		  } catch (IOException e) {
			  logger.error(e);
        lines = null; 
		  } finally {
			  try {
				  if (br != null)br.close();
			  } catch (IOException ex) {
			  }
		  }
      return lines;       
    }
}
