import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
	
	public boolean encrypt(File file) {
		try {
			int i = 0;
			pw = new PrintWriter(new BufferedWriter(new FileWriter(
					appWorkingFolder + "secretkey.txt")));
			byte[] fileData = Files.readAllBytes(file.toPath());

			byte[] secretkey = new byte[fileData.length];
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

	public static String toBinaryString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}

	public void steganography(byte[] key) throws IOException {

		// Once file is read convert the arraylist into an Array for the wavelet
		// transform function.
		double[] waveletInputArray = new double[dataSetFileList.size()];
		int levelSize = 0, levelCounter = 0, indexCounter = 0;
		minimumValueDouble = Double.MAX_VALUE;
		int minimumValueInt = 0;

		// Loop through the wavelet array and list.
		for (int j = 0; j < dataSetFileList.size(); j++) {
			waveletInputArray[j] = dataSetFileList.get(j);

		}

		File output = new File(appWorkingFolder + "waveletdata.txt");
		FileWriter fileWriter = new FileWriter(output);
		PrintWriter writer = new PrintWriter(fileWriter);
		// Create the two dimensional wavelet array.
		double[][] transformedList = Wavelets.transform(4, waveletInputArray);
		double[] inversedList = Wavelets.inverseTransform(4, transformedList);
		
		// Loop through transformed list and count the
		// level.
		for (int i = 0; i < transformedList.length; i++) {
			for (int k = 0; k < transformedList[i].length; k++) {
				if(transformedList[i][k] < minimumValueDouble)
				{
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
		minimumValueDouble = Math.abs(Math.ceil(minimumValueDouble)) +1;

		// TODO: Adjust coefficients
		for (int i = 0; i < transformedList.length; i++) {
			for (int k = 0; k < transformedList[i].length; k++) {
				
				transformedList[i][k]+=minimumValueDouble;
				transformedList[i][k]*=10000;
			}
		}
		
		// Create wavelet to emulate the tree based on level size and values per
		// level
		double[][] splitWavelet = new double[levelSize][512];
		// Counter based on number of total indices inserted
		int listCounter = 0;
		// Loop through tree size and add 512 values per level
		for (int j = 0; j <= levelSize; j++) {
			for (int i = 0; i < 512; i++) {

				if (listCounter == indexCounter - 1) {
					break;
				} else {
					splitWavelet[j][i] = transformedList[0][listCounter];
					listCounter++;
				}
				writer.format("Split Wavelet[%d][%d] is: %f\n", j, i,
						splitWavelet[j][i]);
			}
		}

		writer.close();

		// call the hiding method
		hideData(transformedList);

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

	public double[][] hideData(double[][] tList) throws IOException {
		int s = 1;
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

		count = 0;
      // start hiding
      for(int i = 0; i < row.length; i++) {
         for(int j = 0; j < col.length; j++) {
            if(count < cipher.length) {
               // hide data
               // convert wavelet to binary
               byte[] tmp = toByteArray(tList[row[i]][col[j]]);
               
               // concatenate cipher 8 bits into wavelet
               byte[] newValue = new byte[tmp.length + 1];
               System.arraycopy(tmp, 0, newValue, 0, tmp.length);
               newValue[tmp.length] = cipher[count];
               count++;
               
               // convert back to double
               tList[row[i]][col[j]] = toDouble(newValue);
               
               // store the matrixKey
               matrixKeyRow.add(i);
               matrixKeyCol.add(j);
               pw.print(i + "," + j);
            }
         }
      }

      pw.close();
      
      // Adjust coefficients back
      for (int i = 0; i < tList.length; i++) {
         for (int j = 0; j < tList[i].length; j++) { 
            tList[i][j] = tList[i][j] / 10000;
            tList[i][j] = tList[i][j] - minimumValueDouble;
         }
      }

		return tList;
	}

	public boolean readFileContents(File data) throws IOException {
		// init Variables.
		try {
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
