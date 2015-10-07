package org.rois.asvdroid.test;

import java.util.Iterator;
import org.rois.asvdroid.utils.ParseClassesAndMethodsUtil;

public class ParseMethodsUtilsTest {

	public static void main(String[] args) {
		Iterator<String> iterator1 = ParseClassesAndMethodsUtil.getAPIMisuseMethods().iterator();
		Iterator<String> iterator2 = ParseClassesAndMethodsUtil.getExtrasMethods().iterator();
		
		while (iterator1.hasNext()) {
			String string = (String) iterator1.next();
			System.out.println(string);
		}
		while (iterator2.hasNext()) {
			String string = (String) iterator2.next();
			System.out.println(string);
		}
		System.out.println(ParseClassesAndMethodsUtil.getAndroidClasses());
	}

}
