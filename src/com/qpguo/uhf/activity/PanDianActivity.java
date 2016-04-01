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
	private Button connect;//�̵���������ֳֻ�
	private Button readButton;//��ȡ��ǩ
	private EditText realNumber;//�̵��ʵ������
	private Button writeButton;//����ָ����ǩ������
	
	/*��ʾ��Ϣ��*/
	private TextView displayPositionName;
	private TextView displayMatterName;
	private TextView displayMatterNumber;
	
	/*���������ֳֻ�*/
	private ProgressDialog pd1;
	private ProgressDialog pd2;
	private  boolean BLUETOOTH_OK_FLAG = false;	//����״̬��ʾ
	private String m_strDeviceAddress = "";//���浱ǰ���ӵ��ֳֻ���MAC��ַ
	private Reader m_Reader = null;	//ɨ���ǩ�Ķ�д������
	
	/*��ȡ��ǩ��16��������*/
	private String readHexStorageId=null;
	private String readHexMatterId = null;
	private String readHexCount = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.pandianfunction);
		/*���������ֳֻ�*/
		m_Reader = new Reader(this, m_ReaderHandler);//�յ�handler,��Ϊ����ʱ�����д
		m_Reader.setResponseTimeout(6000);//����ReaderĬ�ϵĶ�ȡ��ʱʱ��
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
		//TODO:����ҳ����뿪ҳ��ʱ�ĵ�������
		m_Reader.SetPowerEx(0);
		m_Reader.OnDestroy();
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
					Toast.makeText(PanDianActivity.this, "�ֻ���֧������", Toast.LENGTH_LONG).show();
				     return;  
				}  
				if(!mBluetoothAdapter.isEnabled()){ //����δ��������������  
				     Toast.makeText(PanDianActivity.this, "���ȿ�������", Toast.LENGTH_LONG).show();
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
			case R.id.panDian_read:
				Log.i(TAG,"����˶�ȡ��ǩ��ť!");
				Log.i(TAG, "���ڱ����ϴζ�ȡ������!");
				savePandianInfo();
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
					return;
				}
				/*��EPC��*/
				m_Reader.ReadMemory(BankType.EPC.getValue(), 2, 3);	
				break;
			case R.id.panDian_write_realNumber:
				Log.i(TAG,"����˸�д��ǩ��ť!");
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(PanDianActivity.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
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
					Toast.makeText(PanDianActivity.this, "�����ʵ��������Ч!��������", Toast.LENGTH_LONG).show();
					return;
				}
				int writeNumber = Integer.parseInt(realCount);
				Log.i(TAG,"�û�������ת��Ϊ����:"+writeNumber);
				//��10����ת��Ϊ16���ƴ�д����
				String newCount = NumberConvert.Decimal_int2Hex_String(writeNumber);
				Log.i(TAG,"�û�������ת��Ϊ16������:"+newCount);
				Log.i(TAG,"��д��ǰ��16����������:"+NumberConvert.hex_StringAutoComplete(newCount,4));
				//д���ǩ
				String str2write = readHexStorageId+readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
				Log.i(TAG, "д���ǩ������:"+str2write);
				m_Reader.WriteMemory(BankType.EPC.getValue(), 2, str2write);
				break;
			}
			
		}
    	
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
				pd1 = ProgressDialog.show(PanDianActivity.this, "�����豸", "�����У����Ժ󡭡�",false,true);
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
							/*�豸���ӳɹ����������ӹ���*/
//							m_Reader.SetPowerEx(19);
							Toast.makeText(PanDianActivity.this, "�豸���ӳɹ�!", Toast.LENGTH_LONG).show();
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
									/*��ȡ�ɹ���,������ǩ����*/
									String result = data.substring(2);
									readHexStorageId = result.substring(0, 4);
									readHexMatterId = result.substring(4,8);
									readHexCount = result.substring(8,12);
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
										Toast.makeText(PanDianActivity.this,"�޷�ʶ���ǩ:"+result, Toast.LENGTH_LONG).show();
									}
								}
								if(code =='A' && cc=='r')
								{
									//��ʼ��
									pd2 = ProgressDialog.show(PanDianActivity.this, "���ڶ�ȡ��ǩ", "���Ժ�.....",false,true);
								}
								if(code =='A' && cc=='w')
								{
									//��ʼд
									pd2 = ProgressDialog.show(PanDianActivity.this, "����д���ǩ", "���Ժ�.....",false,true);
								}
								if(code == 'C')
								{
									    pd2.dismiss();
										if(data.substring(2).equals("01"))
										{
											//д�ɹ�
											Toast.makeText(PanDianActivity.this, "�����ɹ���", Toast.LENGTH_SHORT).show();
										}
										else
										{
											//дʧ��
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