package com.qpguo.uhf.modelDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qpguo.uhf.model.UploadPlanDataModel;
import com.qpguo.uhf.utils.FileService;
import com.qpguo.uhf.utils.UploadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class UploadPlanDataDAO extends BaseClassDAO
{
	private String TAG = "UploadPlanDataDAO";
	public final static String UploadPlanDataTable  = DatabaseOpenHelper.TABLE_UPLOADPLANDATA_NAME;
	private String user;//此操作接口与特定用户绑定
	private Context context;
	private final String UPLOADMETHODNAME = "InOutBat";
	public UploadPlanDataDAO(Context context,String user) 
	{
		super(context);
		this.context = context;
		this.user = user;
	}
	
	/**插入一条记录,用户名默认为当前登陆用户的id*/
	public long insertItem(String InfoId,String InOutCount,String ExcutedTime,String ExcutedNumber,String StorageId)
	{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("InfoId", InfoId);
			values.put("ExcutedTime", ExcutedTime);
			values.put("ExcutedNumber", ExcutedNumber);
			values.put("LoginId", this.user);
			values.put("StorageId", StorageId);
			values.put("InOutCount", InOutCount);
			BaseClassDAO.myDb.beginTransaction();
			long InsertStatus = -1;
			try
			{
					InsertStatus = BaseClassDAO.myDb.insert(UploadPlanDataTable, null, values);
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
	/*将表中当前用户所有内容生成txt格式*/
	public  String createTxt()
	{
		FileService fs = new FileService(this.context);
		String fileName =UploadPlanDataTable+".txt" ;
		fs.writeFile(fileName, this.getJSONArray().toString());
		return fs.getFilePath(fileName);
	}
	/**
	 * 此方法用于上传用户执行计划的数据
	 */
	public boolean uploadPlanData()
	{
		// "http://192.168.1.108/Ashx/Info.ashx?MethodName=InOutBat";
		String BaseUrl = "http://"+BaseClassDAO.SERVERHOST+"/Ashx/Info.ashx?";
		String requestUrl = BaseUrl +"MethodName="+this.UPLOADMETHODNAME;
		Log.i(TAG, "upload requesrUrl:"+requestUrl);
		boolean result =false;
		JSONArray ja;
		try {
			       String returnContent = UploadUtil.uploadFile(new File(this.createTxt()), requestUrl);
			       Log.i(TAG, "返回的内容:"+returnContent);
					ja = new JSONArray(returnContent);
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
	 * 此方法用于在将当前用户数据上传后清空当前用户已执行的计划数据
	 * 必须用在上传成功之后
	 */
	public void ClearPlanData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ UploadPlanDataTable+ " WHERE LoginId ='"+this.user+"'"
				);
		this.closeDatabase();
		Log.i(TAG, "uploadplandata table deleted!!!!LoginId:"+this.user);
	}
	/**
	 * 此方法用于获取表中当前用户所有需要上传的内容
	 */
	public List<UploadPlanDataModel> getUploadPlanData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr ="SELECT * FROM "+UploadPlanDataTable+" WHERE LoginId = "
				+"'"+this.user+"'";
		Cursor cursor =BaseClassDAO.myDb.rawQuery(SQLStr, null);
		List<UploadPlanDataModel> result = new ArrayList<UploadPlanDataModel>();
		while(cursor.moveToNext())
		{
			UploadPlanDataModel updm = new UploadPlanDataModel
					(cursor.getString(cursor.getColumnIndex("InfoId")),
					  cursor.getString(cursor.getColumnIndex("ExcutedTime")),
					  cursor.getString(cursor.getColumnIndex("ExcutedNumber")),
					  cursor.getString(cursor.getColumnIndex("LoginId")),
					  cursor.getString(cursor.getColumnIndex("StorageId")),
					  cursor.getString(cursor.getColumnIndex("InOutCount")));
			result.add(updm);
		}
		cursor.close();
		this.closeDatabase();
		return result;
	}
	/**
	 * JSON格式：
	 * [{"InfoId":"23","ExcutedTime":"2014-07-01","ExcuteNumber":"100","LoginId":"1"
	 * ,"StorageId":"25"}]
	 * @throws JSONException 
	 */
	public JSONArray getJSONArray() 
	{
		List<UploadPlanDataModel> data = this.getUploadPlanData();
		JSONArray ja = new JSONArray();
		try
		{
				for(UploadPlanDataModel x:data)
				{
					JSONObject jo = new JSONObject();
					jo.put("InfoId", x.getInfoId());
					jo.put("ExcutedTime", x.getExcutedTime());
					jo.put("ExcuteNumber", x.getExcutedNumber());
					jo.put("LoginId", x.getLoginId());
					jo.put("StorageId", x.getStorageId());
					jo.put("InOutCount", x.getInOutCount());
					ja.put(jo);
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ja;
	}




}
/*数据库表结构
+"("
+"InfoId varchar(50),"//计划的唯一标识
+"ExcutedTime varchar(50),"//该条计划的部分执行时间
+"ExcutedNumber varchar(50),"//该条计划的此次执行数量
+"LoginId varchar(50),"//登陆人，即计划的执行人
+"StorageId varchar(50))";//该条计划执行的储位
*/
/*数据模型
private String InfoId;
private String ExcutedTime;
private String ExcutedNumber;
private String LoginId;
private String StorageId;
*/