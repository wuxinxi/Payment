package com.szxb.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.szxb.bean.RecordBean;

public class RecordDao {
	private SQLiteDatabase db;
	private DBContext dbc;
	private Context context;

	public RecordDao(Context context) {
		this.context = context;
	}

	public RecordDao Open() {
		dbc = new DBContext(context);
		db = dbc.getWritableDatabase();
		return this;
	}

	public void Cols() {
		dbc.close();
		db.close();

	}

	public void AddRecordBean(RecordBean bean) {
		Open();
		ContentValues values = new ContentValues();
		values.put("outtradeno", bean.getOuttradeno());
		values.put("transactionid", bean.getTransactionid());
		values.put("timeend", bean.getTimeend());
		values.put("total_fee", bean.getMoney());
		db.insert("record", null, values);
		Cols();
	}

	public List<RecordBean> finRecordBeanAll() {
		Open();
		List<RecordBean> list = new ArrayList<RecordBean>();
		Cursor cur = db.rawQuery("select * from record", null);
		while (cur.moveToNext()) {
			RecordBean bean = new RecordBean();
			bean.setId(cur.getInt(cur.getColumnIndex("id")));
			bean.setOuttradeno(cur.getString(cur.getColumnIndex("outtradeno")));
			bean.setTransactionid(cur.getString(cur
					.getColumnIndex("transactionid")));
			bean.setTimeend(cur.getString(cur.getColumnIndex("timeend")));
			bean.setMoney(cur.getString(cur.getColumnIndex("total_fee")));
			list.add(bean);
			
		}
		Cols();
		return list;
	}
	
	public void del(String out_trade_no){
		Open();
		int code=db.delete("record", "outtradeno=?", new String[]{out_trade_no});
		System.out.println("删除是否成功："+code);
		Cols();
	}
	
	
	
}
