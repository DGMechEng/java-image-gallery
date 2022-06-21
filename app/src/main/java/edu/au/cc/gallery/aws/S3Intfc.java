
package edu.au.cc.gallery.aws;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

//import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import java.util.UUID;

public class S3Intfc {
    private static final Region region = Region.US_WEST_2;
    private S3Client client;
    private static final String bucketName = "m5-images-bucket";
    
    public void connect() {
	client = S3Client.builder().region(region).build();
    }

    public void createBucket(String bucketName){
    	CreateBucketRequest createBucketRequest = CreateBucketRequest
    	    .builder()
    	    .bucket(bucketName)
    	    .createBucketConfiguration(CreateBucketConfiguration.builder()
    				       .locationConstraint(region.id())
    				       .build())
    	    .build();
    	client.createBucket(createBucketRequest);
     }

    public String putObject(String bucketName, String key, String value) {
	PutObjectRequest por = PutObjectRequest.builder()
	    .bucket(bucketName)
	    .key(key)
	    .build();
	client.putObject(por, RequestBody.fromString(value));
	return "";
    }

    public void upload(PutObjectRequest por, Path picFile) {
	client.putObject(por, picFile);
    }
    
    public static void  toS3(Path picFile, String uuid) {
	String fileObjKeyName = uuid; //for now, may need to create an index of some sort
	//	String bucketName = "m5-images-bucket";
  	S3Intfc s3 = new S3Intfc();
	File f = new File(picFile.toString());
	s3.connect();

	//	s3.putObject(bucketName,"image" , picFile);
	try {

	    PutObjectRequest por = PutObjectRequest.builder()
		.bucket(bucketName)
		.key(fileObjKeyName)
		.build();
		   
	    s3.upload(por, picFile);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    
    }
    public static void fromS3(String uuid) {

        String key = "uuid";
	S3Intfc s3 = new S3Intfc();
	s3.connect();
	S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
	
        try {
	    //            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
	    //      .withRegion(region)
	    //      .withCredentials(new ProfileCredentialsProvider())
	    //      .build();
	    // Get an object and print its contents.
            System.out.println("Downloading an object");
	    //
	    //
	    //what is actually calling getObject????
	    //
	    //
	     
            fullObject = s3.client.getObject(new GetObjectRequest(bucketName, key));
	    final BufferedInputStream i = new BufferedInputStream(fullObject.getObjectContent());
	    InputStream objectData = fullObject.getObjectContent();
	    Files.copy(objectData, new File("/home/ec2-user/java-image-gallery/app/src/main/java/edu/au/cc/gallery/download/" + uuid).toPath());
	    objectData.close();
	    //   System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            //System.out.println("Content: ");
            //displayTextInputStream(fullObject.getObjectContent());

	    } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if (fullObject != null) {
                fullObject.close();
            }
            if (objectPortion != null) {
                objectPortion.close();
            }
            if (headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
    }
}
