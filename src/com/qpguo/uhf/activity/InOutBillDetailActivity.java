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
 * �˻���ڿ��Ƶ����ⵥ�б�󣬽���ת����ѡ��λ������ҳ����ػ
 */
public class InOutBillDetailActivity extends Activity implements IReaderCallback
{
	private String TAG = "InOutBillDetailActivity";
	/*currentPosition��ʶ��ǰ�ǵڼ������ݵ�����ҳ*/
	private int currentPosition;
	/*displayData��ʾ����δִ�����ݵ�������ϸ��Ϣ*/
	private List<PlanDataModel> displayData;
	/*currentPlan��ʶ��ǰҳ������ʾ�ļƻ�*/
	private PlanDataModel currentPlan;
	//��������ʾ�ؼ� 
	private TextView matterText;//��ʾ��ҳ����������
	private TextView typeText;//��ʾ��ҳ����������
	private TextView operationText;//��ʾ��ǰ�Ĳ�������(����ҳ����InOutCount�ֶ��ж�)
	private TextView notExcutedNumber;//�����Ҫ��̬�䶯�����ʼֵ����ҳ���
	//�����·�1,2,3,4����
	/*����1,2,3,4*/
	private LinearLayout[] layout = new LinearLayout[4];
	private TextView[] select = new TextView[4];
	private EditText[] number = new EditText[4];
	private Button[] button = new Button[4];
	/*���÷�����*/
	private final int RESULT_OK= 1;

	/*�ֳֻ�*/
	private ReaderService m_Reader = null;
	private String m_strDeviceAddress = "";//���浱ǰ���ӵ��ֳֻ���MAC��ַ
	private  boolean BLUETOOTH_OK_FLAG = false;	//����״̬��ʾ
	private String mask;
	/*���ô�λid����*/
	private ArrayList<String> idArray;
	/*��־���ô�λ�Ƿ��б�ǩ������*/
	private ArrayList<String> labelInfoArray;
	/*��Ҫ��ʾ���ô�λ����*/
	private ArrayList<String> data;
	/*�ӽ����϶�ȡ�Ĳ�����������������Ҫ�ڶ���ط�����*/
	private int operateNumber;
	private int currentTotal;
	/*����ҳ�浱ǰ���ִ�а�ť����Ӧ����λ��*/
	private int currentLocation;
	/*������Ϣ��ʾReader��д״̬*/
	private MyHandler myHandler=null;
	private final int CONNECTED = 100;
	private final int WRITESUCCESS = 200;
	private final int TIMEOUT = 300;
	private final int NOTRECON = 400;
	/*��д���ǩ���ַ���*/
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
		/*�������ݲ��ԣ�*/
        /*�������ȡdisplaydata*/
		this.displayData = InOutBillActivity.displayData;
		Log.i(TAG, "��InBillActivity�л�ȡ��displayData:"+displayData.toString());
		if(displayData.isEmpty())
		{
			Toast.makeText(this, "�ƻ���ȫ��ִ�У�", Toast.LENGTH_LONG).show();
			return;
		}
		/*��ǰҳ����ʾ�ļƻ�����������*/
		currentPlan = displayData.get(currentPosition);
		Log.i(TAG, "�����currentPlan��"+currentPlan);
		idArray = new ArrayList<String>(Arrays.asList(currentPlan.getAvailableStorageIdList().split(",")));
		Log.i(TAG, "����ĵ�ǰ�ƻ����õ�idArray:"+idArray.toString());
		labelInfoArray = new ArrayList<String>(Arrays.asList(currentPlan.getLabelInfo().split(",")));
		Log.i(TAG, "��ǰ���üƻ��ı�ǩ���:"+labelInfoArray.toString());
		/*��setListContent֮ǰ����Ҫ�ȳ�ʼ��idArray*/
		this.setListContent();
		/*���������ֳֻ�*/
		// Allocate Reader object
		this.loadConfig();
		m_Reader = new ReaderService(this, this);
		myHandler = new MyHandler();
	}
	
	/**
	 *ִ�а�ť�ļ����¼�,���ڶ�д��ǩ
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
					//TODO:����ǩ�����ɹ������ִ�������޸�������Ȼ��д��ǩ��д�ɹ���
					//1.�޸Ľ�����ʾ��δִ������2.��ִ�мƻ����ݿ��в���һ��ִ�м�¼(��ʱ���ϴ�)
					/*��������Ĭ��д�ɹ���*/
					
