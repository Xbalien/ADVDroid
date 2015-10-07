package org.rois.asvdroid.reachability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.rois.asvdroid.utils.ReachableMethodsUtil;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.callgraph.Sources;

public class APISinkAnalysis {
	
	private static CallGraph cg;
	private static Iterator<String> crmIt;

	private static HashMap<String, String> APISinkVettingResult = new HashMap<String, String>();
	private static HashMap<String, String> APISinkVettingResultDetail = new HashMap<String, String>();
	private static HashMap<String, String> APISinkReachablityPath = new HashMap<String, String>();

	public static void startAnalysis() {
		
		cg = Scene.v().getCallGraph();	
		crmIt = ReachableMethodsUtil.getCandidateAPISinkReachableMethods().keySet().iterator();
		
		while (crmIt.hasNext()) {
			String key = crmIt.next();
			
			List<String> list = ReachableMethodsUtil.getCandidateAPISinkReachableMethods().get(key);
			Iterator<String> listIt = list.iterator();
			
			while (listIt.hasNext()) {
				String path = "";
				String signatrue = (String) listIt.next();
				SootMethod preMethod = Scene.v().getMethod(signatrue);
				ReachableMethods rm = Scene.v().getReachableMethods();
				
				if( rm.contains( preMethod ) ){
					
					if (preMethod.hasActiveBody()) {
						APISinkVettingResult.put(key, preMethod.getSignature().toString());
						APISinkVettingResultDetail.put(key, preMethod.getActiveBody().toString());
					}
					
					Iterator<?> sources = new Sources(cg.edgesInto(preMethod));

					while( sources.hasNext() ) {
						SootMethod sf = (SootMethod) sources.next();
						path += sf.toString() + "<-----";
					}
					APISinkReachablityPath.put(key, path);
				}
			}
		}
	}
	
	
	public static HashMap<String, String> getAPISinkVettingResult() {
		return APISinkVettingResult;
	}
	
	public static HashMap<String, String> getAPIReachablityPath() {
		return APISinkReachablityPath;
	}
	
	public static HashMap<String, String> getAPISinkVettingResultDetail() {
		return APISinkVettingResultDetail;
	}
	
	/*public static void getResult() {
		crmIt = ReachableMethodsUtil.getCandidateReachableMethods().keySet().iterator();
		
		while (crmIt.hasNext()) {
			System.out.println("------------------------------>");
			String key = crmIt.next();
			System.out.println(key);
			List<String> list = ReachableMethodsUtil.getCandidateReachableMethods().get(key);
			Iterator<String> listIt = list.iterator();
			while (listIt.hasNext()) {
				String string = (String) listIt.next();
				System.out.println(string);
			}
			
		}
	}*/

}
