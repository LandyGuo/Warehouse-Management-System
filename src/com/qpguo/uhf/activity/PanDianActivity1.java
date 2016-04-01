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
	private Button connect;//�̵���������ֳֻ�
	private Button readButton;//��ȡ��ǩ
	private EditText realNumber;//�̵��ʵ������
	private Button writeButton;//����ָ����ǩ������
	
	/*��ʾ��Ϣ��*/
	private TextView displayPositionName;
	private TextView displayMatterName;
	private TextView displayMatterNumber;
	
	/*���������ֳֻ�*/
	private  boolean BLUETOOTH_OK_FLAG = false;	//����״̬��ʾ
	private String m_strDeviceAddress = "";//���浱ǰ���ӵ��ֳֻ���MAC��ַ
	private ReaderService m_Reader = null;	//ɨ���ǩ�Ķ�д������
	
	/*��ȡ��ǩ��16��������*/
	private String readHexStorageId=null;
	private String readHexMatterId = null;
	private String readHexCount = null;
	
	/*������Ϣ��ʾReader��д״̬*/
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
		/*���������ֳֻ�*/
		myHandler = new MyHandler();
		m_Reader = new ReaderService(this,this);//�յ�handler,��Ϊ����ʱ�����д
		loadConfig();//�����ϴ�������Ҫ��MAC��m_strDeviceAddress
		this.findAllControls();
		this.setListener();
	}
	
	/*��ȡ��ǰ�������пɲ����ؼ�*/
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
	
	/*Ϊ��ť���ü����¼�*/
	protected void setListener()
	{
		this.connect.setOnClickListener(new buttonListener());
		this.readButton.setOnClickListener(new buttonListener());
		this.writeButton.setOnClickListener(new buttonListener());
	}
	
	/*���浱ǰ���̵�����*/
	protected void savePandianInfo()
	{
		if(readHexStorageId!=null && readHexMatterId!=null&&readHexCount!=null)
		{
		String StorageId = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexStorageId));
		String MatterId = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexMatterId));
		String LabelCount = String.valueOf(NumberConvert.Hex_String2Decimal_int(readHexCount));
		//RealCount�ӱ༭���л�ȡ
		String realCount = realNumber.getText().toString().replace(" ", "");
		if(realCount.equals(""))
		{
			realCount = LabelCount;
		}
		Log.i(TAG,"������Ϣ��StorageId:"+StorageId+"MatterId:"+MatterId
				+"LabelCount:"+LabelCount+"RealCount:"+realCount);
		PanDianDataModel pddm = new PanDianDataModel(StorageId,MatterId,
				LabelCount,realCount);
		PanDianDataDAO pddd = new PanDianDataDAO(this);
		pddd.insertPanDianData(pddm);
		}
		else
		Log.i(TAG, "û�б����κ���Ϣ��");	
	}
	
	/*�û�������صļ����¼�*/
	@Override
	public void onBackPressed() 
	{
		/*���������ʾ���̵�����*/
		savePandianInfo();
		super.onBackPressed();
	}
	
	/*���Ӱ�ť�ļ����¼�*/
    class buttonListener implements OnClickListener
    {
		@Override
		public void onClick(View v) 
		{
			Button btn = (Button)v;
			switch(btn.getId())
			{
			case R.id.panDian_Link:
				Log.i(TAG, "�����������ֳֻ��İ�ť!");
				//������pos��������
				BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
				if(mBluetoothAdapter == null)
				{  
				        //�������ֻ���֧������  
					Toast.makeText(PanDianActivity1.this, "�ֻ���֧������", Toast.LENGTH_LONG).show();
				     return;  
				}  
				if(!mBluetoothAdapter.isEnabled()){ //����δ��������������  
				     Toast.makeText(PanDianActivity1.this, "���ȿ�������", Toast.LENGTH_LONG).show();
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
			case R.id.panDian_read:
				Log.i(TAG,"����˶�ȡ��ǩ��ť!");
				Log.i(TAG, "���ڱ����ϴζ�ȡ������!");
				savePandianInfo();
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity1.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
					return;
				}
				/*��EPC��*/
				m_Reader.readMemory(BankType.EPC, 2, 3);	
				break;
			case R.id.panDian_write_realNumber:
				Log.i(TAG,"����˸�д��ǩ��ť!");
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity1.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
					return;
				}
				/*�ӽ������ʾ�ؼ��ϻ�ȡ����Ϣ*/
				//��ȡ�û�������̵��ʵ������
				String realCount = realNumber.getText().toString();
				Log.i(TAG, "��ȡ���û���������"+realCount);
				//���û�����ת��Ϊint��
				try
				{
					Integer.parseInt(realCount);
				}
				catch(Exception e)
				{
					Toast.makeText(PanDianActivity1.this, "�����ʵ��������Ч!��������", Toast.LENGTH_LONG).show();
					return;
				}
				int writeNumber = Integer.parseInt(realCount);
				Log.i(TAG,"�û�������ת��Ϊ����:"+writeNumber);
				//��10����ת��Ϊ16���ƴ�д����
				String newCount = NumberConvert.Decimal_int2Hex_String(writeNumber);
				Log.i(TAG,"�û�������ת��Ϊ16������:"+newCount);
				Log.i(TAG,"��д��ǰ��16����������:"+NumberConvert.hex_StringAutoComplete(newCount,4));
				//д���ǩ
				String str2write = readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
				Log.i(TAG, "д���ǩ������:"+readHexStorageId+str2write);
				maskSet();
				m_Reader.writeMemory(BankType.EPC, 3, str2write,readHexStorageId);
				break;
			}
			
		}
    	
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
			
			IReaderReceiver readerReceiver = new IReaderReceiver.Stub() 
			{

				@Override
				public void onReaderActionChange(char arg0) throws RemoteException 
				{
					if(arg0=='r')
					{
						//��ʼ��
						Log.i(TAG, "��ʼ��");
					}
					if(arg0=='w')
					{
						//��ʼд
						Log.i(TAG, "��ʼд");
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
						//���ɹ�
						Log.i(TAG, "��ȡ�ɹ�:"+arg1);
						/*��ȡ�ɹ���,������ǩ����*/
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
							//д��ɹ�
							Log.i(TAG,"д��ɹ�!");
							Message msg = Message.obtain();
							msg.what = WRITESUCCESS;
							myHandler.sendMessage(msg);
						}
						else
						{
							Log.i(TAG, "д��ʧ��:arg0:"+arg0+"arg1:"+arg1);
							String msg = ReaderService.getResponses(arg1);
							Log.i(TAG, "д��ʧ����ʾ��Ϣ:"+msg);
						}
						m_Reader.stopOperation();
				}

				@Override
				public void onReaderStateChange(int arg0) throws RemoteException 
				{
					if(arg0 ==3)
					{
						//���ӳɹ�
						BLUETOOTH_OK_FLAG = true;
						m_strDeviceAddress = m_Reader.getDeviceAddress();
						Log.i(TAG, "���ӵ�MAC��ַ:"+m_Reader.getDeviceAddress());
						saveConfig();
						// Reader Activate...
						m_Reader.activate();
						Message msg = Message.obtain();
						msg.what = CONNECTED;
						myHandler.sendMessage(msg);
					    Log.i(TAG, "���ӳɹ�!");
					}
					
				}

				@Override
				public void onReaderTimeout() throws RemoteException 
				{
					//��ȡ��д�볬ʱ
					Log.i(TAG, "Time Out!");
					Message msg = Message.obtain();
					msg.what = TIMEOUT;
					myHandler.sendMessage(msg);
					m_Reader.stopOperation();
				}
				
			};
			
			
			/*��handler�����ڽ���Toast��ʾ��Ϣ*/
			  private class MyHandler  extends Handler
				{
					@SuppressLint("HandlerLeak")
					public  void handleMessage(Message msg)
					{
						switch(msg.what)
						{
						case CONNECTED:
							Toast.makeText(PanDianActivity1.this, "���ӳɹ�!", Toast.LENGTH_LONG).show();
							break;
						case WRITESUCCESS:
							Toast.makeText(PanDianActivity1.this, "д��ɹ�!", Toast.LENGTH_LONG).show();
							break;
						case TIMEOUT:
							Toast.makeText(PanDianActivity1.this, "����ʧ��!", Toast.LENGTH_LONG).show();
							break;
						case READSUCCESS:
							String result = readHexStorageId+readHexMatterId+readHexCount;
							try
							{
								String StorageName = ExplainReadInfo.getHexStorageIdInfo(readHexStorageId).getPositionName();
								String MatterName = ExplainReadInfo.getMatterIdInfo(readHexMatterId).getName();
								int Count = ExplainReadInfo.getNumberInfo(readHexCount);
								//����ȡ����Ϣ������ʾ
								displayPositionName.setText(StorageName);
								displayMatterName.setText(MatterName);
								displayMatterNumber.setText(String.valueOf(Count));
								//�����̵�ʵ��������Ĭ����ʾ
								realNumber.setText(String.valueOf(Count));
								/*��ȡ���񵽴˽���*/
								Log.i(TAG, "result:"+result);
								Log.i(TAG, "StorageName:"+StorageName);
								Log.i(TAG, "MatterName:"+MatterName);
								Log.i(TAG, "TheCount:"+Count);
							}
							catch(NullPointerException e)
							{
								Toast.makeText(PanDianActivity1.this,"�޷�ʶ���ǩ:"+result, Toast.LENGTH_LONG).show();
							}
							break;
						}
					}
				}
			  
			  /*ʹ������֮ǰ�ȶ�Reader��������*/
				private void maskSet()
				{
					m_Reader.setSelectionBank(BankType.EPC);
					Log.i(TAG,"���õ�bankֵ:EPC");
					m_Reader.setSelectionOffset(32);
					Log.i(TAG, "���õ�Offsetֵ:32");
					m_Reader.setSelectionAction(SelectionActionType.Matching);
					Log.i(TAG, "���õĶ���:Matching");
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