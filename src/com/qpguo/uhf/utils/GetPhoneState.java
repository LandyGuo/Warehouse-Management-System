package com.qpguo.uhf.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

/*���ڻ�ȡ�豸��ʶ��*/
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