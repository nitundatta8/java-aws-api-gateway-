package awslambdatest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.lang.IllegalStateException;

public class Hello implements RequestStreamHandler {
	Gson gson = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
		LambdaLogger logger = context.getLogger();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("US-ASCII")));
		PrintWriter writer = new PrintWriter(
				new BufferedWriter(new OutputStreamWriter(outputStream, Charset.forName("US-ASCII"))));
		JSONObject responseJson = new JSONObject();
		JSONParser parser = new JSONParser();
		Response response = new Response();

		// System.out.println("File location: " + file.getAbsolutePath());
		try {

			JSONObject event = (JSONObject) parser.parse(reader);

			if (event.get("body") != null) {

				Gson gson = new Gson();
				Request request = gson.fromJson((event.get("body").toString()), Request.class); // marshaling****
				String result = "This number "+ request.id+ " is " + (isPrime(request.id) ? "" : " not") + " prime" ;
				writeDateToS3(result);

				response.setResult(result);

			}

			JSONObject responseBody = new JSONObject();
			responseBody.put("message", "New item created");

			JSONObject headerJson = new JSONObject();
			headerJson.put("x-custom-header", "my custom header value");

			responseJson.put("statusCode", 200);
			responseJson.put("headers", headerJson);
			responseJson.put("body", gson.toJson(response));

		} catch (Exception exception) {
			logger.log(exception.toString());
			responseJson.put("statusCode", 400);
			responseJson.put("exception", exception);
		}

		writer.write(responseJson.toString());
		reader.close();
		writer.close();
	}

	public void writeDateToS3(String result)

	{
		String dstBucket="";
		String dstKey="nitun-test/"+java.util.UUID.randomUUID() + ".txt" ;
		InputStream targetStream = new ByteArrayInputStream(result.getBytes());
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentLength(result.getBytes().length);
		meta.setContentType("text/plain");

		s3Client.putObject(dstBucket, dstKey, targetStream, meta);

	}

//	public String myHandler(String s, Context context) {
//		// input.subSequence(7, arg1)
//		// LambdaLogger logger = context.getLogger();
//		System.out.println("input:" + s);
//
//		StringBuilder result = new StringBuilder("");
//		int input = Integer.valueOf(s.substring(6).replace("}", ""));
//		result.append("Input Parameter " + input + " is ");
//
//		if (isPrime(input)) {
//			result.append("a PRIME");
//		} else {
//			result.append("NOT a PRIME");
//		}
//		// logger.log(result.toString());
//		return "{\"result\":\"" + result.toString() + "\"}";
//	}

	private boolean isPrime(long n) {
		if (n < 2)
			return false;
		if (n == 2 || n == 3)
			return true;
		if (n % 2 == 0 || n % 3 == 0)
			return false;
		long sqrtN = (long) Math.sqrt(n) + 1;
		for (long i = 6L; i <= sqrtN; i += 6) {
			if (n % (i - 1) == 0 || n % (i + 1) == 0)
				return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Hello s1 = new Hello();
		String s = "{\"id\":7}";
		int prime = Integer.valueOf(s.substring(6).replace("}", ""));

		//System.out.println(s1.myHandler(s, null));
		Gson gson = new GsonBuilder().create();

		HashMap<String, Double> event = gson.fromJson("{\"id\":2}", HashMap.class);

		Double val = event.get("id");
		event.put("id", (val * 7));
		System.out.println("Hello.main()" + event.get("id"));

	}

}
