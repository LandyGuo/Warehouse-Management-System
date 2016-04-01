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
	//�����˵�ҳ��İ�ť
	private Button printPlanButton;//��ӡ�ƻ���ť
	private Button inBillButton;//����ⰴť
	//���ڴ�ӡ
	private BlueToothService mBTService ;
	private Set<BluetoothDevice> devices;
	private ProgressDialog p1;
	public static final int MESSAGE_STATE_CHANGE = 1;//��������״̬
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	//���ڶ�ȡĬ������
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
		//���ڴ�ӡ����
		mBTService = new BlueToothService(this,mhandler);
		//����ɨ��������ӡ���Ĺ㲥�źŽ��շ���
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
	 * ����Ѱ�ҵ�ǰҳ��Ŀؼ���ͼ
	 */
	protected void findAllControl()
	{
		printPlanButton = (Button) this.findViewById(R.id.printInOutPlan_button);
		inBillButton = (Button)this.findViewById(R.id.inBill_button);
	}
	/**
	 * �������ð�ť�ļ����¼�
	 */
	protected void setListeners(Listener listener)
	{
		printPlanButton.setOnClickListener(listener);
		inBillButton.setOnClickListener(listener);
	}
	
	/**
	 * ��ȡ��ǰ��listnerʵ��
	 */
	protected Listener getListener()
	{
		return new Listener();
	}
	/**
	 * �����ڲ��࣬���ڶ�������¼�
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
				//��ӡ�ƻ���ť����Ӧ�¼�
				//�����ӡ�ƻ���ť��ִ���������Ӻʹ�ӡ�ƻ�
				p1 =ProgressDialog.show(InOutMenuActivity.this, "�������Ӵ�ӡ��", "���Ժ�....",false,true);
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
					Toast.makeText(InOutMenuActivity.this, "û�������豸��", Toast.LENGTH_LONG).show();
					return;
				}
				break;
			case R.id.inBill_button:
				//����ⰴť����Ӧ�¼�
				switchInPlanView();//��ת�������ͼ
				break;
			}
		}
	}
	/**
	 * �˺���������ͼ��ת�����ƻ�
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
	
	//����������ӡ��ģ�飬���մ�ӡ������
		Handler mhandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MESSAGE_STATE_CHANGE:// ��������״̬
					switch (msg.arg1) {
					case BlueToothService.STATE_CONNECTED:
						break;
					case BlueToothService.STATE_CONNECTING:// ��������
						break;
					case BlueToothService.STATE_LISTEN:
					case BlueToothService.STATE_NONE:
						break;
					case BlueToothService.SUCCESS_CONNECT:
						p1.dismiss();
						Toast.makeText(InOutMenuActivity.this, "���Ӵ�ӡ���ɹ���", Toast.LENGTH_LONG).show();// �Ѿ�����
						//���ӳɹ�������ִ�д�ӡ�ƻ�
						PlanDataDAO pd = new PlanDataDAO(InOutMenuActivity.this,getUser());
						List<String> BillIdList = pd.getDistinctBillId();
						for(String BillId:BillIdList)
						{
							//Ĭ��ÿ�ݼƻ���ӡ2��
							printPlan(InOutMenuActivity.this,mBTService, BillId,PrinterTimes);
						}
						Toast.makeText(InOutMenuActivity.this, "�ƻ��Ѵ�ӡ��", Toast.LENGTH_LONG).show();
						//��ӡ���Ͽ���������
						mBTService.DisConnected();
						//Toast.makeText(MainMenuActivity.this, "�Ͽ����ӡ�������ӣ�", Toast.LENGTH_LONG).show();
						break;
					case BlueToothService.FAILED_CONNECT:
						Toast.makeText(InOutMenuActivity.this, "���Ӵ�ӡ��ʧ�ܣ�", Toast.LENGTH_LONG).show();
						break;
					case BlueToothService.LOSE_CONNECT:
						Toast.makeText(InOutMenuActivity.this, "ʧȥ���ӡ�������ӣ�", Toast.LENGTH_LONG).show();
						break;
					}
					break;
				case MESSAGE_READ:
					// sendFlag = false;//����������
					break;
				case MESSAGE_WRITE:// ������δ��
					// sendFlag = true;
					break;
				}
			}
		};
		
		//����BillId��ȡ��ӡ�ƻ�����Ҫ������
		public  String getPrintData(String BillId)
		{
			//��ȡ��ǰ��¼�û����ù�����Ҫ��֤�û���¼ʱһ��Ҫ���浱ǰ��¼�û�
			//��ȡ��ǰ�û��ƻ�
			PlanDataDAO pd = new  PlanDataDAO(this,this.getUser());
			List<PlanDataModel> list = pd.getPrintPlanList(BillId);
			StringBuffer sb = new StringBuffer();
			for(PlanDataModel p:list)
			{
				//MatterName:"̧�� ��1800~2000mm ��"
				//���ڻ�ȡ���ƺ��ͺŹ��
				String name = p.getMatterName().split("��")[0];
				String type = p.getMatterName().split("��")[1];
				//��ȡ����
				String InOutCount = String.valueOf(Math.abs(p.getInOutCount()));
				String item = MessageFormat.format("{0}\t\t{1}\t\t{2}\n",name, type,InOutCount);
				sb.append(item);
			}
			return sb.toString();
		}
		//�������ܣ���ӡ��ӦBillId�ļƻ��б�����times��ʾ��ӡ�ķ���
		public void printPlan(Context context,BlueToothService mBTService,String BillId,int times)
		{
			if (mBTService.getState() != BlueToothService.STATE_CONNECTED)
			{
				Toast.makeText(context,"������������ӡ������", 2000).show();
				return;
			}
			PlanDataDAO pd = new PlanDataDAO(this,this.getUser());
			//��ӡ��title����
			String title = "";
			if(pd.getInOutByBillId(BillId))
			{
				title = MessageFormat.format("\t\t   {0}�ⵥ\n","��");
			}
			else
			{
				title = MessageFormat.format("\t\t   {0}�ⵥ\n","��");
			}
			//��ȡ��ӡ��items
			String items = this.getPrintData(BillId);
			//û�����ݲ���ӡ
			if(items.equals(""))return;
			/*�����ӵĲ��֣��������ȡ���ϳ��䣬ʱ�䣬�Լ�������*/
			List<PlanDataModel> pdl = pd.getPrintPlanList(BillId);
			//��ȡ�üƻ��б��еĵ�һ��
			PlanDataModel p =pdl.get(0);
			String Workshop = p.getWorkshop();
			String GetPlanTime = p.getGetPlanTime();
			String GiveOutPerson = p.getGiveOutPerson();
			String GetPerson = p.getGetPerson();
			String info = "",end="";
			if(pd.getInOutByBillId(BillId))
			{/*��ⵥ*/
				//��ӡ��info����,�������ϳ����ʱ������Ҫ�滻������
				info =MessageFormat.format("\nʱ�䣺{0}\n",GetPlanTime);
				//�����˺�������,���﷢��������Ҫ�滻������
				end =MessageFormat.format("�����ˣ�{0}\t\t\n\n\n\n\n\n\n", GetPerson);
			}
			else
			{
				//��ӡ��info����,�������ϳ����ʱ������Ҫ�滻������
				info =MessageFormat.format("���ϳ��䣺{0}\nʱ�䣺{1}\n",Workshop,GetPlanTime);
				//�����˺�������,���﷢��������Ҫ�滻������
				end =MessageFormat.format("�����ˣ�{0}\t\t�����ˣ�\n\n\n\n\n\n\n", GiveOutPerson);
			}

			//��ӡ��������
			String columns ="����\t\t�ͺŹ��\t\t����\n";
		
			for(int i=0;i<times;i++)
			{
				//��ʼ��ӡ����
				mBTService.write(new byte[]{27,68,4,0});
				//���ñ��������С
				mBTService.write(new byte[]{27,56,3});
				//д����
				mBTService.PrintCharacters(title);
				//�������������С
				mBTService.write(new byte[]{27,56,0});
				//д�����ʱ��
				mBTService.PrintCharacters(info);
				//д������
				mBTService.write(new byte[]{27,68,3,0});
				mBTService.PrintCharacters(columns );
				//дitems
				mBTService.PrintCharacters(items);
				//д�����˺�������
				mBTService.PrintCharacters(end);
			}
		}
		//��ȡ��ǰ�û�������õ�ǰ�û���plandata�Ĳ����ӿ�
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
			Log.i(TAG, "��ȡ�Ĵ�ӡ��MAC��ַ:"+this.PrinterMacAddress);
			Log.i(TAG, "��ȡ�Ĵ�ӡ����ӡ����:"+this.PrinterTimes);
		}
		
	



}