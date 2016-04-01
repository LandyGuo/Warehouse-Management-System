package com.qpguo.uhf.modelDAO;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qpguo.uhf.model.LargeMatterModel;
import com.qpguo.uhf.utils.HttpApi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/*大件数据库操作类，此类用于操作存储大件信息的数据库*/
/*此数据库作为大件基础信息库，更新时下载新增的大件操作数据(插入)
 * 其它情况下只用于查询，不分用户，第一次下载所有信息(请求信息时在
 * URL参数中表明下载的数据起点)*/
public class LargeMatterDAO extends BaseClassDAO
{
	private String TAG = "LargeMatterDAO";
	public final static String LargeMatterDataTable  = DatabaseOpenHelper.TABLE_LARGEMATTERDATA_NAME;
	private final static String METHODNAME = "GetLargeMatterInfo";
	public LargeMatterDAO(Context context)
	{
		super(context);
	}
	/**
	 * 此方法用于下载大件基础信息并保存到数据库
	 * 大件基础信息一经保存永不删除
	 */
	public boolean downLoadLargeMatterData()
	{
		boolean result = false;
		HttpApi request = new HttpApi();
		try 
		{
			//TODO:修改这里的From
			String content = request.PostRequest(BaseClassDAO.SERVERHOST, LargeMatterDAO.METHODNAME,
					new BasicNameValuePair("From",String.valueOf(getTotalItemNumber())));
			Log.i(TAG, "From:"+String.valueOf(getTotalItemNumber()));
			Log.i(TAG, "返回的内容:"+content);
			JSONObject jo = new JSONObject(content);
			int flag  = jo.getInt("Flag");
			if(flag==1)
			{
				result = true;
				JSONArray ja = jo.getJSONArray("List");
				for(int i=0;i<ja.length();i++)
				{
					JSONObject temp = ja.getJSONObject(i);
					LargeMatterModel model = new LargeMatterModel
					(
					temp.getString("LargeMatterId"),
					temp.getString("OpeType"),
					temp.getString("MatterId"),
					temp.getString("LoginId"),
					temp.getString("ExcuteTime")
					);
					this.insertLargeMatterData(model);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	//往表中插入一条记录
	public long  insertLargeMatterData(LargeMatterModel largeMatterData)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("LargeMatterId", largeMatterData.getLargeMatterId());
		values.put("OpeType", largeMatterData.getOpeType());
		values.put("MatterId", largeMatterData.getMatterId());
		values.put("LoginId", largeMatterData.getLoginId());
		values.put("ExcuteTime", largeMatterData.getExcuteTime());
		BaseClassDAO.myDb.beginTransaction();
		long InsertStatus = -1;
		try
		{
				InsertStatus = BaseClassDAO.myDb.insert(LargeMatterDataTable, null, values);
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
	
	/*获取大件表中已有的总记录数*/
	public  int getTotalItemNumber()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr = "SELECT COUNT(*) FROM "+LargeMatterDataTable;
		Cursor cursor = (Cursor)BaseClassDAO.myDb.rawQuery(SQLStr, null);
		cursor.moveToNext();
		int number = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));
		cursor.close();
		BaseClassDAO.myDb.close();
		return number;
	}
	
	/**
	 * 此方法用于根据LargeMatterId获取此LargeMatterId的所有操作记录
	 * 并按时间排序返回
	 */
	public List<LargeMatterModel> findHistoryInfoByLargeMatterId(String LargeMatterId)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr = "SELECT * FROM "+LargeMatterDataTable+" WHERE LargeMatterId="
				+"'"+LargeMatterId+"'"+" ORDER BY ExcuteTime DESC ";
		Cursor cursor = (Cursor)BaseClassDAO.myDb.rawQuery(SQLStr, null);
		List<LargeMatterModel> result = new ArrayList<LargeMatterModel>();
		while(cursor.moveToNext())
		{
			LargeMatterModel  model = new LargeMatterModel
			(
			cursor.getString(cursor.getColumnIndex("LargeMatterId")),
			cursor.getString(cursor.getColumnIndex("OpeType")),
			cursor.getString(cursor.getColumnIndex("MatterId")),
			cursor.getString(cursor.getColumnIndex("LoginId")),
			cursor.getString(cursor.getColumnIndex("ExcuteTime"))
			);
			result.add(model);
		}
		cursor.close();
		this.closeDatabase();
		return result;
	}
	/**
	 * 慎用！！！仅用于测试数据时清空表，正式发布后不要调用此方法！！*/
	public void ClearLargeMatterData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ LargeMatterDataTable);
		BaseClassDAO.myDb.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '"+LargeMatterDataTable+"'");
		this.closeDatabase();
		Log.i(TAG, "Important ERROR:LargeMatterBaseData table deleted!!!!");
	}
	



}
/*数据模型
 * 	private String LargeMatterId;//大件自身编号
	private String OpeType;//操作类型
	private String MatterId;//物资id
	private String LoginId;//操作人，即登录者
	private String ExcuteTime;//执行时间
 */
/*数据库字段
 * 			+ "LargeMatterId varchar(50),"//大件自身编号
			+ "OpeType varchar(50),"//操作类型
			+ "MatterId varchar(50),"//物资id，表明其物资类型
			+ "LoginId varchar(50),"//操作者，即登录人
			+ "ExcuteTime varchar(50))";//执行时间		
 */