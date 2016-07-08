package com.szxb.swept_pay;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.swept_pay.R;
import com.google.zxing.WriterException;
import com.szxb.db.RecordDao;
import com.szxb.font.Toast;
import com.szxb.tools.Util;
import com.wizarpos.barcode.scanner.IScanEvent;
import com.wizarpos.barcode.scanner.ScannerRelativeLayout;
import com.wizarpos.barcode.scanner.ScannerResult;

public class Main extends ActionBarActivity {

	private Button button, button2;
	public static ScannerRelativeLayout scanView;// 扫描控件
	public SurfaceView scanShow;// 扫描区域
	public Button autoScan, passiveScan;
	public static LinearLayout imageLayout;
	public static FrameLayout surfaceLayout;
	public View view;
	private ImageView imageView;
	public IScanEvent scanLisenter;
	public TextView textLine1, textLine2;
	public boolean wxAutoClick = false, wxPassiveClick = false;
	private String scanResultMy;
	private Dialog wxDialog;

	private RecordDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		wxDialog = showWXDialog();
		wxDialog.setCanceledOnTouchOutside(false);
		button = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		dao = new RecordDao(this);

		showScanQrcode();
	}

	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.button1:
			wxDialog = showWXDialog();
			wxDialog.setCanceledOnTouchOutside(false);
			break;
		case R.id.button2:
			startActivity(new Intent(Main.this, RecordActivity.class));
			break;

		default:
			break;
		}
	}

	/**
	 * 注册广播，接收广播
	 * 
	 * @author TangRen
	 * @time 2016-7-2
	 */
	private void showScanQrcode() {
		LocalBroadcastManager manager = LocalBroadcastManager
				.getInstance(Main.this);
		IntentFilter filter = new IntentFilter();
		filter.addAction("show");
		BroadcastReceiver receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				try {
					imageView.setBackgroundResource(R.drawable.bg2);
					imageView.setImageBitmap(Util.CreateCode(intent
							.getStringExtra("msg")));
				} catch (WriterException e) {
					e.printStackTrace();
					Toast.makeToast(Main.this, "生成二维码失败！", Toast.LENGTH_SHORT).show();
				}
			}
		};
		manager.registerReceiver(receiver, filter);

	}

	/**
	 * 显示dialog
	 */
	public Dialog showWXDialog() {
		Dialog dialog = new Dialog(this, R.style.bottomDialogStyle);
		view = (View) getLayoutInflater().inflate(R.layout.dialog_view, null);
		imageLayout = (LinearLayout) view.findViewById(R.id.imageLayout); // 图片区域
		surfaceLayout = (FrameLayout) view.findViewById(R.id.surfaceViewLayout); // 扫描区域
		scanView = (ScannerRelativeLayout) view.findViewById(R.id.scanner);// 扫描控件
		autoScan = (Button) view.findViewById(R.id.autoScan);
		imageView = (ImageView) view.findViewById(R.id.imageShow);
		passiveScan = (Button) view.findViewById(R.id.passiveScan);
		textLine1 = (TextView) view.findViewById(R.id.textLineShow1);
		textLine2 = (TextView) view.findViewById(R.id.textLineShow2);
		autoScan.setOnClickListener(new MyButtonClikLisenter());
		passiveScan.setOnClickListener(new MyButtonClikLisenter());
		dialog.setContentView(view);
		Window window = dialog.getWindow();
		WindowManager manager = getWindowManager();
		Display display = manager.getDefaultDisplay();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.height = (int) (display.getHeight() * 0.8);
		lp.width = (int) (display.getWidth() * 0.8);
		window.setAttributes(lp);
		dialog.show();
		return dialog;
	}

	/**
	 * 显示扫描区域
	 */
	public static void showSurfaceView() {
		imageLayout.setVisibility(View.GONE);
		surfaceLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示图片区域
	 */
	public static void showImageView() {
		imageLayout.setVisibility(View.VISIBLE);
		surfaceLayout.setVisibility(View.GONE);
	}

	/**
	 * 显示扫描区域并且开始扫描
	 */
	public void showAndScan() {

		showSurfaceView();
		scanView.onResume();
		scanView.startScan();
		scanLisenter = new ScanSuccesListener();
		scanView.setScanSuccessListener(scanLisenter);
	}

	/**
	 * 微信支付dialog中支付方式按钮点击
	 * 
	 * @author 
	 * 
	 */
	public class MyButtonClikLisenter implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.autoScan:
				wxAutoClick = true;
				wxPassiveClick = false;
				scanView.stopScan();
				imageLayout.setVisibility(View.VISIBLE);
				surfaceLayout.setVisibility(View.INVISIBLE);
				textLine1.setBackgroundColor(getResources().getColor(
						R.color.blueColor));
				textLine2.setBackgroundColor(getResources().getColor(
						R.color.whiteColor));
				scan_Pay();
				break;
			case R.id.passiveScan:
				wxPassiveClick = true;
				wxAutoClick = false;
				imageLayout.setVisibility(View.INVISIBLE);
				surfaceLayout.setVisibility(View.VISIBLE);
				textLine1.setBackgroundColor(getResources().getColor(
						R.color.whiteColor));
				textLine2.setBackgroundColor(getResources().getColor(
						R.color.blueColor));
				
				showAndScan();
				break;

			default:
				break;
			}
		}

		private void scan_Pay() {
		
			MainActivity activity = new MainActivity(Main.this, null, dao);
			activity.scanPay();
		}
	}

	/**
	 * 监听扫描 成功后的结果处理
	 * 
	 * @author 
	 * 
	 */
	private class ScanSuccesListener extends IScanEvent {
		@Override
		public void scanCompleted(ScannerResult scannerResult) {
			scanResultMy = scannerResult.getResult();
			Message msg = handler.obtainMessage();
			scanView.stopScan();
			// 点击了微信支付 并且是被扫支付
			msg.obj = scannerResult;
			msg.what = 10;

			handler.sendMessage(msg);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			// 微信结果处理 被扫支付
			case 10:
				// Toast.makeText(Main.this, scanResultMy, 0).show();
				MainActivity activity = new MainActivity(Main.this,
						scanResultMy, dao);
				activity.pay();
				break;
			// 微信支付 主扫支付
			case 11:
				break;
			// 支付宝支付
			case 12:
				break;

			default:
				break;
			}

		}

	};

	@Override
	protected void onDestroy() {
		scanView.stopScan();
		super.onDestroy();
	}

}
