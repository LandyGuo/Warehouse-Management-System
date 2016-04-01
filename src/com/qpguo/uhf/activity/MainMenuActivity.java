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
	//���˵������ϵĹ��ܰ�ť
	private Button faka_button;
	private Button upload_download_button;
	private Button inOutBill_button;
	private Button bigMatterManagement_button;
	private Button panDian_Button;
	private Button config_Button;
	
	//�������ݸ���ʱ��ʾ
	private ProgressDialog p2;
	private final int INTERNET_UNCONNECTED =500;
	//���������ϴ��¼���Ϣ
	private final int UPLOAD_FAILED =600;
	//���ݸ��³ɹ�(�ϴ�������)
	private final int UPDATE_SUCCESS =700;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_menu);
		//�������ò�������
		config_Button = (Button)this.findViewById(R.id.configuration_button);
		//�����̵㹦��
		panDian_Button = (Button)this.findViewById(R.id.PanDianManagement);
		//���ڴ������
		bigMatterManagement_button = (Button)this.findViewById(R.id.BigMatterManagement_button);
		//���ڷ�������
		faka_button = (Button) this.findViewById(R.id.faka_button);
		//�������ݸ��£������ϴ�����������
		upload_download_button = (Button)this.findViewById(R.id.upload_download_button);

		//���ڳ����ƻ�����
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
						//���������ť����ת������ҳ��
						Intent intent = new Intent();
					    intent.setClass(MainMenuActivity.this, GiveOutActivity.class);
					    MainMenuActivity.this.startActivity(intent);
					    break;
					case(R.id.upload_download_button):
						//��������в������ݺ����ظ�������
						//�����������
						p2 = ProgressDialog.show(MainMenuActivity.this, "���ݸ���", "�����У����Ժ�...");
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
						//�������������ת���������ҳ��
						Intent largeMatterintent = new Intent();
					    largeMatterintent.setClass(MainMenuActivity.this, LargeMatterManagement.class);
					    MainMenuActivity.this.startActivity(largeMatterintent);
					    break;
					case(R.id.inOutBill_button):
						//�������ⰴť����ת�������˵�ҳ��
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
	//��ȡ��ǰ�û�������õ�ǰ�û���plandata�Ĳ����ӿ�
	public String getUser()
	{
		SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		String user = preference.getString("user","");
		return user;
	}
	
	//�ϴ������������ϴ������û�����������
	public boolean uploadData()
	{
		boolean flag1 = true;
		boolean flag2 = true;
		boolean flag3 = true;
		boolean flag4 = true;
		boolean flag5 = true;
		//�ϴ���������
		PositionDataDAO pd =new PositionDataDAO(MainMenuActivity.this);
		if(!pd.getUploadData().isEmpty())
		{
			flag1 =  pd.uploadGivenOutData();
		}
		Log.i(TAG,"���������ϴ������־λ:"+flag1);
		//�ϴ��ƻ�ִ������
		//�ƻ����������ڵ�ǰuser���Ȼ�ȡ��ǰuser
		/*TODO:���ڲ��ԣ���������Ĵ��룬������ɺ����ָ�*/
		UploadPlanDataDAO updd = new UploadPlanDataDAO(this,this.getUser());
		if(!updd.getUploadPlanData().isEmpty())
		{
			flag2 = updd.uploadPlanData();
		}
		Log.i(TAG,"�ƻ�ִ�������ϴ������־λ:"+flag2);
		//�ϴ�����������ݴ�����
		UploadLargeMatterDAO ulmd = new UploadLargeMatterDAO(this);
		if(!ulmd.getUploadLargeMatterData().isEmpty())
		{
			flag3 = ulmd.uploadLargeMatterData();
		}
		Log.i(TAG,"������������ϴ������־λ:"+flag3);
		//�ϴ�����������ݵ����ݿ�
		LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(this);
		if(!lmgod.getLargeMatterGiveOutData(1).isEmpty())
		{
			flag4 = lmgod.uploadLargeMatterGiveOutData();
		}
		Log.i(TAG, "������������ϴ������־λ:"+flag4);
		//�ϴ��̵�����
		//TODO:��������δ��ɣ���ɺ�ָ��˶δ���
		PanDianDataDAO pddd = new PanDianDataDAO(this);
		if(!pddd.getPanDianDataList().isEmpty())
		{
			flag5 = pddd.uploadPandianData();
		}
		Log.i(TAG, "�̵������ϴ���־λ:"+flag5);
		return flag1&&flag2&&flag3&&flag4&&flag5;
	}
	
	/**
	 * 
	 * �����²�����
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
		{//����������ȫ������
			//TODO:�����У������������֮ǰ��Ҫ���ϴ˶δ���
			//����̵�����
			PanDianDataDAO pddd = new PanDianDataDAO(this);
			pddd.ClearPandianData();
			Log.i(TAG, "����̵����ݿ�");
			//��ղ����ش����������
			LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(this);
			boolean flag1 =lmgod.downLoadLargeMatterGiveOutData();
			Log.i(TAG, "���ش���������ݱ�־λ:"+flag1);
			//�����µĴ���������ݿ�
			LargeMatterDAO lmd = new LargeMatterDAO(this);
			boolean flag2 =lmd.downLoadLargeMatterData();
			Log.i(TAG,"���ش�����ݿ�:"+flag2);
			//��մ�����������ݴ����ݿ�
			UploadLargeMatterDAO ulmd = new UploadLargeMatterDAO(this);
			ulmd.ClearUploadLargeMatterData();
			Log.i(TAG, "��մ�������ݴ�����!");
			//plandata��ĸ����ǻ��ڵ�ǰ�û���
			PlanDataDAO p = new PlanDataDAO(this,user);
			p.downLoadPlanData();
			/*��յ�ǰ�û�������ִ�еļƻ�*/
			UploadPlanDataDAO updd = new UploadPlanDataDAO(this,user);
			updd.ClearPlanData();
			Log.i(TAG, "----------------------����plan����----------");
			/*��������ĸ��¸��ݸ��±�־λ��*/
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
		    	Log.i(TAG, "----------------------����position����----------");
			}
			if(updateInfo.contains("MatterBasicInfo"))
			{
				new BaseDataDAO(this).downLoadBaseData();
				Log.i(TAG, "----------------------����basedata����----------");
			}
			if(updateInfo.contains("SystemUser"))
			{
				new UserDataDAO(this).downUserData();
				Log.i(TAG, "----------------------����User����----------");
			}
		}
	}
	
	
	//Ϊandroid4.2.1ϵͳ�ļ����ԣ����߳��м����������
	//�������ݸ���ʱ,��MyThread1���������ϴ������Լ���������
	class myThread1 extends Thread
	{
		public void run()
		{
			Message msg = Message.obtain(handler);
			if(!CheckInternet.checkInternet(MainMenuActivity.this))
			{//û���������ӣ���handler���͸�����Ϣ
				msg.what = INTERNET_UNCONNECTED;
				handler.sendMessage(msg);
				return;
			}
			//�ϴ����ݣ���������ϴ�ʧ�ܣ���ֱ����ʾ����ֹ
			if(!uploadData())
			{
				msg.what = UPLOAD_FAILED;
				handler.sendMessage(msg);
			}
			else
			{//��������ϴ��ɹ�����ʼ��������
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
	//���ڸ�����������ʱ�ĵȴ���
	Handler handler = new Handler()
	{
		public  void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case INTERNET_UNCONNECTED:
				p2.dismiss();
				Toast.makeText(MainMenuActivity.this, "���ݸ���ʧ�ܣ������������ӣ�", Toast.LENGTH_SHORT).show();
				//û�����磬����ִ���������غ͸���
				return;
			case UPLOAD_FAILED:
				p2.dismiss();
				Toast.makeText(MainMenuActivity.this, "�����ϴ�ʧ�ܣ������¸��£�", Toast.LENGTH_SHORT).show();	
				return;
			case UPDATE_SUCCESS:
				p2.dismiss();
				Toast.makeText(MainMenuActivity.this, "���ݸ��³ɹ���", Toast.LENGTH_SHORT).show();	
				break;
			}
		}
	};
	

}