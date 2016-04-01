package com.qpguo.uhf.modelDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qpguo.uhf.model.PanDianDataModel;
import com.qpguo.uhf.utils.FileService;
import com.qpguo.uhf.utils.UploadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class PanDianDataDAO extends BaseClassDAO
{
	private final String TAG = "PanDianDataDAO";
	//存储盘点数据的表名
	private final String PanDianTable = DatabaseOpenHelper.TABLE_PANDIANDATA_NAME;
	private Context context;
	private final String METHODNAME = "CheckInfo";
	public PanDianDataDAO(Context context)
	{
		super(context);
		this.context = context;
	}
	//盘点数据完全由用户输入后上传，用户输入存在数据库中，上传时
	//需要建立.txt文件上传
	public long  insertPanDianData(PanDianDataModel pdData)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("StorageId", pdData.getStorageId());
		values.put("MatterId", pdData.getMatterId());
		values.put("LabelCount", pdData.getLabelCount());
		values.put("RealCount", pdData.getRealCount());
		BaseClassDAO.myDb.beginTransaction();
		long InsertStatus = -1;
		try
		{
				InsertStatus = BaseClassDAO.myDb.insert(PanDianTable, null, values);
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
	 * 生成txt文件，格式如下：
	[{"StorageId":"1","MatterId":"111","LabelCount":"10","RealCount":"10"},
	{"StorageId":"2","MatterId":"222","LabelCount":"10","RealCount":"5"}]
	   返回值：创建文件的路径名
	*/
	public String createTxt()
	{
		FileService fs = new FileService(this.context);
		String fileName = PanDianTable+".txt";
		fs.writeFile(fileName, this.getJSONArray().toString());
		return fs.getFilePath(fileName);
	}
	
	/**
	 * 此方法用于将已经盘点的数据上传,上传成功返回true
	 * 否则返回false
	 */
	public boolean uploadPandianData()
	{
		// "http://192.168.1.108/Ashx/Info.ashx?MethodName=CheckInfo";
		String BaseUrl = "http://"+BaseClassDAO.SERVERHOST+"/Ashx/Info.ashx?";
		String requestUrl = BaseUrl +"MethodName="+this.METHODNAME;
		Log.i("MainActivity", "upload requesrUrl:"+requestUrl);
		boolean result =false;
		JSONArray jo;
		try {
			        String returnContent = UploadUtil.uploadFile(new File(this.createTxt()), requestUrl);
			        Log.i(TAG, "盘点上传返回的内容:"+returnContent);
					jo = new JSONArray(returnContent);
					if(jo.getJSONObject(0).getString("Flag").equals("1"))
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
	此方法主要实现将表中的数据全部转换为JSONArray形式
	*/
	public JSONArray getJSONArray()
	{ 
		JSONArray ja = new JSONArray();
		//获取表中所有List数据
		List<PanDianDataModel> panDianDataList = this.getPanDianDataList();
		for(PanDianDataModel p:panDianDataList)
		{
				JSONObject jo = new JSONObject();
				try
				{
					jo.put("StorageId", p.getStorageId());
					jo.put("MatterId", p.getMatterId());
					jo.put("LabelCount", p.getLabelCount());
					jo.put("RealCount", p.getRealCount());
					ja.put(jo);
				}
				catch(JSONException e)
				{
					e.printStackTrace();
				}
		}
		return ja;
	}
	//以List的形式返回整个表中的数据，用于将表中数据写入txt文件时调用
	public List<PanDianDataModel>  getPanDianDataList()
	{
		List<PanDianDataModel> PandianResult = new ArrayList<PanDianDataModel>();
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLString = "select * from "+this.PanDianTable;
		try
		{
			Cursor cursor =  BaseClassDAO.myDb.rawQuery(SQLString, null);
			while(cursor.moveToNext())
			{
				PanDianDataModel pdData =new PanDianDataModel
				(
				cursor.getString(cursor.getColumnIndex("StorageId")),
				cursor.getString(cursor.getColumnIndex("MatterId")),
				cursor.getString(cursor.getColumnIndex("LabelCount")),
				cursor.getString(cursor.getColumnIndex("RealCount"))
				);
				PandianResult.add(pdData);
				Log.i(TAG, "data added!!:");
			}
			cursor.close();
		}
		finally
		{
			this.closeDatabase();
		}
		return PandianResult;
	}
	//将表中数据上传之后，清空表
	public void ClearPandianData()
	{
		Log.i(TAG, "-----------------------problem------------------");
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		//UPDATE sqlite_sequence SET seq = 0 WHERE name = ‘TableName’
		//清空表数据："DELETE FROM "+PanDianTable
		//清空其自增索引：“UPDATE sqlite_sequence SET seq = 0 WHERE name = ”+'PanDianTable'
		BaseClassDAO.myDb.execSQL("DELETE FROM "+PanDianTable);
		BaseClassDAO.myDb.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '"+PanDianTable+"'");
		this.closeDatabase();
		Log.i(TAG, "table deleted!!!!");
	}
	/*
	盘点数据的四个属性
	private String StorageId;//储位id
	private String MatterId;//物件id
	private String LabelCount;//标签标明的数量
	private String RealCount;//实际数量
	 */
	
	





}