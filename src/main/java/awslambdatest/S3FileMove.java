
package awslambdatest;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3FileMove implements RequestHandler<S3Event, String> {

	public String handleRequest(S3Event s3event, Context context) {
		try {
			S3EventNotificationRecord record = s3event.getRecords().get(0);

			String srcBucket = record.getS3().getBucket().getName();

			// Object key may have spaces or unicode non-ASCII characters.
			String srcKey = record.getS3().getObject().getUrlDecodedKey();
			String dstBucket = "nituntest1";
			String dstKey = "postprocess/" + java.util.UUID.randomUUID() + ".txt";
			if (srcBucket.equals(dstBucket)) {
				System.out.println("Key should be differnt");
				return "";
			}

			// Download the image from S3 into a stream
			AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
			S3Object s3Object = s3Client.getObject(new GetObjectRequest(srcBucket, srcKey));
			InputStream objectData = s3Object.getObjectContent();

			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentType("text/plain");
			
			 // Uploading to S3 destination bucket
            System.out.println("Writing to: " + dstBucket + "/" + dstKey);
            try {
                s3Client.putObject(dstBucket, dstKey,objectData, meta);
            }
            catch(AmazonServiceException e)
            {
                System.err.println(e.getErrorMessage());
                System.exit(1);
            }

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return "Ok";
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
