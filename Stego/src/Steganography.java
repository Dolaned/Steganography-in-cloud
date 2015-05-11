import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
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
   private Path currentRelativePath = Paths.get("");
   private String appWorkingFolder = currentRelativePath.toAbsolutePath().toString();
   
   
   public boolean encrypt(File file)
   {
      try
      {
         int i = 0;
         pw = new PrintWriter(new BufferedWriter(new FileWriter(appWorkingFolder + "secretkey.txt")));
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
         return true;
      }
      catch(Exception e)
      {
    	 
         e.printStackTrace();
      }
      return false;
   }
   
   public static String toBinaryString( byte[] bytes )
   {
       StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
       for( int i = 0; i < Byte.SIZE * bytes.length; i++ )
           sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
       return sb.toString();
   }
   
   public void steganography(File bigData, byte[] key){
       
 	  //once file is read convert the arraylist into an Array for the wavelet transform function.
       double[] waveletList = new double[dataSetFileList.size()];
       
       //loop through the wavelet array and list.
       for(int j = 0; j < dataSetFileList.size(); j++){
     	  waveletList[j] = Double.parseDouble(dataSetFileList.get(j));
       }
       
       //create the two dimensional wavelet array.
       double[][] transformedList = Wavelets.transform(5, waveletList);
       for(int i = 0; i < transformedList.length; i++){
     	    for (int k = 0; k < transformedList[i].length; k++) {
     	    	//adjust coef
     	        transformedList[i][k]+=20;
     	        transformedList[i][k]*=10000;
     	        
     	    }
       }
       //call the hiding method
     //toByteArray(transformedList);
     //hideData(transformedList);  
	   
	   
   }
   
   // loop all files in folder and apply wavelets
   public void stegStart(File folder, File pData) throws IOException
   {
	  //get path relative to where app was started.

	  String appendedFile = appWorkingFolder + "data.txt";
	  
	  //get all files from the passed in folder.
      File[] filesInFolder = folder.listFiles();
      
      //join all the files into one file.
      IOCopier.joinFiles(new File(appendedFile), filesInFolder);
      
      //print file name and directory to screen.
      System.out.println("Appended file is:  \n" + appendedFile);
      
      //grab full text data file
      File bigData = new File(appendedFile);
      
      if(readFileContents(bigData)){
    	  steganography(bigData, cipher);
          
          
      }else{
    	  System.out.println("File not read please try again.");
      }
      
      

    	
      
   }
   
   public static byte[] toByteArray(double[] doubleArray){
	    int times = Double.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[doubleArray.length * times];
	    for(int i=0;i<doubleArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putDouble(doubleArray[i]);
	    }
	    return bytes;
	}
   
   public boolean hideData(double[][] tList){
	   int s = 1;
	   while(s < tList.length){
		   
	   }
	   
	   return true;
   }
   public boolean readFileContents(File data) throws IOException {
	   //init Variables.
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
