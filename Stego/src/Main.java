import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	private static String bucketName     = "stegan";
	private static String keyName        = "*** Provide key ***";
	public static AmazonHandler aHandler = new AmazonHandler();
	public static Steganography steg = new Steganography();
	public static Scanner sc = new Scanner(System.in);
	public static void main(String args[]) {

		String menuInput = "";
		
		
		while(!menuInput.contains("X")){
			
			printMenu();
			menuInput = sc.nextLine().toUpperCase();
			
			switch(menuInput){
			
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
			}
		}
		

		sc.close();

	}
	
	
	private static void pushToCloud() throws IOException{
		
		String folderName;
		String privateData;
		Scanner sc = new Scanner(System.in);
		
		//input file path folder.
		System.out.println("Input the FULL File Path: ");
		folderName = sc.nextLine();
		System.out.println("Input Secret Data CSV filename Including Extension: ");
		privateData = sc.nextLine();
		
		//call steganography function that returns an array of files.
		File folder = new File(folderName);
		File privateDataFile = new File(privateData);
		
		// set file paths.
		steg.setFolderPath(folder);
		steg.setPrivateFile(privateDataFile);
		
		//call secret data encryption method.
		//if(steg.encrypt(privateDataFile)){
			steg.stegStart(steg.getFolderPath(),steg.getPrivateDataFile());
		//}
		
		
		
		//if(aHandler.pushToCloud(bucketName, keyName, null)){
			//System.out.println("File Uploaded");
		//}
		
		sc.close();
	}
	
	private static boolean pullFromCloud(){
		
		return true;
		
	}
	
	private static void printMenu(){
		
		System.out.println("----------------------------------------------");
		System.out.println("Welcome To WuTangCoders Steganography project!");
		System.out.println("\n");
		System.out.println("Please Choose From The Follow Options:");
		System.out.println("1. Input, Encrypt File And Push To Cloud.");
		System.out.println("2. Pull From Cloud And Decrypt.");
		System.out.println("X. Exit");
		System.out.println("\n");
		System.out.println("Please Enter An Option: ");
	}
}
