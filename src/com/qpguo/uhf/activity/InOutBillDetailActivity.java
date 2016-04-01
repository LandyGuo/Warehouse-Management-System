package com.qpguo.uhf.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.atid.at288.lib.IReaderCallback;
import com.atid.at288.lib.ReaderService;
import com.atid.at288.lib.type.BankType;
import com.atid.at288.lib.type.SelectionActionType;
import com.atid.at288.service.aidl.IReaderReceiver;
import com.example.uhf.application.R;
import com.qpguo.uhf.model.PlanDataModel;
import com.qpguo.uhf.modelDAO.PlanDataDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;
import com.qpguo.uhf.modelDAO.UploadPlanDataDAO;
import com.qpguo.uhf.utils.DateTime;
import com.qpguo.uhf.utils.ExplainReadInfo;
import com.qpguo.uhf.utils.LoadInfo;
import com.qpguo.uhf.utils.NumberConvert;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



/**
 * 此活动用于控制点击入库单列表后，将跳转到可选储位的详情页的相关活动
 */
public class InOutBillDetailActivity extends Activity implements IReaderCallback
{
	private String TAG = "InOutBillDetailActivity";
	/*currentPosition标识当前是第几条数据的详情页*/
	private int currentPosition;
	/*displayData表示所有未执行数据的所有详细信息*/
	private List<PlanDataModel> displayData;
	/*currentPlan标识当前页面所显示的计划*/
	private PlanDataModel currentPlan;
	//界面上显示控件 
	private TextView matterText;//显示上页的物资数量
	private TextView typeText;//显示上页的物资类型
	private TextView operationText;//显示当前的操作类型(由上页传来InOutCount字段判断)
	private TextView notExcutedNumber;//这个需要动态变动，其初始值由上页获得
	//界面下方1,2,3,4四组
	/*布局1,2,3,4*/
	private LinearLayout[] layout = new LinearLayout[4];
	private TextView[] select = new TextView[4];
	private EditText[] number = new EditText[4];
	private Button[] button = new Button[4];
	/*设置返回码*/
	private final int RESULT_OK= 1;

