
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
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import org.apache.commons.io.FileUtils;

import java.util.UUID;

public class S3download {
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


    public File download(String uuid) {
	File f;
	//	GetObjectRequest
	S3Object s3object = client.getObject(GetObjectRequest(bucketName, uuid));
	S3ObjectInputStream inputStream = s3object.getObjectContent();
	f = new File("/home/ec2-user/java-image-gallery/app/src/main/java/edu/au/cc/gallery/download/" + uuid);
	FileUtils.copyInputStreamToFile(inputStream,f);
	return f;
    }

}
