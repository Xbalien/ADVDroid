package org.rois.asvdroid.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.StaticBucketMap;
import org.rois.asvdroid.intent.ParseIntentTransform;
import org.rois.asvdroid.reachability.APIMisuseAnalysis;
import org.rois.asvdroid.reachability.APIReachabilityTransformer;
import org.rois.asvdroid.reachability.APISinkAnalysis;
import org.rois.asvdroid.utils.ExposedComponentsUtil;
import org.rois.asvdroid.utils.IntentExtrasUtils;
import org.rois.asvdroid.utils.ProcessManifest;
import org.rois.asvdroid.utils.VettingResultShowUtils;
import org.xmlpull.v1.XmlPullParserException;

import soot.EntryPoints;
import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.entryPointCreators.AndroidEntryPointCreator;
import soot.options.Options;

public class TestSoot {
	
	private static String ANDROID_JAR_DIR = "androidJar/android-17";
	private static String SOURCES_AND_SINKS = "SourcesAndSinks.txt";
	private static String dir;
	private static String apkName;
	private static SootMethod entryPoint;
	private static Set<String> entrypoints = null;
	private static String packageName = "";
	private static String OUT_PREFIX_STRING = "out/";
	private static final String RESULT = "_result";
	private static ProcessManifest processManifest = null;
	private static AndroidEntryPointCreator entryPointCreator;

	public static void initSoot() {
		soot.G.reset();
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_process_dir(Collections.singletonList(apkName));
		Options.v().set_android_jars(ANDROID_JAR_DIR);
		Options.v().set_whole_program(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_J);
		Options.v().set_no_output_source_file_attribute(true);
		Options.v().set_no_output_inner_classes_attribute(true);
		Options.v().setPhaseOption("cg.spark", "on");
		Options.v().ignore_resolution_errors();
		Scene.v().loadNecessaryClasses();      
	}
	
	
	public static void setSootOptions() {
		// explicitly include packages for shorter runtime:
		List<String> excludeList = new LinkedList<String>();
		excludeList.add("java.");
		excludeList.add("sun.misc.");
		excludeList.add("android.");
		excludeList.add("org.apache.");
		excludeList.add("soot.");
		excludeList.add("javax.servlet.");
		Options.v().set_exclude(excludeList);
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_output_format(Options.output_format_none);
	}
	
	
	private static void showResult() {
        IntentExtrasUtils.DumpExtrasJson2File(OUT_PREFIX_STRING + packageName, 1);
        VettingResultShowUtils.showAPIMisuseResult();
        //VettingResultShowUtils.dumpAPIMisuseResult2JsonFile(OUT_PREFIX_STRING + packageName + RESULT);
        VettingResultShowUtils.dumpAPIMisuseResult2XmlFile(OUT_PREFIX_STRING + packageName + RESULT);
	}

	private static AndroidEntryPointCreator createEntryPointCreator() {
		AndroidEntryPointCreator entryPointCreator = new AndroidEntryPointCreator
			(new ArrayList<String>(entrypoints));
		return entryPointCreator;
	}
	
	
	
	private static void printUsage() {
		System.out.println("ASVDroid (c) Xbalien @ ROIS");
		System.out.println();
		System.out.println("Incorrect arguments: [0] = apk-file, [1] = menifest-parse-config [2] = out");
	}
	
	public static void main(String[] args) {
		
		if (args.length < 3) {
			printUsage();
			return;
		} else {
			apkName = args[0];
			dir = args[1];
			OUT_PREFIX_STRING = args[2];
		}
			
		
		SetupApplication app = new SetupApplication(ANDROID_JAR_DIR, apkName);
		try {
		        //app.calculateSourcesSinksEntrypoints(SOURCES_AND_SINKS);
		        initSoot();
		        setSootOptions();
		        
		        processManifest = new ProcessManifest(apkName);
		        packageName = processManifest.getPackageName();
		        entrypoints = processManifest.getEntryPointClasses();
		        
		        entryPointCreator = createEntryPointCreator();

		        ExposedComponentsUtil.paresExposedComponents(app, packageName, dir);

		        //entryPoint = app.getEntryPointCreator().createDummyMain();
		        entryPoint = entryPointCreator.createDummyMain();
		        Options.v().set_main_class(entryPoint.getSignature());
		        Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
		    
		        //LinkJimpleByInstrument.startLink();
		        
		        //parse exposed component intent struct
		        PackManager.v().getPack("jtp").add(new Transform("jtp.ParseIntentTransform", new ParseIntentTransform()));
		        //api reachablity analysis
		        PackManager.v().getPack("jtp").add(new Transform("jtp.APIReachablityTransformer", new APIReachabilityTransformer()));
		        PackManager.v().runPacks();
		        
		        APIMisuseAnalysis.startAnalysis();
		        APISinkAnalysis.startAnalysis();

		        showResult();
		        System.out.println("###################################################");
		        System.out.println(APISinkAnalysis.getAPIReachablityPath());
		        
		    } catch (IOException e) {
		        e.printStackTrace();
		    } catch (XmlPullParserException e) {
		        e.printStackTrace();
		    }
	}
}
		