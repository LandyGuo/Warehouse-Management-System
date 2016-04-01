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
 *�˻���ڿ������ƻ�������ʾ��������Ӧ
 */
public class InOutBillActivity extends Activity implements OnClickListener,IReaderCallback
{
	private String TAG = "InOutBillActivity";
	/*����Reader*/
	private Button connect;
	private ReaderService m_Reader; 
	private String m_strDeviceAddress = "";//���浱ǰ���ӵ��ֳֻ���MAC��ַ
	private  boolean BLUETOOTH_OK_FLAG = false;	//����״̬��ʾ
	//��ͼ�ؼ�
	private ListView inList;	//�б���ͼ
	private TextView matterNameText;//�б�������������
	private TextView matterTypeText;//�б�������������
	private TextView matterPlanText;//�б��������ʼƻ�
	private TextView NotExcutedText;//�б����е�δִ��
	//
	private View currentView;//��ǰѡ�е��б���
	//�ӻ���������
	private final int REQUEST_CODE =1;
	private final int RESULT_CODE =1;
	//���ڴ������ʾ����
	public static  List<PlanDataModel> displayData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.inbillfunction_menu);
		getAllControl();
		Log.i(TAG, "ִ��InBillActivity��OnCreate����,��ʼ��displaydata");
		/*��ȡ���мƻ�����*/
		LoadInfo info = new LoadInfo(this);
		PlanDataDAO pd = new PlanDataDAO(this,info.getUser());
		displayData = pd.getExcutedInfoList(0);
		Log.i(TAG, "��InOutBillActivity�л�ȡ��displayData:"+displayData);
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
			//��ȡ���ص�δִ������
			String notExcutedNumber = data.getExtras().getString("notExcutedNumber");
			//��ȡ���ص�InfoId,�ϴ�ִ�мƻ���InfoId
			int LastExcutedInfoId = data.getExtras().getInt("InfoId");
			//��ȡInOutCount��InfoIdһ����Ϊһ���ƻ���Ψһ��ʶ
			int InOutCount = data.getExtras().getInt("InOutCount");
			//��δִ���������õ�ѡ�е��б�������ʾ
			getListItemControl(currentView);
			NotExcutedText.setText(notExcutedNumber);
			//ִ�����������ʾ
			//TODO:ִ��������޸����ݿ�ִ��λΪ1
			if(notExcutedNumber.trim().equals("0"))
			{
				matterNameText.setTextColor(Color.RED);
			}
			//�޸�plandata���ݿ�ִ��λ
			//TODO:���¼ƻ����ݵ�ʱ��ע��Ψһ��־ΪInOutCount,InfoId����ֶ�
			LoadInfo info =new LoadInfo(this);
			PlanDataDAO pdd =new PlanDataDAO(this,info.getUser());
			pdd.updatePlanData(LastExcutedInfoId, InOutCount,Integer.parseInt(notExcutedNumber.trim()));
		}		
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * �˷������ڻ�ȡ�������Ӧ�����ؼ�
	 */
	protected void getAllControl()
	{
		this.connect = (Button)this.findViewById(R.id.connnectPos);
		this.inList = (ListView)this.findViewById(R.id.inBillList);
	}
	/**
	 * �˷������ڻ�ȡ�б����е�TextView�ؼ�
	 */
	protected void getListItemControl(View listView)
	{
		this.matterNameText=(TextView)listView.findViewById(R.id.listItem_matterName);
		this.matterTypeText=(TextView)listView.findViewById(R.id.listItem_matterType);
		this.matterPlanText=(TextView)listView.findViewById(R.id.listItem_matterPlan);
		this.NotExcutedText=(TextView)listView.findViewById(R.id.listItem_unExcutedNumber);
	}
	/**
	 * �˷������ڽ��б�������ʾ�����Լ��б�����Ӧ
	 */
	protected void setAdapter(Context context)
	{
		InBillAdapter adapter = new InBillAdapter(context);
		inList.setAdapter(adapter);
		inList.setOnItemClickListener(new ItemListener());
	}
	/**
	 * �ڲ��࣬����б������Ӧ�¼�
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
	 * �˷��������б������ת�¼��Լ����ݴ���
	 */
	protected void SwitchView(View currentView,int position)
	{
		Intent intent = new Intent();
		//������ת��ͼ
		intent.setClass(this, InOutBillDetailActivity.class);
		//Я������,ȡ����ǰѡ���е�����
		Bundle bd =new Bundle();
		this.getListItemControl(currentView);
		bd.putString("matterName", this.matterNameText.getText().toString());
		bd.putString("matterType", this.matterTypeText.getText().toString());
		bd.putString("matterPlan", this.matterPlanText.getText().toString());
		bd.putString("NotExcutedNumber",this.NotExcutedText.getText().toString());
		bd.putInt("position", position);
		intent.putExtras(bd);
		//��ת
		this.startActivityForResult(intent, REQUEST_CODE);
		
	}

	@Override
	public void onClick(View v) 
	{
		Button  btn = (Button)v;
		switch(btn.getId())
		{
		case R.id.connnectPos:
			//������pos��������
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
			if(mBluetoothAdapter == null)
			{  
			        //�������ֻ���֧������  
				Toast.makeText(InOutBillActivity.this, "�ֻ���֧������", Toast.LENGTH_LONG).show();
			     return;  
			}  
			if(!mBluetoothAdapter.isEnabled()){ //����δ��������������  
			     Toast.makeText(InOutBillActivity.this, "���ȿ�������", Toast.LENGTH_LONG).show();
			     return;
			}  
			if(!BLUETOOTH_OK_FLAG)
			{
				Log.i(TAG, "��һ�����ӽ�����....");
				//��һ������
				m_Reader.openDeviceListActivity();
			}
			else
			{
				Log.i(TAG, "�ڶ������ӽ�����.....");
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
	
	// ������pos������ʱ��������Load Config
	private void loadConfig() 
	{
		SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
		m_strDeviceAddress = prefs.getString("device_address","");
		BLUETOOTH_OK_FLAG = prefs.getBoolean("BLUETOOTH_OK_FLAG",false);
	}
	
	




}