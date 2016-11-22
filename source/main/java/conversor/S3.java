package conversor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class S3 {

	// public static void main (String[] args) throws IOException{
	public void upload(String bucket, String name, String path) throws IOException {
		String existingBucketName = bucket;
		String keyName = name;
		String filePath = path;

		//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

		// Create a list of UploadPartResponse objects. You get one of these
		// for each part upload.
		List<PartETag> partETags = new ArrayList<PartETag>();

		// Step 1: Initialize.
		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(existingBucketName, keyName);
		InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);

		File file = new File(filePath);
		long contentLength = file.length();
		long partSize = 5242880; // Set part size to 5 MB.

		System.out.print("Autenticacao realizada com sucesso");
		try {
			// Step 2: Upload parts.
			long filePosition = 0;
			for (int i = 1; filePosition < contentLength; i++) {
				// Last part can be less than 5 MB. Adjust part size.
				partSize = Math.min(partSize, (contentLength - filePosition));

				// Create request to upload a part.
				UploadPartRequest uploadRequest = new UploadPartRequest().withBucketName(existingBucketName)
						.withKey(keyName).withUploadId(initResponse.getUploadId()).withPartNumber(i)
						.withFileOffset(filePosition).withFile(file).withPartSize(partSize);

				// Upload part and add response to our list.
				partETags.add(s3Client.uploadPart(uploadRequest).getPartETag());

				filePosition += partSize;
			}

			System.out.print("Upload realizado com sucesso");

			// Step 3: Complete.
			CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(existingBucketName, keyName,
					initResponse.getUploadId(), partETags);

			s3Client.completeMultipartUpload(compRequest);
			System.out.print("Fim");

		} catch (Exception e) {
			s3Client.abortMultipartUpload(
					new AbortMultipartUploadRequest(existingBucketName, keyName, initResponse.getUploadId()));
		}
	}

	public void download(String file) throws IOException {
		String bucketName = "lucasmaiasilva";
		String key = file;
		//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

		try {
			
			System.out.println("Checa se o objeto existe");
		    while(!s3Client.doesObjectExist(bucketName, key)){
		    	
		    }
			System.out.println("Downloading an object");

		    S3Object s3object = s3Client.getObject(new GetObjectRequest(bucketName, key));
			saveToFile(s3object.getObjectContent(), "/var/www/html/videos/video.mp4");

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which" + " means your request made it "
					+ "to Amazon S3, but was rejected with an error response" + " for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means" + " the client encountered "
					+ "an internal error while trying to " + "communicate with S3, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	private static void saveToFile(InputStream inStream, String target) throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		File file = new File(target);
		file.setExecutable(true);
		out = new FileOutputStream(file);
		
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}
}
