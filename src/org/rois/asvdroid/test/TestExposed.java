package org.rois.asvdroid.test;

import java.io.IOException;

import org.rois.asvdroid.utils.ExposedComponentsUtil;

public class TestExposed {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ExposedComponentsUtil.paresExposedComponents("com.wisorg.fzdx");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(ExposedComponentsUtil.getExposedComponents());
	}

}
