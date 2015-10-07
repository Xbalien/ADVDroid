package org.rois.asvdroid.test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.rois.asvdroid.intent.ParseIntentTransform;
import org.rois.asvdroid.reachability.APIMisuseAnalysis;
import org.rois.asvdroid.reachability.APIReachabilityTransformer;
import org.rois.asvdroid.reachability.APISinkAnalysis;
import org.rois.asvdroid.utils.ExposedComponentsUtil;
import org.rois.asvdroid.utils.IntentExtrasUtils;
import org.rois.asvdroid.utils.ProcessManifest;
import org.rois.asvdroid.utils.VettingResultShowUtils;
import org.xmlpull.v1.XmlPullParserException;
import soot.PackManager;
import soot.Scene;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.infoflow.android.SetupApplication;
import soot.options.Options;

public class TestSoot {
	
	private static String ANDROID_JAR_DIR = "androidJar/android-17";
	private static String SOURCES_AND_SINKS = "SourcesAndSinks.txt";
	private static String apkName;
	private static SootMethod entryPoint;
	private static String packageName = "";
	private static final String OUT_PREFIX_STRING = "out/";
	private static final String RESULT = "_result";
	private static ProcessManifest processManifest = null;

	public static void initSoot() {
		soot.G.reset();
		Options.v().set_src_prec(Options.src_prec_apk);
		Options.v().set_process_dir(Collections.singletonList(apkName));
		Options.v().set_android_jars(ANDROID_JAR_DIR);
		Options.v().set_whole_program(true);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_J);
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

	public static void main(String[] args) {
		
		apkName = "L:/security/ubuntu12.04_share/app_audit/others/ifzu/ifzu.apk";
		apkName = "F:/LYX/Android/adt-bundle-windows/android-code/workspace/TestVulnerability/bin/TestVulnerability.apk";
		
		SetupApplication app = new SetupApplication(ANDROID_JAR_DIR, apkName);
		try {
		        app.calculateSourcesSinksEntrypoints(SOURCES_AND_SINKS);
		        
		        initSoot();
		        setSootOptions();
		        
		        processManifest = new ProcessManifest(apkName);
		        packageName = processManifest.getPackageName();

		        ExposedComponentsUtil.paresExposedComponents(app, packageName);

		        entryPoint = app.getEntryPointCreator().createDummyMain();
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
		