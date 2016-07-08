package com.szxb.swept_pay;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.R.integer;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.swept_pay.R;
import com.szxb.bean.RecordBean;
import com.szxb.db.RecordDao;
import com.szxb.font.Toast;
import com.szxb.tools.Config;
import com.szxb.tools.ListParam;
import com.szxb.tools.Util;
import com.szxb.tools.XMlUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.CacheMode;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

public class MainActivity extends ActionBarActivity {

	@SuppressWarnings("unused")
	private Button pay, refund;
	@SuppressWarnings("unused")
	private EditText pay_code;
	@SuppressWarnings("unused")
	private EditText out_trade_no, transaction_id;
	static Context context;
	private String result;

	public int isPay = 0;// 是否已经完成支付

	private RequestQueue swept_queue;// 被扫队列

	private RequestQueue scan_queue;// 主扫队列

	private RecordDao dao;

	private int polling_times = 0;// 轮询次数

	public MainActivity(Context context, String result, RecordDao dao) {
		this.context = context;
		this.result = result;
		this.dao = dao;
	}

	// 防止handler造成内存泄露，故此声明
	static Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeToast(context, "支付成功!", Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();

	}

	private void initView() {

		pay = (Button) findViewById(R.id.pay);
		refund = (Button) findViewById(R.id.refund);
		pay_code = (EditText) findViewById(R.id.pay_code);
		out_trade_no = (EditText) findViewById(R.id.out_trade_no);
		transaction_id = (EditText) findViewById(R.id.transaction_id);
	}

	/********************************************** 被扫 **********************************************/
	// 支付
	public void pay() {
		swept_queue = NoHttp.newRequestQueue();
		Request<String> request = NoHttp.createStringRequest(Config.small_url,
				RequestMethod.POST);
		request.setConnectTimeout(1000 * 5);
		request.setReadTimeout(1000 * 5);
		request.setDefineRequestBodyForXML(ListParam.getProductArgs(result));
		request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
		swept_queue.add(1, request, responseListener);
	}

	// 查询:使用线程同步，操他妈
	private void requestQuery() {
		Request<String> request = NoHttp.createStringRequest(Config.small_url,
				RequestMethod.POST);
		request.setConnectTimeout(1000);
		request.setDefineRequestBodyForXML(ListParam.getQuery());
		request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
		// swept_queue.add(2, request, responseListener_query);
		Response<String> response = NoHttp.startRequestSync(request);

		if (response.isSucceed()) {

			j++;
			System.out.println("j=" + j);
			Map<String, String> xml = XMlUtil.decodeXml(response.get());
			System.out.println(xml.toString());
			int status = Integer.valueOf(xml.get("status"));
			if (status == 0) {
				int result_code = Integer.valueOf(xml.get("result_code"));
				if (result_code == 0) {
					if (xml.get("trade_state").equals("SUCCESS")) {
						isPay = 1;

						String total_fee = xml.get("total_fee");
						String out_trade_no = xml.get("out_trade_no");
						String transaction_id = xml.get("transaction_id");
						String time_end = xml.get("time_end");

						RecordBean bean = new RecordBean();
						bean.setOuttradeno(out_trade_no);
						bean.setTransactionid(transaction_id);
						bean.setTimeend(time_end);
						bean.setMoney(total_fee);
						dao.AddRecordBean(bean);

						Message message = Message.obtain();
						message.what = 0;
						handler.sendMessage(message);

					} else if (xml.get("trade_state").equals("USERPAYING")) {
						isPay = 0;
					} else if (xml.get("trade_state").equals("REVOKED")) {
						isPay = 0;
					}

				}
			}

			System.out.println(response.get());

		} else {

		}
		// NoHttp.startRequestSync(request);
	}

	// 冲正
	private void requestImpact() {
		Request<String> request = NoHttp.createStringRequest(Config.small_url,
				RequestMethod.POST);
		request.setConnectTimeout(1000 * 5);
		request.setReadTimeout(1000 * 5);
		request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
		request.setDefineRequestBodyForXML(ListParam.getImpact());
		swept_queue.add(3, request, impact_responseListener);
	}

