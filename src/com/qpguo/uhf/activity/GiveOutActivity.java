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
	private ProgressDialog pd1;//�������ӵĵȴ���
	private ProgressDialog pd2;//д����̵ĵȴ���
	//����״̬��ʾ
	private  boolean BLUETOOTH_OK_FLAG = false;
	//���浱ǰ���ӵ��ֳֻ���MAC��ַ
	private String m_strDeviceAddress = "";
	//ɨ���ǩ�Ķ�д������
	private Reader m_Reader = null;
	//�����ϵĿɽ����ؼ�
	private Button connectButton;
	//���浱ǰ��ListView���û������ķ�����Ŀ��StorageId
	private static String CURRENT_STORAGEID="";
	//��ǵ�ǰѡ���б����Item��view
	private TextView currentView;
	//���浱ǰѡ�е�������
	private Map<String,Object> currentItem;
	//������д���������׺��
	private boolean WRITEFLAG = false; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.faka_page);
		m_Reader = new Reader(this, m_ReaderHandler);
		m_Reader.setResponseTimeout(6000);//����ReaderĬ�ϵĶ�ȡ��ʱʱ��
		//�����ϴ�������Ҫ��MAC��m_strDeviceAddress
		loadConfig();
		Log.i(TAG, "�������ú�m_strDeviceAddress��"+m_strDeviceAddress);
		Log.i(TAG, "�������ú�BLUETOOTH_OK_FLAG��"+BLUETOOTH_OK_FLAG);
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
				//����ǰViewָ��ѡ�е�View���Handler�ж����޸ģ���д�ɹ�����ʶΪ��ɫ
				currentView = (TextView)view.findViewById(R.id.fakalistitem_positionName);
				// TODO Auto-generated method stub
				//ȷ����������һ�У�����ȡ��һ�е�����
				Log.i(TAG, adapter.data.get(position).toString());
				currentItem = adapter.data.get(position);
				String StorageId = null,MatterId=null ,TheCount= null;
				if(!currentItem.containsKey("LargeMatterId"))
				{
					Log.i(TAG, "��ǰ����Ĳ��Ǵ����");
					//���浱ǰ������StorageId
					CURRENT_STORAGEID = String.valueOf(currentItem.get("StorageId"));
					//��д���ǩ��ԭʼ����StorageId,MatterId,TheCount
					StorageId=NumberConvert.Decimal_int2Hex_String(currentItem.get("StorageId"));
					MatterId =NumberConvert.Decimal_int2Hex_String(currentItem.get("MatterId"));
					TheCount=NumberConvert.Decimal_int2Hex_String(currentItem.get("TheCount"));
					Log.i(TAG, "ת����StorageId:"+StorageId);
					Log.i(TAG, "ת����MatterId:"+MatterId);
					Log.i(TAG, "ת����TheCount:"+TheCount);
				}
				else
				{
					Log.i(TAG, "��ǰ����˴��!");
					//���ڴ���Ĵ���
					StorageId="FFFF";
					MatterId =NumberConvert.Decimal_int2Hex_String(currentItem.get("MatterId"));
					TheCount=NumberConvert.Decimal_int2Hex_String(currentItem.get("LargeMatterId"));
				}
				//���䲹�뵽16bit
				StorageId =NumberConvert.hex_StringAutoComplete(StorageId, 4);
				MatterId =NumberConvert.hex_StringAutoComplete(MatterId, 4);
				TheCount =NumberConvert.hex_StringAutoComplete(TheCount, 4);
				Log.i(TAG, "��д���ǩ��StorageId:"+StorageId);
				Log.i(TAG, "��д���ǩ��MatterId:"+MatterId);
				Log.i(TAG, "��д���ǩ��TheCount:"+TheCount);
				//����ʱ�ӱ�ǩ��32λд��96λ����λֱ�Ӳ�0,write������ʼ2������Ϊ6
				String str2Write = StorageId+MatterId+TheCount;
