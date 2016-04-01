package com.qpguo.uhf.utils;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * 此类主要用于实时获取当前活动下的用户登录信息
 */
public class LoadInfo
{
	private Context context;
	
	public LoadInfo(Context context)
	{
		this.context = context;
	}
	
	public  String getUser()
	{
		SharedPreferences preference = context.getSharedPreferences("configuration", Context.MODE_PRIVATE);
		String user = preference.getString("user","");
		return user;
	}



}