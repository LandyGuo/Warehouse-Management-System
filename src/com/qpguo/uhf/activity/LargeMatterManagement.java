package com.qpguo.uhf.activity;

import java.util.List;

import reader.api.blue.Reader;
import reader.api.blue.type.BankType;

import com.example.uhf.application.R;
import com.qpguo.uhf.adapter.LargeMatterHistoryInfo;
import com.qpguo.uhf.model.BaseDataModel;
import com.qpguo.uhf.model.LargeMatterGiveOutModel;
import com.qpguo.uhf.model.LargeMatterModel;
import com.qpguo.uhf.modelDAO.BaseDataDAO;
import com.qpguo.uhf.modelDAO.LargeMatterDAO;
import com.qpguo.uhf.modelDAO.LargeMatterGiveOutDAO;
import com.qpguo.uhf.modelDAO.UploadLargeMatterDAO;
import com.qpguo.uhf.utils.DateTime;
import com.qpguo.uhf.utils.LoadInfo;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class LargeMatterManagement extends Activity
{
	private String TAG = "LargeMatterManagement";
	/*大件管理页面的操作控件*/
	private Button connectButton;//连接手持机
	private Button readTagButton;//读取标签
	private Spinner operationSpinner;//可选操作项
	private Button confirmButton;//确认操作按键
	/*大件管理页面的动态显示区*/
	private TextView largeMatterName;//读取的大件名称
	private TextView largeMatterType;//读取的大件型号规格
	private TextView largeMatterPiccode;//读取的大件图号
	/*大件历史操作显示区*/
	private ListView matterHistoryOperationList;
	/*用于存储用户操作的一些数据*/
	private String USERCHOOSE_CURRENT_OPERATION;//保存用户选择Spinner当前的操作
	private String READ_LARGEMATTER_ID=null;//保存从标签中读取的LargeMatterId
	private String READ_MATTER_ID=null;//保存从标签中读取的MatterId
	private boolean IS_LARGEMATTER_FLAG = false;//保存从标签中读取的是否为大件的标志位
	/*用于连接pos机*/
	/*关于连接手持机*/
	private ProgressDialog pd1;
	private  boolean BLUETOOTH_OK_FLAG = false;	//蓝牙状态表示
	private String m_strDeviceAddress = "";//保存当前连接的手持机的MAC地址
	private Reader m_Reader = null;	//扫描标签的读写工具类
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.largematter);
		/*连接pos机*/
		m_Reader = new Reader(this, m_ReaderHandler);//空的handler,因为连接时不需读写
		m_Reader.setResponseTimeout(6000);//设置Reader默认的读取超时时间
		loadConfig();//加载上次配置主要是MAC：m_strDeviceAddress
		//TODO:用于测试，测试完成后弃用此方法
		//this.generateTestData();
		
		this.findAllControls();
		this.setSpinnerContentAndListener();
		this.setConfirmButtonListener();
		this.setLargeMatterInfoDisplay();
	}
	/**
	 * 此方法用于生成测试数据
	 */
	protected void generateTestData()
	{
		LargeMatterModel lmm1 = new LargeMatterModel("333","出库","84","1","2014-08-08 12:48:00");
		LargeMatterModel lmm2 = new LargeMatterModel("333","出库","84","1",DateTime.getDateTime());
		LargeMatterModel lmm3= new LargeMatterModel("333","出库","84","1","2013-08-08 11:48:00");
		LargeMatterModel lmm4 = new LargeMatterModel("333","出库","84","1",DateTime.getDateTime());
		LargeMatterModel lmm5 = new LargeMatterModel("333","出库","84","1",DateTime.getDateTime());
		LargeMatterModel lmm6 = new LargeMatterModel("333","出库","84","1","2013-08-08 12:48:00");
		LargeMatterModel lmm7 = new LargeMatterModel("666","入库","83","1","2014-08-08 12:48:00");
		LargeMatterModel lmm8 = new LargeMatterModel("666","入库","83","1",DateTime.getDateTime());
		LargeMatterModel lmm9= new LargeMatterModel("666","入库","83","1","2013-08-08 11:48:00");
		LargeMatterModel lmm10 = new LargeMatterModel("666","入库","83","1",DateTime.getDateTime());
		LargeMatterModel lmm11 = new LargeMatterModel("666","入库","83","1",DateTime.getDateTime());
		LargeMatterModel lmm12 = new LargeMatterModel("666","入库","83","1","2013-08-08 12:48:00");
		LargeMatterDAO lmd = new LargeMatterDAO(this);
		lmd.ClearLargeMatterData();
		lmd.insertLargeMatterData(lmm1);
		lmd.insertLargeMatterData(lmm2);
		lmd.insertLargeMatterData(lmm3);
		lmd.insertLargeMatterData(lmm4);
		lmd.insertLargeMatterData(lmm5);
		lmd.insertLargeMatterData(lmm6);
		lmd.insertLargeMatterData(lmm7);
		lmd.insertLargeMatterData(lmm8);
		lmd.insertLargeMatterData(lmm9);
		lmd.insertLargeMatterData(lmm10);
		lmd.insertLargeMatterData(lmm11);
		lmd.insertLargeMatterData(lmm12);
		List<LargeMatterModel> lst =lmd.findHistoryInfoByLargeMatterId("100");
		for(LargeMatterModel l:lst)
		{
			Log.i(TAG, l.toString());
		}
		List<LargeMatterModel> lst1 =lmd.findHistoryInfoByLargeMatterId("200");
		for(LargeMatterModel l:lst1)
		{
			Log.i(TAG, l.toString());
		}
		LargeMatterGiveOutDAO lmgod =new LargeMatterGiveOutDAO(this);
		LargeMatterGiveOutModel model1 = new LargeMatterGiveOutModel
				("222","83","0");
		LargeMatterGiveOutModel model2 = new LargeMatterGiveOutModel
				("333","84","0");
		LargeMatterGiveOutModel model3 = new LargeMatterGiveOutModel
				("444","85","0");
		LargeMatterGiveOutModel model4 = new LargeMatterGiveOutModel
				("555","83","0");
		LargeMatterGiveOutModel model5 = new LargeMatterGiveOutModel
				("666","84","0");
		LargeMatterGiveOutModel model6 = new LargeMatterGiveOutModel
				("777","85","0");
		lmgod.insertItem(model1);
		lmgod.insertItem(model2);
		lmgod.insertItem(model3);
		lmgod.insertItem(model4);
		lmgod.insertItem(model5);
		lmgod.insertItem(model6);
		Log.i(TAG, "生成大件发卡数据!");
	}

	/**
	 * 此方法用于获取当前页面所有可操作控件
	 */
	protected void findAllControls()
	{
		this.connectButton = (Button)this.findViewById(R.id.largeMatter_connect);
		this.readTagButton =(Button)this.findViewById(R.id.readTag);
		this.confirmButton = (Button)this.findViewById(R.id.confirmOperation);
		this.operationSpinner =(Spinner)this.findViewById(R.id.operations);
		this.largeMatterName = (TextView)this.findViewById(R.id.LargeMatterName);
		this.largeMatterType = (TextView)this.findViewById(R.id.LargeMatterType);
		this.largeMatterPiccode = (TextView)this.findViewById(R.id.LargeMatterPiccode);
		this.matterHistoryOperationList = (ListView)this.findViewById(R.id.MatterOperationList);
	}
	/**
	 * 此方法用于设置确认操作按钮的响应的事件
	 */
	protected void setConfirmButtonListener()
	{
		this.confirmButton.setOnClickListener(new ButtonListener());
		this.connectButton.setOnClickListener(new ButtonListener());
		this.readTagButton.setOnClickListener(new ButtonListener());
	}
	/*内部类，用于处理确认按钮的响应事件*/
	class ButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			Button btn = (Button)v;
			switch(btn.getId())
			{
			case R.id.confirmOperation:
				Log.i(TAG,"点击了确认操作按钮！");
				if(IS_LARGEMATTER_FLAG==false)
				{
					Toast.makeText(LargeMatterManagement.this, "请先扫描一个大件！", Toast.LENGTH_LONG).show();
					return;
				}
				//获取当前登录用户,即操作人
				LoadInfo info = new LoadInfo(LargeMatterManagement.this);
				String currentUser = info.getUser();
				//获取操作时间
				String currentTime = DateTime.getDateTime();
				//获取大件暂存操作数据库
				UploadLargeMatterDAO ulmd = new UploadLargeMatterDAO(LargeMatterManagement.this);
				LargeMatterModel lmm = new LargeMatterModel(
						NumberConvert.hex_StringAutoComplete(READ_LARGEMATTER_ID, 6),USERCHOOSE_CURRENT_OPERATION,
						READ_MATTER_ID,currentUser,currentTime);
				ulmd.insertItem(lmm);
				Log.i(TAG, "操作信息:"+lmm.toString());
				Toast.makeText(LargeMatterManagement.this, "操作成功！", Toast.LENGTH_SHORT).show();
				break;		
			case R.id.largeMatter_connect:
				//与手持机连接
				//建立与pos机的连接
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
				if(mBluetoothAdapter == null)
				{  
				        //表明此手机不支持蓝牙  
					Toast.makeText(LargeMatterManagement.this, "手机不支持蓝牙", Toast.LENGTH_LONG).show();
				     return;  
				}  
				if(!mBluetoothAdapter.isEnabled()){ //蓝牙未开启，则开启蓝牙  
				     Toast.makeText(LargeMatterManagement.this, "请先开启蓝牙", Toast.LENGTH_LONG).show();
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
			case R.id.readTag:
				//读取大件标签
				Log.i(TAG,"点击了读取标签！");
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(LargeMatterManagement.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
					return;
				}
				m_Reader.ReadMemory(BankType.EPC.getValue(), 2, 3);
				break;
				
			}
			
		}
		
	}
	
	/**
	 * 此方法用于设置Spinner的内容和监听
	 */
	protected void setSpinnerContentAndListener()
	{
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.operations_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.operationSpinner.setAdapter(adapter);
		this.operationSpinner.setOnItemSelectedListener(new SpinnerListener());
	} 
	/**
	 * 此内部类用于处理Spinner的监听事件
	 */
	class SpinnerListener implements Spinner.OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) 
		{
			// TODO Auto-generated method stub
//			Toast.makeText(LargeMatterManagement.this, "选中了："+parent.getItemAtPosition(position), 
//					Toast.LENGTH_LONG).show();
			USERCHOOSE_CURRENT_OPERATION =(String) parent.getItemAtPosition(position);
		}
		@Override
		public void onNothingSelected(AdapterView<?> parent) 
		{
			// TODO Auto-generated method stub
		}
	}
	
	/**
	 * 此方法用于根据读取大件标签的id信息，查询basedata数据库并显示
	 * 该大件的物资名称、型号规格和图号
	 * 每读取一次标签就需要调用一次来完成列表的显示
	 */
	protected void setLargeMatterInfoDisplay()
	{
		//列表显示部分
		Log.i(TAG, "读取大件的大件编号:"+READ_LARGEMATTER_ID);
		LargeMatterHistoryInfo adapter = new LargeMatterHistoryInfo(this,this.READ_LARGEMATTER_ID);
		this.matterHistoryOperationList.setAdapter(adapter);
		//根据读取的MatterId查询基础数据库获取大件的物资名称、型号规格和图号
		BaseDataDAO bdd = new BaseDataDAO(this);
		Log.i(TAG,"读取该大件的物资编号:"+READ_MATTER_ID);
		BaseDataModel model =BaseDataDAO.findItem(READ_MATTER_ID);
		if(model==null)
		{
			Toast.makeText(LargeMatterManagement.this, "无法找到该大件！", Toast.LENGTH_SHORT).show();
			return;
		}
		this.largeMatterName.setText(model.getName());
		this.largeMatterType.setText(model.getType());
		this.largeMatterPiccode.setText(model.getPiccode());
	}
	
	/**
	 * 此方法用于根据读取大件标签的自身编号信息，查询matterLarge数据库
	 * 获取大件历史操作信息
	 * @param <HistoryItem>
	 */
	protected <HistoryItem> List<HistoryItem> getHistoryInfoByCode()
	{
		//TODO:补充该方法
		return null;
	}
	/**
	 * 此方法用于根据大件的历史操作数据设置显示列表
	 */
	protected void setHistoryListDisplay()
	{
		//TODO:补充该方法
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
				pd1 = ProgressDialog.show(LargeMatterManagement.this, "连接设备", "连接中，请稍后……",false,true);
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
					Toast.makeText(LargeMatterManagement.this, "设备连接成功!", Toast.LENGTH_LONG).show();
				}
				break;
			case Reader.MESSAGE_READ:
				if (msg.arg1 == Reader.ET_TIMEOUT) 
				{
					m_Reader.StopOperation();
					Toast.makeText(LargeMatterManagement.this, "Time Out", Toast.LENGTH_SHORT).show();
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
							/*读取成功后,解析标签内容*/
							String result = data.substring(2);
							/*大件的标签内容为：
							 * FFFF - 32-47 ：大件标识FFFF位
							 * XXXX - 48-63:    大件的物资编号
							 * XXXX - 64-79：大件的自增编号*/
							String readHexLargeMatterFlag = result.substring(0, 4);
							String readHexMatterId = result.substring(4,8);
							String readHexLargeMatterId = result.substring(8,12);
							Log.i(TAG,"读取信息-大件标识:"+readHexLargeMatterFlag
									+" 大件物资编号:"+readHexMatterId+"自增编号:"+readHexLargeMatterId);
							/*解析大件信息*/
							if(readHexLargeMatterFlag.compareToIgnoreCase("FFFF")!=0)
							{
								Toast.makeText(LargeMatterManagement.this, "此物品非大件！", Toast.LENGTH_SHORT).show();
								return;
							}
							IS_LARGEMATTER_FLAG = true;
							READ_LARGEMATTER_ID = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexLargeMatterId)).trim();
							READ_MATTER_ID =String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexMatterId)).trim();
							Log.i(TAG, "解析的大件数据:LargeMatterId:"+READ_LARGEMATTER_ID+" MatterId:"+READ_MATTER_ID);
							/*每读一次显示该大件相关信息*/
							setLargeMatterInfoDisplay();
						}
						if(code =='A' && cc=='r')
						{
							//开始读
						}
						}
			        }
				break;
		        }
		}};
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		m_Reader.OnDestroy();
		super.onBackPressed();
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