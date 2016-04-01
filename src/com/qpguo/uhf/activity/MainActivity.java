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
	//登陆界面的控件
	private EditText userName;
	private EditText passWord;
	private EditText hostIp;
	private CheckBox cb_me;
	private CheckBox cb_ip;
	private Button    loginIn;
	private ProgressDialog pd;
	//登陆时的数据库操作
	private UserDataDAO user;
	String userNameText;
	String passWordText;
	String hostIpText;
	//handler返回信号
	private final int DOWNLOAD_SUCCESS = 1;
	private final int DOWNLOAD_FAILED = 2;	
	private final int LOGINSUCCESS = 3;	
	private final int INTERNET_UNCONNECTED =4;
	private final int LOGINFAILED=5;
	//用来保存用户登录的基本信息
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
		//加载配置文件
		loadConfig();
		Log.i(TAG, "此时的配置：USERNAME"+USERNAME
				+"PASSWORD:"+PASSWORD
				+"HOSTIP:"+HOSTIP
				+"REMEMBERME:"+REMEMBERME
				+"REMEMBERHOST:"+REMEMBERHOST);
		//REMEMBERME = true,则直接加载用户名和密码
		if(REMEMBERME)
		{
			userName.setText(USERNAME);
			passWord.setText(PASSWORD);
		}
		//REMEMBERHOST = true,则直接加载IP
		if(REMEMBERHOST)
		{
			hostIp.setText(HOSTIP);
		}
		//初始化有关数据库的所有操作
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
				Log.i(TAG, "按下登陆按键");
				switch(pressButton.getId())
				{
					case R.id.Login:
						//获取上次登录用户
						String recordUser =getUser();
						//获取并保存此次登录的用户信息
						hostIpText = hostIp.getText().toString();
						userNameText = userName.getText().toString();
						passWordText =passWord.getText().toString();
						USERNAME = userNameText;
						PASSWORD = passWordText;
						HOSTIP = hostIpText;
						
						Log.i(TAG, "用户名："+userNameText);
						Log.i(TAG, "密码："+passWordText);
						//执行输入检测
						if(hostIpText.equals(""))
						{
							Toast.makeText(MainActivity.this, "请输入服务器IP", Toast.LENGTH_SHORT).show();
							return;
						}
						if(userNameText .equals(""))
						{
							Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
							return;
						}
						if(passWordText.equals(""))
						{
							Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
							return;
						}
						//输入检测合格
						BaseClassDAO.SERVERHOST = hostIpText;
						Log.i(TAG, "主机ip地址:"+hostIpText);
						//登录程序，分为首次登录和非首次登录
						pd = ProgressDialog.show(MainActivity.this, "登录服务器", "请稍后....",false,true);
						if(recordUser.equals(""))
						{
							//表明是第一次登陆,必须要有网络连接，并下载所有数据
							new myThread().start();
						}
						else
						{//不是第一次登陆，则说明有User表，不用下载可直接查表
							//返回给UI数据
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
							//给handler发送
							myHandler.sendMessage(msg);
						}
				}	
			}};
		loginIn.setOnClickListener(listener);
		
	}
	//为android4.2.1系统的兼容性，在线程中检测网络连接
	class myThread extends Thread
	{
		public void run()
		{
			Message msg = Message.obtain();
			if(!CheckInternet.checkLink(MainActivity.this))
			{//没有网络连接	
				msg.what = INTERNET_UNCONNECTED;
				myHandler.sendMessage(msg);
			}
			else
			{
				//有网络连接则下载基本数据表
				//返回给UI数据
				downLoadAllData(); 
				UserDataDAO user = new UserDataDAO(MainActivity.this);
				if(user.getUserByLoginId(userNameText)==null)
				{
					//下载失败
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
				//给handler发送
				myHandler.sendMessage(msg);		
			}
			
		}
	};
	/**
	 * 在有网络连接时，下载所有数据
	 * 注意：调用此方法时，一定要调用checkLink()确保程序已经可以和服务器交互
	 * 否则系统会崩溃
	 */
	public  void downLoadAllData() 
	{
		//可以联网则全部更新
		//plandata表的更新是基于当前用户的
		PlanDataDAO p = new PlanDataDAO(this,this.getUser());
		p.downLoadPlanData();
		Log.i(TAG, "----------------------更新plan表！！----------");
    	PositionDataDAO pd = new PositionDataDAO(this);
    	pd.downLoadPositionData();
    	Log.i(TAG, "----------------------更新position表！！----------");
		new BaseDataDAO(this).downLoadBaseData();
		Log.i(TAG, "----------------------更新basedata表！！----------");
		new UserDataDAO(this).downUserData();
		Log.i(TAG, "----------------------更新User表！！----------");
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
					Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
					//登录成功再保存用户登录信息
					saveConfig();
					//登陆成功后页面跳转到主Menu
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, MainMenuActivity.class);
					MainActivity.this.startActivity(intent);
				}
				if(msg.arg1==LOGINFAILED)
				{
					Log.i(TAG, "LOGINFAILED:------------1");
					pd.dismiss();
					Toast.makeText(MainActivity.this, "用户名不存在！联网更新用户名后再试！", Toast.LENGTH_SHORT).show();
					Log.i(TAG, "LOGINFAILED:------------2");
					new UserDataDAO(MainActivity.this).downUserData();
					Log.i(TAG, "LOGINFAILED:------------3");
				}
				break;
			case DOWNLOAD_FAILED:
				pd.dismiss();
				Toast.makeText(MainActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
				break;
			case INTERNET_UNCONNECTED:
				pd.dismiss();
				Toast.makeText(MainActivity.this, "初次登陆请先连接网络下载数据！", Toast.LENGTH_LONG).show();
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
//		//REMEMBERHOST = true,则直接加载IP
//		if(REMEMBERHOST)
//		{
//			hostIp.setText(HOSTIP);
//		}
//		
//	}

	

}