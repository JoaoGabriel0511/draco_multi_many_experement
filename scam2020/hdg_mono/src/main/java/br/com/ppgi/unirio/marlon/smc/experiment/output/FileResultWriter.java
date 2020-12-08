package br.com.ppgi.unirio.marlon.smc.experiment.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Saida do experimeto em arquivo
 * @author kiko
 */
public class FileResultWriter extends ResultWriter{
	private static final String FOLDER = "./output";
	
        /**
         * Cria um arquivo de saida novo, pronto para ser utilizado
         * @param folderName
         * @param filePrefix
         * @param ext
         * @throws IOException 
         */
	public FileResultWriter(String folderName,String filePrefix, String ext) throws IOException{
		String fixedFolder = FOLDER+"/"+folderName;
                File outFolder = new File(fixedFolder);
		if(!outFolder.isDirectory()){
			outFolder.mkdirs();
		}
		
		if(filePrefix == null || filePrefix.length()==0){ filePrefix ="";}
		else {filePrefix = filePrefix+"_";}
		
		String fileName = fixedFolder+"/"+filePrefix+System.currentTimeMillis()+"."+ext;
		
		File outFile = new File(fileName);
		outFile.createNewFile();
				
		out = new PrintStream(fileName);
	}
}
