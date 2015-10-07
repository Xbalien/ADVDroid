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

public class APIMisuseAnalysis {
	
	private static CallGraph cg;
	private static Iterator<String> crmIt;
	//api miuse result
	private static HashMap<String, String> APIMisuseVettingResult = new HashMap<String, String>();
	//api miuse result include method detail
	private static HashMap<String, String> APIMisuseVettingResultDetail = new HashMap<String, String>();
	//api reachable path
	private static HashMap<String, String> APIMisuseReachablityPath = new HashMap<String, String>();

	public static void startAnalysis() {
		
		cg = Scene.v().getCallGraph();	
		crmIt = ReachableMethodsUtil.getCandidateAPIMisuseReachableMethods().keySet().iterator();
		
		while (crmIt.hasNext()) {
			String key = crmIt.next();
			System.out.println(key);
			List<String> list = ReachableMethodsUtil.getCandidateAPIMisuseReachableMethods().get(key);
			Iterator<String> listIt = list.iterator();
			System.out.println(list);
			
			while (listIt.hasNext()) {
				String path = "";
				String signatrue = (String) listIt.next();
				SootMethod preMethod = Scene.v().getMethod(signatrue);
				ReachableMethods rm = Scene.v().getReachableMethods();
				
				if( rm.contains( preMethod ) ){
					
					if (preMethod.hasActiveBody()) {
						APIMisuseVettingResult.put(key, preMethod.getSignature().toString());
						APIMisuseVettingResultDetail.put(key, preMethod.getActiveBody().toString());
					}
					
					Iterator<?> sources = new Sources(cg.edgesInto(preMethod));

					while( sources.hasNext() ) {
						SootMethod sf = (SootMethod) sources.next();
						path += sf.toString() + "<-----";
					}
					APIMisuseReachablityPath.put(key, path);
				}
			}
		}
	}
	
	
	public static HashMap<String, String> getAPIMisuseVettingResult() {
		return APIMisuseVettingResult;
	}
	
	public static HashMap<String, String> getAPIReachablityPath() {
		return APIMisuseReachablityPath;
	}
	
	public static HashMap<String, String> getAPIMisuseVettingResultDetail() {
		return APIMisuseVettingResultDetail;
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
