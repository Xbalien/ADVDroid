package org.rois.asvdroid.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.sf.json.JSONObject;

import org.rois.asvdroid.reachability.APIMisuseAnalysis;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class VettingResultShowUtils {

	public static void showAPIMisuseResult() {
		
		for (String key : APIMisuseAnalysis.getAPIMisuseVettingResult().keySet()) {
			System.out.println(APIMisuseAnalysis.getAPIMisuseVettingResult().get(key) + " ----> " + key);
			System.out.println(APIMisuseAnalysis.getAPIMisuseVettingResultDetail().get(key));
		}
	}
	
	public static void dumpAPIMisuseResult2JsonFile(String fileName) {
		JSONObject resJsonObject = JSONObject.fromObject(APIMisuseAnalysis.getAPIMisuseVettingResult());
		File file = new File(fileName + ".json");
		PrintStream outputStream = null;
		
		try {
			outputStream = new PrintStream(new FileOutputStream(file));
			outputStream.print(resJsonObject);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
	
	public static boolean doc2XmlFile(Document document, String filename) {
		boolean flag = true;
		try {
		    TransformerFactory tFactory = TransformerFactory.newInstance();
		    Transformer transformer = tFactory.newTransformer();
		    /** ±àÂë */
		    // transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
		    DOMSource source = new DOMSource(document);
		    StreamResult result = new StreamResult(new File(filename));
		    transformer.transform(source, result);
		    
		} catch (Exception ex) {
		    flag = false;
		    ex.printStackTrace();
		}
	   return flag;
	}
	
	
	public static void dumpAPIMisuseResult2XmlFile(String fileName) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			System.err.println(pce);
			System.exit(1);
		}
		Document doc = null;
		doc = builder.newDocument();

		Element root = doc.createElement("APIMisuseResult");
		doc.appendChild(root);
		
		
		for (String key : APIMisuseAnalysis.getAPIMisuseVettingResult().keySet()) {
			
			Element misuseAPI = doc.createElement("MisuseAPI");
			misuseAPI.setAttribute("Name", key);
			root.appendChild(misuseAPI);
			
			Element invokeFrom = doc.createElement("InvokeFrom");
			misuseAPI.appendChild(invokeFrom);
			
			Text invoke = doc.createTextNode(APIMisuseAnalysis.getAPIMisuseVettingResult().get(key));
			invokeFrom.appendChild(invoke);
			
			Element misuseMethodBody = doc.createElement("MisuseMethodBody");
			misuseAPI.appendChild(misuseMethodBody);
			
			Text body = doc.createTextNode(APIMisuseAnalysis.getAPIMisuseVettingResultDetail().get(key));
			misuseMethodBody.appendChild(body);
		}
		
		doc2XmlFile(doc, fileName + ".xml");
	}

}
