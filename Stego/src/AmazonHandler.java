import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
public class AmazonHandler {

		public void pushToCloud(String bucketName, String accessKey, File[] files){
			
	        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
	        for(int i = 0; i < files.length; i++){
		        try {
		        	//print out the name of the file and bucket to upload too.
		            System.out.println("Uploading: " + files[i].getName() + " to S3 bucket: " + bucketName +" \n");
		            // get the current file iteration
		            File f = files[i];
		            
		            //checks if the amazon bucket name exists.
		            if(s3client.doesBucketExist(bucketName)){
		            	if(s3client.putObject(new PutObjectRequest(
       		                 bucketName, accessKey, f)) != null){
		            		System.out.println("File Uploaded: " + f.getName() + " File "+ (i+1) +" of "+ files.length);
		            	}
		            	
		            }else if(s3client.createBucket(bucketName) != null){
		            	if(s3client.putObject(new PutObjectRequest(
	       		                 bucketName, accessKey, f)) != null){
		            		System.out.println("File Uploaded: "+ f.getName()+ " File "+ (i+1) +" of "+ files.length);
		            	}
		            }else{
		            	System.out.println("Could not create bucket!");
		            }

	
		         } catch (AmazonServiceException ase) {
		            System.out.println("Caught an AmazonServiceException, which " +
		            		"means your request made it " +
		                    "to Amazon S3, but was rejected with an error response" +
		                    " for some reason.");
		            System.out.println("Error Message:    " + ase.getMessage());
		            System.out.println("HTTP Status Code: " + ase.getStatusCode());
		            System.out.println("AWS Error Code:   " + ase.getErrorCode());
		            System.out.println("Error Type:       " + ase.getErrorType());
		            System.out.println("Request ID:       " + ase.getRequestId());
		        } catch (AmazonClientException ace) {
		            System.out.println("Caught an AmazonClientException, which " +
		            		"means the client encountered " +
		                    "an internal error while trying to " +
		                    "communicate with S3, " +
		                    "such as not being able to access the network.");
		            System.out.println("Error Message: " + ace.getMessage());
		        }
				
	        }
		}
		
		public ArrayList<File> pullFromCloud(String bucketName, String accessKey){
			
			//init amazon client.
	        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
	        
	        
	        try {
	        	if(s3client.doesBucketExist(bucketName)){
	        		System.out.println(s3client.listObjects(bucketName));
	        	}else{
	        		System.out.println("Your bucket doesnt Exist, choose from the following buckets.");
	        	}

	         } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which " +
	            		"means your request made it " +
	                    "to Amazon S3, but was rejected with an error response" +
	                    " for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which " +
	            		"means the client encountered " +
	                    "an internal error while trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }
			return null;
			
		}
}
