package com.qpguo.uhf.modelDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qpguo.uhf.model.LargeMatterGiveOutModel;
import com.qpguo.uhf.utils.FileService;
import com.qpguo.uhf.utils.HttpApi;
import com.qpguo.uhf.utils.UploadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class LargeMatterGiveOutDAO extends BaseClassDAO
{
	private String TAG = "LargeMatterGiveOutDAO";
	private final String LargeMatterGiveOut  = DatabaseOpenHelper.TABLE_LARGEMATTERGIVEOUT_NAME;
	private final String METHODNAME = "GetLargeMatterGiveOutInfo";
	private final String UPLOADMETHODNAME = "UploadLargeMatterGiveOutInfo";
	private Context context;
	
	public LargeMatterGiveOutDAO(Context context) 
	{
		super(context);
		this.context = context;
	}
	
	/**下载大件的发卡数据*/
	public boolean downLoadLargeMatterGiveOutData()
	{
		boolean result = false;
		this.ClearLargeMatterGiveOutData();
		HttpApi request = new HttpApi();
		try 
		{
			String content = request.GetRequest(BaseClassDAO.SERVERHOST, this.METHODNAME);
			Log.i(TAG, "大件发卡数据下载返回的内容:"+content);
			JSONObject jo = new JSONObject(content);
			int flag  = jo.getInt("Flag");
			if(flag==1)
			{
				result = true;
				JSONArray ja = jo.getJSONArray("List");
				for(int i=0;i<ja.length();i++)
				{
					JSONObject temp = ja.getJSONObject(i);
					LargeMatterGiveOutModel model = new LargeMatterGiveOutModel
					(
					temp.getString("LargeMatterId"),
					temp.getString("MatterId"),
					"0"//初始化为都没有发卡
					);
					this.insertItem(model);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	//往表中插入一条记录
	public long  insertItem(LargeMatterGiveOutModel model)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("LargeMatterId", model.getLargeMatterId());
		values.put("MatterId", model.getMatterId());
		values.put("IsGiveOut", model.getIsGiveOut());
		BaseClassDAO.myDb.beginTransaction();
		long InsertStatus = -1;
		try
		{
				InsertStatus = BaseClassDAO.myDb.insert(LargeMatterGiveOut, null, values);
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
	
	/**
	 * 此方法用于获取表中所有需要上传的内容
	 * IsGiveOut = 0 获取所有需要发卡数据
	 * IsGiveOut = 1 获取所有已发卡数据
	 */
	public List<LargeMatterGiveOutModel> getLargeMatterGiveOutData(int IsGiveOut)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr ="SELECT * FROM "+LargeMatterGiveOut+" WHERE IsGiveOut="+IsGiveOut;
		Cursor cursor =BaseClassDAO.myDb.rawQuery(SQLStr, null);
		List<LargeMatterGiveOutModel> result = new ArrayList<LargeMatterGiveOutModel>();
		while(cursor.moveToNext())
		{
			LargeMatterGiveOutModel updm = new LargeMatterGiveOutModel
					(cursor.getString(cursor.getColumnIndex("LargeMatterId")),
					  cursor.getString(cursor.getColumnIndex("MatterId")),
					  cursor.getString(cursor.getColumnIndex("IsGiveOut")));
			result.add(updm);
		}
		cursor.close();
		this.closeDatabase();
		return result;
	}
	
	/**
	 * JSON格式：[{"LargeMatterId":"23","MatterId":"45"},{"LargeMatterId":"23","MatterId":"45"}]
	 * @throws JSONException 
	 */
	public JSONArray getJSONArray() 
	{
		List<LargeMatterGiveOutModel> data = this.getLargeMatterGiveOutData(1);
		JSONArray ja = new JSONArray();
		try
		{
				for(LargeMatterGiveOutModel x:data)
				{
					JSONObject jo = new JSONObject();
					jo.put("LargeMatterId", x.getLargeMatterId());
					jo.put("MatterId", x.getMatterId());
					ja.put(jo);
				}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ja;
	}
	/**
	 * 此方法用于上传用户执行计划的数据
	 */
	public boolean uploadLargeMatterGiveOutData()
	{
		// "http://192.168.1.108/Ashx/Info.ashx?MethodName=InOutBat";
		String BaseUrl = "http://"+BaseClassDAO.SERVERHOST+"/Ashx/Info.ashx?";
		String requestUrl = BaseUrl +"MethodName="+this.UPLOADMETHODNAME;
		Log.i(TAG, "upload requesrUrl:"+requestUrl);
		boolean result =false;
		JSONObject ja;
		try {
			       String returnContent = UploadUtil.uploadFile(new File(this.createTxt()), requestUrl);
			       Log.i(TAG, "大件打卡数据上传返回的内容:"+returnContent);
					ja = new JSONObject(returnContent);
					if(ja.getInt("Flag")==1)
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
	
	/*将表中当前用户所有内容生成txt格式*/
	public  String createTxt()
	{
		FileService fs = new FileService(this.context);
		String fileName =LargeMatterGiveOut+".txt" ;
		fs.writeFile(fileName, this.getJSONArray().toString());
		return fs.getFilePath(fileName);
	}
	
	/**执行完一条大件发卡数据后，更改发卡标志位*/
	public void updateGiveOutInfo(String largeMatterId)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr ="UPDATE "+LargeMatterGiveOut+" SET IsGiveOut=1"
				+" WHERE LargeMatterId = "+"'"+largeMatterId+"'" ;
		BaseClassDAO.myDb.beginTransaction();
		try
		{
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
	 * 清空大件发卡数据库，用于大件下载时使用
	 */
	public void ClearLargeMatterGiveOutData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ LargeMatterGiveOut);
		this.closeDatabase();
		Log.i(TAG, "table deleted!!!!");
	}
	
	
	



}
/*	       +"LargeMatterId varchar(50),"//大件自身编号
	       +"MatterId varchar(50),"//大件的物资编号
	       +"IsGiveOut varchar(50))";//是否已发卡
*/