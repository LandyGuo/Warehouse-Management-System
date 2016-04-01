package com.qpguo.uhf.modelDAO;
import org.json.JSONArray;
import org.json.JSONObject;

import com.qpguo.uhf.model.BaseDataModel;



import com.qpguo.uhf.utils.HttpApi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class BaseDataDAO extends BaseClassDAO
{
	//test
	private static final String TAG = "BaseDataDAO";
	//�洢basedata�ı���
	public static final String BASEDATA_TABLE = DatabaseOpenHelper.TABLE_BASEDATA_NAME;
	private final String METHODNAME = "GetList_Matter";
	public BaseDataDAO(Context context)
	{
		super(context);
	}
	//ͨ��http�����ȡbasedata���ݣ��洢��basedata����
	//��������֮ǰһ��Ҫ�����basedata��
	public boolean downLoadBaseData()
	{
		boolean result =false;
		this.ClearBaseData();
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
					BaseDataModel model = new BaseDataModel
					(
					temp.getInt("id"),//id
					temp.getString("���ʷ���"),//classes
					temp.getString("���ʱ��"),//pid
					temp.getString("��������"),//name
					temp.getString("����ͺ�"),//type
					temp.getString("������λ"),//unit
					String.valueOf(temp.getDouble("Ŀ¼��")),//price
					temp.getString("ͼƬ"),//img
					temp.getString("��;"),//use
					temp.getString("��ע"),//mark
					temp.getString("ͼ��")//piccode
					);
					this.insertBaseData(model);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return result;
		
	}
	//�����в���һ����¼
	public long  insertBaseData(BaseDataModel baseData)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("id", baseData.getId());
		values.put("class", baseData.getClasses());
		values.put("pid", baseData.getPid());
		values.put("name", baseData.getName());
		values.put("type", baseData.getType());
		values.put("unit", baseData.getUnit());
		values.put("price", baseData.getPrice());
		values.put("img", baseData.getImg());
		values.put("use", baseData.getUse());
		values.put("mark", baseData.getMark());
		values.put("piccode", baseData.getPiccode());
		BaseClassDAO.myDb.beginTransaction();
		long InsertStatus = -1;
		try
		{
				InsertStatus = BaseClassDAO.myDb.insert(BASEDATA_TABLE, null, values);
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
	
	public static BaseDataModel findItem(String id)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr = "SELECT * FROM "+BASEDATA_TABLE+" WHERE id = "
		+"'"+id+"'";
		Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
		while(cursor.moveToNext())
		{
			return new BaseDataModel
			(
			cursor.getInt(cursor.getColumnIndex("id")),
			cursor.getString(cursor.getColumnIndex("class")),
			cursor.getString(cursor.getColumnIndex("pid")),
			cursor.getString(cursor.getColumnIndex("name")),
			cursor.getString(cursor.getColumnIndex("type")),
			cursor.getString(cursor.getColumnIndex("unit")),
			cursor.getString(cursor.getColumnIndex("price")),
			cursor.getString(cursor.getColumnIndex("img")),
			cursor.getString(cursor.getColumnIndex("use")),
			cursor.getString(cursor.getColumnIndex("mark")),
			cursor.getString(cursor.getColumnIndex("piccode")));
		}
		cursor.close();
		BaseClassDAO.myDb.close();
		return null;
	}
	
	//��ձ������ݣ�����ÿ�����ر�֮ǰ��գ�������ظ�������ͬ�ļ�¼
	public void ClearBaseData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ BASEDATA_TABLE);
		this.closeDatabase();
		Log.i(TAG, "table deleted!!!!");
	}
/*
 * {
"Flag": 1,
"List": [
{
"id": 1,
"���ʷ���": "17",
"���ʱ��": "275190100042",
"��������": "̧�쿨",
"����ͺ�": "1",
"������λ": "��",
"Ŀ¼��": 1,
"ͼƬ": "",
"��;": "",
"��ע": "",
"ͼ��": "XXXX"
},
{
"id": 2,
"���ʷ���": "17",
"���ʱ��": "0001",
"��������": "ץ��",
"����ͺ�": "1",
"������λ": "��",
"Ŀ¼��": 1,
"ͼƬ": "",
"��;": "",
"��ע": "",
"ͼ��": ""
}
]
}
 */
/*
 * 	private Integer id;
	private String classes;
	private String pid;
	private String name;
	private String type;
	private String unit;
	private String price;
	private String img;
	private String use;
	private String mark;
	private String piccode;
 */







}