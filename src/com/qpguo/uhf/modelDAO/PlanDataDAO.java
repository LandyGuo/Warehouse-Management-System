package com.qpguo.uhf.modelDAO;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qpguo.uhf.model.PlanDataModel;
import com.qpguo.uhf.utils.HttpApi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public  class PlanDataDAO extends BaseClassDAO
{
	private final String TAG = "PlanDataDAO";
	private final String PlanDataTable  = DatabaseOpenHelper.TABLE_PLANDATA_NAME;
	private final String METHODNAME = "GetInOutPlan";
	private String user ;
	public PlanDataDAO(Context context,String user)
	{
		super(context);
		this.user  = user;
	}
	//����plandata���������û���أ�ÿ���û���õļƻ���ͬ
	public String getCurrentUser()
	{
		return user;
	}
	//�����û���ΪLoginId�ļƻ���һ��Ҫ����������µ����мƻ���
	//��Ϊÿ�δӷ������ϻ�ȡ�Ķ�������δִ�еı�
			public boolean downLoadPlanData()
			{
				boolean result =false;
				this.ClearPlanData();
				HttpApi request = new HttpApi();
				try 
				{
					String content = request.PostRequest(BaseClassDAO.SERVERHOST, this.METHODNAME,
							new BasicNameValuePair("UserId",user));
					JSONObject jo = new JSONObject(content);
					int flag  = jo.getInt("Flag");
					if(flag==1)
					{
						result = true;
						JSONArray ja = jo.getJSONArray("List");
						for(int i=0;i<ja.length();i++)
						{
							JSONObject temp = ja.getJSONObject(i);
							PlanDataModel model = new PlanDataModel
							(
							temp.getInt("InfoId"),
							0,//��ʼ����ʱִ��λ��Ϊ0����Ϊ���صľ�Ϊδִ��
							temp.getString("MatterId"),
							temp.getInt("InOutCount"),
							temp.getString("MatterName"),
							temp.getString("BillId"),
							temp.getInt("IsHasLabel"),
							this.user,
							temp.getInt("NotExcutedNumber"),
							temp.getString("AvailableStorageIdList"),
							temp.getString("LabelInfo"),
							temp.getString("Workshop"),
							temp.getString("GetPlanTime"),
							temp.getString("GiveOutPerson"),
							temp.getString("GetPerson"),
							temp.getInt("IsCatch")
							);
							this.insertPlanData(model);
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				return result;
				
			}
			//�����в���һ����¼
			public long  insertPlanData(PlanDataModel planData)
			{
				BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("InfoId", planData.getInfoId());
				values.put("IsExcuted", planData.getIsExcuted());
				values.put("MatterId", planData.getMatterId());
				values.put("InOutCount", planData.getInOutCount()); 
				values.put("MatterName", planData.getMatterName());
				values.put("BillId", planData.getBillId());
				values.put("IsHasLabel", planData.getIsHasLabel());
				values.put("LoginId", planData.getLoginId());
				values.put("NotExcutedNumbers", planData.getNotExcutedNumbers());
				values.put("LabelInfo", planData.getLabelInfo());
				values.put("AvailableStorageIdList", planData.getAvailableStorageIdList());
				values.put("Workshop", planData.getWorkshop());
				values.put("GetPlanTime", planData.getGetPlanTime());
				values.put("GiveOutPerson", planData.getGiveOutPerson());
				values.put("GetPerson", planData.getGetPerson());
				values.put("IsCatch", planData.getIsCatch());
				BaseClassDAO.myDb.beginTransaction();
				long InsertStatus = -1;
				try
				{
						InsertStatus = BaseClassDAO.myDb.insert(PlanDataTable, null, values);
						BaseClassDAO.myDb.setTransactionSuccessful();
						Log.i(TAG, "PlanDataDAO InsertStatus:"+InsertStatus);
				}
				finally
				{
					BaseClassDAO.myDb.endTransaction();
					this.closeDatabase();
				}
				return InsertStatus;
			}
			//�����ǰ�û�����
			public void ClearPlanData()
			{
				BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			//	BaseClassDAO.myDb.execSQL("DELETE FROM "+ PlanDataTable+ " WHERE LoginId ='"+this.user+"'"
			//			+" AND IsExcute=1");
				BaseClassDAO.myDb.execSQL("DELETE FROM "+ PlanDataTable+ " WHERE LoginId ='"+this.user+"'"
						);
				this.closeDatabase();
				Log.i(TAG, "table deleted!!!!LoginId:"+this.user);
			}
			/**
			 * �˺������ڴ�plandata���л�ȡ��ִ�е���(IsExcuted=1)
			 *��δִ��(IsExcute=0)��
			 * ������IsExcuted=0����������δִ����
			                 IsExcuted=1������������ִ����
			*/
			public List<PlanDataModel> getExcutedInfoList(int IsExcuted)
			{
				BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
				String SQLStr = "SELECT *  FROM "+this.PlanDataTable+" WHERE LoginId ='"+this.user+"' and "
										+"IsExcuted = "+IsExcuted;
				Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
				List<PlanDataModel> result = new ArrayList<PlanDataModel>();
				while(cursor.moveToNext())
				{
					result.add(new PlanDataModel
					(
					cursor.getInt(cursor.getColumnIndex("InfoId")),
					cursor.getInt(cursor.getColumnIndex("IsExcuted")),
					cursor.getString(cursor.getColumnIndex("MatterId")),
					cursor.getInt(cursor.getColumnIndex("InOutCount")),
					cursor.getString(cursor.getColumnIndex("MatterName")),
					cursor.getString(cursor.getColumnIndex("BillId")),
					cursor.getInt(cursor.getColumnIndex("IsHasLabel")),
					cursor.getString(cursor.getColumnIndex("LoginId")),
					cursor.getInt(cursor.getColumnIndex("NotExcutedNumbers")),
					cursor.getString(cursor.getColumnIndex("AvailableStorageIdList")),
					cursor.getString(cursor.getColumnIndex("LabelInfo")),
					cursor.getString(cursor.getColumnIndex("Workshop")),
					cursor.getString(cursor.getColumnIndex("GetPlanTime")),
					cursor.getString(cursor.getColumnIndex("GiveOutPerson")),
					cursor.getString(cursor.getColumnIndex("GetPerson")),
					cursor.getInt(cursor.getColumnIndex("IsCatch"))));
				}
				cursor.close();
				this.closeDatabase();
				return result;
			}
			//����InfoId����IsExcuted(int)��־λ
			/**
			 * ��һ���ƻ���ȫ��ִ��ʱ(NotExcutedNumbersΪ0),��IsExcuted��Ϊ1,����ɸѡ
			 * �´ν����б�ʱ����ʾ���������ȫִ�е���
			 * ����ִ��һ���ƻ������󣬸�����ִ��IsExcuted��־λ��NotExcutedNumbers
			 * @param InfoId
			 * @param IsExcuted ��ȫִ����ȫΪ1��δִ��Ϊ0
			 */
			public void updatePlanData(int InfoId,int InOutCount,int NotExcutedNumbers)
			{
				int IsExcuted = 0;
				if(NotExcutedNumbers==0)
				{
					IsExcuted = 1;
				}
				BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
				BaseClassDAO.myDb.beginTransaction();
				try
				{
				String SQLStr = "UPDATE "+this.PlanDataTable+" SET IsExcuted = '"+IsExcuted+"',NotExcutedNumbers="
				+"'"+NotExcutedNumbers+"'"
				+" WHERE LoginId="+"'"+this.user+"' and "+"InfoId ="+InfoId+" and "
				+"InOutCount="+"'"+InOutCount+"'";
				BaseClassDAO.myDb.execSQL(SQLStr);
				BaseClassDAO.myDb.setTransactionSuccessful();
				}
				finally
				{
					BaseClassDAO.myDb.endTransaction();
					this.closeDatabase();
				}
			}
			/**
			 * �˷����������û�������ʾ�����ƻ�ʱ������inOutCount��
			 * ����
			 */
			public static String explainInOutCount(int inOutCount)
			{
				String prefix = "";
				String content = "";
				if(inOutCount>0)
				{
					prefix = "���";
				}
				else prefix = "����";
				content = String.valueOf(Math.abs(inOutCount));
				return prefix+content;
			}

			/**
			 * �˷������ڴ�ӡ�ƻ�ʱ��ȡ��ǰ�û��ƻ��в�ͬ��BillId��
			 */
			public List<String> getDistinctBillId()
			{
				String SQLStr ="SELECT DISTINCT BillId FROM "+PlanDataTable
						+" WHERE LoginId='"+this.user+"'";
				BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
				Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
				List<String> lst =new ArrayList<String>();
				while(cursor.moveToNext())
				{
					lst.add(cursor.getString(cursor.getColumnIndex("BillId")));
				}
				cursor.close();
				return lst;
			}
			/**
			 * �˷������ڴ�ӡ�ƻ�ʱ������BillId���ص�ǰ�û��ļƻ��б�
			 * һ��BillId����һ���б���ӡ��һ��ֽ��
			 */
			public List<PlanDataModel> getPrintPlanList(String  BillId)
			{
				BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
				String SQLStr = "SELECT *  FROM "+this.PlanDataTable+" WHERE LoginId ='"+this.user+"' and "
										+"BillId = '"+BillId+"'";
				Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
				List<PlanDataModel> result = new ArrayList<PlanDataModel>();
				while(cursor.moveToNext())
				{
					result.add(new PlanDataModel
					(
					cursor.getInt(cursor.getColumnIndex("InfoId")),
					cursor.getInt(cursor.getColumnIndex("IsExcuted")),
					cursor.getString(cursor.getColumnIndex("MatterId")),
					cursor.getInt(cursor.getColumnIndex("InOutCount")),
					cursor.getString(cursor.getColumnIndex("MatterName")),
					cursor.getString(cursor.getColumnIndex("BillId")),
					cursor.getInt(cursor.getColumnIndex("IsHasLabel")),
					cursor.getString(cursor.getColumnIndex("LoginId")),
					cursor.getInt(cursor.getColumnIndex("NotExcutedNumbers")),
					cursor.getString(cursor.getColumnIndex("AvailableStorageIdList")),
					cursor.getString(cursor.getColumnIndex("LabelInfo")),
					cursor.getString(cursor.getColumnIndex("Workshop")),
					cursor.getString(cursor.getColumnIndex("GetPlanTime")),
					cursor.getString(cursor.getColumnIndex("GiveOutPerson")),
					cursor.getString(cursor.getColumnIndex("GetPerson")),
					cursor.getInt(cursor.getColumnIndex("IsCatch"))));
				}
				cursor.close();
				this.closeDatabase();
				return result;
			}
			/**
			 * �˷������ڴ�ӡʱ��������һ��BillIdʱ����ȡ��BillId��Ӧ�ļƻ�Ϊ
			 * ����ƻ�(����Ϊfalse)�������ƻ�(����Ϊtrue)
			 */
			public boolean getInOutByBillId(String BillId)
			{
				List<PlanDataModel> pdl = getPrintPlanList(BillId);
				//��ȡ�üƻ��б��еĵ�һ�������InOutCount�ֶ��ж������ƻ�
				//���ǳ���ƻ�
				PlanDataModel p =pdl.get(0);
				if(p.getInOutCount()>0)
				{
					return true;
				}
				else
				{
					return false;
				}
			}

}
/*
			+ "InfoId integer,"
			+ "StorageId varchar(50),"
			+ "MatterId varchar(50),"
			+ "InOutCount integer,"
			+ "StorageName integer,"
			+ "MatterName varchar(50),"
			+ "BillId varchar(50),"
			+ "IsExcute integer,"
			+ "IsHasLabel integer,"
			+ "LoginId varchar(50),"
			+ "ExcuteTime varchar(50),"
			+ "IsCatch integer)";
			
	private int InfoId;
	private String StorageId;
	private String MatterId;
	private int InOutCount;
	private String StorageName;
	private String MatterName;
	private String BillId;
	private int IsExcute;
	private int IsHasLabel;//�Ƿ��б�ǩ
	private int IsCatch;//�Ƿ����
	private String LoginId;//ִ����
	private String ExcuteTime;//ִ��ʱ��
 */
/*GetInOutPlan:
{
"Flag": "1",
"List": [
{
"InfoId": 38,
"StorageId": "11",
"MatterId": "11",
"InOutCount": 100,
"StorageName": "��ʩ__03��__B��__01��",
"MatterName": "�а� ��P60 ��",
"BillId": "30",
"IsExcute": 0,
"IsHasLabel": 1,
"IsCatch": 0
},
{
"InfoId": 39,
"StorageId": "12",
"MatterId": "6",
"InOutCount": 20,
"StorageName": "��ʩ__03��__B��__02��",
"MatterName": "�а壻P50 ��",
"BillId": "31",
"IsExcute": 0,
"IsHasLabel": 1,
"IsCatch": 0
}
]
}

*/