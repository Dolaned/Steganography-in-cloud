import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	private static String bucketName = "stegan";
	private static String keyName = "*** Provide key ***";
	public static AmazonHandler aHandler = new AmazonHandler();
	public static Steganography steg = new Steganography();
	public static Scanner sc = new Scanner(System.in);

	public static void main(String args[]) throws IOException {

		// scanner for menu input

		// string menu input
		String menuInput = new String();

		while (!menuInput.contains("X")) {
			// print menu and pull scanner input
			printMenu();
			menuInput = sc.nextLine().toUpperCase();

			if (menuInput.length() == 1 && !menuInput.contains("X")) {
				switch (menuInput) {

				case "1":
					try {
						pushToCloud();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "2":
					pullFromCloud();
					break;
				case "3":
					try {
						checkPrd(Steganography.getAppFolder());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				default:
					System.out.println("Enter Valid Input!");
				}
			} else {
				// if the above if statement is not run and input does not
				// contain
				// and Upper-case X then print and error.
				if (!menuInput.contains("X")) {
					System.out.println("Enter a valid input");
				} else {
					// Where i will write to the file as the program Exits.
					System.out.println("Program closed");
				}

			}
		}
	}

	private static void checkPrd(String dir) throws IOException {
		
		//pass in the folder working directory and creat files from the original and stego data.
		File stegData = new File(dir + "/stegodata.txt");
		File orignalData = new File(dir + "/originaldata.txt");
		//create two array lists for the data.
		ArrayList<Double> stegDataList = new ArrayList<Double>(); 
		ArrayList<Double> origDataList = new ArrayList<Double>();
		
		//check if the files exist.
		if (stegData.exists() && orignalData.exists()) {
			try {
				//Read stegodata to array list
				Scanner stegoFileReader = new Scanner(stegData);
				// fileReader.useDelimiter("\n|\t|,");
				while (stegoFileReader.hasNextLine()) {
					stegDataList.add(Double.parseDouble(stegoFileReader.nextLine()));
				}
				stegoFileReader.close();
				
				//Read orignalData to array list.
				Scanner orginalFileReader = new Scanner(orignalData);
				while (orginalFileReader.hasNextLine()) {
					origDataList.add(Double.parseDouble(orginalFileReader.nextLine()));
				}
				orginalFileReader.close();
				
				//call the actual prd  function
				calculatePrd(stegDataList, origDataList);
				
				
			} catch (FileNotFoundException e) {
				System.out.println("Data file not found!");
			}
		}else{
			if(!stegData.exists()){
				System.out.println("Stego Data File Not Found!");
			}else if(!orignalData.exists()){
				System.out.println("Original Data File Not Found!");
			}else{
				System.out.println("Some Other Error Occured Or You Must Run Option 1 or 2 First!!");
			}
		}

	}

	private static void calculatePrd(ArrayList<Double> stegDataList, ArrayList<Double> origDataList) {
		
		double m=0;
		double u=0;
		double x;
		
		//Loop through the size of the double array list, the two of them should be the same length.
		for (int i=0 ; i < stegDataList.size(); i++)
		{
			m += Math.pow(stegDataList.get(i) - origDataList.get(i), 2);
			
			u += Math.pow(stegDataList.get(i), 2);
		}
		
		x = (Math.pow(m / u, 0.5) *100);
		System.out.println("The Distortion is: "+ x +" %\n");
		
	}

	private static void pushToCloud() throws IOException {

		String folderName;
		String privateData;

		// input file path folder.
		System.out.println("Input the FULL File Path: ");
		folderName = sc.nextLine();
		System.out
				.println("Input Secret Data CSV filename Including Extension: ");
		privateData = sc.nextLine();

		// call steganography function that returns an array of files.
		File folder = new File(folderName);
		File privateDataFile = new File(privateData);

		// set file paths.
		steg.setFolderPath(folder);
		steg.setPrivateFile(privateDataFile);

		// call secret data encryption method.
		if(steg.encrypt(privateDataFile)){
		   steg.stegStart(steg.getFolderPath(), steg.getPrivateDataFile());
		}

		// if(aHandler.pushToCloud(bucketName, keyName, null)){
		// System.out.println("File Uploaded");
		// }

	}

	private static boolean pullFromCloud() throws IOException {
	   steg.extract();
		return true;

	}

	private static void printMenu() {

		System.out.println("----------------------------------------------");
		System.out.println("Welcome To WuTangCoders Steganography project!");
		System.out.println("\n");
		System.out.println("Please Choose From The Follow Options:");
		System.out.println("1. Input, Encrypt File And Push To Cloud.");
		System.out.println("2. Pull From Cloud And Decrypt.");
		System.out
				.println("3. Check prd of the stego file against original data");
		System.out.println("X. Exit");
		System.out.println("\n");
		System.out.println("Please Enter An Option: ");
	}
}
