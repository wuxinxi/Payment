package com.szxb.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ListParam {

	/**
	 * 生成提交的订单（被扫）
	 * 
	 * @author TangRen
	 * @param pay_code:支付授权码
	 * @return
	 * @time 2016-7-1
	 */

	private static String out_trade_no;// 商户订单号

	public static String getProductArgs(String pay_code) {
		StringBuffer xml = new StringBuffer();
		String xmlString = "";

		xml.append("</xml>");

		out_trade_no = Util.OrderNo();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("auth_code", pay_code));
		params.add(new BasicNameValuePair("body", "优衣库衣服"));
		params.add(new BasicNameValuePair("charset", "UTF-8"));
		params.add(new BasicNameValuePair("mch_create_ip", Util.localIp()));
		params.add(new BasicNameValuePair("mch_id", Config.mch_id));
		params.add(new BasicNameValuePair("nonce_str", Util.Random(15)));// 随机字符串
		params.add(new BasicNameValuePair("out_trade_no", out_trade_no));// 模拟订单号（当前日期+4位随数）
		params.add(new BasicNameValuePair("service", "unified.trade.micropay"));
		params.add(new BasicNameValuePair("total_fee", "1"));
		// 添加签名
		String sign = SignUtil.getSignTest(params);
		params.add(new BasicNameValuePair("sign", sign));

		// 转换成xml格式
		xmlString = XMlUtil.changeToXml(params);

		System.out.println("sign签名:" + sign);
		System.out.println("xml:" + xmlString);

		return xmlString;
	}

	/**
	 * 退款参数（被扫）
	 * 
	 * @author TangRen
	 * @param out_trade_no
	 * @param transaction_id
	 * @return
	 * @time 2016-7-1
	 */
	public static String getreFundArgs(String out_trade_no) {
		StringBuffer xml = new StringBuffer();
		String xmlString = "";

		xml.append("</xml>");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("charset", "UTF-8"));
		params.add(new BasicNameValuePair("mch_id", Config.mch_id));
		params.add(new BasicNameValuePair("nonce_str", Util.Random(15)));// 随机字符串
		params.add(new BasicNameValuePair("op_user_id", Config.mch_id));
		params.add(new BasicNameValuePair("out_refund_no", Util.OrderNo()));// 模拟订单号（当前日期+4位随数）
		params.add(new BasicNameValuePair("out_trade_no", out_trade_no));
		params.add(new BasicNameValuePair("refund_fee", "1"));
		params.add(new BasicNameValuePair("service", "unified.trade.refund"));
		params.add(new BasicNameValuePair("total_fee", "1"));
		// 添加签名
		String sign = SignUtil.getSignTest(params);
		params.add(new BasicNameValuePair("sign", sign));

		// 转换成xml格式
		xmlString = XMlUtil.changeToXml(params);

		System.out.println("sign签名:" + sign);
		System.out.println("xml:" + xmlString);

		return xmlString;
	}

	/**
	 * 查询订单（被扫）
	 * 
	 * @author TangRen
	 * @param out_trade_no
	 * @param transaction_id
	 * @return
	 * @time 2016-7-1
	 */
	public static String getQuery() {
		StringBuffer xml = new StringBuffer();
		String xmlString = "";

		xml.append("</xml>");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("charset", "UTF-8"));
		params.add(new BasicNameValuePair("mch_id", Config.mch_id));
		params.add(new BasicNameValuePair("nonce_str", Util.Random(15)));// 随机字符串
		params.add(new BasicNameValuePair("out_trade_no", out_trade_no));
		params.add(new BasicNameValuePair("service", "unified.trade.query"));

		// 添加签名
		String sign = SignUtil.getSignTest(params);
		params.add(new BasicNameValuePair("sign", sign));

		// 转换成xml格式
		xmlString = XMlUtil.changeToXml(params);

		System.out.println("sign签名:" + sign);
		System.out.println("xml:" + xmlString);

		return xmlString;
	}

	/**
	 * 冲正
	 * 
	 * @author TangRen
	 * @param out_trade_no
	 * @param transaction_id
	 * @return
	 * @time 2016-7-2
	 */
	public static String getImpact() {
		StringBuffer xml = new StringBuffer();
		String xmlString = "";

		xml.append("</xml>");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("charset", "UTF-8"));
		params.add(new BasicNameValuePair("mch_id", Config.mch_id));
		params.add(new BasicNameValuePair("nonce_str", Util.Random(15)));// 随机字符串
		params.add(new BasicNameValuePair("out_trade_no", out_trade_no));
		params.add(new BasicNameValuePair("service", "unified.micropay.reverse"));

		// 添加签名
		String sign = SignUtil.getSignTest(params);
		params.add(new BasicNameValuePair("sign", sign));

		// 转换成xml格式
		xmlString = XMlUtil.changeToXml(params);

		System.out.println("sign签名:" + sign);
		System.out.println("xml:" + xmlString);

		return xmlString;
	}

	/******************************************************************** 主扫(下面) ***********************************************************************/
	/**
	 * 主扫支付参数
	 * 
	 * @author TangRen
	 * @param pay_code
	 * @return
	 * @time 2016-7-2
	 */
	public static String getscanPay() {
		StringBuffer xml = new StringBuffer();
		String xmlString = "";

		xml.append("</xml>");

		out_trade_no = Util.OrderNo();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("body", "test"));
		params.add(new BasicNameValuePair("charset", "UTF-8"));
		params.add(new BasicNameValuePair("mch_create_ip", Util.localIp()));
		params.add(new BasicNameValuePair("mch_id", Config.mch_id));
		params.add(new BasicNameValuePair("nonce_str", Util.Random(15)));// 随机字符串
		params.add(new BasicNameValuePair("notify_url", "http://www.baidu.com"));
		params.add(new BasicNameValuePair("out_trade_no", out_trade_no));// 模拟订单号（当前日期+4位随数）
		params.add(new BasicNameValuePair("service", "pay.weixin.native"));
		params.add(new BasicNameValuePair("total_fee", "1"));

		// 添加签名
		String sign = SignUtil.getSignTest(params);
		params.add(new BasicNameValuePair("sign", sign));

		// 转换成xml格式
		xmlString = XMlUtil.changeToXml(params);

		System.out.println("sign签名:" + sign);
		System.out.println("xml:" + xmlString);

		return xmlString;
	}
}
