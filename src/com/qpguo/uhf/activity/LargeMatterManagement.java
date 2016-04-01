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
	/*�������ҳ��Ĳ����ؼ�*/
	private Button connectButton;//�����ֳֻ�
	private Button readTagButton;//��ȡ��ǩ
	private Spinner operationSpinner;//��ѡ������
	private Button confirmButton;//ȷ�ϲ�������
	/*�������ҳ��Ķ�̬��ʾ��*/
	private TextView largeMatterName;//��ȡ�Ĵ������
	private TextView largeMatterType;//��ȡ�Ĵ���ͺŹ��
	private TextView largeMatterPiccode;//��ȡ�Ĵ��ͼ��
	/*�����ʷ������ʾ��*/
	private ListView matterHistoryOperationList;
	/*���ڴ洢�û�������һЩ����*/
	private String USERCHOOSE_CURRENT_OPERATION;//�����û�ѡ��Spinner��ǰ�Ĳ���
	private String READ_LARGEMATTER_ID=null;//����ӱ�ǩ�ж�ȡ��LargeMatterId
	private String READ_MATTER_ID=null;//����ӱ�ǩ�ж�ȡ��MatterId
	private boolean IS_LARGEMATTER_FLAG = false;//����ӱ�ǩ�ж�ȡ���Ƿ�Ϊ����ı�־λ
	/*��������pos��*/
	/*���������ֳֻ�*/
	private ProgressDialog pd1;
	private  boolean BLUETOOTH_OK_FLAG = false;	//����״̬��ʾ
	private String m_strDeviceAddress = "";//���浱ǰ���ӵ��ֳֻ���MAC��ַ
	private Reader m_Reader = null;	//ɨ���ǩ�Ķ�д������
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.largematter);
		/*����pos��*/
		m_Reader = new Reader(this, m_ReaderHandler);//�յ�handler,��Ϊ����ʱ�����д
		m_Reader.setResponseTimeout(6000);//����ReaderĬ�ϵĶ�ȡ��ʱʱ��
		loadConfig();//�����ϴ�������Ҫ��MAC��m_strDeviceAddress
		//TODO:���ڲ��ԣ�������ɺ����ô˷���
		//this.generateTestData();
		
		this.findAllControls();
		this.setSpinnerContentAndListener();
		this.setConfirmButtonListener();
		this.setLargeMatterInfoDisplay();
	}
	/**
	 * �˷����������ɲ�������
	 */
	protected void generateTestData()
	{
		LargeMatterModel lmm1 = new LargeMatterModel("333","����","84","1","2014-08-08 12:48:00");
		LargeMatterModel lmm2 = new LargeMatterModel("333","����","84","1",DateTime.getDateTime());
		LargeMatterModel lmm3= new LargeMatterModel("333","����","84","1","2013-08-08 11:48:00");
		LargeMatterModel lmm4 = new LargeMatterModel("333","����","84","1",DateTime.getDateTime());
		LargeMatterModel lmm5 = new LargeMatterModel("333","����","84","1",DateTime.getDateTime());
		LargeMatterModel lmm6 = new LargeMatterModel("333","����","84","1","2013-08-08 12:48:00");
		LargeMatterModel lmm7 = new LargeMatterModel("666","���","83","1","2014-08-08 12:48:00");
		LargeMatterModel lmm8 = new LargeMatterModel("666","���","83","1",DateTime.getDateTime());
		LargeMatterModel lmm9= new LargeMatterModel("666","���","83","1","2013-08-08 11:48:00");
		LargeMatterModel lmm10 = new LargeMatterModel("666","���","83","1",DateTime.getDateTime());
		LargeMatterModel lmm11 = new LargeMatterModel("666","���","83","1",DateTime.getDateTime());
		LargeMatterModel lmm12 = new LargeMatterModel("666","���","83","1","2013-08-08 12:48:00");
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
		Log.i(TAG, "���ɴ����������!");
	}

	/**
	 * �˷������ڻ�ȡ��ǰҳ�����пɲ����ؼ�
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
	 * �˷�����������ȷ�ϲ�����ť����Ӧ���¼�
	 */
	protected void setConfirmButtonListener()
	{
		this.confirmButton.setOnClickListener(new ButtonListener());
		this.connectButton.setOnClickListener(new ButtonListener());
		this.readTagButton.setOnClickListener(new ButtonListener());
	}
	/*�ڲ��࣬���ڴ���ȷ�ϰ�ť����Ӧ�¼�*/
	class ButtonListener implements OnClickListener
	{
		@Override
		public void onClick(View v) 
		{
			Button btn = (Button)v;
			switch(btn.getId())
			{
			case R.id.confirmOperation:
				Log.i(TAG,"�����ȷ�ϲ�����ť��");
				if(IS_LARGEMATTER_FLAG==false)
				{
					Toast.makeText(LargeMatterManagement.this, "����ɨ��һ�������", Toast.LENGTH_LONG).show();
					return;
				}
				//��ȡ��ǰ��¼�û�,��������
				LoadInfo info = new LoadInfo(LargeMatterManagement.this);
				String currentUser = info.getUser();
				//��ȡ����ʱ��
				String currentTime = DateTime.getDateTime();
				//��ȡ����ݴ�������ݿ�
				UploadLargeMatterDAO ulmd = new UploadLargeMatterDAO(LargeMatterManagement.this);
				LargeMatterModel lmm = new LargeMatterModel(
						NumberConvert.hex_StringAutoComplete(READ_LARGEMATTER_ID, 6),USERCHOOSE_CURRENT_OPERATION,
						READ_MATTER_ID,currentUser,currentTime);
				ulmd.insertItem(lmm);
				Log.i(TAG, "������Ϣ:"+lmm.toString());
				Toast.makeText(LargeMatterManagement.this, "�����ɹ���", Toast.LENGTH_SHORT).show();
				break;		
			case R.id.largeMatter_connect:
				//���ֳֻ�����
				//������pos��������
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
				if(mBluetoothAdapter == null)
				{  
				        //�������ֻ���֧������  
					Toast.makeText(LargeMatterManagement.this, "�ֻ���֧������", Toast.LENGTH_LONG).show();
				     return;  
				}  
				if(!mBluetoothAdapter.isEnabled()){ //����δ��������������  
				     Toast.makeText(LargeMatterManagement.this, "���ȿ�������", Toast.LENGTH_LONG).show();
				     return;
				}  
				if(!BLUETOOTH_OK_FLAG)
				{
					Log.i(TAG, "��һ�����ӽ�����....");
					//��һ������
					m_Reader.OpenDeviceListActivity();
				}
				else
				{
					Log.i(TAG, "�ڶ������ӽ�����.....");
					m_Reader.mDeviceAddress = m_strDeviceAddress;
					m_Reader.ConnectMostRecentDevice();
				}
				break;
			case R.id.readTag:
				//��ȡ�����ǩ
				Log.i(TAG,"����˶�ȡ��ǩ��");
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(LargeMatterManagement.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
					return;
				}
				m_Reader.ReadMemory(BankType.EPC.getValue(), 2, 3);
				break;
				
			}
			
		}
		
	}
	
	/**
	 * �˷�����������Spinner�����ݺͼ���
	 */
	protected void setSpinnerContentAndListener()
	{
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.operations_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.operationSpinner.setAdapter(adapter);
		this.operationSpinner.setOnItemSelectedListener(new SpinnerListener());
	} 
	/**
	 * ���ڲ������ڴ���Spinner�ļ����¼�
	 */
	class SpinnerListener implements Spinner.OnItemSelectedListener
	{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) 
		{
			// TODO Auto-generated method stub
//			Toast.makeText(LargeMatterManagement.this, "ѡ���ˣ�"+parent.getItemAtPosition(position), 
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
	 * �˷������ڸ��ݶ�ȡ�����ǩ��id��Ϣ����ѯbasedata���ݿⲢ��ʾ
	 * �ô�����������ơ��ͺŹ���ͼ��
	 * ÿ��ȡһ�α�ǩ����Ҫ����һ��������б����ʾ
	 */
	protected void setLargeMatterInfoDisplay()
	{
		//�б���ʾ����
		Log.i(TAG, "��ȡ����Ĵ�����:"+READ_LARGEMATTER_ID);
		LargeMatterHistoryInfo adapter = new LargeMatterHistoryInfo(this,this.READ_LARGEMATTER_ID);
		this.matterHistoryOperationList.setAdapter(adapter);
		//���ݶ�ȡ��MatterId��ѯ�������ݿ��ȡ������������ơ��ͺŹ���ͼ��
		BaseDataDAO bdd = new BaseDataDAO(this);
		Log.i(TAG,"��ȡ�ô�������ʱ��:"+READ_MATTER_ID);
		BaseDataModel model =BaseDataDAO.findItem(READ_MATTER_ID);
		if(model==null)
		{
			Toast.makeText(LargeMatterManagement.this, "�޷��ҵ��ô����", Toast.LENGTH_SHORT).show();
			return;
		}
		this.largeMatterName.setText(model.getName());
		this.largeMatterType.setText(model.getType());
		this.largeMatterPiccode.setText(model.getPiccode());
	}
	
	/**
	 * �˷������ڸ��ݶ�ȡ�����ǩ����������Ϣ����ѯmatterLarge���ݿ�
	 * ��ȡ�����ʷ������Ϣ
	 * @param <HistoryItem>
	 */
	protected <HistoryItem> List<HistoryItem> getHistoryInfoByCode()
	{
		//TODO:����÷���
		return null;
	}
	/**
	 * �˷������ڸ��ݴ������ʷ��������������ʾ�б�
	 */
	protected void setHistoryListDisplay()
	{
		//TODO:����÷���
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		m_Reader.OnActivityResult(requestCode, resultCode, data);
		//�����Ѿ�����   
		if (requestCode == Reader.REQUEST_CONNECT_DEVICE
		            			  && resultCode == Activity.RESULT_OK)
		  {
			if(!BLUETOOTH_OK_FLAG)
			 {
				pd1 = ProgressDialog.show(LargeMatterManagement.this, "�����豸", "�����У����Ժ󡭡�",false,true);
			 }
		  } 
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	// ������pos������ʱ��������Load Config
	private void loadConfig() 
	{
		SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
		m_strDeviceAddress = prefs.getString("device_address","");
		BLUETOOTH_OK_FLAG = prefs.getBoolean("BLUETOOTH_OK_FLAG",false);
	}

	// ������pos�����Ӻ󱣴�����Save Config
	private void saveConfig()
	{
		SharedPreferences prefs = getSharedPreferences("Config", MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("device_address", m_strDeviceAddress);
		editor.putBoolean("BLUETOOTH_OK_FLAG", BLUETOOTH_OK_FLAG);
		editor.commit();
	}
	//���ڽ������ֳֻ����ӵ���Ϣ
	//�����ȡ��ȡ����д����
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
					//�������ӳɹ��󱣴�����
					m_strDeviceAddress = m_Reader.mDeviceAddress;
					Log.i(TAG, "��һ�����ӽ�����m_strDeviceAddress:"+m_strDeviceAddress);
					Log.i(TAG, "��һ�����ӽ�����BLUETOOTH_OK_FLAG:"+BLUETOOTH_OK_FLAG);				
					saveConfig();
					Toast.makeText(LargeMatterManagement.this, "�豸���ӳɹ�!", Toast.LENGTH_LONG).show();
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
							/*��ȡ�ɹ���,������ǩ����*/
							String result = data.substring(2);
							/*����ı�ǩ����Ϊ��
							 * FFFF - 32-47 �������ʶFFFFλ
							 * XXXX - 48-63:    ��������ʱ��
							 * XXXX - 64-79��������������*/
							String readHexLargeMatterFlag = result.substring(0, 4);
							String readHexMatterId = result.substring(4,8);
							String readHexLargeMatterId = result.substring(8,12);
							Log.i(TAG,"��ȡ��Ϣ-�����ʶ:"+readHexLargeMatterFlag
									+" ������ʱ��:"+readHexMatterId+"�������:"+readHexLargeMatterId);
							/*���������Ϣ*/
							if(readHexLargeMatterFlag.compareToIgnoreCase("FFFF")!=0)
							{
								Toast.makeText(LargeMatterManagement.this, "����Ʒ�Ǵ����", Toast.LENGTH_SHORT).show();
								return;
							}
							IS_LARGEMATTER_FLAG = true;
							READ_LARGEMATTER_ID = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexLargeMatterId)).trim();
							READ_MATTER_ID =String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexMatterId)).trim();
							Log.i(TAG, "�����Ĵ������:LargeMatterId:"+READ_LARGEMATTER_ID+" MatterId:"+READ_MATTER_ID);
							/*ÿ��һ����ʾ�ô�������Ϣ*/
							setLargeMatterInfoDisplay();
						}
						if(code =='A' && cc=='r')
						{
							//��ʼ��
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