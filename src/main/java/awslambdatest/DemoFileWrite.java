package awslambdatest;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;

public class DemoFileWrite {
	 public static void main(String[] args) {
		    try {
		      FileWriter myWriter = new FileWriter("demoFile.txt");
		      File file = new File("demoFile.txt");
		      myWriter.write("Files in Java might be tricky, but it is fun enough!");
		      myWriter.close();
		      System.out.println("Successfully wrote to the file.");
		      System.out.println("File location: "+ file.getAbsolutePath());
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
		  }
}
