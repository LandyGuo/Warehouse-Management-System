package com.qpguo.uhf.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.example.uhf.application.R;
import com.qpguo.uhf.modelDAO.BaseClassDAO;
import com.qpguo.uhf.modelDAO.BaseDataDAO;
import com.qpguo.uhf.modelDAO.LargeMatterDAO;
import com.qpguo.uhf.modelDAO.LargeMatterGiveOutDAO;
import com.qpguo.uhf.modelDAO.PanDianDataDAO;
import com.qpguo.uhf.modelDAO.PlanDataDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;
import com.qpguo.uhf.modelDAO.UploadLargeMatterDAO;
import com.qpguo.uhf.modelDAO.UploadPlanDataDAO;
import com.qpguo.uhf.modelDAO.UserDataDAO;
import com.qpguo.uhf.utils.CheckInternet;
import com.qpguo.uhf.utils.HttpApi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;



public class MainMenuActivity extends Activity
{
	private String TAG = "MainMenuActivity";
	//主菜单界面上的功能按钮
	private Button faka_button;
	private Button upload_download_button;
	private Button inOutBill_button;
	private Button bigMatterManagement_button;
	private Button panDian_Button;
	private Button config_Button;
	
	//用于数据更新时显示
	private ProgressDialog p2;
	private final int INTERNET_UNCONNECTED =500;
	//用于数据上传事件消息
	private final int UPLOAD_FAILED =600;
	//数据更新成功(上传及下载)
	private final int UPDATE_SUCCESS =700;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_menu);
		//用于设置参数功能
		config_Button = (Button)this.findViewById(R.id.configuration_button);
		//用于盘点功能
		panDian_Button = (Button)this.findViewById(R.id.PanDianManagement);
		//用于大件管理
		bigMatterManagement_button = (Button)this.findViewById(R.id.BigMatterManagement_button);
		//用于发卡功能
		faka_button = (Button) this.findViewById(R.id.faka_button);
		//用于数据更新，包括上传和下载数据
		upload_download_button = (Button)this.findViewById(R.id.upload_download_button);

		//用于出入库计划功能
		inOutBill_button =  (Button)this.findViewById(R.id.inOutBill_button);

		
		OnClickListener listener = new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Button btn  =(Button)v;
				switch(btn.getId())
				{
					case(R.id.faka_button):
						//点击发卡按钮后跳转到发卡页面
						Intent intent = new Intent();
					    intent.setClass(MainMenuActivity.this, GiveOutActivity.class);
					    MainMenuActivity.this.startActivity(intent);
					    break;
					case(R.id.upload_download_button):
						//点击传所有操作数据后下载更新数据
						//检测网络连接
						p2 = ProgressDialog.show(MainMenuActivity.this, "数据更新", "更新中，请稍后...");
					    new myThread1().start();
						break;
					case(R.id.PanDianManagement):
				        Intent Pandianintent = new Intent();
				        Pandianintent.setClass(MainMenuActivity.this, PanDianActivity1.class);
				        MainMenuActivity.this.startActivity(Pandianintent);
						break;
					case(R.id.configuration_button):
				        Intent Configintent = new Intent();
				        Configintent.setClass(MainMenuActivity.this, ConfigActivity.class);
				        MainMenuActivity.this.startActivity(Configintent);
						break;
					case(R.id.BigMatterManagement_button):
						//点击大件管理后跳转到大件管理页面
						Intent largeMatterintent = new Intent();
					    largeMatterintent.setClass(MainMenuActivity.this, LargeMatterManagement.class);
					    MainMenuActivity.this.startActivity(largeMatterintent);
					    break;
					case(R.id.inOutBill_button):
						//点击出入库按钮后跳转到出入库菜单页面
						Intent inOutIntent = new Intent();
						inOutIntent.setClass(MainMenuActivity.this, InOutMenuActivity.class);
					    MainMenuActivity.this.startActivity(inOutIntent);
					    break;						
				}
				
			}
		};
		
		faka_button.setOnClickListener(listener);
		upload_download_button.setOnClickListener(listener);
		inOutBill_button.setOnClickListener(listener);
		bigMatterManagement_button.setOnClickListener(listener);
		panDian_Button.setOnClickListener(listener);
		config_Button.setOnClickListener(listener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	//获取当前用户名，获得当前用户的plandata的操作接口
	public String getUser()
	{
		SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		String user = preference.getString("user","");
		return user;
	}
	
	//上传函数：用于上传所有用户操作的数据
	public boolean uploadData()
	{
		boolean flag1 = true;
		boolean flag2 = true;
		boolean flag3 = true;
		boolean flag4 = true;
		boolean flag5 = true;
		//上传发卡数据
		PositionDataDAO pd =new PositionDataDAO(MainMenuActivity.this);
		if(!pd.getUploadData().isEmpty())
		{
			flag1 =  pd.uploadGivenOutData();
		}
		Log.i(TAG,"发卡数据上传情况标志位:"+flag1);
		//上传计划执行数据
		//计划数据依赖于当前user，先获取当前user
		/*TODO:用于测试，屏蔽这里的代码，测试完成后必须恢复*/
		UploadPlanDataDAO updd = new UploadPlanDataDAO(this,this.getUser());
		if(!updd.getUploadPlanData().isEmpty())
		{
			flag2 = updd.uploadPlanData();
		}
		Log.i(TAG,"计划执行数据上传情况标志位:"+flag2);
		//上传大件操作的暂存数据
		UploadLargeMatterDAO ulmd = new UploadLargeMatterDAO(this);
		if(!ulmd.getUploadLargeMatterData().isEmpty())
		{
			flag3 = ulmd.uploadLargeMatterData();
		}
		Log.i(TAG,"大件操作数据上传情况标志位:"+flag3);
		//上传大件发卡数据的数据库
		LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(this);
		if(!lmgod.getLargeMatterGiveOutData(1).isEmpty())
		{
			flag4 = lmgod.uploadLargeMatterGiveOutData();
		}
		Log.i(TAG, "大件发卡数据上传情况标志位:"+flag4);
		//上传盘点数据
		//TODO:服务器端未完成，完成后恢复此段代码
		PanDianDataDAO pddd = new PanDianDataDAO(this);
		if(!pddd.getPanDianDataList().isEmpty())
		{
			flag5 = pddd.uploadPandianData();
		}
		Log.i(TAG, "盘点数据上传标志位:"+flag5);
		return flag1&&flag2&&flag3&&flag4&&flag5;
	}
	
	/**
	 * 
	 * 检查更新并下载
	 * http://localhost/Ashx/Info.ashx?MethodName=GetInfoStatus&UserId=1
	 * @throws JSONException 
	 */
	
	public  void checkUpdate() throws JSONException
	{
		HttpApi request = new HttpApi();			
		SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		String user = preference.getString("user","");
		String content = request.PostRequest(BaseClassDAO.SERVERHOST, "GetInfoStatus",new BasicNameValuePair("UserId", user));
		JSONObject jo = new JSONObject(content);
		if(jo.getInt("Flag")==1)
		{//可以联网则全部更新
			//TODO:测试中，服务器端完成之前不要加上此段代码
			//清空盘点数据
			PanDianDataDAO pddd = new PanDianDataDAO(this);
			pddd.ClearPandianData();
			Log.i(TAG, "清空盘点数据库");
			//清空并下载大件发卡数据
			LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(this);
			boolean flag1 =lmgod.downLoadLargeMatterGiveOutData();
			Log.i(TAG, "下载大件发卡数据标志位:"+flag1);
			//下载新的大件操作数据库
			LargeMatterDAO lmd = new LargeMatterDAO(this);
			boolean flag2 =lmd.downLoadLargeMatterData();
			Log.i(TAG,"下载大件数据库:"+flag2);
			//清空大件操作数据暂存数据库
			UploadLargeMatterDAO ulmd = new UploadLargeMatterDAO(this);
			ulmd.ClearUploadLargeMatterData();
			Log.i(TAG, "清空大件操作暂存数据!");
			//plandata表的更新是基于当前用户的
			PlanDataDAO p = new PlanDataDAO(this,user);
			p.downLoadPlanData();
			/*清空当前用户所有已执行的计划*/
			UploadPlanDataDAO updd = new UploadPlanDataDAO(this,user);
			updd.ClearPlanData();
			Log.i(TAG, "----------------------更新plan表！！----------");
			/*以下三表的更新根据更新标志位来*/
			List<String> updateInfo = new ArrayList<String>();
			JSONArray ja = jo.getJSONArray("List");
			for(int i=0;i<ja.length();i++)
			{
				JSONObject j = ja.getJSONObject(i);
				if(j.getInt("IsNeedUpdate")==1)
				{
					updateInfo.add(j.getString("MainTableName"));
				}
			}
			if(updateInfo.contains("StoragePosition"))
			{
		    	PositionDataDAO pd = new PositionDataDAO(this);
		    	pd.downLoadPositionData();
		    	Log.i(TAG, "----------------------更新position表！！----------");
			}
			if(updateInfo.contains("MatterBasicInfo"))
			{
				new BaseDataDAO(this).downLoadBaseData();
				Log.i(TAG, "----------------------更新basedata表！！----------");
			}
			if(updateInfo.contains("SystemUser"))
			{
				new UserDataDAO(this).downUserData();
				Log.i(TAG, "----------------------更新User表！！----------");
			}
		}
	}
	
	
	//为android4.2.1系统的兼容性，在线程中检测网络连接
	//用于数据更新时,在MyThread1检查网络后，上传数据以及下载数据
	class myThread1 extends Thread
	{
		public void run()
		{
			Message msg = Message.obtain(handler);
			if(!CheckInternet.checkInternet(MainMenuActivity.this))
			{//没有网络连接，用handler发送更新消息
				msg.what = INTERNET_UNCONNECTED;
				handler.sendMessage(msg);
				return;
			}
			//上传数据，如果数据上传失败，则直接提示并终止
			if(!uploadData())
			{
				msg.what = UPLOAD_FAILED;
				handler.sendMessage(msg);
			}
			else
			{//如果数据上传成功，开始下载数据
				try
				{
					checkUpdate();
					msg.what = UPDATE_SUCCESS;
					handler.sendMessage(msg);
				} catch (JSONException e) 
				{
					e.printStackTrace();
				}
			}	
		}	
	}
	//用于更新下载数据时的等待框
	Handler handler = new Handler()
	{
		public  void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case INTERNET_UNCONNECTED:
				p2.dismiss();
				Toast.makeText(MainMenuActivity.this, "数据更新失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
				//没有网络，则不再执行数据下载和更新
				return;
			case UPLOAD_FAILED:
				p2.dismiss();
				Toast.makeText(MainMenuActivity.this, "数据上传失败，请重新更新！", Toast.LENGTH_SHORT).show();	
				return;
			case UPDATE_SUCCESS:
				p2.dismiss();
				Toast.makeText(MainMenuActivity.this, "数据更新成功！", Toast.LENGTH_SHORT).show();	
				break;
			}
		}
	};
	

}