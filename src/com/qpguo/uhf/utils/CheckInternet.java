package com.qpguo.uhf.utils;

import org.json.JSONObject;

import com.qpguo.uhf.modelDAO.BaseClassDAO;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
public class CheckInternet
{
	/**
	 * �����ڳ�����ʼ��½����������ʱ����������Ӳ���ʾ
	 * ���������ӷ���true
	 * @param context
	 * @return
	 */
	public  static boolean checkInternet(Context context)
	{
		ConnectivityManager con=(ConnectivityManager)context.getSystemService(Activity.CONNECTIVITY_SERVICE);  
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();  
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		boolean result = false;
		if(wifi|internet)
		{  
			result = true;
		}
		return result;  
	}
	/**
	 * ͨ�������������һ��http����������������������������
	 * ����֮ǰ���Ƿ��������������
	 */
	public static boolean checkLink(Context context)
	{
		HttpApi request = new HttpApi();
		boolean result = false;
		try 
		{
			String content = request.GetRequest(BaseClassDAO.SERVERHOST, "GetList_User");
			JSONObject jo = new JSONObject(content);
			int flag  = jo.getInt("Flag");
			if(flag==1)
			{
				result = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}




}