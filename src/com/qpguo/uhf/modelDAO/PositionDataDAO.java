package com.qpguo.uhf.modelDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qpguo.uhf.model.PositionModel;
import com.qpguo.uhf.utils.FileService;
import com.qpguo.uhf.utils.HttpApi;
import com.qpguo.uhf.utils.UploadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class PositionDataDAO extends BaseClassDAO
{
	private final static String TAG = "PositionDataDAO";
	public final static String PositonDataTable  = DatabaseOpenHelper.TABLE_POSITIONDATA_NAME;
	private final String METHODNAME = "GetList_StoragePosition";
	private final String UPLOADMETHODNAME ="PostGiveOutInfo";
	private Context context;
	public PositionDataDAO(Context context)
	{
		super(context);
		this.context = context;
	}
		//下载数据之前一定要先清空表
		public boolean downLoadPositionData()
		{
			boolean result = false;
			this.ClearPositionData();
			HttpApi request = new HttpApi();
			try 
			{
				String content = request.GetRequest(BaseClassDAO.SERVERHOST, this.METHODNAME);
				JSONObject jo = new JSONObject(content);
				int flag  = jo.getInt("Flag");
				if(flag==1)
				{
					result = true;
					JSONArray ja = jo.getJSONArray("List");
					for(int i=0;i<ja.length();i++)
					{
						JSONObject temp = ja.getJSONObject(i);
						PositionModel model = new PositionModel
						(
						temp.getInt("id"),
						0,//默认下载下来后都不需要回传
						temp.getInt("PositionCode"),
						temp.getString("PositionName"),
						temp.getString("MatterId"),
						temp.getInt("TheCount"),
						temp.getInt("IsGiveOut"),
						temp.getInt("IsCatch"),
						temp.getInt("IsHasLabel")
						);
						this.insertPositionData(model);
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return result;
		}
		//往表中插入一条记录
		public long  insertPositionData(PositionModel positionData)
		{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("id", positionData.getId());
			values.put("IsUpload", positionData.getIsUpload());
			values.put("PositionCode", positionData.getPositionCode());
			values.put("PositionName", positionData.getPositionName());
			values.put("MatterId", positionData.getMatterId());
			values.put("TheCount", positionData.getTheCount());
			values.put("IsGiveOut", positionData.getIsGiveOut());
			values.put("IsCatch", positionData.getIsCatch());
			values.put("IsHasLabel", positionData.getIsHasLabel());
			BaseClassDAO.myDb.beginTransaction();
			long InsertStatus = -1;
			try
			{
					InsertStatus = BaseClassDAO.myDb.insert(PositonDataTable, null, values);
					BaseClassDAO.myDb.setTransactionSuccessful();
					Log.i(TAG, "InsertStatus:"+InsertStatus);
			}
			finally
			{
				BaseClassDAO.myDb.endTransaction();
				this.closeDatabase();
			}
			return InsertStatus;
		}
		
		public void ClearPositionData()
		{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			BaseClassDAO.myDb.execSQL("DELETE FROM "+ PositonDataTable);
			this.closeDatabase();
			Log.i(TAG, "table deleted!!!!");
		}
		//获取未发卡的列表
		public List<PositionModel> getNotGiveOutData()
		{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			String SQLStr = "SELECT * FROM "+PositonDataTable+" WHERE IsGiveOut = 0 AND IsHasLabel =1";
			Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
			List<PositionModel> result = new ArrayList<PositionModel>();
			while(cursor.moveToNext())
			{
				result.add(new PositionModel(
				cursor.getInt(cursor.getColumnIndex("id")),
				cursor.getInt(cursor.getColumnIndex("IsUpload")),
				cursor.getInt(cursor.getColumnIndex("PositionCode")),
				cursor.getString(cursor.getColumnIndex("PositionName")),
				cursor.getString(cursor.getColumnIndex("MatterId")),
				cursor.getInt(cursor.getColumnIndex("TheCount")),
				cursor.getInt(cursor.getColumnIndex("IsGiveOut")),
				cursor.getInt(cursor.getColumnIndex("IsCatch")),
				cursor.getInt(cursor.getColumnIndex("IsHasLabel"))));
			}
			cursor.close();
			this.closeDatabase();
			return result;
		}
		/**
		 * 此方法由物资的StorageId在position表中获取当前储位的物资信息
		 */
		public static PositionModel findPositionData(String StorageId)
		{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			String SQLStr = "SELECT * FROM "+PositonDataTable+" WHERE PositionCode="+StorageId;
			Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
			PositionModel model = null;
			while(cursor.moveToNext())
			{
				model =new PositionModel
				(
				cursor.getInt(cursor.getColumnIndex("id")),
				cursor.getInt(cursor.getColumnIndex("IsUpload")),
				cursor.getInt(cursor.getColumnIndex("PositionCode")),
				cursor.getString(cursor.getColumnIndex("PositionName")),
				cursor.getString(cursor.getColumnIndex("MatterId")),
				cursor.getInt(cursor.getColumnIndex("TheCount")),
				cursor.getInt(cursor.getColumnIndex("IsGiveOut")),
				cursor.getInt(cursor.getColumnIndex("IsCatch")),
				cursor.getInt(cursor.getColumnIndex("IsHasLabel")));
			}
			cursor.close();
			BaseClassDAO.myDb.close();
			return model;
		}
		/**
		 * 此方法用于在执行完一条PlanData后，根据StorageId查找该储位的
		 * 物资数量，并根据PlanData中的InOutCount修改当前position表中的
		 * 物资的数量
		 */
		public static void UpdateTheCount(String StorageId,int InOutCount)
		{
			PositionModel updateItem =  findPositionData(StorageId);
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			BaseClassDAO.myDb.beginTransaction();
			try
			{
				if(updateItem!=null)
				{
					//更新此记录的TheCount
					int TheCount = updateItem.getTheCount();
					int NowCount = TheCount + InOutCount;
					Log.i("PositionDataDAO", "TheCount:"+TheCount+"NowCount:"+NowCount);
					String SQLStr = "UPDATE "+PositonDataTable+" SET TheCount="
							+NowCount+" WHERE PositionCode="+StorageId;
					BaseClassDAO.myDb.execSQL(SQLStr);
					BaseClassDAO.myDb.setTransactionSuccessful();
				}
			}
			finally
			{
				BaseClassDAO.myDb.endTransaction();
				BaseClassDAO.myDb.close();
			}
		}
		/**
		 * 此方法用于发卡完成后修改position表中的IsUpload和IsGiveOut标志位为1
		 */
		public static void updateTheFlag(String StorageId)
		{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			BaseClassDAO.myDb.beginTransaction();
			try
			{
					String SQLStr = "UPDATE "+PositonDataTable+" SET IsGiveOut=1, IsUpload=1"
							+" WHERE PositionCode="+StorageId;
					BaseClassDAO.myDb.execSQL(SQLStr);
					BaseClassDAO.myDb.setTransactionSuccessful();
					Log.i(TAG,"update Position positioncode = "+StorageId);
			}
			finally
			{
				BaseClassDAO.myDb.endTransaction();
				BaseClassDAO.myDb.close();
			}
		}
		/**
		 * 此方法用于用户发卡完毕后获得准备上传的已发卡的数据
		 */
		public List<PositionModel> getUploadData()
		{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			String SQLStr = "SELECT * FROM "+PositonDataTable+" WHERE IsUpload = 1";
			Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
			List<PositionModel> result = new ArrayList<PositionModel>();
			while(cursor.moveToNext())
			{
				result.add(new PositionModel(
				cursor.getInt(cursor.getColumnIndex("id")),
				cursor.getInt(cursor.getColumnIndex("IsUpload")),
				cursor.getInt(cursor.getColumnIndex("PositionCode")),
				cursor.getString(cursor.getColumnIndex("PositionName")),
				cursor.getString(cursor.getColumnIndex("MatterId")),
				cursor.getInt(cursor.getColumnIndex("TheCount")),
				cursor.getInt(cursor.getColumnIndex("IsGiveOut")),
				cursor.getInt(cursor.getColumnIndex("IsCatch")),
				cursor.getInt(cursor.getColumnIndex("IsHasLabel"))));
			}
			cursor.close();
			this.closeDatabase();
			return result;
		}
		/**
		 * 此方法用于上传已发卡的数据之前，获得TXT文本格式的
		 * JSONArray
		 * 其具体格式如下：
		 * [{“IsGiveOut”:”1”,”StorageId”:”1”}]
		 */
		public JSONArray getJSONArray()
		{
			List<PositionModel> CardGivenOut =  getUploadData();
			JSONArray ja = new JSONArray();
			try
			{
					for(PositionModel plan : CardGivenOut)
					{
						JSONObject jo = new JSONObject();
						jo.put("IsGiveOut", String.valueOf(plan.getIsGiveOut()));
						jo.put("StorageId",String.valueOf(plan.getPositionCode()));
						ja.put(jo);
					}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return  ja;
		}
		/**
		 * 此方法用于生成上传所需要的发卡的txt文件并返回其绝对路径
		 */
		public  String createTxt()
		{
			FileService fs = new FileService(this.context);
			String fileName =PositonDataTable+".txt" ;
			fs.writeFile(fileName, this.getJSONArray().toString());
			return fs.getFilePath(fileName);
		}
		/**
		 * 此方法用于将当前用户已经执行完成的发卡数据列表上传
		 */
		public boolean uploadGivenOutData()
		{
			// "http://192.168.1.108/Ashx/Info.ashx?MethodName=InOutBat";
			String BaseUrl = "http://"+BaseClassDAO.SERVERHOST+"/Ashx/Info.ashx?";
			String requestUrl = BaseUrl +"MethodName="+this.UPLOADMETHODNAME;
			Log.i("MainActivity", "upload requesrUrl:"+requestUrl);
			boolean result =false;
			JSONArray ja;
			try {
						ja = new JSONArray(UploadUtil.uploadFile(new File(this.createTxt()), requestUrl));
						if(ja.getJSONObject(0).getString("Flag").equals("1"))
						{
							result = true;
						}
					}
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			return result;
		}
		
		/**
		 * 此方法用于在更新position表时，先获取原表中IsUpdate=1的数据项列表
		 * (List<PositionModel>)，然后清空position表并下载，下载后仍将在
		 * List<PositionModel>中的数据项的IsUpdate字段置为1,这样保证了每次更新
		 * position表时的未上传的发卡数据不会丢失
		 */
		public void updateDownloadPosition()
		{
			//获取下载前的已发卡列表
			List<PositionModel> lst = this.getUploadData();
			//下载新的position数据
			this.downLoadPositionData();
			//逐项更新新数据中已上传过的数据
			for(PositionModel p:lst)
			{
				updateTheFlag(String.valueOf(p.getPositionCode()));
			}
		}
		
}
/*
 * 
GetList_StoragePosition：
{
"Flag": 1,
"List": [
{
"id": 11,
"PositionCode": 11,
"PositionName": "恩施__03库__B区__01箱",
"MatterId": "11",
"TheCount": 100,
"IsGiveOut": 1,
"IsCatch": 0,
"IsHasLabel": 1
},
{
"id": 12,
"PositionCode": 12,
"PositionName": "恩施__03库__B区__02箱",
"MatterId": "6",
"TheCount": 20,
"IsGiveOut": 1,
"IsCatch": 0,
"IsHasLabel": 1
}
]
}

/*
			+ "id integer,"
			+"IsUpload integer,"//用于手机本地上传发卡数据的标志位，此数据仅供手机客户端使用
			//不要上传
			+ "PositionCode varchar(50)," // 位置名称
			+ "PositionName varchar(50),"// 对照编号
			+ "MatterId varchar(50),"// 物料编号
			+ "TheCount varchar(50)," //数量
			+ "IsGiveOut varchar(50),"// 是否已发卡
			+ "IsCatch varchar(50)," //是否跟踪
			+ "IsHasLabel varchar(50))";  //是否有标签;
*/



