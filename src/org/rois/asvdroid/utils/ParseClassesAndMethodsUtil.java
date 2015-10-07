package org.rois.asvdroid.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;



public class ParseClassesAndMethodsUtil {
	
	private static final String INTENT_GET_EXTRAS_METHODS = "config/IntentGetExtraMethods.txt";
	private static final String API_MISUSE_METHODS = "config/APIMisuseMethods.txt";
	private static final String ANDROID_CLASSES = "config/AndroidClassPrefixes.list";
	private static final String API_SINK_METHODS = "config/APISinkMethods.txt";
	
	private static HashSet<String> APISinkMethods = new HashSet<String>();
	private static HashSet<String> intentExtrasMethods = new HashSet<String>();
	private static HashSet<String> APIMisuseMethods = new HashSet<String>();
	private static HashSet<String> androidClassPrefixes = new HashSet<String>();
	
	static {
		loadAndParseMethods();
	}
	
	public static void loadAndParseMethods() {
		try {
			parseIntentExtrasMethods();
			parseAPIMisuseMethods();
			parseAPISinkMethods();
			loadAndroidClasses();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isAndroidClass(String clsName) {
		
		for (String prefix : androidClassPrefixes) {
			if (clsName.startsWith(prefix)) {
				System.out.println(clsName);
				return true;
			}
		}
		return false;
	}
	
	public static void parseIntentExtrasMethods() throws IOException {
		readFile(INTENT_GET_EXTRAS_METHODS);
	}
	
	public static void parseAPIMisuseMethods() throws IOException {
		readFile(API_MISUSE_METHODS);
	}
	
	public static void parseAPISinkMethods() throws IOException {
		readFile(API_SINK_METHODS);
	}
	
	public static void loadAndroidClasses() throws IOException {
		readFile(ANDROID_CLASSES);
	}

	public static void fromFile(String fileName) throws IOException {
		readFile(fileName);
	}
	
	public static HashSet<String> getExtrasMethods(){
		return intentExtrasMethods;
	}
	
	public static HashSet<String> getAPIMisuseMethods(){
		return APIMisuseMethods;
	}
	
	public static HashSet<String> getAPISinkMethods(){
		return APISinkMethods;
	}
	
	public static HashSet<String> getAndroidClasses() {
		return androidClassPrefixes;
	}
	
	
	private static void readFile(String fileName) throws IOException{
		String line;
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(fileName);
			br = new BufferedReader(fr);
			while((line = br.readLine()) != null){
				
				if (line.isEmpty() || line.startsWith("%"))
					continue;
				if (fileName.equals(INTENT_GET_EXTRAS_METHODS)){
				    intentExtrasMethods.add(line);
				} else if (fileName.equals(API_MISUSE_METHODS)) {
				    APIMisuseMethods.add(line);
				} else if (fileName.equals(ANDROID_CLASSES)) {
					androidClassPrefixes.add(line);
				} else if (fileName.equals(API_SINK_METHODS)){
					APISinkMethods.add(line);
				}
			}
			
		    
		}
		finally {
			if (br != null)
				br.close();
			if (fr != null)
				fr.close();
		}
	}

}

	

