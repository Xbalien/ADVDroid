package org.rois.asvdroid.utils;

import java.util.HashMap;

public class AuditTypeUtils {

	public static HashMap<String, HashMap<String, String>> AuditType;
	
	private static final String WEBVIEW_RCE = "Webview组件远程代码执行漏洞";
	private static final String WEBVIEW_LEAK = "Webview组件file域隐私泄露";
	private static final String FILE_READ_OR_WRITE ="全局文件可读写漏洞";
	private static final String INTENT_SCHEME = "Intent Scheme URL攻击漏洞";
	private static final String HTTPS_ALLOW_ALL = "HTTPS关闭主机名验证";
	private static final String SQL_INJECT = "本地SQL注入";
	private static final String DOS = "拒绝服务漏洞";

	private static final HashMap<String, String> WEBVIEW_RCE_DETAL = new HashMap<>();
	
	static{
		//AuditType.put(key, value)
	}
}
