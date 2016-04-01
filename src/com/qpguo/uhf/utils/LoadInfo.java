package com.qpguo.uhf.utils;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * ������Ҫ����ʵʱ��ȡ��ǰ��µ��û���¼��Ϣ
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