package com.qpguo.uhf.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpApi
{
	//��Get��ʽ����
	private static String TAG = "HttpApi";
	public String GetRequest(String Serverhost,String MethodName)
	{
		/*���ӣ�http://192.168.1.157/Ashx/Info.ashx?MethodName=GetList_User
		 * BaseUrl = http://192.168.1.112/Ashx/Info.ashx
		 * Serverhost =192.168.1.112
		 * MethodName = "GetList_User"
		 */
		String resultResponse="";
		//Http�ͻ���
		HttpClient client = new DefaultHttpClient();
		//����GET��ʽ�����URL��ע��GET����������URL��
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("MethodName", MethodName));
		//�Բ�������URL����
		String paramStr = URLEncodedUtils.format(params, "UTF-8");
		Log.i(TAG,"Get  paramStr:"+paramStr);
		String BaseUrl = "http://"+Serverhost+"/Ashx/Info.ashx";
		Log.i(TAG, "Get  BaseUrl:"+BaseUrl);
		HttpGet httpGet = new HttpGet(BaseUrl+"?"+paramStr);
		try
		{
			HttpResponse response = client.execute(httpGet);
			if(response.getStatusLine().getStatusCode()==200)
			{
				Log.i(TAG, "--------------http success-----------");
				resultResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (ClientProtocolException e) 
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return resultResponse;
	}
	
	
	//�����ϴ�����
	public String PostRequest(String Serverhost,String MethodName,BasicNameValuePair param)
	{
		String resultResponse ="";
		HttpClient client = new DefaultHttpClient();
		List<BasicNameValuePair>params = new ArrayList<BasicNameValuePair>();
		params.add(param);
		params.add(new BasicNameValuePair("MethodName", MethodName));
		String BaseUrl = "http://"+Serverhost+"/Ashx/Info.ashx";
		HttpPost httpPost = new HttpPost(BaseUrl);
		try
		{
			httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
			HttpResponse response = client.execute(httpPost);
			if(response.getStatusLine().getStatusCode()==200)
			{
				resultResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return resultResponse;	
	}
}
