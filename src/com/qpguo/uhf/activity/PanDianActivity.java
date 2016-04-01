package com.qpguo.uhf.activity;


import reader.api.blue.Reader;
import reader.api.blue.type.BankType;

import com.example.uhf.application.R;
import com.qpguo.uhf.model.PanDianDataModel;
import com.qpguo.uhf.modelDAO.PanDianDataDAO;
import com.qpguo.uhf.utils.ExplainReadInfo;
import com.qpguo.uhf.utils.NumberConvert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public  class PanDianActivity extends Activity
{
	private String TAG = "PanDianActivity";
	private Button connect;//盘点界面连接手持机
	private Button readButton;//读取标签
	private EditText realNumber;//盘点的实际数量
	private Button writeButton;//更改指定标签的数量
	
	/*显示信息区*/
	private TextView displayPositionName;
	private TextView displayMatterName;
	private TextView displayMatterNumber;
	
	/*关于连接手持机*/
	private ProgressDialog pd1;
	private ProgressDialog pd2;
	private  boolean BLUETOOTH_OK_FLAG = false;	//蓝牙状态表示
	private String m_strDeviceAddress = "";//保存当前连接的手持机的MAC地址
	private Reader m_Reader = null;	//扫描标签的读写工具类
	
	/*读取标签的16进制内容*/
	private String readHexStorageId=null;
	private String readHexMatterId = null;
	private String readHexCount = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pandianfunction);
		/*关于连接手持机*/
		m_Reader = new Reader(this, m_ReaderHandler);//空的handler,因为连接时不需读写
		m_Reader.setResponseTimeout(6000);//设置Reader默认的读取超时时间
		loadConfig();//加载上次配置主要是MAC：m_strDeviceAddress
		this.findAllControls();
		this.setListener();
	}
	
	/*获取当前界面所有可操作控件*/
	protected void findAllControls()
	{
		this.connect =(Button)this.findViewById(R.id.panDian_Link);
		this.readButton = (Button)this.findViewById(R.id.panDian_read);
		this.displayPositionName = (TextView)this.findViewById(R.id.panDian_display_position);
		this.displayMatterName = (TextView)this.findViewById(R.id.panDian_display_matterName);
		this.displayMatterNumber = (TextView)this.findViewById(R.id.panDian_display_Number);
		this.realNumber = (EditText)this.findViewById(R.id.panDian_edit_realNumber);
		this.writeButton = (Button)this.findViewById(R.id.panDian_write_realNumber);
	}
	
	/*为按钮设置监听事件*/
	protected void setListener()
	{
		this.connect.setOnClickListener(new buttonListener());
		this.readButton.setOnClickListener(new buttonListener());
		this.writeButton.setOnClickListener(new buttonListener());
	}
	
	/*保存当前的盘点数据*/
	protected void savePandianInfo()
	{
		if(readHexStorageId!=null && readHexMatterId!=null&&readHexCount!=null)
		{
		String StorageId = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexStorageId));
		String MatterId = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexMatterId));
		String LabelCount = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexCount));
		//RealCount从编辑框中获取
		String realCount = realNumber.getText().toString().replace(" ", "");
		if(realCount.equals(""))
		{
			realCount = LabelCount;
		}
		Log.i(TAG,"保存信息：StorageId:"+StorageId+"MatterId:"+MatterId
				+"LabelCount:"+LabelCount+"RealCount:"+realCount);
		PanDianDataModel pddm = new PanDianDataModel(StorageId,MatterId,
				LabelCount,realCount);
		PanDianDataDAO pddd = new PanDianDataDAO(this);
		pddd.insertPanDianData(pddm);
		}
		else
		Log.i(TAG, "没有保存任何信息！");	
	}
	
	/*用户点击返回的监听事件*/
	@Override
	public void onBackPressed() 
	{
		/*保存界面显示的盘点数据*/
		savePandianInfo();
		//TODO:进入页面和离开页面时的调整功率
		m_Reader.SetPowerEx(0);
		m_Reader.OnDestroy();
		super.onBackPressed();
	}
	
	/*连接按钮的监听事件*/
    class buttonListener implements OnClickListener
    {
		@Override
		public void onClick(View v) 
		{
			Button btn = (Button)v;
			switch(btn.getId())
			{
			case R.id.panDian_Link:
				Log.i(TAG, "你点击了连接手持机的按钮!");
				//建立与pos机的连接
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
				if(mBluetoothAdapter == null)
				{  
				        //表明此手机不支持蓝牙  
					Toast.makeText(PanDianActivity.this, "手机不支持蓝牙", Toast.LENGTH_LONG).show();
				     return;  
				}  
				if(!mBluetoothAdapter.isEnabled()){ //蓝牙未开启，则开启蓝牙  
				     Toast.makeText(PanDianActivity.this, "请先开启蓝牙", Toast.LENGTH_LONG).show();
				     return;
				}  
				if(!BLUETOOTH_OK_FLAG)
				{
					Log.i(TAG, "第一次连接建立中....");
					//第一次连接
					m_Reader.OpenDeviceListActivity();
				}
				else
				{
					Log.i(TAG, "第二次连接建立中.....");
					m_Reader.mDeviceAddress = m_strDeviceAddress;
					m_Reader.ConnectMostRecentDevice();
				}
				break;
			case R.id.panDian_read:
				Log.i(TAG,"点击了读取标签按钮!");
				Log.i(TAG, "正在保存上次读取的数据!");
				savePandianInfo();
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
					return;
				}
				/*读EPC区*/
				m_Reader.ReadMemory(BankType.EPC.getValue(), 2, 3);	
				break;
			case R.id.panDian_write_realNumber:
				Log.i(TAG,"点击了改写标签按钮!");
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
					return;
				}
				/*从界面的显示控件上获取待信息*/
				//获取用户输入的盘点的实际数量
				String realCount = realNumber.getText().toString();
				Log.i(TAG, "获取的用户输入数："+realCount);
				//将用户输入转化为int型
				try
				{
					Integer.parseInt(realCount);
				}
				catch(Exception e)
				{
					Toast.makeText(PanDianActivity.this, "输入的实际数量无效!重新输入", Toast.LENGTH_LONG).show();
					return;
				}
				int writeNumber = Integer.parseInt(realCount);
				Log.i(TAG,"用户输入数转化为整数:"+writeNumber);
				//将10进制转化为16进制待写入数
				String newCount = NumberConvert.Decimal_int2Hex_String(writeNumber);
				Log.i(TAG,"用户输入数转化为16进制数:"+newCount);
				Log.i(TAG,"待写入前将16进制数补齐:"+NumberConvert.hex_StringAutoComplete(newCount,4));
				//写入标签
				String str2write = readHexStorageId+readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
				Log.i(TAG, "写入标签的内容:"+str2write);
				m_Reader.WriteMemory(BankType.EPC.getValue(), 2, str2write);
				break;
			}
			
		}
    	
    }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		m_Reader.OnActivityResult(requestCode, resultCode, data);
		//蓝牙已经开启   
		if (requestCode == Reader.REQUEST_CONNECT_DEVICE
		            			  && resultCode == Activity.RESULT_OK)
		  {
			if(!BLUETOOTH_OK_FLAG)
			 {
				pd1 = ProgressDialog.show(PanDianActivity.this, "连接设备", "连接中，请稍后……",false,true);
			 }
		  } 
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
	
	// 用于与pos机连接时加载设置Load Config
			private void loadConfig() 
			{
				SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
				m_strDeviceAddress = prefs.getString("device_address","");
				BLUETOOTH_OK_FLAG = prefs.getBoolean("BLUETOOTH_OK_FLAG",false);
			}

			// 用于与pos机连接后保存设置Save Config
			private void saveConfig()
			{
				SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("device_address", m_strDeviceAddress);
				editor.putBoolean("BLUETOOTH_OK_FLAG", BLUETOOTH_OK_FLAG);
				editor.commit();
			}
			//用于接收与手持机连接的信息
			//这里获取读取或者写入结果
			private Handler m_ReaderHandler = new Handler() 
			{
				public void handleMessage(Message msg)
				{
					switch (msg.what) 
					{
					case Reader.MESSAGE_STATE_CHANGE:
						if(msg.arg1==Reader.STATE_CONNECTED)
						{
							try 
							{
								Thread.sleep(500);
							} catch (InterruptedException e) 
							{
							}
							if(!BLUETOOTH_OK_FLAG)
							{
								pd1.dismiss();
							}
							// Reader Activate...
							m_Reader.Activate();
							BLUETOOTH_OK_FLAG = true;
							//蓝牙连接成功后保存设置
							m_strDeviceAddress = m_Reader.mDeviceAddress;
							Log.i(TAG, "第一次连接建立后：m_strDeviceAddress:"+m_strDeviceAddress);
							Log.i(TAG, "第一次连接建立后：BLUETOOTH_OK_FLAG:"+BLUETOOTH_OK_FLAG);				
							saveConfig();
							/*设备连接成功后设置连接功率*/
//							m_Reader.SetPowerEx(19);
							Toast.makeText(PanDianActivity.this, "设备连接成功!", Toast.LENGTH_LONG).show();
						}
						break;
					case Reader.MESSAGE_READ:
						if (msg.arg1 == Reader.ET_TIMEOUT) 
						{
							pd2.dismiss();
							m_Reader.StopOperation();
							Toast.makeText(PanDianActivity.this, "Time Out", Toast.LENGTH_SHORT).show();
						}
						else 
						{
							String data = new String((byte[]) msg.obj);
							char prefix = data.charAt(0);
							char code = data.charAt(1);
							char cc = data.charAt(2);
							if (prefix == '>') 
							{
								// Reponse Command
								if (code == 'T')
								{
									pd2.dismiss();
									/*读取成功后,解析标签内容*/
									String result = data.substring(2);
									readHexStorageId = result.substring(0, 4);
									readHexMatterId = result.substring(4,8);
									readHexCount = result.substring(8,12);
									try
									{
										String StorageName = ExplainReadInfo.getHexStorageIdInfo(readHexStorageId).getPositionName();
										String MatterName = ExplainReadInfo.getMatterIdInfo(readHexMatterId).getName();
										int Count = ExplainReadInfo.getNumberInfo(readHexCount);
										//将读取的信息设置显示
										displayPositionName.setText(StorageName);
										displayMatterName.setText(MatterName);
										displayMatterNumber.setText(String.valueOf(Count));
										//设置盘点实际数量的默认提示
										realNumber.setText(String.valueOf(Count));
										/*读取任务到此结束*/
										Log.i(TAG, "result:"+result);
										Log.i(TAG, "StorageName:"+StorageName);
										Log.i(TAG, "MatterName:"+MatterName);
										Log.i(TAG, "TheCount:"+Count);
									}
									catch(NullPointerException e)
									{
										Toast.makeText(PanDianActivity.this,"无法识别标签:"+result, Toast.LENGTH_LONG).show();
									}
								}
								if(code =='A' && cc=='r')
								{
									//开始读
									pd2 = ProgressDialog.show(PanDianActivity.this, "正在读取标签", "请稍侯.....",false,true);
								}
								if(code =='A' && cc=='w')
								{
									//开始写
									pd2 = ProgressDialog.show(PanDianActivity.this, "正在写入标签", "请稍侯.....",false,true);
								}
								if(code == 'C')
								{
									    pd2.dismiss();
										if(data.substring(2).equals("01"))
										{
											//写成功
											Toast.makeText(PanDianActivity.this, "操作成功！", Toast.LENGTH_SHORT).show();
										}
										else
										{
											//写失败
											m_Reader.StopOperation();
											Toast.makeText(PanDianActivity.this, Reader.Responses(data.substring(2)), Toast.LENGTH_SHORT).show();
										}
								}
					        }
				        }
				}};
			};
			
			@Override
			protected void onStart() {
				super.onStart();
				m_Reader.OnStart();
			}

			@Override
			protected void onResume() {
				super.onResume();
				loadConfig();
				m_Reader.OnResume();
			}

			@Override
			protected void onPause() {
				m_Reader.OnPause();
				super.onPause();
			}

			@Override
			protected void onStop() {
				saveConfig();
				m_Reader.OnStop();
				super.onStop();
			}

			@Override
			protected void onDestroy() {
				saveConfig();
				super.onDestroy();
				m_Reader.OnDestroy();
			}
	


}