package com.qpguo.uhf.activity;


import java.util.Map;

import reader.api.blue.Reader;
import reader.api.blue.type.BankType;

import com.example.uhf.application.R;
import com.qpguo.uhf.adapter.GiveOutAdapter;
import com.qpguo.uhf.modelDAO.LargeMatterGiveOutDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;
import com.qpguo.uhf.utils.NumberConvert;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class GiveOutActivity extends Activity
{

	private String TAG = "GiveOutActivity";
	private ProgressDialog pd1;//蓝牙连接的等待框
	private ProgressDialog pd2;//写入过程的等待框
	//蓝牙状态表示
	private  boolean BLUETOOTH_OK_FLAG = false;
	//保存当前连接的手持机的MAC地址
	private String m_strDeviceAddress = "";
	//扫描标签的读写工具类
	private Reader m_Reader = null;
	//界面上的可交互控件
	private Button connectButton;
	//保存当前的ListView中用户操作的发卡条目的StorageId
	private static String CURRENT_STORAGEID="";
	//标记当前选到列表的子Item项view
	private TextView currentView;
	//保存当前选中的数据项
	private Map<String,Object> currentItem;
	//用来区写入内容与后缀码
	private boolean WRITEFLAG = false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.faka_page);
		m_Reader = new Reader(this, m_ReaderHandler);
		m_Reader.setResponseTimeout(6000);//设置Reader默认的读取超时时间
		//加载上次配置主要是MAC：m_strDeviceAddress
		loadConfig();
		Log.i(TAG, "加载配置后：m_strDeviceAddress："+m_strDeviceAddress);
		Log.i(TAG, "加载配置后：BLUETOOTH_OK_FLAG："+BLUETOOTH_OK_FLAG);
		ListView lst = (ListView) this.findViewById(R.id.list_space);
		connectButton =(Button)this.findViewById(R.id.connect_pos_button);
		final GiveOutAdapter adapter = new GiveOutAdapter(this);
		lst.setAdapter(adapter);
		Log.i(TAG, "-----------5--------------");
		lst.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) 
			{
				//将当前View指向选中的View项，在Handler中对其修改，若写成功，标识为红色
				currentView = (TextView)view.findViewById(R.id.fakalistitem_positionName);
				// TODO Auto-generated method stub
				//确定按的是哪一行，并获取那一行的数据
				Log.i(TAG, adapter.data.get(position).toString());
				currentItem = adapter.data.get(position);
				String StorageId = null,MatterId=null ,TheCount= null;
				if(!currentItem.containsKey("LargeMatterId"))
				{
					Log.i(TAG, "当前点击的不是大件！");
					//保存当前操作的StorageId
					CURRENT_STORAGEID = String.valueOf(currentItem.get("StorageId"));
					//待写入标签的原始数据StorageId,MatterId,TheCount
					StorageId=NumberConvert.Decimal_int2Hex_String(currentItem.get("StorageId"));
					MatterId =NumberConvert.Decimal_int2Hex_String(currentItem.get("MatterId"));
					TheCount=NumberConvert.Decimal_int2Hex_String(currentItem.get("TheCount"));
					Log.i(TAG, "转换后：StorageId:"+StorageId);
					Log.i(TAG, "转换后：MatterId:"+MatterId);
					Log.i(TAG, "转换后：TheCount:"+TheCount);
				}
				else
				{
					Log.i(TAG, "当前点击了大件!");
					//对于大件的处理
					StorageId="FFFF";
					MatterId =NumberConvert.Decimal_int2Hex_String(currentItem.get("MatterId"));
					TheCount=NumberConvert.Decimal_int2Hex_String(currentItem.get("LargeMatterId"));
				}
				//将其补齐到16bit
				StorageId =NumberConvert.hex_StringAutoComplete(StorageId, 4);
				MatterId =NumberConvert.hex_StringAutoComplete(MatterId, 4);
				TheCount =NumberConvert.hex_StringAutoComplete(TheCount, 4);
				Log.i(TAG, "待写入标签：StorageId:"+StorageId);
				Log.i(TAG, "待写入标签：MatterId:"+MatterId);
				Log.i(TAG, "待写入标签：TheCount:"+TheCount);
				//发卡时从标签的32位写到96位，高位直接补0,write函数起始2，长度为6
				String str2Write = StorageId+MatterId+TheCount;
//				String str2Write = StorageId+MatterId+TheCount+"000000000000";
				/*                                    4*4            4*4           4*4                 4*12        */
				//点击某一行准备写入标签时，首先检测蓝牙连接
				Log.i(TAG, "待写入数据："+str2Write);
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(GiveOutActivity.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
					return;
				}
				//若连接成功则写入数据 EPC区,发卡时无mask
				//WRITEFLAG=true 表明是写入数据
				WRITEFLAG = true;
				m_Reader.WriteMemory(BankType.EPC.getValue(), 2, str2Write);	
			}
		});
		
		//connectButton的listener,用于点击按钮时与手持机蓝牙连接
		OnClickListener buttonListener = new OnClickListener()
		{
			public void onClick(View v) 
			{
				Button btn =(Button)v;
				switch(btn.getId())
				{
				case R.id.connect_pos_button:
					//连接蓝牙
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
					if(mBluetoothAdapter == null)
					{  
					        //表明此手机不支持蓝牙  
						Toast.makeText(GiveOutActivity.this, "手机不支持蓝牙", Toast.LENGTH_LONG).show();
					     return;  
					}  
					if(!mBluetoothAdapter.isEnabled()){ //蓝牙未开启，则开启蓝牙  
					     Toast.makeText(GiveOutActivity.this, "请先开启蓝牙", Toast.LENGTH_LONG).show();
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
				}	
			}
		};
		connectButton.setOnClickListener(buttonListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		// TODO Auto-generated method stub
		m_Reader.OnActivityResult(requestCode, resultCode, data);
		//蓝牙已经开启   
		if (requestCode == Reader.REQUEST_CONNECT_DEVICE
		            			  && resultCode == Activity.RESULT_OK)
		  {
			if(!BLUETOOTH_OK_FLAG)
			 {
				pd1 = ProgressDialog.show(GiveOutActivity.this, "连接设备", "连接中，请稍后……",false,true);
			 }
		  } 
	}

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
						Toast.makeText(GiveOutActivity.this, "设备连接成功!", Toast.LENGTH_LONG).show();
					}
					break;
				case Reader.MESSAGE_READ:
					if (msg.arg1 == Reader.ET_TIMEOUT) 
					{
						pd2.dismiss();
						m_Reader.StopOperation();
						Toast.makeText(GiveOutActivity.this, "Time Out", Toast.LENGTH_SHORT).show();
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
//								Toast.makeText(GiveOutActivity.this, "读取结果："+data.substring(2), Toast.LENGTH_LONG).show();
							}
							if(code =='A' && cc=='r')
							{
								//开始读
							}
							if(code =='A' && cc=='w')
							{
								//开始写
								pd2 = ProgressDialog.show(GiveOutActivity.this, "正在写入标签", "请稍侯.....",false,true);
							}
							if(code == 'C')
							{
									pd2.dismiss();
									if(data.substring(2).equals("01"))
									{
									if(WRITEFLAG)
									{
									//第一次写入成功，后需再次写填充位
									if(!currentItem.containsKey("LargeMatterId"))
									{
									//写入成功后修改position表中已发卡的标志位IsGiveOut和上传标志位IsUpload
									PositionDataDAO.updateTheFlag(CURRENT_STORAGEID);
									}
									else
									{//大件写成功
										LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(GiveOutActivity.this);
										lmgod.updateGiveOutInfo((String)currentItem.get("LargeMatterId"));
									}
									//上传数据，用于测试,实际情况下应扫完之后一起上传
									//PositionDataDAO pdd = new PositionDataDAO(GiveOutActivity.this);
									//pdd.uploadGivenOutData();
									//Toast.makeText(GiveOutActivity.this, "数据写入成功！", Toast.LENGTH_SHORT).show();
									WRITEFLAG = false;
									m_Reader.WriteMemory(BankType.EPC.getValue(), 5, "000000000000");
									}
									else
									{
										//第二次写入成功
										//Toast.makeText(GiveOutActivity.this, "填充位写入成功！", Toast.LENGTH_SHORT).show();
										Toast.makeText(GiveOutActivity.this, "写入成功！", Toast.LENGTH_SHORT).show();
										currentView.setTextColor(Color.RED);
									}
								}
								else
								{
									m_Reader.StopOperation();
									WRITEFLAG = false;
									Toast.makeText(GiveOutActivity.this, Reader.Responses(data.substring(2)), Toast.LENGTH_SHORT).show();
								}
							}
				        }
			        }
				}
				super.handleMessage(msg);
	      }
		};
		
		// Load Config
		private void loadConfig() 
		{
			SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
			m_strDeviceAddress = prefs.getString("device_address","");
			BLUETOOTH_OK_FLAG = prefs.getBoolean("BLUETOOTH_OK_FLAG",false);
		}

		// Save Config
		private void saveConfig()
		{
			SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("device_address", m_strDeviceAddress);
			editor.putBoolean("BLUETOOTH_OK_FLAG", BLUETOOTH_OK_FLAG);
			editor.commit();
		}

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