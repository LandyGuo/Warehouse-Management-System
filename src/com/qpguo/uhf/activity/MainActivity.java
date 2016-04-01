package com.qpguo.uhf.activity;


import com.example.uhf.application.R;
import com.qpguo.uhf.modelDAO.BaseClassDAO;
import com.qpguo.uhf.modelDAO.BaseDataDAO;
import com.qpguo.uhf.modelDAO.PlanDataDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;
import com.qpguo.uhf.modelDAO.UserDataDAO;
import com.qpguo.uhf.utils.CheckInternet;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;




public class MainActivity extends Activity
{
	private String TAG = "MainActivity";
	//��½����Ŀؼ�
	private EditText userName;
	private EditText passWord;
	private EditText hostIp;
	private CheckBox cb_me;
	private CheckBox cb_ip;
	private Button    loginIn;
	private ProgressDialog pd;
	//��½ʱ�����ݿ����
	private UserDataDAO user;
	String userNameText;
	String passWordText;
	String hostIpText;
	//handler�����ź�
	private final int DOWNLOAD_SUCCESS = 1;
	private final int DOWNLOAD_FAILED = 2;	
	private final int LOGINSUCCESS = 3;	
	private final int INTERNET_UNCONNECTED =4;
	private final int LOGINFAILED=5;
	//���������û���¼�Ļ�����Ϣ
	private String USERNAME;
	private String PASSWORD;
	private String HOSTIP;
	private boolean REMEMBERME=true;
	private boolean REMEMBERHOST=true;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.start);
		this.getAllControl();
		//���������ļ�
		loadConfig();
		Log.i(TAG, "��ʱ�����ã�USERNAME"+USERNAME
				+"PASSWORD:"+PASSWORD
				+"HOSTIP:"+HOSTIP
				+"REMEMBERME:"+REMEMBERME
				+"REMEMBERHOST:"+REMEMBERHOST);
		//REMEMBERME = true,��ֱ�Ӽ����û���������
		if(REMEMBERME)
		{
			userName.setText(USERNAME);
			passWord.setText(PASSWORD);
		}
		//REMEMBERHOST = true,��ֱ�Ӽ���IP
		if(REMEMBERHOST)
		{
			hostIp.setText(HOSTIP);
		}
		//��ʼ���й����ݿ�����в���
		OnCheckedChangeListener checkListener = new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) 
			{
				if(cb_me.isChecked())
				{
					Log.i(TAG, "remember me!!");
					REMEMBERME = true;
				}
				else
				{
					Log.i(TAG, "not remember me!!");
					REMEMBERME = false;
				}
				if(cb_ip.isChecked())
				{
					Log.i(TAG, "remember host!!");
					REMEMBERHOST = true;
				}
				else
				{
					Log.i(TAG, "not remember host!!");
					REMEMBERHOST =false;
				}
			}	
		};
		
		cb_me.setOnCheckedChangeListener(checkListener);
		cb_ip.setOnCheckedChangeListener(checkListener);
		
		OnClickListener listener = new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Button pressButton = (Button)v;
				Log.i(TAG, "���µ�½����");
				switch(pressButton.getId())
				{
					case R.id.Login:
						//��ȡ�ϴε�¼�û�
						String recordUser =getUser();
						//��ȡ������˴ε�¼���û���Ϣ
						hostIpText = hostIp.getText().toString();
						userNameText = userName.getText().toString();
						passWordText =passWord.getText().toString();
						USERNAME = userNameText;
						PASSWORD = passWordText;
						HOSTIP = hostIpText;
						
						Log.i(TAG, "�û�����"+userNameText);
						Log.i(TAG, "���룺"+passWordText);
						//ִ��������
						if(hostIpText.equals(""))
						{
							Toast.makeText(MainActivity.this, "�����������IP", Toast.LENGTH_SHORT).show();
							return;
						}
						if(userNameText .equals(""))
						{
							Toast.makeText(MainActivity.this, "�������û���", Toast.LENGTH_SHORT).show();
							return;
						}
						if(passWordText.equals(""))
						{
							Toast.makeText(MainActivity.this, "����������", Toast.LENGTH_SHORT).show();
							return;
						}
						//������ϸ�
						BaseClassDAO.SERVERHOST = hostIpText;
						Log.i(TAG, "����ip��ַ:"+hostIpText);
						//��¼���򣬷�Ϊ�״ε�¼�ͷ��״ε�¼
						pd = ProgressDialog.show(MainActivity.this, "��¼������", "���Ժ�....",false,true);
						if(recordUser.equals(""))
						{
							//�����ǵ�һ�ε�½,����Ҫ���������ӣ���������������
							new myThread().start();
						}
						else
						{//���ǵ�һ�ε�½����˵����User���������ؿ�ֱ�Ӳ��
							//���ظ�UI����
							Message msg = Message.obtain();
							msg.what = DOWNLOAD_SUCCESS;
							user = new UserDataDAO(MainActivity.this);
							if(user.getUserByLoginId(userNameText)==null)
							{
								msg.arg1 = LOGINFAILED;
							}
							else if(!user.getUserByLoginId(userNameText).getPassword().equals(passWordText))
							{
								msg.arg1 = LOGINFAILED;
							}
							else
							{
								msg.arg1 = LOGINSUCCESS;
							}
							//��handler����
							myHandler.sendMessage(msg);
						}
				}	
			}};
		loginIn.setOnClickListener(listener);
		
	}
	//Ϊandroid4.2.1ϵͳ�ļ����ԣ����߳��м����������
	class myThread extends Thread
	{
		public void run()
		{
			Message msg = Message.obtain();
			if(!CheckInternet.checkLink(MainActivity.this))
			{//û����������	
				msg.what = INTERNET_UNCONNECTED;
				myHandler.sendMessage(msg);
			}
			else
			{
				//���������������ػ������ݱ�
				//���ظ�UI����
				downLoadAllData(); 
				UserDataDAO user = new UserDataDAO(MainActivity.this);
				if(user.getUserByLoginId(userNameText)==null)
				{
					//����ʧ��
					Log.i(TAG, "DOWNLOAD_FAILED");
					msg.what = DOWNLOAD_FAILED;
				}
				else if(!user.getUserByLoginId(userNameText).getPassword().equals(passWordText))
				{
					msg.what = DOWNLOAD_SUCCESS;
					msg.arg1 = LOGINFAILED;
					Log.i(TAG, "LOGINFAILED");
				}
				else
				{
					msg.what = DOWNLOAD_SUCCESS;
					msg.arg1 = LOGINSUCCESS;
					Log.i(TAG, "LOGINSUCCESS");
				}
				//��handler����
				myHandler.sendMessage(msg);		
			}
			
		}
	};
	/**
	 * ������������ʱ��������������
	 * ע�⣺���ô˷���ʱ��һ��Ҫ����checkLink()ȷ�������Ѿ����Ժͷ���������
	 * ����ϵͳ�����
	 */
	public  void downLoadAllData() 
	{
		//����������ȫ������
		//plandata��ĸ����ǻ��ڵ�ǰ�û���
		PlanDataDAO p = new PlanDataDAO(this,this.getUser());
		p.downLoadPlanData();
		Log.i(TAG, "----------------------����plan����----------");
    	PositionDataDAO pd = new PositionDataDAO(this);
    	pd.downLoadPositionData();
    	Log.i(TAG, "----------------------����position����----------");
		new BaseDataDAO(this).downLoadBaseData();
		Log.i(TAG, "----------------------����basedata����----------");
		new UserDataDAO(this).downUserData();
		Log.i(TAG, "----------------------����User����----------");
	}
	
	protected void saveConfig()
	{
		SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		SharedPreferences.Editor  editor= preference.edit();
		editor.putString("user", USERNAME);
		editor.putString("password", PASSWORD);
		editor.putString("hostIp", HOSTIP);
		editor.putBoolean("rememberme", REMEMBERME);
		editor.putBoolean("rememberhost", REMEMBERHOST);
		editor.commit();
	}
	
	protected void loadConfig()
	{
		SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		USERNAME = preference.getString("user", "");
		PASSWORD = preference.getString("password", "");
		HOSTIP = preference.getString("hostIp", "");
		REMEMBERME = preference.getBoolean("rememberme", true);
		REMEMBERHOST = preference.getBoolean("rememberhost", true);
	}
	protected String getUser()
	{
		SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		return preference.getString("user", "");
	}
	
	protected void getAllControl()
	{
		userName =(EditText)this.findViewById(R.id.username);
		passWord =(EditText)this.findViewById(R.id.password);
		hostIp =(EditText)this.findViewById(R.id.hostIp);
		loginIn = (Button)this.findViewById(R.id.Login);
		cb_me = (CheckBox) this.findViewById(R.id.check_rememberme);
		cb_ip = (CheckBox) this.findViewById(R.id.check_rememberHost);
	}
	
	Handler myHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case DOWNLOAD_SUCCESS:
				if(msg.arg1==LOGINSUCCESS)
				{
					pd.dismiss();
					Toast.makeText(MainActivity.this, "��½�ɹ�", Toast.LENGTH_SHORT).show();
					//��¼�ɹ��ٱ����û���¼��Ϣ
					saveConfig();
					//��½�ɹ���ҳ����ת����Menu
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MainMenuActivity.class);
					MainActivity.this.startActivity(intent);
				}
				if(msg.arg1==LOGINFAILED)
				{
					Log.i(TAG, "LOGINFAILED:------------1");
					pd.dismiss();
					Toast.makeText(MainActivity.this, "�û��������ڣ����������û��������ԣ�", Toast.LENGTH_SHORT).show();
					Log.i(TAG, "LOGINFAILED:------------2");
					new UserDataDAO(MainActivity.this).downUserData();
					Log.i(TAG, "LOGINFAILED:------------3");
				}
				break;
			case DOWNLOAD_FAILED:
				pd.dismiss();
				Toast.makeText(MainActivity.this, "��½ʧ��", Toast.LENGTH_SHORT).show();
				break;
			case INTERNET_UNCONNECTED:
				pd.dismiss();
				Toast.makeText(MainActivity.this, "���ε�½�������������������ݣ�", Toast.LENGTH_LONG).show();
				return;
				
			}
		}	
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
//	@Override
//	protected void onResume() {
//		loadConfig();
//		super.onResume();
//		if(REMEMBERME)
//		{
//			userName.setText(USERNAME);
//			passWord.setText(PASSWORD);
//		}
//		//REMEMBERHOST = true,��ֱ�Ӽ���IP
//		if(REMEMBERHOST)
//		{
//			hostIp.setText(HOSTIP);
//		}
//		
//	}

	

}