package com.qpguo.uhf.modelDAO;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public class BaseClassDAO
{
	public static DatabaseOpenHelper helper;
	public static SQLiteDatabase myDb;
	//下载数据的服务器IP地址,在程序中应由用户输入
	public static String SERVERHOST = "192.168.1.101"; 
	//存储的txt文件名
	public static final String  TXT_BASEDATA_NAME = "basedata.txt";
	public static final String  TXT_USERDATA_NAME = "userdata.txt";
	public static final String  TXT_PANDIANDATA_NAME = "pandiandata.txt";
	public static final String  TXT_PLANDATA_NAME = "plandata.txt";
	public static final String  TXT_POSITIONDATA_NAME = "position.txt";
	public static final String  TXT_LARGEMATTERDATA_NAME = "LargeMatter.txt";
	public BaseClassDAO(Context context)
	{
		helper = new DatabaseOpenHelper(context);
	}
	public void closeDatabase()
	{
		if(myDb.isOpen())
		{
			myDb.close();
		}
	}

}