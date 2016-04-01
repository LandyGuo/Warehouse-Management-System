package com.qpguo.uhf.modelDAO;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qpguo.uhf.model.UserDataModel;
import com.qpguo.uhf.utils.HttpApi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class UserDataDAO extends BaseClassDAO
{
	private final String TAG = "UserDataDAO";
	private  final String UserDataTable = DatabaseOpenHelper.TABLE_USERDATA_NAME;
	private  final String METHODNAME = "GetList_User";
	public UserDataDAO(Context context)
	{
		super(context);
	}
	//通过http请求获取basedata数据，存储在basedata表中
	//下载数据之前一定要先清空basedata表
	public boolean downUserData()
	{
		boolean result=false;
		this.ClearUserData();
		HttpApi request = new HttpApi();
		try 
		{
			String content = request.GetRequest(BaseClassDAO.SERVERHOST, this.METHODNAME);
			Log.i(TAG, "JSON:USERSTRING："+content);
			JSONObject jo = new JSONObject(content);
			int flag  = jo.getInt("Flag");
			if(flag==1)
			{
				result = true;
				JSONArray ja = jo.getJSONArray("List");
				for(int i=0;i<ja.length();i++)
				{
					JSONObject temp = ja.getJSONObject(i);
					UserDataModel model = new UserDataModel
					(
					temp.getString("LoginID"),//LoginID
					temp.getString("Password")//Password
					);
					Log.i(TAG, temp.getString("LoginID"));
					Log.i(TAG, temp.getString("Password"));
					this.insertUserData(model);
				}
			}
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		return result;
	}
//插入一条UerData数据
	public long  insertUserData(UserDataModel userData)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("LoginID", userData.getLoginID());
		values.put("Password", userData.getPassword());
		BaseClassDAO.myDb.beginTransaction();
		long InsertStatus = -1;
		try
		{
				InsertStatus = BaseClassDAO.myDb.insert(UserDataTable, null, values);
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
		

//清空表中数据，用于每次下载表之前清空，否则会重复插入相同的记录
	public void ClearUserData()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		BaseClassDAO.myDb.execSQL("DELETE FROM "+ UserDataTable);
		BaseClassDAO.myDb.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '"+UserDataTable+"'");
		this.closeDatabase();
		Log.i(TAG, "table deleted!!!!");
	}
	//获取所有用户列表
	public List<UserDataModel> getUserList()
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr = "SELECT LoginID,Password  FROM "+this.UserDataTable;
		Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr,null);
		List<UserDataModel> result = new ArrayList<UserDataModel>();
		while(cursor.moveToNext())
		{
			result.add(
					new UserDataModel(
							cursor.getString(cursor.getColumnIndex("LoginID")),
							cursor.getString(cursor.getColumnIndex("Password"))));
		}
		this.closeDatabase();
		return  result;
	}
	public UserDataModel getUserByLoginId(String LoginId)
	{
		BaseClassDAO.myDb = BaseClassDAO.helper.getWritableDatabase();
		String SQLStr = "SELECT LoginID,Password  FROM "+this.UserDataTable
				+" WHERE LoginId="+"'"+LoginId+"'";
		Cursor cursor = BaseClassDAO.myDb.rawQuery(SQLStr, null);
		UserDataModel user = null;
		while(cursor.moveToNext())
		{
			user  = new UserDataModel
			(cursor.getString(cursor.getColumnIndex("LoginID")),
			cursor.getString(cursor.getColumnIndex("Password")));			
		}
		cursor.close();
		this.closeDatabase();
		return user;
	}








}
/*
 * GetList_User:
{
"Flag": 1,
"List": [
{
"id": "1",
"LoginID": "1",
"Password": "jmqgd123"
},
{
"id": "11",
"LoginID": "liurenxiu",
"Password": "lrx123"
},
{
"id": "12",
"LoginID": "2",
"Password": "2"
}
]
}
 */
/*
			+ "id integer primary key autoincrement," 
			+ "LoginID varchar(50)," // 用户名
			+ "Password varchar(50))"; // 密码
*/

