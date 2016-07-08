package com.szxb.tools;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

public class XMlUtil {
	/**
	 * 将集合转换成xml
	 * 
	 * @author TangRen
	 * @param param
	 * @return 下午2:45:26
	 */
	public static String changeToXml(List<NameValuePair> param) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("<xml>");
		for (int i = 0; i < param.size(); i++) {
			sBuilder.append("<" + param.get(i).getName() + ">");
			sBuilder.append(param.get(i).getValue());
			sBuilder.append("</" + param.get(i).getName() + ">");
		}
		sBuilder.append("</xml>");
		try {
//			return new String(sBuilder.toString().getBytes(), "ISO8859-1");
			return new String(sBuilder.toString().getBytes(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 解析xml
	 * 
	 * @author TangRen
	 * @param content
	 * @return 下午2:46:27
	 */
	public static Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("xml解析出现异常", e.toString());
		}
		return null;

	}

}
