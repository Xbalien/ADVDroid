package org.rois.asvdroid.temp;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class IntentConstraintAnalysis extends ForwardFlowAnalysis{

	public IntentConstraintAnalysis(DirectedGraph graph) {
		super(graph);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void flowThrough(Object arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void copy(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object entryInitialFlow() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void merge(Object arg0, Object arg1, Object arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object newInitialFlow() {
		// TODO Auto-generated method stub
		return null;
	}



}
