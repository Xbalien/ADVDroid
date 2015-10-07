package org.rois.asvdroid.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReachableMethodsUtil {
	
	private static HashMap<String, List<String>> reachableHashMap = new HashMap<String, List<String>>();
	private static HashMap<String, List<String>> candidateAPIMisuseReachableHashMap = new HashMap<String, List<String>>();
	private static HashMap<String, List<String>> candidateAPISinkReachableHashMap = new HashMap<String, List<String>>();
	
	public static HashMap<String, List<String>> getReachableMethods(){
		return reachableHashMap;
	}
	
	public static HashMap<String, List<String>> getCandidateAPIMisuseReachableMethods(){
		return candidateAPIMisuseReachableHashMap;
	}
	
	public static HashMap<String, List<String>> getCandidateAPISinkReachableMethods(){
		return candidateAPISinkReachableHashMap;
	}
	
	public static void putCandidateReachable(String preMethodSignature, String reachable, String methodsType) {
		if ("APISink".equals(methodsType)) {
			if (ReachableMethodsUtil.getCandidateAPISinkReachableMethods().containsKey(preMethodSignature)) {
				ReachableMethodsUtil.addCandidateAPISinkReachableToExist(preMethodSignature, reachable);
			} else {
				ReachableMethodsUtil.addCandidateAPISinkReachableToNew(preMethodSignature,reachable);
			}
		}
		else if ("APIMisuse".equals(methodsType)) {
			if (ReachableMethodsUtil.getCandidateAPIMisuseReachableMethods().containsKey(preMethodSignature)) {
				ReachableMethodsUtil.addCandidateAPIMisuseReachableToExist(preMethodSignature, reachable);
			} else {
				ReachableMethodsUtil.addCandidateAPIMisuseReachableToNew(preMethodSignature,reachable);
			}
		}
		
	}
	
	public static void addCandidateAPISinkReachableToExist(String preMethodSignature, String reachable) {
		List<String> list = ReachableMethodsUtil.candidateAPISinkReachableHashMap.get(preMethodSignature);
		if (list.contains(reachable)) return;
		list.add(reachable);
		ReachableMethodsUtil.candidateAPISinkReachableHashMap.put(preMethodSignature, list);
		
	}
	
	public static void addCandidateAPISinkReachableToNew(String preMethodSignature, String reachable) {
		List<String> list = new ArrayList<String>();
		list.add(reachable);
		ReachableMethodsUtil.candidateAPISinkReachableHashMap.put(preMethodSignature, list);
	}
	
	public static void addCandidateAPIMisuseReachableToExist(String preMethodSignature, String reachable) {
		List<String> list = ReachableMethodsUtil.candidateAPIMisuseReachableHashMap.get(preMethodSignature);
		if (list.contains(reachable)) return;
		list.add(reachable);
		ReachableMethodsUtil.candidateAPIMisuseReachableHashMap.put(preMethodSignature, list);
		
	}
	
	public static void addCandidateAPIMisuseReachableToNew(String preMethodSignature, String reachable) {
		List<String> list = new ArrayList<String>();
		list.add(reachable);
		ReachableMethodsUtil.candidateAPIMisuseReachableHashMap.put(preMethodSignature, list);
	}
}
