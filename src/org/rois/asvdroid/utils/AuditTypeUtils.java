package org.rois.asvdroid.utils;

import java.util.HashMap;

public class AuditTypeUtils {

	public static HashMap<String, HashMap<String, String>> AuditType;
	
	private static final String WEBVIEW_RCE = "Webview���Զ�̴���ִ��©��";
	private static final String WEBVIEW_LEAK = "Webview���file����˽й¶";
	private static final String FILE_READ_OR_WRITE ="ȫ���ļ��ɶ�д©��";
	private static final String INTENT_SCHEME = "Intent Scheme URL����©��";
	private static final String HTTPS_ALLOW_ALL = "HTTPS�ر���������֤";
	private static final String SQL_INJECT = "����SQLע��";
	private static final String DOS = "�ܾ�����©��";

	private static final HashMap<String, String> WEBVIEW_RCE_DETAL = new HashMap<>();
	
	static{
		//AuditType.put(key, value)
	}
}
