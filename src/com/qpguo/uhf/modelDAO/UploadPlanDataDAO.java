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
	private String user;//�˲����ӿ����ض��û���
	private Context context;
	private final String UPLOADMETHODNAME = "InOutBat";
	public UploadPlanDataDAO(Context context,String user) 
	{
		super(context);
		this.context = context;
		this.user = user;
	}
	
	/**����һ����¼,�û���Ĭ��Ϊ��ǰ��½�û���id*/
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
	/*�����е�ǰ�û�������������txt��ʽ*/
	public  String createTxt()
	{
		FileService fs = new FileService(this.context);
		String fileName =UploadPlanDataTable+".txt" ;
		fs.writeFile(fileName, this.getJSONArray().toString());
		return fs.getFilePath(fileName);
	}
	/**
	 * �˷��������ϴ��û�ִ�мƻ�������
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
			       Log.i(TAG, "���ص�����:"+returnContent);
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
	 * �˷��������ڽ���ǰ�û������ϴ�����յ�ǰ�û���ִ�еļƻ�����
	 * ���������ϴ��ɹ�֮��
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
	 * �˷������ڻ�ȡ���е�ǰ�û�������Ҫ�ϴ�������
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
	 * JSON��ʽ��
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
/*���ݿ��ṹ
+"("
+"InfoId varchar(50),"//�ƻ���Ψһ��ʶ
+"ExcutedTime varchar(50),"//�����ƻ��Ĳ���ִ��ʱ��
+"ExcutedNumber varchar(50),"//�����ƻ��Ĵ˴�ִ������
+"LoginId varchar(50),"//��½�ˣ����ƻ���ִ����
+"StorageId varchar(50))";//�����ƻ�ִ�еĴ�λ
*/
/*����ģ��
private String InfoId;
private String ExcutedTime;
private String ExcutedNumber;
private String LoginId;
private String StorageId;
*/