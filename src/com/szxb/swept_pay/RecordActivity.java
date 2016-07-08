package com.szxb.swept_pay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.example.swept_pay.R;
import com.szxb.adapter.RecordAdapter;
import com.szxb.bean.RecordBean;
import com.szxb.db.RecordDao;
import com.szxb.font.Toast;
import com.szxb.tools.Config;
import com.szxb.tools.ListParam;
import com.szxb.tools.XMlUtil;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.RequestQueue;
import com.yolanda.nohttp.rest.Response;

public class RecordActivity extends ActionBarActivity implements
		OnItemLongClickListener {

	private ListView mListView;

	private List<RecordBean> list_beans = new ArrayList<RecordBean>();

	private RecordAdapter mAdapter;

	private RecordDao dao;

	private RequestQueue queue;

	private ProgressDialog mDialog;
	
	private int custom_postion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_main);
		initView();
		list_beans = dao.finRecordBeanAll();
		mAdapter = new RecordAdapter(this, list_beans);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemLongClickListener(this);
	}

	private void initView() {
		dao = new RecordDao(this);
		mListView = (ListView) findViewById(R.id.listView);
		queue = NoHttp.newRequestQueue();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		final RecordBean recordBean = list_beans.get(position);
		custom_postion=position;
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("退款")
				.setMessage("是否发起退款？")
				.setPositiveButton("是", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						String out_trade_no = recordBean.getOuttradeno();
						Request<String> request = NoHttp.createStringRequest(
								Config.small_url, RequestMethod.POST);
						request.setConnectTimeout(5 * 1000);
						request.setReadTimeout(5 * 1000);
						request.setDefineRequestBodyForXML(ListParam
								.getreFundArgs(out_trade_no));
						queue.add(1, request, responseListener);
						
						mDialog = ProgressDialog.show(RecordActivity.this, "",
								"正在申请退款,请稍后……");
						
						dialog.dismiss();

					}
				}).setNegativeButton("否", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

		return true;
	}

	private OnResponseListener<String> responseListener = new OnResponseListener<String>() {

		@Override
		public void onFailed(int arg0, String arg1, Object arg2,
				Exception arg3, int arg4, long arg5) {
			Toast.makeToast(RecordActivity.this, "网络超时！", Toast.LENGTH_LONG).show();
			if (mDialog!=null) {
				mDialog.dismiss();
			}
		}

		@Override
		public void onFinish(int arg0) {
			

		}

		@Override
		public void onStart(int arg0) {
			

		}

		@Override
		public void onSucceed(int arg0, Response<String> arg1) {
			if (mDialog!=null) {
				mDialog.dismiss();
			}
			Map<String, String>xml=XMlUtil.decodeXml(arg1.get());
			System.out.println(xml.toString() );
			int status=Integer.valueOf(xml.get("status"));
			if (status==0) {
				int result_code=Integer.valueOf(xml.get("result_code"));
				if (result_code==0) {
					dao.del(xml.get("out_trade_no"));
					list_beans.remove(custom_postion);
					mAdapter.notifyDataSetChanged();
					Toast.makeToast(RecordActivity.this, "申请退款成功,将在1-3工作日将退款退到您的账户！", Toast.LENGTH_LONG).show();
				}else if (result_code==1) {
					Toast.makeToast(RecordActivity.this, "退款金额大于支付金额或已退款！", Toast.LENGTH_LONG).show();
				}
			}else {
				Toast.makeToast(RecordActivity.this, "申请退款失败！", Toast.LENGTH_LONG).show();
			}

		}
	};
}
