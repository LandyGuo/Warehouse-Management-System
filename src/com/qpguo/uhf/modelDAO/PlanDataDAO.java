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
	//由于plandata的数据与用户相关，每个用户获得的计划表不同
	public String getCurrentUser()
	{
		return user;
	}
	//下载用户名为LoginId的计划表，一定要先清空其名下的所有计划表
	//因为每次从服务器上获取的都是所有未执行的表
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
							0,//初始下载时执行位置为0，因为返回的均为未执行
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
			//往表中插入一条记录
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
			//清除当前用户数据
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
			 * 此函数用于从plandata表中获取已执行的项(IsExcuted=1)
			 *或未执行(IsExcute=0)项
			 * 参数：IsExcuted=0：返回所有未执行项
			                 IsExcuted=1：返回所有已执行项
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
			//根据InfoId更新IsExcuted(int)标志位
			/**
			 * 当一条计划完全被执行时(NotExcutedNumbers为0),将IsExcuted置为1,用于筛选
			 * 下次进入列表时的显示项不包含已完全执行的项
			 * 用于执行一条计划结束后，更新其执行IsExcuted标志位和NotExcutedNumbers
			 * @param InfoId
			 * @param IsExcuted 完全执行完全为1，未执行为0
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
			 * 此方法用于在用户界面显示出入库计划时，解释inOutCount的
			 * 含义
			 */
			public static String explainInOutCount(int inOutCount)
			{
				String prefix = "";
				String content = "";
				if(inOutCount>0)
				{
					prefix = "入库";
				}
				else prefix = "出库";
				content = String.valueOf(Math.abs(inOutCount));
				return prefix+content;
			}

			/**
			 * 此方法用于打印计划时获取当前用户计划中不同的BillId项
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
			 * 此方法用于打印计划时，根据BillId返回当前用户的计划列表
			 * 一个BillId返回一个列表，打印到一张纸上
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
			 * 此方法用于打印时，当给定一个BillId时，获取该BillId对应的计划为
			 * 出库计划(返回为false)还是入库计划(返回为true)
			 */
			public boolean getInOutByBillId(String BillId)
			{
				List<PlanDataModel> pdl = getPrintPlanList(BillId);
				//获取该计划列表中的第一项并根据其InOutCount字段判断是入库计划
				//还是出库计划
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
	private int IsHasLabel;//是否有标签
	private int IsCatch;//是否跟踪
	private String LoginId;//执行人
	private String ExcuteTime;//执行时间
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
"StorageName": "恩施__03库__B区__01箱",
"MatterName": "夹板 ；P60 ；",
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
"StorageName": "恩施__03库__B区__02箱",
"MatterName": "夹板；P50 ；",
"BillId": "31",
"IsExcute": 0,
"IsHasLabel": 1,
"IsCatch": 0
}
]
}

*/