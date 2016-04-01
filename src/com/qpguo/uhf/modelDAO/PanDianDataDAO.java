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
	//�洢�̵����ݵı���
	private final String PanDianTable = DatabaseOpenHelper.TABLE_PANDIANDATA_NAME;
	private Context context;
	private final String METHODNAME = "CheckInfo";
	public PanDianDataDAO(Context context)
	{
		super(context);
		this.context = context;
	}
	//�̵�������ȫ���û�������ϴ����û�����������ݿ��У��ϴ�ʱ
	//��Ҫ����.txt�ļ��ϴ�
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
	 * ����txt�ļ�����ʽ���£�
	[{"StorageId":"1","MatterId":"111","LabelCount":"10","RealCount":"10"},
	{"StorageId":"2","MatterId":"222","LabelCount":"10","RealCount":"5"}]
	   ����ֵ�������ļ���·����
	*/
	public String createTxt()
	{
		FileService fs = new FileService(this.context);
		String fileName = PanDianTable+".txt";
		fs.writeFile(fileName, this.getJSONArray().toString());
		return fs.getFilePath(fileName);
	}
	
	/**
	 * �˷������ڽ��Ѿ��̵�������ϴ�,�ϴ��ɹ�����true
	 * ���򷵻�false
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
			        Log.i(TAG, "�̵��ϴ����ص�����:"+returnContent);
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
	�˷�����Ҫʵ�ֽ����е�����ȫ��ת��ΪJSONArray��ʽ
	*/
	public JSONArray getJSONArray()
	{ 
		JSONArray ja = new JSONArray();
		//��ȡ��������List����
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
	//��List����ʽ�����������е����ݣ����ڽ���������д��txt�ļ�ʱ����
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
	//�����������ϴ�֮����ձ�
	public void ClearPandianData()
	{
		Log.i(TAG, "-----------------------problem------------------");
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		//UPDATE sqlite_sequence SET seq = 0 WHERE name = ��TableName��
		//��ձ����ݣ�"DELETE FROM "+PanDianTable
		//�����������������UPDATE sqlite_sequence SET seq = 0 WHERE name = ��+'PanDianTable'
		BaseClassDAO.myDb.execSQL("DELETE FROM "+PanDianTable);
		BaseClassDAO.myDb.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '"+PanDianTable+"'");
		this.closeDatabase();
		Log.i(TAG, "table deleted!!!!");
	}
	/*
	�̵����ݵ��ĸ�����
	private String StorageId;//��λid
	private String MatterId;//���id
	private String LabelCount;//��ǩ����������
	private String RealCount;//ʵ������
	 */
	
	





}