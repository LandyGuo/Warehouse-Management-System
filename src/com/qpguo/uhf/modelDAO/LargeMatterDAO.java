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

/*������ݿ�����࣬�������ڲ����洢�����Ϣ�����ݿ�*/
/*�����ݿ���Ϊ���������Ϣ�⣬����ʱ���������Ĵ����������(����)
 * ���������ֻ���ڲ�ѯ�������û�����һ������������Ϣ(������Ϣʱ��
 * URL�����б������ص��������)*/
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
	 * �˷����������ش��������Ϣ�����浽���ݿ�
	 * ���������Ϣһ����������ɾ��
	 */
	public boolean downLoadLargeMatterData()
	{
		boolean result = false;
		HttpApi request = new HttpApi();
		try 
		{
			//TODO:�޸������From
			String content = request.PostRequest(BaseClassDAO.SERVERHOST, LargeMatterDAO.METHODNAME,
					new BasicNameValuePair("From",String.valueOf(getTotalItemNumber())));
			Log.i(TAG, "From:"+String.valueOf(getTotalItemNumber()));
			Log.i(TAG, "���ص�����:"+content);
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
	
	
	//�����в���һ����¼
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
	
	/*��ȡ����������е��ܼ�¼��*/
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
	 * �˷������ڸ���LargeMatterId��ȡ��LargeMatterId�����в�����¼
	 * ����ʱ�����򷵻�
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
	 * ���ã����������ڲ�������ʱ��ձ���ʽ������Ҫ���ô˷�������*/
	public void ClearLargeMatterData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ LargeMatterDataTable);
		BaseClassDAO.myDb.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '"+LargeMatterDataTable+"'");
		this.closeDatabase();
		Log.i(TAG, "Important ERROR:LargeMatterBaseData table deleted!!!!");
	}
	



}
/*����ģ��
 * 	private String LargeMatterId;//���������
	private String OpeType;//��������
	private String MatterId;//����id
	private String LoginId;//�����ˣ�����¼��
	private String ExcuteTime;//ִ��ʱ��
 */
/*���ݿ��ֶ�
 * 			+ "LargeMatterId varchar(50),"//���������
			+ "OpeType varchar(50),"//��������
			+ "MatterId varchar(50),"//����id����������������
			+ "LoginId varchar(50),"//�����ߣ�����¼��
			+ "ExcuteTime varchar(50))";//ִ��ʱ��		
 */