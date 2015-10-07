package org.rois.asvdroid.intent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import soot.Body;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Stmt;
import org.rois.asvdroid.utils.IntentExtrasUtils;

public class ComponentIntentExtraExtract{
	
	public static final String INTENT_CLASS = "android.content.Intent";
	public static final String BUNDLE_CLASS = "android.os.Bundle";
	private static String packageName;
	
	public static void startExtraExtract(Body body) {
		
		PatchingChain<Unit> units = body.getUnits();
		String componentName = body.getMethod().getDeclaringClass().getName();
		HashMap<String, Set<String>> getExtraKeys = new HashMap<String, Set<String>>();
		HashMap<String, Set<String>> putExtraKeys = new HashMap<String, Set<String>>();
		//System.out.println(body.getMethod().getSignature().toString());
		
		for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
			Stmt stmt = (Stmt) iter.next();
			
			if (stmt.containsInvokeExpr()) {
				SootMethod sm = stmt.getInvokeExpr().getMethod();
				String methodName = sm.getName();
				int type = 0; 
				
				//key
				String extraKey = null;
				
				//start with "get" or "put" methods
				if (methodName.startsWith("get")) {
					type = 1;
				} else if (methodName.startsWith("put")) {
					type = 2;
				} else if (0 == type) {
					continue;
				}
				
				//getXXExtras or putXXExtras
				if ((sm.getDeclaringClass().toString().equals(INTENT_CLASS) && methodName.contains("Extra")) ||
					 (sm.getDeclaringClass().toString().equals(BUNDLE_CLASS))) {
					if (stmt.getInvokeExpr().getArgs().size() > 0){
						Value v = stmt.getInvokeExpr().getArgs().get(0);
						extraKey = v.toString().replaceAll("\"","");
					}
				}
				
				if (type == 1 && extraKey != null) {
					IntentExtrasUtils.parseExtras(getExtraKeys, methodName, extraKey);
				} else if (type == 2 && extraKey != null) {
					IntentExtrasUtils.parseExtras(putExtraKeys, methodName, extraKey);
				}
			}
		}

		if (getExtraKeys.size() != 0) {
			IntentExtrasUtils.putExtras(componentName, getExtraKeys, 1);
		}
		if (putExtraKeys.size() != 0) {
			IntentExtrasUtils.putExtras(componentName, putExtraKeys, 2);
		}
	}
	
	
	public static void setPackageName(String name) {
		packageName = name;
	}
}
