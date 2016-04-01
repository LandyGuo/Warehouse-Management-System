package com.qpguo.uhf.activity;

import com.atid.at288.lib.IReaderCallback;
import com.atid.at288.lib.ReaderService;
import com.atid.at288.lib.type.BankType;
import com.atid.at288.lib.type.SelectionActionType;
import com.atid.at288.service.aidl.IReaderReceiver;
import com.example.uhf.application.R;
import com.qpguo.uhf.model.PanDianDataModel;
import com.qpguo.uhf.modelDAO.PanDianDataDAO;
import com.qpguo.uhf.utils.ExplainReadInfo;
import com.qpguo.uhf.utils.NumberConvert;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public  class PanDianActivity1 extends Activity implements IReaderCallback
{
	private String TAG = "PanDianActivity1";
	private Button connect;//盘点界面连接手持机
	private Button readButton;//读取标签
	private EditText realNumber;//盘点的实际数量
	private Button writeButton;//更改指定标签的数量
	
	/*显示信息区*/
	private TextView displayPositionName;
	private TextView displayMatterName;
	private TextView displayMatterNumber;
	
	/*关于连接手持机*/
	private  boolean BLUETOOTH_OK_FLAG = false;	//蓝牙状态表示
	private String m_strDeviceAddress = "";//保存当前连接的手持机的MAC地址
	private ReaderService m_Reader = null;	//扫描标签的读写工具类
	
	/*读取标签的16进制内容*/
	private String readHexStorageId=null;
	private String readHexMatterId = null;
	private String readHexCount = null;
	
	/*发送消息表示Reader读写状态*/
	private MyHandler myHandler=null;
	private final int CONNECTED = 100;
	private final int WRITESUCCESS = 200;
	private final int TIMEOUT = 300;
	private final int READSUCCESS = 500;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pandianfunction);
		/*关于连接手持机*/
		myHandler = new MyHandler();
		m_Reader = new ReaderService(this,this);//空的handler,因为连接时不需读写
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
					Toast.makeText(PanDianActivity1.this, "手机不支持蓝牙", Toast.LENGTH_LONG).show();
				     return;  
				}  
				if(!mBluetoothAdapter.isEnabled()){ //蓝牙未开启，则开启蓝牙  
				     Toast.makeText(PanDianActivity1.this, "请先开启蓝牙", Toast.LENGTH_LONG).show();
				     return;
				}  
				if(!BLUETOOTH_OK_FLAG)
				{
					Log.i(TAG, "第一次连接建立中....");
					//第一次连接
					m_Reader.openDeviceListActivity();
				}
				else
				{
					Log.i(TAG, "第二次连接建立中.....");
					m_Reader.setDeviceAddress(m_strDeviceAddress);
					m_Reader.connectMostRecentDevice();
				}
				break;
			case R.id.panDian_read:
				Log.i(TAG,"点击了读取标签按钮!");
				Log.i(TAG, "正在保存上次读取的数据!");
				savePandianInfo();
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity1.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
					return;
				}
				/*读EPC区*/
				m_Reader.readMemory(BankType.EPC, 2, 3);	
				break;
			case R.id.panDian_write_realNumber:
				Log.i(TAG,"点击了改写标签按钮!");
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity1.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
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
					Toast.makeText(PanDianActivity1.this, "输入的实际数量无效!重新输入", Toast.LENGTH_LONG).show();
					return;
				}
				int writeNumber = Integer.parseInt(realCount);
				Log.i(TAG,"用户输入数转化为整数:"+writeNumber);
				//将10进制转化为16进制待写入数
				String newCount = NumberConvert.Decimal_int2Hex_String(writeNumber);
				Log.i(TAG,"用户输入数转化为16进制数:"+newCount);
				Log.i(TAG,"待写入前将16进制数补齐:"+NumberConvert.hex_StringAutoComplete(newCount,4));
				//写入标签
				String str2write = readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
				Log.i(TAG, "写入标签的内容:"+readHexStorageId+str2write);
				maskSet();
				m_Reader.writeMemory(BankType.EPC, 3, str2write,readHexStorageId);
				break;
			}
			
		}
    	
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
			
			IReaderReceiver readerReceiver = new IReaderReceiver.Stub() 
			{

				@Override
				public void onReaderActionChange(char arg0) throws RemoteException 
				{
					if(arg0=='r')
					{
						//开始读
						Log.i(TAG, "开始读");
					}
					if(arg0=='w')
					{
						//开始写
						Log.i(TAG, "开始写");
					}

				}

				@Override
				public void onReaderExtendedProperty(char arg0, String arg1)
						throws RemoteException
				{
					
				}

				@Override
				public void onReaderMessage(String arg0) throws RemoteException 
				{
					
				}

				@Override
				public void onReaderProperty(char arg0, String arg1)
						throws RemoteException 
				{
					
				}

				@Override
				public void onReaderReadTag(int arg0, String arg1)
						throws RemoteException 
				{
					if(arg0==6)
					{
						//读成功
						Log.i(TAG, "读取成功:"+arg1);
						/*读取成功后,解析标签内容*/
						String result = arg1;
						readHexStorageId = result.substring(0, 4);
						readHexMatterId = result.substring(4,8);
						readHexCount = result.substring(8,12);
						Message msg = Message.obtain();
						msg.what = READSUCCESS;
						myHandler.sendMessage(msg);
					}
					m_Reader.stopOperation();
					
				}

				@Override
				public void onReaderResponse(int arg0, String arg1)
						throws RemoteException 
				{
						if(arg0==7&&arg1.equals("01"))
						{
							//写入成功
							Log.i(TAG,"写入成功!");
							Message msg = Message.obtain();
							msg.what = WRITESUCCESS;
							myHandler.sendMessage(msg);
						}
						else
						{
							Log.i(TAG, "写入失败:arg0:"+arg0+"arg1:"+arg1);
							String msg = ReaderService.getResponses(arg1);
							Log.i(TAG, "写入失败提示信息:"+msg);
						}
						m_Reader.stopOperation();
				}

				@Override
				public void onReaderStateChange(int arg0) throws RemoteException 
				{
					if(arg0 ==3)
					{
						//连接成功
						BLUETOOTH_OK_FLAG = true;
						m_strDeviceAddress = m_Reader.getDeviceAddress();
						Log.i(TAG, "连接的MAC地址:"+m_Reader.getDeviceAddress());
						saveConfig();
						// Reader Activate...
						m_Reader.activate();
						Message msg = Message.obtain();
						msg.what = CONNECTED;
						myHandler.sendMessage(msg);
					    Log.i(TAG, "连接成功!");
					}
					
				}

				@Override
				public void onReaderTimeout() throws RemoteException 
				{
					//读取或写入超时
					Log.i(TAG, "Time Out!");
					Message msg = Message.obtain();
					msg.what = TIMEOUT;
					myHandler.sendMessage(msg);
					m_Reader.stopOperation();
				}
				
			};
			
			
			/*此handler用于在界面Toast提示信息*/
			  private class MyHandler  extends Handler
				{
					@SuppressLint("HandlerLeak")
					public  void handleMessage(Message msg)
					{
						switch(msg.what)
						{
						case CONNECTED:
							Toast.makeText(PanDianActivity1.this, "连接成功!", Toast.LENGTH_LONG).show();
							break;
						case WRITESUCCESS:
							Toast.makeText(PanDianActivity1.this, "写入成功!", Toast.LENGTH_LONG).show();
							break;
						case TIMEOUT:
							Toast.makeText(PanDianActivity1.this, "操作失败!", Toast.LENGTH_LONG).show();
							break;
						case READSUCCESS:
							String result = readHexStorageId+readHexMatterId+readHexCount;
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
								Toast.makeText(PanDianActivity1.this,"无法识别标签:"+result, Toast.LENGTH_LONG).show();
							}
							break;
						}
					}
				}
			  
			  /*使用掩码之前先对Reader进行设置*/
				private void maskSet()
				{
					m_Reader.setSelectionBank(BankType.EPC);
					Log.i(TAG,"设置的bank值:EPC");
					m_Reader.setSelectionOffset(32);
					Log.i(TAG, "设置的Offset值:32");
					m_Reader.setSelectionAction(SelectionActionType.Matching);
					Log.i(TAG, "设置的动作:Matching");
				}
			
			
			
			
			


			@Override
			public void onReaderCreated(int errorCode) {
				Log.d(TAG, "onReaderCreated()");
				if (ReaderService.NO_ERROR == errorCode) {
					new Thread() {
						@Override
						public void run() {
							m_Reader.registerReceiver(readerReceiver);
							m_Reader.setDeviceAddress(m_strDeviceAddress);
							m_Reader.setResponseTime(6000);
							m_Reader.startListen();
						}
					}.start();
				} else {
					m_Reader.destroy();
					m_Reader = null;
					Log.i(TAG, "Failed to create reader");
				}
			}
			
			@Override
			protected void onStart() {
				Log.d(TAG, "onStart()");
				super.onStart();
			}

			@Override
			protected void onResume() {
				Log.d(TAG, "onResume()");
				m_Reader.registerReceiver(readerReceiver);
				super.onResume();
			}

			@Override
			protected void onPause() {
				Log.d(TAG, "onPause()");
				m_Reader.unregisterReceiver(readerReceiver);
				super.onPause();
			}

			@Override
			protected void onStop() {
				Log.d(TAG, "onStop()");
				super.onStop();
			}

			

	


}