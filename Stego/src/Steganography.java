import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.mathIT.approximation.Wavelets;

public class Steganography {
	private static byte[] cipher;
	private byte[] secretkey;
	private PrintWriter pw;
	private File folderPath;
	private File pDataFile;
	private static ArrayList<Double> dataSetFileList = new ArrayList<Double>();
	private static Path currentRelativePath = Paths.get("");
	private static String appWorkingFolder = currentRelativePath
			.toAbsolutePath().toString();
	final String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	final int alphLength = alphabet.length();
	private double minimumValueDouble;
	private List<Integer> matrixKeyRow = new ArrayList<Integer>();
	private List<Integer> matrixKeyCol = new ArrayList<Integer>();
	private int[][] tListInt;
	private double[][] transformedListGlobal;
	
	public boolean encrypt(File file) {
		try {
			int i = 0;
			pw = new PrintWriter(new BufferedWriter(new FileWriter(
					appWorkingFolder + "secretkey.txt")));
			byte[] fileData = Files.readAllBytes(file.toPath());

			secretkey = new byte[fileData.length];
			new Random().nextBytes(secretkey);

			pw.write(toBinaryString(secretkey));
			pw.close();
			cipher = new byte[fileData.length];

			for (byte b : fileData) {
				cipher[i] = (byte) (b ^ secretkey[i]);
				i++;
			}
			return true;
		} catch (Exception e) {

			e.printStackTrace();
		}
		return false;
	}

	public String decrypt(byte[] cipherPInfo) {
		byte[] bytePInfo = new byte[secretkey.length];
		;
		int i = 0;

		for (byte b : cipherPInfo) {
			bytePInfo[i] = (byte) (b ^ secretkey[i]);
			i++;
		}

		return (new String(bytePInfo));
	}

