package org.rois.asvdroid.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.entryPointCreators.AndroidEntryPointConstants;

public class ExposedComponentsUtil {
	
	private static final String DIR = "python/sampleapk/";
	private static final String EXPOSED_CP = ".exposed_cp";
	private static Set<String> exposedComponents = new HashSet<String>();
	
	public static final String[] LIFECYCLEMETHODS = {
		AndroidEntryPointConstants.ACTIVITY_ONCREATE, AndroidEntryPointConstants.ACTIVITY_ONRESUME,
		AndroidEntryPointConstants.ACTIVITY_ONSTART,AndroidEntryPointConstants.SERVICE_ONCREATE,
		AndroidEntryPointConstants.SERVICE_ONSTART1,AndroidEntryPointConstants.SERVICE_ONSTART2,
		AndroidEntryPointConstants.BROADCAST_ONRECEIVE,AndroidEntryPointConstants.CONTENTPROVIDER_ONCREATE};
	
	public static List<String> getComponentLifeCycleMethods() {
		return Arrays.asList(LIFECYCLEMETHODS);
	}
	
	
	public static boolean paresExposedComponents(SetupApplication app, String packageName) throws IOException {
		if (paresExposedComponents(packageName)) return true;
		else paresExposedComponents(app);
		return true;
	}
	
	public static void paresExposedComponents(SetupApplication app) {
		Set<String> entrypointClasses = app.getEntrypointClasses();
		
		for (Iterator<String> iterator = entrypointClasses.iterator(); iterator.hasNext();) {
			ExposedComponentsUtil.exposedComponents.add((String) iterator.next());
		}
	}
	
	public static boolean paresExposedComponents(String packageName) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(DIR + packageName + EXPOSED_CP));
			String line;
			line = br.readLine();
			while(line != null) {
				ExposedComponentsUtil.exposedComponents.add(line);
				line = br.readLine();
			}
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				br.close();
		}
		return false;
	}
	
	
	public static Set<String> getExposedComponents() {
		return exposedComponents;
	}
	

}