//					/*���ݵ�ǰ�������ô�λ��id��ȡ��16���Ʊ�ʾ*/
//					String id =NumberConvert.Decimal_int2Hex_String(idArray.get(0));
//					/*�Ѵ�λid����Ϊ4��16������*/
//					String StorageId = NumberConvert.hex_StringAutoComplete(id, 4);
//					/*���ݵ�ǰ��λ��id��Ϊ����ȥ����ǩ*/
//					String readMask =StorageId;
//					Log.i(TAG, "��λ16���Ʊ�ʾ��"+StorageId);
//					/*��EPC��*/
//					m_Reader.ReadMemory(BankType.EPC.getValue(), 2, 3,"00B8");
//					/*�м���ִ�гɹ�֮��Ҫ���½���������ʾ�������ݿⱣ��Ȳ���*/
			}
			
		}
		
		
	/**
	 * ��ȡ�ý����ϵ���ʾ�ؼ�
	 */
	protected void getAllControl()
	{
		matterText =(TextView) this.findViewById(R.id.matterText);
		typeText = (TextView) this.findViewById(R.id.typeText);
		operationText =(TextView)this.findViewById(R.id.operationText);
		notExcutedNumber = (TextView)this.findViewById(R.id.notExcutedNumber);
		/*�����·���4������*/
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
		/*4�鲼��*/
		layout[0] = (LinearLayout)this.findViewById(R.id.layout1);
		layout[1]= (LinearLayout)this.findViewById(R.id.layout2);
		layout[2]= (LinearLayout)this.findViewById(R.id.layout3);
		layout[3]= (LinearLayout)this.findViewById(R.id.layout4);
	}
	/**
	 * �˷��������ڽ��濪ʼʱ��ʾ��һҳ�������Ϣ
	 */
	protected void setDisplay()
	{
		Intent intent = this.getIntent();
		Bundle bd = intent.getExtras();
		/*�����ڽ����ʼ��ʱ��ʶ�����ƻ�������λ��*/
		currentPosition = bd.getInt("position");
		Log.i(TAG, "����б����currentPosition:"+currentPosition);
		/*�����Ϸ���ʾ*/
		matterText.setText(bd.getString("matterName"));
		typeText.setText(bd.getString("matterType"));
		operationText.setText(bd.getString("matterPlan"));
		notExcutedNumber.setText(bd.getString("NotExcutedNumber"));
	}
	/**
	 * �˷��������б���ʾ
	 */
	protected void setListContent()
	{
		this.data = this.getDisplayData();//��ȡ��ʾ����
		setVisiable();//���ÿɼ���
		setAvailablePosition();//��ʾ���ô�λ
		setHint();//������ʾ
		setButtonListener();//���ð�ť�ļ����¼�
	}
	/*һЩ����������÷���*/
	/**
	 * �˷������ڻ�ȡ��Ҫ��ʾ������
	 */
	public int getLength()
	{
		return data.size();
	}
	/**
	 * �˷������ڸ�����ʾ�������ò��ֵĿɼ���
	 */
	public void setVisiable()
	{
		for(int i=getLength();i<4;i++)
		{
			this.layout[i].setVisibility(View.INVISIBLE);
		}
	}
	/**
	 * �˷������ڿ�ѡ��λ����ʾ
	 */
	public void setAvailablePosition()
	{
		for(int i=0;i<getLength();i++)
		{
			select[i].setText(data.get(i));
		}
	}
	/**
	 * �˷�������������ʾ���ڱ༭���һ����������
	 */
	public void setHint()
	{
		number[0].setText(notExcutedNumber.getText().toString());
	}
	/**
	 * �˷�������ΪButton���ü����¼�
	 */
	public void setButtonListener()
	{
		for(int i= 0;i<getLength();i++)
		{
			button[i].setOnClickListener(new Listener(i));
		}
	}
	/**
	 *�˷������ڻ�ȡ��ʾ����
	 */
	public ArrayList<String> getDisplayData()
	{
		ArrayList<String> result = new ArrayList<String>();
		for(String id:idArray)
		{
			//����id����position���ȡ��ѡ��λ����
			Log.i(TAG, "��ѡ��λ��id:"+id);
			try
			{
				PositionDataDAO.findPositionData(id).getPositionName();
			}
			catch(Exception e)
			{
				Toast.makeText(this, "��Ч�Ĵ�λ����", Toast.LENGTH_LONG).show();
				return result;
			}
			String positionName = PositionDataDAO.findPositionData(id).getPositionName();
			Log.i(TAG,"��ѡ��λ����:"+positionName);
			result.add(positionName);
		}
		Log.i(TAG, "���ҵĿ��ô�λ���ƣ�"+result.toString());
		//TODO:���������,��Ҫ�ӷ������˻�ÿ�ѡ��λid���ѯ��ÿ�ѡ��λ�б�
		return  result;
	}
	
	
	/**
	 * �˷��������ڰ�ť�����ִ�н������ֵ��޸�
	 * locationΪ��ǰ�����ť�ı��
	 */
	public void ExecutePlan(int location)
	{
		currentLocation = location;
		//��ȡnumber[currentLocation]��ֵ
		if(number[location].getText().toString().equals(""))
		{
			Toast.makeText(this, "������ƻ�����ִ�У�",Toast.LENGTH_SHORT).show();
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
				Toast.makeText(this, "��ʽ������������Ҫִ�е�������",Toast.LENGTH_SHORT).show();
				return;
			}
		}
		operateNumber =Integer.parseInt(number[location].getText().toString().replace(" ", "").trim());
		currentTotal = Integer.parseInt(notExcutedNumber.getText().toString().trim());
		Log.i(TAG, "����������"+operateNumber);
		Log.i(TAG, "����������"+currentTotal);
		if(operateNumber>currentTotal)
		{
			Toast.makeText(this, "�����ƻ���",Toast.LENGTH_SHORT).show();
			return;
		}
		//TODO:����ÿ��ô�λû�б�ǩ����ֱ����Ϊִ�гɹ�
		//TODO:���Ժ�ָ��˶δ���
		else if( labelInfoArray.get(location).equals("0"))
		{
				successOperation();
		}
		else
		{
			if(!BLUETOOTH_OK_FLAG)
			{
				Toast.makeText(InOutBillDetailActivity.this, "���������ֳֻ���", Toast.LENGTH_LONG).show();
				return;
			}
			this.maskSet();
			/*��ǰ�����Ŀ��ô�λid*/
			int OperateStorageId = Integer.parseInt(idArray.get(location).replace(" ", ""));
			/*����ת��Ϊ16���Ʋ�������λ��Ϊ����*/
			mask =NumberConvert.hex_StringAutoComplete(NumberConvert.Decimal_int2Hex_String(OperateStorageId), 4);
			Log.i(TAG, "����ǩ��Mask:"+mask);
			//�����ӳɹ����ȶ�ȡ��ǩ
			ReadFlag = false;
			m_Reader.readMemory(BankType.EPC, 2, 3,mask);	

		}
		
	}
	
	

	/**
	 * �˷������ڼ���back������ʱ����δִ���������ص��ϸ��б�ҳ��
	 */
	@Override
	public void onBackPressed() 
	{
		/*��ȡ����ʱnotExcutedNumber����ʾֵ*/
		Intent intent = new Intent();
		Bundle bd =new Bundle();
		bd.putString("notExcutedNumber",notExcutedNumber.getText().toString());
		//��currentPlan��infoId���أ������޸����ݿ��Ƿ�ִ����
		bd.putInt("InfoId", currentPlan.getInfoId());
		//��currentPlan��InOutCount���أ���ΪInfoId��InOutCountһ���Ψһ��ʶһ���ƻ�
		bd.putInt("InOutCount", currentPlan.getInOutCount());
		intent.putExtras(bd);
		this.setResult(RESULT_OK, intent);
		this.finish();
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

		
		/**
		 * �˷��������ڶ�д��ǩ���ɹ���ʱ��Ӧִ�еĲ���
		 */
		public void successOperation()
		{
			Log.i(TAG, "-----------�����޸Ľ���--------------:"+String.valueOf(currentTotal-operateNumber));
			//�޸Ľ���
			notExcutedNumber.setText(String.valueOf(currentTotal-operateNumber));
			//��ȡ��ǰ��½�û�
			LoadInfo info = new LoadInfo(this);
			String user = info.getUser();
			//��ȡ�ϴ��ƻ����ݿ�
			UploadPlanDataDAO upd = new UploadPlanDataDAO(this,user);
			Log.i(TAG, "�������ݿ�����ݣ�InfoId:"+String.valueOf(currentPlan.getInfoId()));
			Log.i(TAG, "�������ݿ�����ݣ�ִ��ʱ��:"+DateTime.getDateTime());
			Log.i(TAG, "�������ݿ�����ݣ���������:"+String.valueOf(operateNumber));
			Log.i(TAG, "�������ݿ�����ݣ�StorageId:"+idArray.get(currentLocation));
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
					/*��ȡ�ɹ���,������ǩ���ݣ�������ȡ���,��������������д���ǩ*/
					String result = arg1;
					if(!ReadFlag)
					{
						Log.i(TAG, "��һ�ζ�ȡ�ɹ�:"+arg1);
					}
					else
					{
						Log.i(TAG, "�ڶ��ζ�ȡ�ɹ�:"+arg1);
					}
					if(result.compareToIgnoreCase(str2write)==0&&ReadFlag)
					{
						Log.i(TAG,"��ʱ��ȡ��ֵ���ϴ�д��ֵ��ͬ,�ϴ�д��ɹ�!");
						Message msg = Message.obtain();
						msg.what = WRITESUCCESS;
						myHandler.sendMessage(msg);
						//д��ɹ�
						Log.i(TAG,"д��ɹ�!");
						return;
					}
					else if(ReadFlag)
					{
						//�ϴ�д��ʧ��
						Log.i(TAG, "�ϴ�д��ʧ��");
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
						//�ж���⻹�ǳ���
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
						Log.i(TAG, "׼��д���ǩ��ֵ:"+fixCount);
						//��ȡ�겢�޸ĺ�д���ǩ
						String newCount = NumberConvert.Decimal_int2Hex_String(fixCount);
						str2write = readHexStorageId+readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
						String strtemp = readHexMatterId+NumberConvert.hex_StringAutoComplete(newCount,4);
						//TODO:д����mask
						Log.i(TAG, "д���ǩ������:"+str2write);
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
						/*д��ɹ����ٶ�*/
						m_Reader.readMemory(BankType.EPC, 2, 3,mask);	
					}
					else
					{
						Log.i(TAG, "д��ʧ��:arg0:"+arg0+"arg1:"+arg1);
						String mesg = ReaderService.getResponses(arg1);
						Log.i(TAG, "д��ʧ����ʾ��Ϣ:"+mesg);
						m_Reader.stopOperation();
					}
					
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
		
		/*��handler�����ڽ���Toast��ʾ��Ϣ*/
	  private class MyHandler  extends Handler
		{
			public  void handleMessage(Message msg)
			{
				//super.handleMessage(msg);
				switch(msg.what)
				{
				case CONNECTED:
					Toast.makeText(InOutBillDetailActivity.this, "���ӳɹ�!", Toast.LENGTH_LONG).show();
					break;
				case WRITESUCCESS:
					Toast.makeText(InOutBillDetailActivity.this, "д��ɹ�!", Toast.LENGTH_LONG).show();
					successOperation();
					break;
				case TIMEOUT:
					Toast.makeText(InOutBillDetailActivity.this, "Time Out", Toast.LENGTH_LONG).show();
					break;
				case NOTRECON:
					Toast.makeText(InOutBillDetailActivity.this,"�޷�ʶ���ǩ!", Toast.LENGTH_LONG).show();
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

		
		
		
		
		
		
		
		
		
}