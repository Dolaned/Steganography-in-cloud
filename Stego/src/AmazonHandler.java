import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AmazonHandler {
	
	AmazonS3 s3Client;
	private String folderName = "encrypted-input";
	private String bucketName = "steganography-data-store";
	private static final String FOLDERSUFFIX = "/";
	String accessKey = "AKIAJNZBVXL22VUP7J7Q";
	String secretKey = "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";
	
	
	public AmazonHandler(){
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		s3Client = new AmazonS3Client(credentials);
	}

	public void pushToCloud(String bucketName, String folderName, File[] files) {

		for (int i = 0; i < files.length; i++) {
			try {

				// get the current file iteration
				File f = files[i];
				String fileName = folderName + FOLDERSUFFIX + f.getName();

				// checks if the amazon bucket name exists.
				if (s3Client.doesBucketExist(bucketName)) {
					if (s3Client.putObject(new PutObjectRequest(bucketName,
							fileName, f)) != null) {
						System.out.println("File Uploaded: " + f.getName()
								+ " File " + (i + 1) + " of " + files.length);
					}

				} else if (s3Client.createBucket(bucketName) != null) {
					if (s3Client.putObject(new PutObjectRequest(bucketName,
							fileName, f)) != null) {
						System.out.println("File Uploaded: " + f.getName()
								+ " File " + (i + 1) + " of " + files.length);
					}
				} else {
					System.out.println("Could not create bucket!");
				}

			} catch (AmazonServiceException ase) {
				printASEError(ase);
			} catch (AmazonClientException ace) {
				printACEError(ace);
			}

		}
	}

	public ArrayList<File> pullFromCloud(String bucketName, String accessKey) {

		// init amazon client.

		try {
			if (s3Client.doesBucketExist(bucketName)) {
				System.out.println(s3Client.listObjects(bucketName));
			} else {
				System.out
						.println("Your bucket doesnt Exist, choose from the following buckets.");
			}

		} catch (AmazonServiceException ase) {
			printASEError(ase);
		} catch (AmazonClientException ace) {
			printACEError(ace);
		}
		return null;

	}
	
	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + FOLDERSUFFIX, emptyContent, metadata);
		// send request to S3 to create folder
		client.putObject(putObjectRequest);
	}
	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public static void deleteFolder(String bucketName, String folderName, AmazonS3 client) {
		java.util.List<S3ObjectSummary> fileList = 
				client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) {
			client.deleteObject(bucketName, file.getKey());
		}
		client.deleteObject(bucketName, folderName);
	}
	
	public AmazonS3 getS3Client() {
		return s3Client;
	}

	public String getFolderName() {
		return folderName;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void printASEError(AmazonServiceException ase){
		System.out.println("Caught an AmazonServiceException, which "
				+ "means your request made it "
				+ "to Amazon S3, but was rejected with an error response"
				+ " for some reason.");
		System.out.println("Error Message:    " + ase.getMessage());
		System.out.println("HTTP Status Code: " + ase.getStatusCode());
		System.out.println("AWS Error Code:   " + ase.getErrorCode());
		System.out.println("Error Type:       " + ase.getErrorType());
		System.out.println("Request ID:       " + ase.getRequestId());
	}
	public void printACEError(AmazonClientException ace){
		System.out.println("Caught an AmazonClientException, which "
				+ "means the client encountered "
				+ "an internal error while trying to "
				+ "communicate with S3, "
				+ "such as not being able to access the network.");
		System.out.println("Error Message: " + ace.getMessage());
	}
}
