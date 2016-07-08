package com.szxb.tools;

import java.util.List;

import org.apache.http.NameValuePair;

public class SignUtil {

	/**
	 * 生成签名(加密)
	 * 
	 * @author TangRen
	 * @param param
	 * @return 下午2:44:16
	 */
	public static String getSignTest(List<NameValuePair> param) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < param.size(); i++) {
			sb.append(param.get(i).getName());
			sb.append('=');
			sb.append(param.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Config.key);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();

		return appSign;
	}
}
