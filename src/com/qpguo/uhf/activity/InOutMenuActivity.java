package com.qpguo.uhf.activity;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import com.example.uhf.application.R;
import com.qpguo.uhf.activity.BlueToothService.OnReceiveDataHandleEvent;
import com.qpguo.uhf.model.PlanDataModel;
import com.qpguo.uhf.modelDAO.PlanDataDAO;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
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




public  class InOutMenuActivity extends Activity
{
	private String TAG = "InOutMenuActivity";
	//出入库菜单页面的按钮
	private Button printPlanButton;//打印计划按钮
	private Button inBillButton;//出入库按钮
	//用于打印
	private BlueToothService mBTService ;
	private Set<BluetoothDevice> devices;
	private ProgressDialog p1;
	public static final int MESSAGE_STATE_CHANGE = 1;//蓝牙连接状态
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	//用于读取默认配置
	private String PrinterMacAddress;
	private int PrinterTimes;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.inout_menu);
		this.loadConfig();
		findAllControl();
		Listener listener = this.getListener();
		setListeners(listener);
		//用于打印功能
		mBTService = new BlueToothService(this,mhandler);
		//用于扫描蓝牙打印机的广播信号接收反馈
				mBTService.setOnReceive(new OnReceiveDataHandleEvent()
				{
					@Override
					public void OnReceive(BluetoothDevice devices) 
					{
						if(devices!=null)
						{
							Log.i(TAG, "----------------4--------------");
							if(devices.getAddress().equals(PrinterMacAddress))
							{
								Log.i(TAG, "----------------5--------------");
								mBTService.ConnectToDevice(devices.getAddress());
							}
						}
					}	
				});
	}
	/**
	 * 用于寻找当前页面的控件视图
	 */
	protected void findAllControl()
	{
		printPlanButton = (Button) this.findViewById(R.id.printInOutPlan_button);
		inBillButton = (Button)this.findViewById(R.id.inBill_button);
	}
	/**
	 * 用于设置按钮的监听事件
	 */
	protected void setListeners(Listener listener)
	{
		printPlanButton.setOnClickListener(listener);
		inBillButton.setOnClickListener(listener);
	}
	
	/**
	 * 获取当前的listner实例
	 */
	protected Listener getListener()
	{
		return new Listener();
	}
	/**
	 * 监听内部类，用于定义监听事件
	 */
	class  Listener implements OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			Button temp = (Button)v;
			switch(temp.getId())
			{
			case R.id.printInOutPlan_button:
				//打印计划按钮的响应事件
				//点击打印计划按钮后执行蓝牙连接和打印计划
				p1 =ProgressDialog.show(InOutMenuActivity.this, "正在连接打印机", "请稍后....",false,true);
				if(mBTService.HasDevice())
				{
					if(mBTService.IsOpen())
					{
						new Thread() 
						{
							public void run() 
							{
								mBTService.ScanDevice();
							}
						}.start();
						devices = mBTService.GetBondedDevice();
						if(!devices.isEmpty())
						{
							for(BluetoothDevice bt:devices)
							{
								if(bt.getAddress().equals(PrinterMacAddress))
								{
									mBTService.StopScan();
									//mBTService.DisConnected();
									mBTService.ConnectToDevice(bt.getAddress());
								}
							}
						}
						Log.i(TAG,"state:"+mBTService.GetScanState());
					}
					else
					{
						mBTService.OpenDevice();
					}
				}
				else
				{
					Toast.makeText(InOutMenuActivity.this, "没有蓝牙设备！", Toast.LENGTH_LONG).show();
					return;
				}
				break;
			case R.id.inBill_button:
				//出入库按钮的响应事件
				switchInPlanView();//跳转到入库视图
				break;
			}
		}
	}
	/**
	 * 此函数用于视图跳转到入库计划
	 */
	protected void switchInPlanView()
	{
		Intent intent = new Intent();
		intent.setClass(this,InOutBillActivity.class);
		this.startActivity(intent);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//用于蓝牙打印机模块，接收打印机反馈
		Handler mhandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MESSAGE_STATE_CHANGE:// 蓝牙连接状态
					switch (msg.arg1) {
					case BlueToothService.STATE_CONNECTED:
						break;
					case BlueToothService.STATE_CONNECTING:// 正在连接
						break;
					case BlueToothService.STATE_LISTEN:
					case BlueToothService.STATE_NONE:
						break;
					case BlueToothService.SUCCESS_CONNECT:
						p1.dismiss();
						Toast.makeText(InOutMenuActivity.this, "连接打印机成功！", Toast.LENGTH_LONG).show();// 已经连接
						//连接成功在这里执行打印计划
						PlanDataDAO pd = new PlanDataDAO(InOutMenuActivity.this,getUser());
						List<String> BillIdList = pd.getDistinctBillId();
						for(String BillId:BillIdList)
						{
							//默认每份计划打印2次
							printPlan(InOutMenuActivity.this,mBTService, BillId,PrinterTimes);
						}
						Toast.makeText(InOutMenuActivity.this, "计划已打印！", Toast.LENGTH_LONG).show();
						//打印完后断开蓝牙连接
						mBTService.DisConnected();
						//Toast.makeText(MainMenuActivity.this, "断开与打印机的连接！", Toast.LENGTH_LONG).show();
						break;
					case BlueToothService.FAILED_CONNECT:
						Toast.makeText(InOutMenuActivity.this, "连接打印机失败！", Toast.LENGTH_LONG).show();
						break;
					case BlueToothService.LOSE_CONNECT:
						Toast.makeText(InOutMenuActivity.this, "失去与打印机的连接！", Toast.LENGTH_LONG).show();
						break;
					}
					break;
				case MESSAGE_READ:
					// sendFlag = false;//缓冲区已满
					break;
				case MESSAGE_WRITE:// 缓冲区未满
					// sendFlag = true;
					break;
				}
			}
		};
		
		//根据BillId获取打印计划所需要的数据
		public  String getPrintData(String BillId)
		{
			//获取当前登录用户，该功能需要保证用户登录时一定要保存当前登录用户
			//获取当前用户计划
			PlanDataDAO pd = new  PlanDataDAO(this,this.getUser());
			List<PlanDataModel> list = pd.getPrintPlanList(BillId);
			StringBuffer sb = new StringBuffer();
			for(PlanDataModel p:list)
			{
				//MatterName:"抬杠 ；1800~2000mm ；"
				//用于获取名称和型号规格
				String name = p.getMatterName().split("；")[0];
				String type = p.getMatterName().split("；")[1];
				//获取数量
				String InOutCount = String.valueOf(Math.abs(p.getInOutCount()));
				String item = MessageFormat.format("{0}\t\t{1}\t\t{2}\n",name, type,InOutCount);
				sb.append(item);
			}
			return sb.toString();
		}
		//函数功能：打印相应BillId的计划列表，参数times表示打印的份数
		public void printPlan(Context context,BlueToothService mBTService,String BillId,int times)
		{
			if (mBTService.getState() != BlueToothService.STATE_CONNECTED)
			{
				Toast.makeText(context,"请先与蓝牙打印机连接", 2000).show();
				return;
			}
			PlanDataDAO pd = new PlanDataDAO(this,this.getUser());
			//打印的title内容
			String title = "";
			if(pd.getInOutByBillId(BillId))
			{
				title = MessageFormat.format("\t\t   {0}库单\n","入");
			}
			else
			{
				title = MessageFormat.format("\t\t   {0}库单\n","出");
			}
			//获取打印的items
			String items = this.getPrintData(BillId);
			//没有内容不打印
			if(items.equals(""))return;
			/*新增加的部分，在这里获取领料车间，时间，以及发料人*/
			List<PlanDataModel> pdl = pd.getPrintPlanList(BillId);
			//获取该计划列表中的第一项
			PlanDataModel p =pdl.get(0);
			String Workshop = p.getWorkshop();
			String GetPlanTime = p.getGetPlanTime();
			String GiveOutPerson = p.getGiveOutPerson();
			String GetPerson = p.getGetPerson();
			String info = "",end="";
			if(pd.getInOutByBillId(BillId))
			{/*入库单*/
				//打印的info内容,这里领料车间和时间是需要替换的内容
				info =MessageFormat.format("\n时间：{0}\n",GetPlanTime);
				//发料人和领料人,这里发料人是需要替换的内容
				end =MessageFormat.format("收料人：{0}\t\t\n\n\n\n\n\n\n", GetPerson);
			}
			else
			{
				//打印的info内容,这里领料车间和时间是需要替换的内容
				info =MessageFormat.format("领料车间：{0}\n时间：{1}\n",Workshop,GetPlanTime);
				//发料人和领料人,这里发料人是需要替换的内容
				end =MessageFormat.format("发料人：{0}\t\t领料人：\n\n\n\n\n\n\n", GiveOutPerson);
			}

			//打印的列名称
			String columns ="名称\t\t型号规格\t\t数量\n";
		
			for(int i=0;i<times;i++)
			{
				//开始打印过程
				mBTService.write(new byte[]{27,68,4,0});
				//设置标题字体大小
				mBTService.write(new byte[]{27,56,3});
				//写标题
				mBTService.PrintCharacters(title);
				//设置其它字体大小
				mBTService.write(new byte[]{27,56,0});
				//写车间和时间
				mBTService.PrintCharacters(info);
				//写副标题
				mBTService.write(new byte[]{27,68,3,0});
				mBTService.PrintCharacters(columns );
				//写items
				mBTService.PrintCharacters(items);
				//写发料人和领料人
				mBTService.PrintCharacters(end);
			}
		}
		//获取当前用户名，获得当前用户的plandata的操作接口
		public String getUser()
		{
			SharedPreferences preference = this.getSharedPreferences("configuration", Context.MODE_PRIVATE);
			String user = preference.getString("user","");
			return user;
		}
		
		protected void loadConfig()
		{
			SharedPreferences pref = this.getSharedPreferences("Config", Context.MODE_PRIVATE);
			this.PrinterMacAddress = pref.getString("printer_address", "00:19:5D:23:FA:D9");
			this.PrinterTimes = pref.getInt("print_number", 2);
			Log.i(TAG, "读取的打印机MAC地址:"+this.PrinterMacAddress);
			Log.i(TAG, "读取的打印机打印份数:"+this.PrinterTimes);
		}
		
	



}