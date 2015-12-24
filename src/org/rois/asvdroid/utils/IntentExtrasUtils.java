package org.rois.asvdroid.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONObject;

public class IntentExtrasUtils {

	private static HashMap<String, HashMap<String, Set<String>>> intentGetExtrasData; 
	private static HashMap<String, HashMap<String, Set<String>>> intentPutExtrasData;
	
	private static JSONObject getExtrasJsonData;
	private static JSONObject putExtrasJsonData;
	
	
	//Component has a struct for intent parse
	static {
		intentGetExtrasData = new HashMap<String, HashMap<String, Set<String>>>();
		intentPutExtrasData = new HashMap<String, HashMap<String, Set<String>>>();
	}
	
	public static void addStrings(String componentName, Set<String> strings){
		
	}
	
	public static void parseExtras(HashMap<String, Set<String>> extraKeys, String methodName, String extraKey) {
		if (extraKeys.containsKey(methodName)) {
			Set<String> set = extraKeys.get(methodName);
			set.add(extraKey);
			extraKeys.put(methodName, set);
		} else {
			Set<String> set =new HashSet<String>();
			set.add(extraKey);
			extraKeys.put(methodName, set);
		}
	}
	
	public static void parseGetExtras(HashMap<String, Set<String>> getExtraKeys, String methodName, String extraKey) {
		if (getExtraKeys.containsKey(methodName)) {
			Set<String> set = getExtraKeys.get(methodName);
			set.add(extraKey);
			getExtraKeys.put(methodName, set);
		} else {
			Set<String> set =new HashSet<String>();
			set.add(extraKey);
			getExtraKeys.put(methodName, set);
		}
	}
	
	public static void parsePutExtras(HashMap<String, Set<String>> putExtraKeys, String methodName, String extraKey) {
		if (putExtraKeys.containsKey(methodName)) {
			Set<String> set = putExtraKeys.get(methodName);
			set.add(extraKey);
			putExtraKeys.put(methodName, set);
		} else {
			Set<String> set = new HashSet<String>();
			set.add(extraKey);
			putExtraKeys.put(methodName, set);
		}
	}
	
	public static void putExtras(String componentName, HashMap<String, Set<String>> getExtraKeys, int type){
		
		HashMap<String, HashMap<String, Set<String>>> extrasData = null;
		
		if (type == 1) {
			extrasData = intentGetExtrasData;
		} else if (type == 2) {
			extrasData = intentPutExtrasData;
		}
		
		if (extrasData.containsKey(componentName)) {
			HashMap<String, Set<String>> hashMap = extrasData.get(componentName);
			for (String key : getExtraKeys.keySet()){
				if (hashMap.containsKey(key)) {
					hashMap.get(key).addAll(getExtraKeys.get(key));
				}
			}
			extrasData.put(componentName, hashMap);
		}
		else extrasData.put(componentName, getExtraKeys);
	}
	
	public static void putPutExtras(String componentName, HashMap<String, Set<String>> putExtraKeys){
		intentPutExtrasData.put(componentName, putExtraKeys);
	}
	
	public static HashMap<String, HashMap<String, Set<String>>> getGetExtras(){
		return intentGetExtrasData;
	}
	
	public static HashMap<String, HashMap<String, Set<String>>> getPutExtras(){
		return intentPutExtrasData;
	}
	
	public static void extras2Json() {
		getExtrasJsonData = JSONObject.fromObject(intentGetExtrasData);
		putExtrasJsonData = JSONObject.fromObject(intentPutExtrasData);
	}
	
	public static JSONObject getGetExtras2Json(){
		return JSONObject.fromObject(intentGetExtrasData);
	}
	
	public static JSONObject getPutExtras2Json(){
		return JSONObject.fromObject(intentPutExtrasData);
	}
	
	public static void DumpExtrasJson2File(String fileName, int type){
		
		extras2Json();
		
		File file = new File(fileName + ".json");
		PrintStream outputStream = null;
		
		try {
			outputStream = new PrintStream(new FileOutputStream(file));
			if (type == 1) {
				outputStream.print(getExtrasJsonData);
			} else {
				outputStream.print(putExtrasJsonData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	
}