//				String str2Write = StorageId+MatterId+TheCount+"000000000000";
				/*                                    4*4            4*4           4*4                 4*12        */
				//���ĳһ��׼��д���ǩʱ�����ȼ����������
				Log.i(TAG, "��д�����ݣ�"+str2Write);
				if(!BLUETOOTH_OK_FLAG)
				{
					Toast.makeText(GiveOutActivity.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
					return;
				}
				//�����ӳɹ���д������ EPC��,����ʱ��mask
				//WRITEFLAG=true ������д������
				WRITEFLAG = true;
				m_Reader.WriteMemory(BankType.EPC.getValue(), 2, str2Write);	
			}
		});
		
		//connectButton��listener,���ڵ����ťʱ���ֳֻ���������
		OnClickListener buttonListener = new OnClickListener()
		{
			public void onClick(View v) 
			{
				Button btn =(Button)v;
				switch(btn.getId())
				{
				case R.id.connect_pos_button:
					//��������
					BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
					if(mBluetoothAdapter == null)
					{  
					        //�������ֻ���֧������  
						Toast.makeText(GiveOutActivity.this, "�ֻ���֧������", Toast.LENGTH_LONG).show();
					     return;  
					}  
					if(!mBluetoothAdapter.isEnabled()){ //����δ��������������  
					     Toast.makeText(GiveOutActivity.this, "���ȿ�������", Toast.LENGTH_LONG).show();
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
		//�����Ѿ�����   
		if (requestCode == Reader.REQUEST_CONNECT_DEVICE
		            			  && resultCode == Activity.RESULT_OK)
		  {
			if(!BLUETOOTH_OK_FLAG)
			 {
				pd1 = ProgressDialog.show(GiveOutActivity.this, "�����豸", "�����У����Ժ󡭡�",false,true);
			 }
		  } 
	}

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
						Toast.makeText(GiveOutActivity.this, "�豸���ӳɹ�!", Toast.LENGTH_LONG).show();
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
//								Toast.makeText(GiveOutActivity.this, "��ȡ�����"+data.substring(2), Toast.LENGTH_LONG).show();
							}
							if(code =='A' && cc=='r')
							{
								//��ʼ��
							}
							if(code =='A' && cc=='w')
							{
								//��ʼд
								pd2 = ProgressDialog.show(GiveOutActivity.this, "����д���ǩ", "���Ժ�.....",false,true);
							}
							if(code == 'C')
							{
									pd2.dismiss();
									if(data.substring(2).equals("01"))
									{
									if(WRITEFLAG)
									{
									//��һ��д��ɹ��������ٴ�д���λ
									if(!currentItem.containsKey("LargeMatterId"))
									{
									//д��ɹ����޸�position�����ѷ����ı�־λIsGiveOut���ϴ���־λIsUpload
									PositionDataDAO.updateTheFlag(CURRENT_STORAGEID);
									}
									else
									{//���д�ɹ�
										LargeMatterGiveOutDAO lmgod = new LargeMatterGiveOutDAO(GiveOutActivity.this);
										lmgod.updateGiveOutInfo((String)currentItem.get("LargeMatterId"));
									}
									//�ϴ����ݣ����ڲ���,ʵ�������Ӧɨ��֮��һ���ϴ�
									//PositionDataDAO pdd = new PositionDataDAO(GiveOutActivity.this);
									//pdd.uploadGivenOutData();
									//Toast.makeText(GiveOutActivity.this, "����д��ɹ���", Toast.LENGTH_SHORT).show();
									WRITEFLAG = false;
									m_Reader.WriteMemory(BankType.EPC.getValue(), 5, "000000000000");
									}
									else
									{
										//�ڶ���д��ɹ�
										//Toast.makeText(GiveOutActivity.this, "���λд��ɹ���", Toast.LENGTH_SHORT).show();
										Toast.makeText(GiveOutActivity.this, "д��ɹ���", Toast.LENGTH_SHORT).show();
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