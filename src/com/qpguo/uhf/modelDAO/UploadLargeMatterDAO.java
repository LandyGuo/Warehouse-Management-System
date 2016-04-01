package com.qpguo.uhf.modelDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qpguo.uhf.model.LargeMatterModel;
import com.qpguo.uhf.utils.FileService;
import com.qpguo.uhf.utils.UploadUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class UploadLargeMatterDAO extends BaseClassDAO
{
	private String TAG = "UploadLargeMatterDAO";
	private Context context;
	private final String UPLOADMETHODNAME = "UploadLargeMatter";
	private final String UploadLargeMatter= DatabaseOpenHelper.TABLE_UPLOADLARGEMATTERDATA_NAME;

	public UploadLargeMatterDAO(Context context) 
	{
		super(context);
		this.context = context;
	}
	
	/**
	 * �˷��������ϴ��û�ִ�мƻ�������
	 */
	public boolean uploadLargeMatterData()
	{
		// "http://192.168.1.108/Ashx/Info.ashx?MethodName=InOutBat";
		String BaseUrl = "http://"+BaseClassDAO.SERVERHOST+"/Ashx/Info.ashx?";
		String requestUrl = BaseUrl +"MethodName="+this.UPLOADMETHODNAME;
		Log.i(TAG, "upload requesrUrl:"+requestUrl);
		boolean result =false;
		JSONObject ja;
		try {
			       String returnContent = UploadUtil.uploadFile(new File(this.createTxt()), requestUrl);
			       Log.i(TAG, "���ص�����:"+returnContent);
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
	
	/*�����е�ǰ�û�������������txt��ʽ*/
	public  String createTxt()
	{
		FileService fs = new FileService(this.context);
		String fileName =UploadLargeMatter+".txt" ;
		fs.writeFile(fileName, this.getJSONArray().toString());
		return fs.getFilePath(fileName);
	}
	
	/**
	 * �����ϴ����ݿ����һ������
	 */
	public long insertItem(LargeMatterModel model)
	{
			BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("LargeMatterId", model.getLargeMatterId());
			values.put("OpeType", model.getOpeType());
			values.put("MatterId", model.getMatterId());
			values.put("LoginId", model.getLoginId());
			values.put("ExcuteTime", model.getExcuteTime());
			BaseClassDAO.myDb.beginTransaction();
			long InsertStatus = -1;
			try
			{
					InsertStatus = BaseClassDAO.myDb.insert(UploadLargeMatter, null, values);
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
	 * �˷������ڻ�ȡ����������Ҫ�ϴ�������
	 */
	public List<LargeMatterModel> getUploadLargeMatterData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr ="SELECT * FROM "+UploadLargeMatter;
		Cursor cursor =BaseClassDAO.myDb.rawQuery(SQLStr, null);
		List<LargeMatterModel> result = new ArrayList<LargeMatterModel>();
		while(cursor.moveToNext())
		{
			LargeMatterModel updm = new LargeMatterModel
					(cursor.getString(cursor.getColumnIndex("LargeMatterId")),
					  cursor.getString(cursor.getColumnIndex("OpeType")),
					  cursor.getString(cursor.getColumnIndex("MatterId")),
					  cursor.getString(cursor.getColumnIndex("LoginId")),
					  cursor.getString(cursor.getColumnIndex("ExcuteTime")));
			result.add(updm);
		}
		cursor.close();
		this.closeDatabase();
		return result;
	}
	
	/**
	 * JSON��ʽ��
	 * [{"LargeMatterId":"23","OpeType":"���","MatterId":"100","LoginId":"1"
	 * ,"ExcuteTime":"2014-07-01 12:22:26"}]
	 * @throws JSONException 
	 */
	public JSONArray getJSONArray() 
	{
		List<LargeMatterModel> data = this.getUploadLargeMatterData();
		JSONArray ja = new JSONArray();
		try
		{
				for(LargeMatterModel x:data)
				{
					JSONObject jo = new JSONObject();
					jo.put("LargeMatterId", x.getLargeMatterId());
					jo.put("OpeType", x.getOpeType());
					jo.put("MatterId", x.getMatterId());
					jo.put("LoginId", x.getLoginId());
					jo.put("ExcuteTime", x.getExcuteTime());
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
	 * �˷��������ڽ���ǰ�û������ϴ�����յ�ǰ�û���ִ�еļƻ�����
	 * ���������ϴ��ɹ�֮��
	 */
	public void ClearUploadLargeMatterData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ UploadLargeMatter);
		this.closeDatabase();
		Log.i(TAG, "uploadlargeMatterdata table deleted!!!!");
	}




}
/*
private String LargeMatterId;//���������
private String OpeType;//��������
private String MatterId;//����id
private String LoginId;//�����ˣ�����¼��
private String ExcuteTime;//ִ��ʱ��
*/