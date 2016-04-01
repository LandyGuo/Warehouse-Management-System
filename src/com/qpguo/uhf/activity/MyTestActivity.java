package com.qpguo.uhf.activity;

import java.util.List;

import com.qpguo.uhf.model.LargeMatterModel;
import com.qpguo.uhf.modelDAO.UploadLargeMatterDAO;
import com.qpguo.uhf.utils.DateTime;
import com.qpguo.uhf.utils.GetPhoneState;
import com.qpguo.uhf.utils.NumberConvert;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class MyTestActivity extends Activity
{
	private String TAG = "MyTestActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		GetPhoneState ps = new GetPhoneState(this);
//		Log.i(TAG, "该设备的id:"+ps.getDeviceId());
		String s = "31";
		String s1 = NumberConvert.hex_StringAutoComplete(s, 6);
		Log.i(TAG,"大件自增编号:"+s1);
		



	}
	
	/**
	 * 此方法用于生成测试数据
	 */
	protected void generateTestData()
	{
		LargeMatterModel lmm1 = new LargeMatterModel("100","入库","84","1","2014-08-08 12:48:00");
		LargeMatterModel lmm2 = new LargeMatterModel("100","出库","84","1",DateTime.getDateTime());
		LargeMatterModel lmm3= new LargeMatterModel("100","入库","84","1","2013-08-08 11:48:00");
		LargeMatterModel lmm4 = new LargeMatterModel("100","出库","84","1",DateTime.getDateTime());
		LargeMatterModel lmm5 = new LargeMatterModel("100","出库","84","1",DateTime.getDateTime());
		LargeMatterModel lmm6 = new LargeMatterModel("100","入库","84","1","2013-08-08 12:48:00");
		LargeMatterModel lmm7 = new LargeMatterModel("200","入库","83","1","2014-08-08 12:48:00");
		LargeMatterModel lmm8 = new LargeMatterModel("200","出库","83","1",DateTime.getDateTime());
		LargeMatterModel lmm9= new LargeMatterModel("200","入库","83","1","2013-08-08 11:48:00");
		LargeMatterModel lmm10 = new LargeMatterModel("200","出库","83","1",DateTime.getDateTime());
		LargeMatterModel lmm11 = new LargeMatterModel("200","出库","83","1",DateTime.getDateTime());
		LargeMatterModel lmm12 = new LargeMatterModel("200","入库","83","1","2013-08-08 12:48:00");
		UploadLargeMatterDAO lmd = new UploadLargeMatterDAO(this);
		lmd.ClearUploadLargeMatterData();
		lmd.insertItem(lmm1);
		lmd.insertItem(lmm2);
		lmd.insertItem(lmm3);
		lmd.insertItem(lmm4);
		lmd.insertItem(lmm5);
		lmd.insertItem(lmm6);
		lmd.insertItem(lmm7);
		lmd.insertItem(lmm8);
		lmd.insertItem(lmm9);
		lmd.insertItem(lmm10);
		lmd.insertItem(lmm11);
		lmd.insertItem(lmm12);
		List<LargeMatterModel> lst =lmd.getUploadLargeMatterData();
		for(LargeMatterModel l:lst)
		{
			Log.i(TAG, l.toString());
		}
	}
	





}