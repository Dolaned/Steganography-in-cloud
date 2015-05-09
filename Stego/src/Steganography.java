import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mathIT.approximation.Wavelets;


public class Steganography
{  
   private static ArrayList<String> pInfo = new ArrayList<String>();
   private static byte[] cipher;
   private static BufferedReader fr;
   private PrintWriter pw;
   private File folderPath;
   private File pDataFile;
   private static ArrayList<String> dataSetFileList = new ArrayList<String>();
   
   
   public void encrypt(File file)
   {
      try
      {
         int i = 0;
         pw = new PrintWriter(new BufferedWriter(new FileWriter("secretkey.txt")));
         byte[] fileData = Files.readAllBytes(file.toPath());
         
         byte[] secretkey = new byte[fileData.length];
         new Random().nextBytes(secretkey);
         
         pw.write(toBinaryString(secretkey));
         
         cipher = new byte[fileData.length];
         
         for(byte b : fileData)
         {
            cipher[i] = (byte) (b ^ secretkey[i]);
            i++;
         }
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }
   
   public static String toBinaryString( byte[] bytes )
   {
       StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
       for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
           sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
       return sb.toString();
   }
   
   // loop all files in folder and apply wavelets
   public void wavelets(File folder) throws IOException
   {
	  //get path relative to where app was started.
	  Path currentRelativePath = Paths.get("");
	  String appWorkingFolder = currentRelativePath.toAbsolutePath().toString();
	  String appendedFile = appWorkingFolder + "data.txt";
	  
	  //get all files from the passed in folder.
      File[] filesInFolder = folder.listFiles();
      
      //join all the files into one file.
      IOCopier.joinFiles(new File(appendedFile), filesInFolder);
      
      //print file name and directory to screen.
      System.out.println(appWorkingFolder);
      System.out.println("Appended file is:  \n" + appendedFile);
      
      //grab full text data file
      File bigData = new File(appendedFile);
      
      if(readFileContents(bigData)){
          
    	  //once file is read convert the arraylist into an Array for the wavelet transform function.
          double[] waveletList = new double[dataSetFileList.size()];
          
          //loop through the wavelet array and list.
          for(int j = 0; j < dataSetFileList.size(); j++){
        	  waveletList[j] = Double.parseDouble(dataSetFileList.get(j));
          }
          
          double[][] transformedList = Wavelets.transform(5, waveletList);
          
          for(int i = 0; i < transformedList.length; i++){
        	    for (int k = 0; k < transformedList[i].length; k++) {
        	        double p1 = transformedList[i][k];
        	        System.out.println(p1);
        	    }
          }
          
      }else{
    	  System.out.println("File not read please try again.");
      }
      
      

    	
      
   }
   public boolean readFileContents(File data) throws IOException {
	   //init Varibles.
	   try{
		   Scanner fileReader = new Scanner(data);
		   
		   fileReader.useDelimiter("\n|\t|,");
		   while(fileReader.hasNext()){
			   dataSetFileList.add(fileReader.next());
		   }
		   fileReader.close();
		   return true;
	   }catch(FileNotFoundException e){

	         System.out.println("Data file not found!");
	         return false;
	   }
	   
   }
   public static void readPrivateInfo() throws IOException
   {
      String strLine;
      int lineNum = 0;
      
      try
      {
         fr = new BufferedReader(new FileReader("private.txt"));
         
         // when there is a line
         while ((strLine = fr.readLine()) != null)
         {
            pInfo.add(strLine);
            
            lineNum++;
         } // end while

         // if file is empty
         if (lineNum == 0)
         {
            System.out.println("No data loaded.");
         }

         // close BufferedReader, FileReader
         fr.close();
      }
      catch (FileNotFoundException e)
      {
         System.out.println("Data file not found!");
      }
   }
   public void setFolderPath(File name){
	   folderPath = name;
   }
   public File getFolderPath(){
	   return folderPath;
   }

public void setPrivateFile(File privateDataFile) {
	// TODO Auto-generated method stub
	pDataFile = privateDataFile;
}
public File getPrivateDataFile(){
	return pDataFile;
}
}
