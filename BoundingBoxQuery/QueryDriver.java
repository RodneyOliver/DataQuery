
/*------------------------------------------------------------------------------
*
*  Class Name:  QueryDriver.java
*  Purpose:     ACME Company needs an algorithm that can quickly return points
*  that are located in a bounding box of a dataset.  The dataset potentially has
*  millions of records with x and y coordinates and a value.  The values need to
*  be retrieved for a given bounding box.
*
*  AUTHOR:      Rodney W. Oliver
*  Date:        12/07/2015
*  Version:     1.0
*
*  DETAIL:      Input file:  sample_data.csv  signature<float float float>
*               Output file: output_sample_data.csv
*               This program will accept 4 args on the command line.
*  USAGE:  	    args[0] = Minimum X value of bounding box
*               args[1] = Maximum X value of bounding box
*               args[2] = Minimum Y value of bounding box
*               args[3] = Maximum Y value of bounding box
*               =========================================
*               Once acheived, the algorithm will take these values and compare
*  them against the *.csv file.  If any results are found within the parameters
*  of this search, they will be collected and written to the named outfile.
*  ============================================================================
*  Updates:     N/A (reserved for any modifications)
*  ============================================================================
*  In Progress: 1.1  Create Error Checking (Priority 1)(Add Versioning(Git)
*               1.2  Optimize / Create modular components
*               1.3  Update smoother IO transitions / (File IO, Args)
*               1.4  Add potential Multithreaded programming for dual core
*               1.5  Update / modularize (readLines) method to allow for a 1GB
*                    read, (reading file in chunks, (small magnifying glass
*                    over the data set)).If filesize > 1.5GB..(checkFirstChunk)
*               1.6  Add a method to calculate "cost" time for each component
*               1.7  Add an extra arg to command line, allowing the ability to
*                    turn flags on or off.  It will be args[4], and if this is
*                    null at runtime, then it will default to an "off" state.
*                    let's use "flags" as the keyword to enable this method.
------------------------------------------------------------------------------*/

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.io.File;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.io.*;


public class QueryDriver
{
/*-----------------------------------------------------------------------------
*  Function:  readLines(String file_name)
*
*  Purpose:   Instantiates Scanner to iterate through IO, Instantiates a HashMap
*             which is formatted, filled and then returned.
*
*  Arguments: file_name - current path for incoming data
*  Throws:    Throws exception for file not FileNotFound
------------------------------------------------------------------------------*/
private static HashMap<Point2D, Float> readLines(String file_name) 
		throws FileNotFoundException
{
	Scanner scanner = new Scanner(new FileReader(file_name));

	HashMap<Point2D, Float> returnMap = new HashMap<Point2D, Float>();

	while(scanner.hasNextLine())
  {
		String[] point = scanner.nextLine().split(",");

		// casting from strings to floats from string array []point
		float float1 = Float.parseFloat(point[0]);
		float float2 = Float.parseFloat(point[1]);
		float float3 = Float.parseFloat(point[2]);

		// instantiates a new Point2D object which takes in two floats
		Point2D pointXandY = new Point2D.Float(float1, float2);
		float value = float3;
		// passing 2 2D point values in as the hash key, alongside associated 
		// value.
		
    returnMap.put(pointXandY, value);
  }
	scanner.close();
	return returnMap;
}

/*-----------------------------------------------------------------------------
*  Function:  captureAndWriteToFile(double xResult, double yResult, 
*             float coordValue)
*
*  Purpose:   This function passes in captured results found by initial args as
*             the bound for this bounding box.  This function will perform
*             writes to a specified file for detected output.  Each call to this
*             function will also append any written data to the outfile as calls
*             are made.
*
*  Arguments: double xResult - value from X coordinate data entry
*             double yResult - value from Y coordinate data entry
*             float coordValue - value assigned to (X,Y) coordinate
*  Throws:    Throws IOException
------------------------------------------------------------------------------*/
private static void captureAndWriteToFile(double xResult, double yResult, 
		float coordValue) throws IOException
{
  String outputFileName = "/Users/RodneyOliver/Desktop/output_sample_data.csv";

	File out_f = new File(outputFileName);

	try
	{
	  if(!out_f.exists())
	  {
		  System.out.println("No Outfile Detected, Creating...");
		  out_f.createNewFile();
	  }

	  FileWriter fw = new FileWriter(out_f,true);
	  BufferedWriter bw = new BufferedWriter(fw);
		// Concatenation of formatted write
	  bw.write(String.format("%6.2f",xResult) + ", " +
		String.format("%6.2f",yResult) + ", " + 
			  String.format("%6.2f",coordValue));

		bw.newLine();
	  bw.close();
	}
	catch(IOException e)
	{
		System.out.println("Could not Create File");
	}
}
/*-----------------------------------------------------------------------------
*  Function:  main(String[] args)
*
*  Arguments: args - accepts 4 command line arguments
*  Throws:    Throws IOException
------------------------------------------------------------------------------*/
  public static void main(String[] args) throws IOException
  {
		// We are looking for 4 arguments on the command line
		if(args.length  != 4)
    {
      System.out.println("Usage:  Enter  MIN-X  MAX-X  MIN-Y  MAX-Y For Args");
      System.exit(1);
    }

	// Assign and cast string Args from command line to floats
	float xmin = Float.parseFloat(args[0]);
	float xmax = Float.parseFloat(args[1]);
	float ymin = Float.parseFloat(args[2]);
    float ymax = Float.parseFloat(args[3]);


	String outputFileName="/Users/RodneyOliver/Desktop/output_sample_data.csv";
	File output_file = new File(outputFileName);

	// Before testing, prepping for file IO to ensure no duplicate writes
	if(output_file.exists());
	{
		System.out.println("File exists, Removing...");
		output_file.delete();
	}

	String inputFileName = "/Users/RodneyOliver/Desktop/sample_data.csv";

	HashMap<Point2D, Float> myMap = readLines(inputFileName);

  File in_f = new File(inputFileName);
	if(in_f.exists())
  {
	  for (Map.Entry<Point2D, Float> entry : myMap.entrySet())
	  {
      float tempx = (float) entry.getKey().getX();
		  float tempy = (float) entry.getKey().getY();

		  if ((tempx > xmin && tempx < xmax) && (tempy > ymin && tempy < ymax))
			{
		    try
		    {
		    	captureAndWriteToFile(entry.getKey().getX(), 
		    			              entry.getKey().getY(), 
		    			              entry.getValue());
		    }
		    catch(IOException e)
		    {
		      System.out.println(e.getMessage());
		    }
		  }
	  }
  }// if in_f exists
  System.out.println("Query Complete");
}

}