	public static String toBinaryString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}
	public double[][] createHiddenWavelet(double [][] tree, int indices)
	{
		double [][] hiddenWavelet = this.transformedListGlobal;
		int hiddenCounter = 0;
		for(int i=0; i < tree.length; i++)
		{
			for(int j=0; j < tree[0].length; j++)
			{
				if (hiddenCounter == indices - 1) {
					break;
				} 
				else 
				{
					hiddenWavelet[0][hiddenCounter] = tree[i][j];
					hiddenCounter++;
				}
				
			}
		}
		
		return hiddenWavelet;
	}
	public double[][] splitWavelet(int levelSize, int indices, double[][] list) {

		// Create wavelet to emulate the tree based on level size and values per
		// level
		double[][] splitWavelet = new double[levelSize][512];
		// Counter based on number of total indices inserted
		int listCounter = 0;
		// Loop through tree size and add 512 values per level
		for (int j = 0; j <= levelSize; j++) {
			for (int i = 0; i < 512; i++) {

				if (listCounter == indices - 1) {
					break;
				} else {
					splitWavelet[j][i] = list[0][listCounter];
					listCounter++;
				}
			}
		}

		return splitWavelet;
	}

	public void steganography(byte[] key) throws IOException {

		// Once file is read convert the arraylist into an Array for the wavelet
		// transform function.
		double[] waveletInputArray = new double[dataSetFileList.size()];
		int padding_cutoff = waveletInputArray.length;
		int levelSize = 0, levelCounter = 0, indexCounter = 0;
		minimumValueDouble = Double.MAX_VALUE;

		// Loop through the wavelet array and list.
		for (int j = 0; j < dataSetFileList.size(); j++) {
			waveletInputArray[j] = dataSetFileList.get(j);

		}

		File stegoReadings = new File(appWorkingFolder + "/stegodata.txt");
		FileWriter fileWriter = new FileWriter(stegoReadings);
		PrintWriter writer = new PrintWriter(fileWriter);
		// Create the two dimensional wavelet array.
		double[][] transformedList = Wavelets.transform(4, waveletInputArray);
		
		this.transformedListGlobal = transformedList;
		// double[] inversedList = Wavelets.inverseTransform(4,
		// transformedList);
		
		// Loop through transformed list and count the
		// level.
		for (int i = 0; i < transformedList.length; i++) {
			for (int k = 0; k < transformedList[i].length; k++) {
				if (transformedList[i][k] < minimumValueDouble) {
					minimumValueDouble = transformedList[i][k];
				}
				// Iterate number of levels (512 per level)
				levelCounter++;
				// Iterate number of indices (total indices in the tree)
				indexCounter++;
				if (levelCounter == 512) {
					levelSize++;
					levelCounter = 0;
				}
			}
		}
		
		minimumValueDouble = Math.abs(Math.ceil(minimumValueDouble)) + 1;
		
		// TODO: Adjust coefficients
		for (int k = 0; k < transformedList[0].length; k++) {

			transformedList[0][k] += minimumValueDouble;
			transformedList[0][k] *= 10000;
			
			// Convert to int
			//intTransformedList[0][k] = (int)transformedList[0][k];
		}
		
		// call the hiding method
		double[][] tree = splitWavelet(levelSize, indexCounter, transformedList);
		
		// convert to int
		int[][] intTransformedList = new int[tree.length][tree[0].length];
		for(int i = 0; i < tree.length; i++) {
		   for(int j = 0; j < tree[0].length; j++) {
		      intTransformedList[i][j] = (int) tree[i][j];
		   }
		}
		
		double[][] hiddenDouble = hideData(intTransformedList);
		// Adjust coefficients back
      for (int i = 0; i < hiddenDouble.length; i++) {
         for (int j = 0; j < hiddenDouble[0].length; j++) {
            hiddenDouble[i][j] = hiddenDouble[i][j] / 10000;
            hiddenDouble[i][j] = hiddenDouble[i][j] - minimumValueDouble;
         }
      }
      
		double[][] hiddenWavelet = createHiddenWavelet(hiddenDouble, indexCounter);
		double[] inverseList = Wavelets.inverseTransform(4, hiddenWavelet);
		double[] inverseListNoPadding = new double[padding_cutoff];
		
		// Trim the Inversed Wavelet to match the original file
		for(int i=0; i < padding_cutoff; i++)
		{
			inverseListNoPadding[i] = inverseList[i];
		}
		for(int i=0; i < inverseListNoPadding.length; i++)
		{
			writer.println(inverseListNoPadding[i]);
		}
		
		writer.close();
	}

	// loop all files in folder and apply wavelets
	public void stegStart(File folder, File pData) throws IOException {
		// Get relative path

		String appendedFile = appWorkingFolder + "/originaldata.txt";

		// Existence checking to avoid appending
		File existenceCheck = new File(appendedFile);
		if (existenceCheck.exists()) {
			existenceCheck.delete();
		}

		// Get all files from the passed in folder.
		File[] filesInFolder = folder.listFiles();
		File stegoOriginal = new File(appendedFile);

		// Join all the files into one file.
		IOCopier.joinFiles(stegoOriginal, filesInFolder);

		// Print file name and directory to screen.
		System.out.println("Appended file is:  \n" + stegoOriginal.getName());

		// Grab full text data file
		if (readFileContents(stegoOriginal)) {
			steganography(cipher);
		} else {
			System.out.println("File not read please try again.");
		}
	}

	public static byte[] toByteArray(double[] doubleArray) {
		int times = Double.SIZE / Byte.SIZE;
		byte[] bytes = new byte[doubleArray.length * times];
		for (int i = 0; i < doubleArray.length; i++) {
			ByteBuffer.wrap(bytes, i * times, times).putDouble(doubleArray[i]);
		}
		return bytes;
	}

	public static byte[] toByteArray(double value) {
		byte[] bytes = new byte[8];
		ByteBuffer.wrap(bytes).putDouble(value);
		return bytes;
	}

	public static double toDouble(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getDouble();
	}

	public double[][] hideData(int[][] tList) throws IOException {
		char[] key = new char[tList.length];
		Random r = new Random();
		pw = new PrintWriter(new BufferedWriter(new FileWriter(appWorkingFolder
				+ "matrixkey.txt")));

		// generate random key
		for (int i = 0; i < tList.length; i++) {
			key[i] = alphabet.charAt(r.nextInt(alphLength));
		}

		// desc order
		char[] keyCopy = Arrays.copyOf(key, key.length);
		char[] keyDesc = new char[key.length];
		int count = 0;
		for (int i = key.length - 1; i >= 0; i--) {
			keyDesc[count] = keyCopy[i];
			count++;
		}

		int[] col = new int[key.length];
		for (int i = 0; i < key.length; i++) {
			for (int j = 0; j < key.length; j++) {
				if (keyDesc[i] == key[j])
					col[j] = i;
			}
		}

		// asc order
		char[] keyAsc = Arrays.copyOf(key, key.length);
		Arrays.sort(keyAsc);

		int[] row = new int[key.length];
		for (int i = 0; i < key.length; i++) {
			for (int j = 0; j < key.length; j++) {
				if (keyAsc[i] == key[j])
					row[j] = i;
			}
		}
		// row[] and col[] as the position matrix for the hiding position
		
		String cipherStr = toBinaryString(cipher);
		
		count = 0;
		int countKey = 0;
		//TODO here start hiding
		for (int i = 0; i < row.length; i++) {
         for (int j = 0; j < col.length; j++) {
            if (count < cipherStr.length()) {
               ByteBuffer b = ByteBuffer.allocate(4);
               b.putInt(tList[row[i]][col[j]]);
               byte[] tmp = b.array();
               
               String tmpStr = toBinaryString(tmp);
               // concatenate cipher 8 bits into wavelet
               tmpStr = tmpStr.substring(0, tmpStr.length()-4) + cipherStr.substring(count, count+4);

               // convert back to double
               //ByteBuffer bb = ByteBuffer.wrap(tmp);
               //tList[row[i]][col[j]] = bb.getInt();
               tList[row[i]][col[j]] = Integer.parseInt(tmpStr, 2);
               
               matrixKeyRow.add(countKey, row[i]);
               matrixKeyCol.add(countKey, col[j]);
               countKey++;
               count = count + 4;
               
               //pw.println(tmpStr);
               pw.println(row[i] + "," + col[j]);
            }
         }
      }

		pw.close();
		
		// TODO reverse the levels and inverse the wavelet
		double[][] doubleList = new double[tList.length][tList[0].length];
      for(int i = 0; i < doubleList.length; i++) {
         for(int j = 0; j < doubleList[0].length; j++) {
            doubleList[i][j] = tList[i][j];
         }
      }

		return doubleList;
	}

	public boolean extract() throws IOException {
      byte[] cipherPInfo = new byte[cipher.length];
      int count = 0;
      double[] waveletInputArray = new double[dataSetFileList.size()];
      int levelSize = 0, levelCounter = 0, indexCounter = 0;
      
      String appendedFile = appWorkingFolder + "/stegodata.txt";
      
      // read the stego file
      dataSetFileList.clear();
      File stegoFile = new File(appendedFile);
      
      if (!readFileContents(stegoFile)) {
         System.out.println("File not read please try again.");
         return false;
      }
      
      // Loop through the wavelet array and list.
      for (int j = 0; j < dataSetFileList.size(); j++) {
         waveletInputArray[j] = dataSetFileList.get(j);

      }

      // Create the two dimensional wavelet array.
      double[][] transformedList = Wavelets.transform(4, waveletInputArray);
      
      // Loop through transformed list and count the
      // level.
      for (int i = 0; i < transformedList.length; i++) {
         for (int k = 0; k < transformedList[i].length; k++) {
            if (transformedList[i][k] < minimumValueDouble) {
               minimumValueDouble = transformedList[i][k];
            }
            // Iterate number of levels (512 per level)
            levelCounter++;
            // Iterate number of indices (total indices in the tree)
            indexCounter++;
            if (levelCounter == 512) {
               levelSize++;
               levelCounter = 0;
            }
         }
      }
      minimumValueDouble = Math.abs(Math.ceil(minimumValueDouble)) + 1;
      
      // Adjust coefficients
      for (int k = 0; k < transformedList[0].length; k++) {

         transformedList[0][k] += minimumValueDouble;
         transformedList[0][k] *= 10000;
      }

      // TODO
      double[][] tList = splitWavelet(levelSize, indexCounter, transformedList);
      int[][] tree = new int[tList.length][tList[0].length];
      for(int i = 0; i < tree.length; i++) {
         for(int j = 0; j < tree[0].length; j++) {
            tree[i][j] = (int)tList[i][j];
         }
      }
      
      String cipherStr = "";
      pw = new PrintWriter(new BufferedWriter(new FileWriter(appWorkingFolder
                                                             + "privateInfo.txt")));
      // start extraction
      while (count < matrixKeyRow.size()) {
         ByteBuffer b = ByteBuffer.allocate(4);
         b.putInt(tree[matrixKeyRow.get(count)][matrixKeyCol.get(count)]);
         byte[] tmp = b.array();
         
         String tmpStr = toBinaryString(tmp);
         pw.println(tmpStr);
         cipherStr += tmpStr.substring(tmpStr.length()-4, tmpStr.length());
         
         //cipherPInfo[count] = tmp[tmp.length-1];
         count++;
      }

      // TODO reverse the levels and inverse the wavelet

      // decrypt private info and write to file
      
      pw.println(cipherStr);
      pw.println(toBinaryString(cipher));
      pw.print(decrypt(cipherPInfo));
      pw.close();
      
      return true;
   }

	public boolean readFileContents(File data) throws IOException {
		// init Variables.
		try {
		   dataSetFileList.clear();
			Scanner fileReader = new Scanner(data);
			// fileReader.useDelimiter("\n|\t|,");
			while (fileReader.hasNextLine()) {
				dataSetFileList.add(Double.parseDouble(fileReader.nextLine()));
			}
			fileReader.close();
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("Data file not found!");
			return false;
		}

	}

	public void setFolderPath(File name) {
		folderPath = name;
	}

	public File getFolderPath() {
		return folderPath;
	}

	public void setPrivateFile(File privateDataFile) {
		// TODO Auto-generated method stub
		pDataFile = privateDataFile;
	}

	public File getPrivateDataFile() {
		return pDataFile;
	}

	public static String getAppFolder() {
		return appWorkingFolder;
	}
}