	/**
	 * 支付
	 */
	private OnResponseListener<String> responseListener = new OnResponseListener<String>() {

		@Override
		public void onFailed(int arg0, String arg1, Object arg2,
				Exception arg3, int arg4, long arg5) {
			// 如果超时则发起冲正
			Toast.makeToast(context, "交易失败，正在发起冲正！", Toast.LENGTH_SHORT).show();

			requestImpact();

		}

		@Override
		public void onSucceed(int arg0, Response<String> arg1) {

			Map<String, String> xml = XMlUtil.decodeXml(arg1.get());
			System.out.println(xml.toString());
			String status = xml.get("status");

			// 当满足下面的条件时说明已经支付成功
			if (Integer.valueOf(status) == 0) {

				String resultCode = xml.get("result_code");

				if (Integer.valueOf(resultCode) == 0) {
					String total_fee = xml.get("total_fee");
					String out_trade_no = xml.get("out_trade_no");
					String transaction_id = xml.get("transaction_id");
					String time_end = xml.get("time_end");

					RecordBean bean = new RecordBean();
					bean.setOuttradeno(out_trade_no);
					bean.setTransactionid(transaction_id);
					bean.setTimeend(time_end);
					bean.setMoney(total_fee);
					dao.AddRecordBean(bean);

					Toast.makeToast(context, "支付成功！", Toast.LENGTH_LONG).show();
				} else if (Integer.valueOf(resultCode) == 1) {
					Toast.makeToast(context, "请在1分钟内输入密码,完成支付！",
							Toast.LENGTH_LONG).show();
					System.out.println(resultCode);
					// 等待用户输入完密码，进行查询，每10S查询1次,3次之后还没有完成支付则以冲正处理

					ExecutorService executorService = Executors
							.newSingleThreadExecutor();
					executorService.execute(new Runnable() {

						@Override
						public void run() {

							while (isPay == 0 && polling_times < 5) {

								try {

									Thread.sleep(10 * 10 * 100);

									polling_times++;

									requestQuery();

								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});

					executorService.shutdown();

				}
			} else {
				// 支付失败则冲正
				requestImpact();

				Toast.makeToast(context, "支付失败,正在发起冲正！", Toast.LENGTH_LONG)
						.show();
			}
			// Toast.makeText(context, arg1.get(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFinish(int arg0) {

		}

		@Override
		public void onStart(int arg0) {

		}
	};

	/**
	 * 查询
	 */
	private int j = 0;
	private OnResponseListener<String> responseListener_query = new OnResponseListener<String>() {

		@Override
		public void onFailed(int arg0, String arg1, Object arg2,
				Exception arg3, int arg4, long arg5) {

			Toast.makeToast(context, "失败！", Toast.LENGTH_SHORT).show();

		}

		@Override
		public void onFinish(int arg0) {

		}

		@Override
		public void onStart(int arg0) {

		}

		@Override
		public void onSucceed(int arg0, Response<String> arg1) {
			j++;
			System.out.println("j=" + j);
			Map<String, String> xml = XMlUtil.decodeXml(arg1.get());
			System.out.println(xml.toString());
			int status = Integer.valueOf(xml.get("status"));
			if (status == 0) {
				int result_code = Integer.valueOf(xml.get("result_code"));
				if (result_code == 0) {
					if (xml.get("trade_state").equals("SUCCESS")) {
						isPay = 1;

						String total_fee = xml.get("total_fee");
						String out_trade_no = xml.get("out_trade_no");
						String transaction_id = xml.get("transaction_id");
						String time_end = xml.get("time_end");

						RecordBean bean = new RecordBean();
						bean.setOuttradeno(out_trade_no);
						bean.setTransactionid(transaction_id);
						bean.setTimeend(time_end);
						bean.setMoney(total_fee);
						dao.AddRecordBean(bean);

						Toast.makeToast(context, "支付成功!", Toast.LENGTH_LONG)
								.show();

					} else if (xml.get("trade_state").equals("USERPAYING")) {
						isPay = 0;
					} else if (xml.get("trade_state").equals("REVOKED")) {
						isPay = 0;
					}

				}
			}

			System.out.println(arg1.get());

		}
	};

	/**
	 * 冲正
	 */
	private OnResponseListener<String> impact_responseListener = new OnResponseListener<String>() {

		@Override
		public void onFailed(int arg0, String arg1, Object arg2,
				Exception arg3, int arg4, long arg5) {

			Toast.makeToast(context, "冲正失败！", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onFinish(int arg0) {

		}

		@Override
		public void onStart(int arg0) {

		}

		@Override
		public void onSucceed(int arg0, Response<String> arg1) {
			Map<String, String> xml = XMlUtil.decodeXml(arg1.get());
			System.out.println("冲正：" + xml.toString());
			int status = Integer.valueOf(xml.get("status"));
			if (status == 0) {
				int result_code = Integer.valueOf(xml.get("result_code"));
				if (result_code == 0) {
					Toast.makeToast(context, "已完成冲正", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	};

	/********************************************** 主扫 **********************************************/

	public void scanPay() {
		scan_queue = NoHttp.newRequestQueue();
		Request<String> request = NoHttp.createStringRequest(Config.small_url,
				RequestMethod.POST);
		request.setConnectTimeout(6000);
		System.out.println(request.getConnectTimeout() + "");
		System.out.println(request.getConnectTimeout() + "");
		request.setCacheMode(CacheMode.ONLY_REQUEST_NETWORK);
		request.setDefineRequestBodyForXML(ListParam.getscanPay());
		scan_queue.add(1, request, scan_responseListener);
	}
	

	private OnResponseListener<String> scan_responseListener = new OnResponseListener<String>() {

		@Override
		public void onFailed(int arg0, String arg1, Object arg2,
				Exception arg3, int arg4, long arg5) {
			System.out.println(arg1.toString() + arg3.toString());
			Toast.makeToast(context, "网络超时", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFinish(int arg0) {

		}

		@Override
		public void onStart(int arg0) {

		}

		@Override
		public void onSucceed(int arg0, Response<String> arg1) {

			Map<String, String> xml = XMlUtil.decodeXml(arg1.get());
			int status = Integer.valueOf(xml.get("status"));
			int result_code = Integer.valueOf(xml.get("result_code"));
			if (status == 0 && result_code == 0) {
				// 发送广播在Main中显示二维码
				Util.Intent(context, "show", xml.get("code_url"));

				System.out.println("支付地址：" + xml.get("code_url"));
				System.out.println("二维码图片地址" + xml.get("code_img_url"));

				//暂时没有通知URL，所以不知道是否已经支付完成，办法：做查询处理,跟被扫一样
				
				
				ExecutorService executorService = Executors
						.newSingleThreadExecutor();
				executorService.execute(new Runnable() {

					@Override
					public void run() {

						while (isPay == 0 && polling_times < 3) {

							try {

								Thread.sleep(10 * 10 * 100);

								polling_times++;

								requestQuery();

							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});

				executorService.shutdown();
				
			}

			Log.d("返回数据：", xml.toString());
		}
	};

}
