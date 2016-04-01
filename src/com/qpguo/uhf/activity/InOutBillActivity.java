package com.qpguo.uhf.activity;

import java.util.List;

import com.atid.at288.lib.IReaderCallback;
import com.atid.at288.lib.ReaderService;
import com.example.uhf.application.R;
import com.qpguo.uhf.adapter.InBillAdapter;
import com.qpguo.uhf.model.PlanDataModel;
import com.qpguo.uhf.modelDAO.PlanDataDAO;
import com.qpguo.uhf.utils.LoadInfo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


/**
 *此活动用于控制入库计划界面显示及操作响应
 */
public class InOutBillActivity extends Activity implements OnClickListener,IReaderCallback
{
	private String TAG = "InOutBillActivity";
	/*连接Reader*/
	private Button connect;
	private ReaderService m_Reader; 
	private String m_strDeviceAddress = "";//保存当前连接的手持机的MAC地址
	private  boolean BLUETOOTH_OK_FLAG = false;	//蓝牙状态表示
	//视图控件
	private ListView inList;	//列表视图
	private TextView matterNameText;//列表项中物资名称
	private TextView matterTypeText;//列表项中物资类型
	private TextView matterPlanText;//列表项中物资计划
	private TextView NotExcutedText;//列表项中的未执行
	//
	private View currentView;//当前选中的列表栏
	//子活动结果返回码
	private final int REQUEST_CODE =1;
	private final int RESULT_CODE =1;
	//用于传输的显示数据
	public static  List<PlanDataModel> displayData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.inbillfunction_menu);
		getAllControl();
		Log.i(TAG, "执行InBillActivity的OnCreate方法,初始化displaydata");
		/*获取所有计划数据*/
		LoadInfo info = new LoadInfo(this);
		PlanDataDAO pd = new PlanDataDAO(this,info.getUser());
		displayData = pd.getExcutedInfoList(0);
		Log.i(TAG, "在InOutBillActivity中获取的displayData:"+displayData);
		this.setButtonListener();
		setAdapter(this);
		this.loadConfig();
		m_Reader = new ReaderService(this,this);
	}
	
	private void setButtonListener()
	{
		this.connect.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{		
		
		if(requestCode ==REQUEST_CODE &&resultCode== RESULT_CODE)
		{
			//获取返回的未执行数量
			String notExcutedNumber = data.getExtras().getString("notExcutedNumber");
			//获取返回的InfoId,上次执行计划的InfoId
			int LastExcutedInfoId = data.getExtras().getInt("InfoId");
			//获取InOutCount和InfoId一起作为一条计划的唯一标识
			int InOutCount = data.getExtras().getInt("InOutCount");
			//将未执行数量设置到选中的列表栏中显示
			getListItemControl(currentView);
			NotExcutedText.setText(notExcutedNumber);
			//执行完的项标红显示
			//TODO:执行完的项修改数据库执行位为1
			if(notExcutedNumber.trim().equals("0"))
			{
				matterNameText.setTextColor(Color.RED);
			}
			//修改plandata数据库执行位
			//TODO:更新计划数据的时候，注意唯一标志为InOutCount,InfoId组合字段
			LoadInfo info =new LoadInfo(this);
			PlanDataDAO pdd =new PlanDataDAO(this,info.getUser());
			pdd.updatePlanData(LastExcutedInfoId, InOutCount,Integer.parseInt(notExcutedNumber.trim()));
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 此方法用于获取界面的响应操作控件
	 */
	protected void getAllControl()
	{
		this.connect = (Button)this.findViewById(R.id.connnectPos);
		this.inList = (ListView)this.findViewById(R.id.inBillList);
	}
	/**
	 * 此方法用于获取列表项中的TextView控件
	 */
	protected void getListItemControl(View listView)
	{
		this.matterNameText=(TextView)listView.findViewById(R.id.listItem_matterName);
		this.matterTypeText=(TextView)listView.findViewById(R.id.listItem_matterType);
		this.matterPlanText=(TextView)listView.findViewById(R.id.listItem_matterPlan);
		this.NotExcutedText=(TextView)listView.findViewById(R.id.listItem_unExcutedNumber);
	}
	/**
	 * 此方法用于将列表设置显示内容以及列表项响应
	 */
	protected void setAdapter(Context context)
	{
		InBillAdapter adapter = new InBillAdapter(context);
		inList.setAdapter(adapter);
		inList.setOnItemClickListener(new ItemListener());
	}
	/**
	 * 内部类，点击列表项的响应事件
	 */
	class ItemListener implements ListView.OnItemClickListener
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			currentView = view;
			SwitchView(view,position);
		}
	}
	/**
	 * 此方法用于列表项的跳转事件以及数据传递
	 */
	protected void SwitchView(View currentView,int position)
	{
		Intent intent = new Intent();
		//设置跳转视图
		intent.setClass(this, InOutBillDetailActivity.class);
		//携带数据,取出当前选项中的数据
		Bundle bd =new Bundle();
		this.getListItemControl(currentView);
		bd.putString("matterName", this.matterNameText.getText().toString());
		bd.putString("matterType", this.matterTypeText.getText().toString());
		bd.putString("matterPlan", this.matterPlanText.getText().toString());
		bd.putString("NotExcutedNumber",this.NotExcutedText.getText().toString());
		bd.putInt("position", position);
		intent.putExtras(bd);
		//跳转
		this.startActivityForResult(intent, REQUEST_CODE);
		
	}

	@Override
	public void onClick(View v) 
	{
		Button  btn = (Button)v;
		switch(btn.getId())
		{
		case R.id.connnectPos:
			//建立与pos机的连接
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
			if(mBluetoothAdapter == null)
			{  
			        //表明此手机不支持蓝牙  
				Toast.makeText(InOutBillActivity.this, "手机不支持蓝牙", Toast.LENGTH_LONG).show();
			     return;  
			}  
			if(!mBluetoothAdapter.isEnabled()){ //蓝牙未开启，则开启蓝牙  
			     Toast.makeText(InOutBillActivity.this, "请先开启蓝牙", Toast.LENGTH_LONG).show();
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
		}
		
	}

	@Override
	public void onReaderCreated(int errorCode) {
		Log.d(TAG, "onReaderCreated()");

		if (ReaderService.NO_ERROR == errorCode) {
			new Thread() {
				@Override
				public void run() {
					m_Reader.registerReceiver(null);
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
		m_Reader.registerReceiver(null);
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		m_Reader.unregisterReceiver(null);
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop()");
		super.onStop();
	}
	
	// 用于与pos机连接时加载设置Load Config
	private void loadConfig() 
	{
		SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
		m_strDeviceAddress = prefs.getString("device_address","");
		BLUETOOTH_OK_FLAG = prefs.getBoolean("BLUETOOTH_OK_FLAG",false);
	}
	
	




}