	/*手持机*/
	private ReaderService m_Reader = null;
	private String m_strDeviceAddress = "";//保存当前连接的手持机的MAC地址
	private  boolean BLUETOOTH_OK_FLAG = false;	//蓝牙状态表示
	private String mask;
	/*可用储位id数组*/
	private ArrayList<String> idArray;
	/*标志可用储位是否有标签的数组*/
	private ArrayList<String> labelInfoArray;
	/*需要显示可用储位数据*/
	private ArrayList<String> data;
	/*从界面上读取的操作数量和总数，需要在多个地方操作*/
	private int operateNumber;
	private int currentTotal;
	/*详情页面当前点击执行按钮所对应的行位置*/
	private int currentLocation;
	/*发送消息表示Reader读写状态*/
	private MyHandler myHandler=null;
	private final int CONNECTED = 100;
	private final int WRITESUCCESS = 200;
	private final int TIMEOUT = 300;
	private final int NOTRECON = 400;
	/*待写入标签的字符串*/
   private String str2write = "";
   private Boolean ReadFlag = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		Log.i(TAG, "--------------InBillDetailActivity---------------");
		this.setContentView(R.layout.inbilldetail_info);
		this.getAllControl();
		this.setDisplay();
		/*用于数据测试，*/
        /*在这里获取displaydata*/
		this.displayData = InOutBillActivity.displayData;
		Log.i(TAG, "在InBillActivity中获取的displayData:"+displayData.toString());
		if(displayData.isEmpty())
		{
			Toast.makeText(this, "计划已全部执行！", Toast.LENGTH_LONG).show();
			return;
		}
		/*当前页面显示的计划的所有数据*/
		currentPlan = displayData.get(currentPosition);
		Log.i(TAG, "点击的currentPlan："+currentPlan);
		idArray = new ArrayList<String>(Arrays.asList(currentPlan.getAvailableStorageIdList().split(",")));
		Log.i(TAG, "点击的当前计划可用的idArray:"+idArray.toString());
		labelInfoArray = new ArrayList<String>(Arrays.asList(currentPlan.getLabelInfo().split(",")));
		Log.i(TAG, "当前可用计划的标签情况:"+labelInfoArray.toString());
		/*在setListContent之前必须要先初始化idArray*/
		this.setListContent();
		/*关于连接手持机*/
		// Allocate Reader object
		this.loadConfig();
		m_Reader = new ReaderService(this, this);
		myHandler = new MyHandler();
	}
	
	/**
	 *执行按钮的监听事件,用于读写标签
	 */
	public class Listener implements OnClickListener
	{
		private int id;
		public  Listener(int id)
		{
			super();
			this.id = id;
		}
		@Override
		public void onClick(View v) 
		{
					ExecutePlan(id);
					//TODO:读标签，读成功后根据执行数量修改数量，然后写标签，写成功后
					//1.修改界面显示的未执行数量2.往执行计划数据库中插入一条执行记录(暂时不上传)
					/*这里暂且默认写成功了*/
					
//					/*根据当前按键可用储位的id获取其16进制表示*/
//					String id =NumberConvert.Decimal_int2Hex_String(idArray.get(0));
//					/*把储位id补齐为4个16进制数*/
//					String StorageId = NumberConvert.hex_StringAutoComplete(id, 4);
//					/*根据当前储位的id作为掩码去读标签*/
//					String readMask =StorageId;
//					Log.i(TAG, "储位16进制表示："+StorageId);
//					/*读EPC区*/
//					m_Reader.ReadMemory(BankType.EPC.getValue(), 2, 3,"00B8");
//					/*切记在执行成功之后要更新界面数量显示，及数据库保存等操作*/
			}
			
		}
		
		
	/**
	 * 获取该界面上的显示控件
	 */
	protected void getAllControl()
	{
		matterText =(TextView) this.findViewById(R.id.matterText);
		typeText = (TextView) this.findViewById(R.id.typeText);
		operationText =(TextView)this.findViewById(R.id.operationText);
		notExcutedNumber = (TextView)this.findViewById(R.id.notExcutedNumber);
		/*界面下方的4组数字*/
		select[0] = (TextView)this.findViewById(R.id.select1);
		select[1] = (TextView)this.findViewById(R.id.select2);
		select[2] = (TextView)this.findViewById(R.id.select3);
		select[3] =  (TextView)this.findViewById(R.id.select4);
		number[0] = (EditText)this.findViewById(R.id.number1);
		number[1] = (EditText)this.findViewById(R.id.number2);
	    number[2]=(EditText)this.findViewById(R.id.number3);
		number[3] = (EditText)this.findViewById(R.id.number4);
		button[0] = (Button)this.findViewById(R.id.button1); 
		button[1] = (Button)this.findViewById(R.id.button2); 
		button[2] = (Button)this.findViewById(R.id.button3);
		button[3] = (Button)this.findViewById(R.id.button4);
		/*4组布局*/
		layout[0] = (LinearLayout)this.findViewById(R.id.layout1);
		layout[1]= (LinearLayout)this.findViewById(R.id.layout2);
		layout[2]= (LinearLayout)this.findViewById(R.id.layout3);
		layout[3]= (LinearLayout)this.findViewById(R.id.layout4);
	}
	/**
	 * 此方法用于在界面开始时显示上一页的相关信息
	 */
	protected void setDisplay()
	{
		Intent intent = this.getIntent();
		Bundle bd = intent.getExtras();
		/*用于在界面初始化时标识这条计划所处的位置*/
		currentPosition = bd.getInt("position");
		Log.i(TAG, "点击列表项的currentPosition:"+currentPosition);
		/*界面上方显示*/
		matterText.setText(bd.getString("matterName"));
		typeText.setText(bd.getString("matterType"));
		operationText.setText(bd.getString("matterPlan"));
		notExcutedNumber.setText(bd.getString("NotExcutedNumber"));
	}
	/**
	 * 此方法用于列表显示
	 */
	protected void setListContent()
	{
		this.data = this.getDisplayData();//获取显示数据
		setVisiable();//设置可见性
		setAvailablePosition();//显示可用储位
		setHint();//设置提示
		setButtonListener();//设置按钮的监听事件
	}
	/*一些界面操作常用方法*/
	/**
	 * 此方法用于获取需要显示的行数
	 */
	public int getLength()
	{
		return data.size();
	}
	/**
	 * 此方法用于根据显示数据设置布局的可见性
	 */
	public void setVisiable()
	{
		for(int i=getLength();i<4;i++)
		{
			this.layout[i].setVisibility(View.INVISIBLE);
		}
	}
	/**
	 * 此方法用于可选储位的显示
	 */
	public void setAvailablePosition()
	{
		for(int i=0;i<getLength();i++)
		{
			select[i].setText(data.get(i));
		}
	}
	/**
	 * 此方法用于设置提示，在编辑框第一栏设置数量
	 */
	public void setHint()
	{
		number[0].setText(notExcutedNumber.getText().toString());
	}
	/**
	 * 此方法用于为Button设置监听事件
	 */
	public void setButtonListener()
	{
		for(int i= 0;i<getLength();i++)
		{
			button[i].setOnClickListener(new Listener(i));
		}
	}
	/**
	 *此方法用于获取显示数据
	 */
	public ArrayList<String> getDisplayData()
	{
		ArrayList<String> result = new ArrayList<String>();
		for(String id:idArray)
		{
			//根据id查找position表获取可选储位名称
			Log.i(TAG, "可选储位的id:"+id);
			try
			{
				PositionDataDAO.findPositionData(id).getPositionName();
			}
			catch(Exception e)
			{
				Toast.makeText(this, "无效的储位！！", Toast.LENGTH_LONG).show();
				return result;
			}
			String positionName = PositionDataDAO.findPositionData(id).getPositionName();
			Log.i(TAG,"可选储位名称:"+positionName);
			result.add(positionName);
		}
		Log.i(TAG, "查找的可用储位名称："+result.toString());
		//TODO:虚拟的数据,需要从服务器端获得可选储位id后查询获得可选储位列表
		return  result;
	}
	
	
	/**
	 * 此方法用于在按钮点击后，执行界面数字的修改
	 * location为当前点击按钮的编号
	 */
	public void ExecutePlan(int location)
	{
		currentLocation = location;
		//读取number[currentLocation]的值
		if(number[location].getText().toString().equals(""))
		{
			Toast.makeText(this, "请输入计划后再执行！",Toast.LENGTH_SHORT).show();
			return;
		}
		else 
		{
			try
			{
				Integer.parseInt(number[location].getText().toString().replace(" ", "").trim()); 
			}
			catch(NumberFormatException e)
			{
				Toast.makeText(this, "格式错误，请输入需要执行的数量！",Toast.LENGTH_SHORT).show();
				return;
			}
		}
		operateNumber =Integer.parseInt(number[location].getText().toString().replace(" ", "").trim());
		currentTotal = Integer.parseInt(notExcutedNumber.getText().toString().trim());
		Log.i(TAG, "操作数量："+operateNumber);
		Log.i(TAG, "操作总数："+currentTotal);
		if(operateNumber>currentTotal)
		{
			Toast.makeText(this, "超出计划！",Toast.LENGTH_SHORT).show();
			return;
		}
		//TODO:如果该可用储位没有标签，则直接认为执行成功
		//TODO:测试后恢复此段代码
		else if( labelInfoArray.get(location).equals("0"))
		{
				successOperation();
		}
		else
		{
			if(!BLUETOOTH_OK_FLAG)
			{
				Toast.makeText(InOutBillDetailActivity.this, "请先连接手持机！", Toast.LENGTH_LONG).show();
				return;
			}
			this.maskSet();
			/*当前操作的可用储位id*/
			int OperateStorageId = Integer.parseInt(idArray.get(location).replace(" ", ""));
			/*将其转化为16进制并补齐四位作为掩码*/
			mask =NumberConvert.hex_StringAutoComplete(NumberConvert.Decimal_int2Hex_String(OperateStorageId), 4);
			Log.i(TAG, "读标签的Mask:"+mask);
			//若连接成功则先读取标签
			ReadFlag = false;
			m_Reader.readMemory(BankType.EPC, 2, 3,mask);	

		}
		
	}
	
	

	/**
	 * 此方法用于监听back键返回时，将未执行数量返回到上个列表页面
	 */
	@Override
	public void onBackPressed() 
	{
		/*获取返回时notExcutedNumber的显示值*/
		Intent intent = new Intent();
		Bundle bd =new Bundle();
		bd.putString("notExcutedNumber",notExcutedNumber.getText().toString());
		//把currentPlan的infoId返回，用于修改数据库是否执行完
		bd.putInt("InfoId", currentPlan.getInfoId());
		//把currentPlan的InOutCount返回，因为InfoId和InOutCount一起才唯一标识一条计划
		bd.putInt("InOutCount", currentPlan.getInOutCount());
		intent.putExtras(bd);
		this.setResult(RESULT_OK, intent);
		this.finish();
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

		
		/**
		 * 此方法用于在读写标签都成功的时候，应执行的操作
		 */
		public void successOperation()
		{
			Log.i(TAG, "-----------进入修改界面--------------:"+String.valueOf(currentTotal-operateNumber));
			//修改界面
			notExcutedNumber.setText(String.valueOf(currentTotal-operateNumber));
			//获取当前登陆用户
			LoadInfo info = new LoadInfo(this);
			String user = info.getUser();
			//获取上传计划数据库
			UploadPlanDataDAO upd = new UploadPlanDataDAO(this,user);
			Log.i(TAG, "插入数据库的数据：InfoId:"+String.valueOf(currentPlan.getInfoId()));
			Log.i(TAG, "插入数据库的数据：执行时间:"+DateTime.getDateTime());
			Log.i(TAG, "插入数据库的数据：操作数量:"+String.valueOf(operateNumber));
			Log.i(TAG, "插入数据库的数据：StorageId:"+idArray.get(currentLocation));
			upd.insertItem(String.valueOf(currentPlan.getInfoId()),String.valueOf(currentPlan.getInOutCount()) ,DateTime.getDateTime(), String.valueOf(operateNumber), idArray.get(currentLocation));
		}

		
		@Override
		public void onReaderCreated(int errorCode)
		{
			if (ReaderService.NO_ERROR == errorCode) 
			{
				Log.i(TAG, "Begin Create Reader");
				m_Reader.registerReceiver(readerReceiver);
			} 
			else 
			{
				m_Reader = null;
				Log.i(TAG, "Failed to create reader");
			}
		}
		
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
					/*读取成功后,解析标签内容，弹出读取结果,对于数量增减后，写入标签*/
					String result = arg1;
					if(!ReadFlag)
					{
						Log.i(TAG, "第一次读取成功:"+arg1);
					}
					else
					{
						Log.i(TAG, "第二次读取成功:"+arg1);
					}
					if(result.compareToIgnoreCase(str2write)==0&&ReadFlag)
					{
						Log.i(TAG,"此时读取的值与上次写入值相同,上次写入成功!");
						Message msg = Message.obtain();
						msg.what = WRITESUCCESS;
						myHandler.sendMessage(msg);
						//写入成功
						Log.i(TAG,"写入成功!");
						return;
					}
					else if(ReadFlag)
					{
						//上次写入失败
						Log.i(TAG, "上次写入失败");
						Message msg = Message.obtain();
						msg.what = TIMEOUT;
						myHandler.sendMessage(msg);
						return;
					}
					ReadFlag = true;
					String readHexStorageId = result.substring(0, 4);
					String readHexMatterId = result.substring(4,8);
					String readHexCount = result.substring(8,12);
					try
					{
						String StorageName = ExplainReadInfo.getHexStorageIdInfo(readHexStorageId).getPositionName();
						String MatterName = ExplainReadInfo.getMatterIdInfo(readHexMatterId).getName();
						int Count = ExplainReadInfo.getNumberInfo(readHexCount);
						Log.i(TAG, "result:"+result);
						Log.i(TAG, "StorageId:"+StorageName);
						Log.i(TAG, "MatterId:"+MatterName);
						Log.i(TAG, "TheCount:"+Count);
						int fixCount = 0;
						//判断入库还是出库
						LoadInfo info = new LoadInfo(InOutBillDetailActivity.this);
						PlanDataDAO pdd = new PlanDataDAO(InOutBillDetailActivity.this,info.getUser());
						if(pdd.getInOutByBillId(currentPlan.getBillId()))
						{
							fixCount = Count + operateNumber;
						}
						else
						{
							fixCount = Count - operateNumber;
						}
						Log.i(TAG, "准备写入标签的值:"+fixCount);
						//读取完并修改后写入标签
						String newCount = NumberConvert.Decimal_int2Hex_String(fixCount);
						str2write = readHexStorageId+readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
						String strtemp = readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
						//TODO:写加上mask
						Log.i(TAG, "写入标签的内容:"+str2write);
						m_Reader.writeMemory(BankType.EPC, 3, strtemp,mask);
					}catch(NullPointerException e)
					{
						Message msg = Message.obtain();
						msg.what = NOTRECON;
						myHandler.sendMessage(msg);
					}
				}
			}

			@Override
			public void onReaderResponse(int arg0, String arg1)
					throws RemoteException 
			{
					if(arg0==7&&arg1.equals("01"))
					{
						/*写入成功后再读*/
						m_Reader.readMemory(BankType.EPC, 2, 3,mask);	
					}
					else
					{
						Log.i(TAG, "写入失败:arg0:"+arg0+"arg1:"+arg1);
						String mesg = ReaderService.getResponses(arg1);
						Log.i(TAG, "写入失败提示信息:"+mesg);
						m_Reader.stopOperation();
					}
					
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
		
		/*此handler用于在界面Toast提示信息*/
	  private class MyHandler  extends Handler
		{
			public  void handleMessage(Message msg)
			{
				//super.handleMessage(msg);
				switch(msg.what)
				{
				case CONNECTED:
					Toast.makeText(InOutBillDetailActivity.this, "连接成功!", Toast.LENGTH_LONG).show();
					break;
				case WRITESUCCESS:
					Toast.makeText(InOutBillDetailActivity.this, "写入成功!", Toast.LENGTH_LONG).show();
					successOperation();
					break;
				case TIMEOUT:
					Toast.makeText(InOutBillDetailActivity.this, "Time Out", Toast.LENGTH_LONG).show();
					break;
				case NOTRECON:
					Toast.makeText(InOutBillDetailActivity.this,"无法识别标签!", Toast.LENGTH_LONG).show();
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

		
		
		
		
		
		
		
		
		
}