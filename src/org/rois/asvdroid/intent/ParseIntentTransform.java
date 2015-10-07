package org.rois.asvdroid.intent;

import java.util.Map;
import soot.Body;
import soot.BodyTransformer;

import org.rois.asvdroid.utils.ExposedComponentsUtil;


public class ParseIntentTransform extends BodyTransformer{
	
	@Override
	protected void internalTransform(Body b, String phaseName,Map<String, String> options) {
		//<com.example.testvulnerability.VulActivity: void onCreate(android.os.Bundle)>
		String[] signature = b.getMethod().toString().split("[<:>]");
		//Some intent parser are in the user's define method
		if (ExposedComponentsUtil.getExposedComponents().contains(signature[1])) {
			//if (ExposedComponentsUtil.getComponentLifeCycleMethods().contains(signature[2].substring(1)))
			ComponentIntentExtraExtract.startExtraExtract(b);

		}		
	}
}
