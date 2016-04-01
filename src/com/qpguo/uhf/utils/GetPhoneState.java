package com.qpguo.uhf.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/*用于获取设备标识码*/
public class GetPhoneState
{
	private Context context;
	
	public GetPhoneState(Context context)
	{
		this.context = context;
	}
	public  String getDeviceId()
	{
		TelephonyManager tm =(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

}