package org.rois.asvdroid.intent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.util.Chain;

public class LinkJimpleByInstrument {
	
	
	public static void startLink() {
		Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
		Iterator<SootClass> it = sootClasses.iterator();
		while (it.hasNext()) {
			SootClass sc = (SootClass) it.next();
			if (!sc.toString().contains("android.support")) {
				List<SootMethod> sms = sc.getMethods();
				
				for (SootMethod sm : sms){
					if (sm.hasActiveBody()) {
						Body b = sm.retrieveActiveBody();
						internalStartLink(b);
					}
					
				}
			}
			//System.out.println(type);
		}
	}
	
	
	private static void internalStartLink(Body b) {
		
		PatchingChain<Unit> units = b.getUnits();
		
		for (Iterator<Unit> unitsIt = units.snapshotIterator(); unitsIt.hasNext();) {
			Stmt stmt = (Stmt)unitsIt.next();
			
			if (stmt.containsInvokeExpr()) {
				String signature = stmt.getInvokeExpr().getMethod().getSignature();
				//System.out.println(signature);
				
				if (signature.contains("android.view.View findViewById")) {
					linkFindViewById(units, b, unitsIt);
				} else if (signature.contains("<android.webkit.WebView: android.webkit.WebSettings getSettings")) {
					linkWebViewSettings(units, b, stmt, unitsIt);
				}
				//System.out.println("success Instrument");
			}
		}
		//System.out.println(b.toString());
	}
	
	
	/*
	 *   $r4 = virtualinvoke $r0.<com.example.testvulnerability.EntryPoint: android.view.View findViewById(int)>(2131165185);
	 *   $r5 = (com.example.testvulnerability.SafeWebView) $r4;
	 *
	 *   $r4 = virtualinvoke $r0.<com.test.sootapk.TestEntry: android.view.View findViewById(int)>(2131165185);
	 *   $r5 = (android.webkit.WebView) $r4;
	 *   $r5 = new android.webkit.WebView;
	 *   specialinvoke $r5.<android.webkit.WebView: void <init>(android.content.Context)>(tmpContext);
	 */
	private static void linkFindViewById(PatchingChain<Unit> units, Body b, Iterator<Unit> unitsIt){
		Stmt nextStmt = (Stmt) unitsIt.next();
		System.out.println(nextStmt);
		if (nextStmt instanceof AssignStmt) {
			AssignStmt assignStmt = (AssignStmt) nextStmt;
			//findViewById Cast Type
			Type type = assignStmt.getRightOp().getType();
			//Cast Type Locals
			Value value = assignStmt.getLeftOp();
			Local parm = addTmpParm(b, "android.content.Context");
			addInstanceExpr(b, units, nextStmt, value, (RefType)type, "void <init>(android.content.Context)", parm);
			//addInstanceWithParmExpr(b, units, nextStmt, value, (RefType)type, "void <init>(android.content.Context)", parm);
		} 
	}
	
	/*  
	 *   $r6 = virtualinvoke $r5.<android.webkit.WebView: android.webkit.WebSettings getSettings()>();
	 *   
	 *   $r6 = new android.webkit.WebSettings;
	 *   specialinvoke $r6.<android.webkit.WebSettings: void <init>()>();
	 */
	private static void linkWebViewSettings(PatchingChain<Unit> units, Body b, Stmt stmt, Iterator<Unit> unitsIt) {
		if (stmt instanceof AssignStmt) {
			AssignStmt assignStmt = (AssignStmt) stmt;
			Value value = assignStmt.getLeftOp();
			addInstanceExpr(b, units, stmt, value, RefType.v("android.webkit.WebSettings"), "void <init>()", null);
			//addInstanceNoParmExpr(b, units, stmt, value, RefType.v("android.webkit.WebSettings"), "void <init>()");
		} 
	}
	
	
	/**
	 * value is Locals ref type is expect instantce type,
	 * @param b body
	 * @param units body units
	 * @param toInsert insert unit
	 * @param value Locals ref
	 * @param type	Expect instantce type		
	 * @param initSignature link init method signature
	 * @param parm instantce parm 
	 */
	/*private static void addInstanceWithParmExpr(Body b, PatchingChain<Unit> units, Unit toInsert, Value value, RefType type, String initSignature, Local parm) {
		Unit addNewAssign = Jimple.v().newAssignStmt(value, Jimple.v().newNewExpr(type));
        //specialinvoke $r.<type: void <init>(android.content.Context)>();
		SootMethod toCall = Scene.v().getSootClass(type.toString()).getMethod(initSignature);
		//Local context = addTmpContext(b);
		Unit addInitCall = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr((Local)value, toCall.makeRef(), parm));
		units.insertAfter(addNewAssign, toInsert);
		units.insertAfter(addInitCall, addNewAssign);
		b.validate();
	}*/
	
	
	@SuppressWarnings("unchecked")
	private static void addInstanceExpr(Body b, PatchingChain<Unit> units, Unit toInsert, Value value, RefType type, String initSignature, Local parm){
		Unit addInitCall;
		Unit addNewAssign = Jimple.v().newAssignStmt(value, Jimple.v().newNewExpr(type));
		SootMethod toCall = Scene.v().getSootClass(type.toString()).getMethod(initSignature);
		//not parm
		if (parm == null) {
			addInitCall = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr((Local)value, toCall.makeRef(), (List<? extends Value>) new ArrayList<>()));
		} else {
			addInitCall = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr((Local)value, toCall.makeRef(), parm));
		}
		units.insertAfter(addNewAssign, toInsert);
		units.insertAfter(addInitCall, addNewAssign);
		b.validate();
	}
	
    private static Local addTmpParm(Body body, String className){
        Local tmpParm = Jimple.v().newLocal("tmpParm", RefType.v(className)); 
        body.getLocals().add(tmpParm);
        return tmpParm;
    }

}
