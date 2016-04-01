package com.qpguo.uhf.utils;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;

public class DateTime
{
	@SuppressLint("SimpleDateFormat")
	public static String getDateTime()
	{
	SimpleDateFormat sDateFormat =new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");       
	String date= sDateFormat.format(new java.util.Date()); 
	return date;
	}

}