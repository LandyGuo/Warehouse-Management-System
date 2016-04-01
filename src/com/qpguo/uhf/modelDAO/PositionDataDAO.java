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
		//��������֮ǰһ��Ҫ����ձ�
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
						0,//Ĭ�����������󶼲���Ҫ�ش�
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
		//�����в���һ����¼
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
		//��ȡδ�������б�
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
		 * �˷��������ʵ�StorageId��position���л�ȡ��ǰ��λ��������Ϣ
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
		 * �˷���������ִ����һ��PlanData�󣬸���StorageId���Ҹô�λ��
		 * ����������������PlanData�е�InOutCount�޸ĵ�ǰposition���е�
		 * ���ʵ�����
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
					//���´˼�¼��TheCount
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
		 * �˷������ڷ�����ɺ��޸�position���е�IsUpload��IsGiveOut��־λΪ1
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
		 * �˷��������û�������Ϻ���׼���ϴ����ѷ���������
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
		 * �˷��������ϴ��ѷ���������֮ǰ�����TXT�ı���ʽ��
		 * JSONArray
		 * ������ʽ���£�
		 * [{��IsGiveOut��:��1��,��StorageId��:��1��}]
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
		 * �˷������������ϴ�����Ҫ�ķ�����txt�ļ������������·��
		 */
		public  String createTxt()
		{
			FileService fs = new FileService(this.context);
			String fileName =PositonDataTable+".txt" ;
			fs.writeFile(fileName, this.getJSONArray().toString());
			return fs.getFilePath(fileName);
		}
		/**
		 * �˷������ڽ���ǰ�û��Ѿ�ִ����ɵķ��������б��ϴ�
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
		 * �˷��������ڸ���position��ʱ���Ȼ�ȡԭ����IsUpdate=1���������б�
		 * (List<PositionModel>)��Ȼ�����position�����أ����غ��Խ���
		 * List<PositionModel>�е��������IsUpdate�ֶ���Ϊ1,������֤��ÿ�θ���
		 * position��ʱ��δ�ϴ��ķ������ݲ��ᶪʧ
		 */
		public void updateDownloadPosition()
		{
			//��ȡ����ǰ���ѷ����б�
			List<PositionModel> lst = this.getUploadData();
			//�����µ�position����
			this.downLoadPositionData();
			//������������������ϴ���������
			for(PositionModel p:lst)
			{
				updateTheFlag(String.valueOf(p.getPositionCode()));
			}
		}
		
}
/*
 * 
GetList_StoragePosition��
{
"Flag": 1,
"List": [
{
"id": 11,
"PositionCode": 11,
"PositionName": "��ʩ__03��__B��__01��",
"MatterId": "11",
"TheCount": 100,
"IsGiveOut": 1,
"IsCatch": 0,
"IsHasLabel": 1
},
{
"id": 12,
"PositionCode": 12,
"PositionName": "��ʩ__03��__B��__02��",
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
			+"IsUpload integer,"//�����ֻ������ϴ��������ݵı�־λ�������ݽ����ֻ��ͻ���ʹ��
			//��Ҫ�ϴ�
			+ "PositionCode varchar(50)," // λ������
			+ "PositionName varchar(50),"// ���ձ��
			+ "MatterId varchar(50),"// ���ϱ��
			+ "TheCount varchar(50)," //����
			+ "IsGiveOut varchar(50),"// �Ƿ��ѷ���
			+ "IsCatch varchar(50)," //�Ƿ����
			+ "IsHasLabel varchar(50))";  //�Ƿ��б�ǩ;
*/



