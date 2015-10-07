package org.rois.asvdroid.reachability;

import java.util.ListIterator;
import java.util.Map;
import org.rois.asvdroid.utils.ParseClassesAndMethodsUtil;
import org.rois.asvdroid.utils.ReachableMethodsUtil;

import pxb.android.axml.R;
import soot.Body;
import soot.BodyTransformer;
import soot.PatchingChain;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;

public class APIReachabilityTransformer extends BodyTransformer{

	public void parseInvoke(PatchingChain<Unit> units, String preMethodSignature) {
		
		// ListIterator can invoke previous() and next() methods
		for (ListIterator<Unit> unitsIt = (ListIterator<Unit>) units.snapshotIterator(); unitsIt.hasNext();) {
			Stmt stmt = (Stmt)unitsIt.next();
			
			if (stmt.containsInvokeExpr()) {
				String signature = stmt.getInvokeExpr().getMethod().getSignature();
				
				//api misuse analysis
				if (ParseClassesAndMethodsUtil.getAPIMisuseMethods().contains(signature)) {
					String reachable = stmt.toString();

					//setHostnameVerifier methods parameter is not primitive class
					if (signature.equals("<org.apache.http.conn.ssl.SSLSocketFactory: void setHostnameVerifier(org.apache.http.conn.ssl.X509HostnameVerifier)>")) {
						String param = stmt.getInvokeExpr().getArgs().get(0).toString();
						//go to pre stmt
						unitsIt.previous();
						Stmt preStmt = (Stmt) unitsIt.previous();
						if (preStmt instanceof AssignStmt) {
							
							AssignStmt assignStmt = (AssignStmt) preStmt;
							Type type = assignStmt.getRightOp().getType();
							
							if (type.toString().equals("org.apache.http.conn.ssl.X509HostnameVerifier")) {
								Value value = assignStmt.getRightOp();
								//<org.apache.http.conn.ssl.SSLSocketFactory: org.apache.http.conn.ssl.X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER>
								reachable = reachable.replace(param, value.toString().split(": ")[1]);
							}
						}
						
						unitsIt.next();
						unitsIt.next();
					}

					ReachableMethodsUtil.putCandidateReachable(reachable, preMethodSignature, "APIMisuse");
				}
				
				//api sink reach analysis
				if (ParseClassesAndMethodsUtil.getAPISinkMethods().contains(signature)) {
					String reachable = stmt.toString();
					ReachableMethodsUtil.putCandidateReachable(reachable, preMethodSignature, "APISink");
					
				}
												
			}
		}
		
		
	}

	
	@Override
	protected void internalTransform(Body b, String phaseName,Map<String, String> options) {
		
		String[] signature = b.getMethod().toString().split("[<:>]");
		SootMethod preMethod = b.getMethod();
		
		if (!signature[1].contains("android.support")) {
			//System.out.println(preMethod.getActiveBody());
			PatchingChain<Unit> units = b.getUnits();
			parseInvoke(units,preMethod.getSignature());
		}
	}		
}

