package com.qpguo.uhf.utils;

import org.json.JSONObject;

import com.qpguo.uhf.modelDAO.BaseClassDAO;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
public class CheckInternet
{
	/**
	 * 用于在程序起始登陆和下载数据时检测网络连接并提示
	 * 有网络连接返回true
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
	 * 通过向服务器发送一个http请求，用来检测在与服务器进行数据
	 * 交互之前，是否真的有网络连接
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