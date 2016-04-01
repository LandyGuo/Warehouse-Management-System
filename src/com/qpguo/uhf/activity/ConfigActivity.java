package com.qpguo.uhf.activity;

import com.example.uhf.application.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class ConfigActivity extends Activity
{
	private String TAG = "ConfigActivity";
	//�����༭��
	private EditText PosMacText;//�ֳֻ�MAC
	private EditText PrinterMacText;//������ӡ��MAC
	private EditText PrintNumber;//Ĭ�ϴ�ӡ����
	//ȷ�ϰ�ť
	private Button saveButton;
	//��ȡ�û���ǰ����ֵ
	private String PosMacStr;
	private String PrinterMacStr;
	private int PrintNumberInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.config);
		this.findAllControls();
		this.setHint();
		this.setListener();
	}
	
	/*��ȡ���пؼ�*/
	protected void findAllControls()
	{
		this.PosMacText = (EditText)this.findViewById(R.id.pos_mac);
		this.PrinterMacText=(EditText)this.findViewById(R.id.print_mac);
		this.PrintNumber = (EditText)this.findViewById(R.id.print_number);
		this.saveButton = (Button)this.findViewById(R.id.saveButton);
	}
	
	protected void setListener()
	{
		this.saveButton.setOnClickListener(new Listener());
	}
	/*ȷ����ť�ļ����¼�*/
	class Listener implements OnClickListener
	{
		public void onClick(View v)
		{
			Button btn = (Button)v;
			switch(btn.getId())
			{
			case R.id.saveButton:
				getText();//��ȡ�û�����
				saveConfiguration();//����
				Toast.makeText(ConfigActivity.this , "����ɹ�!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}
	/*��ʼ������ʱ��ʾ��preference�ж�ȡ��ֵ*/
	protected void setHint()
	{
		SharedPreferences preference = this.getSharedPreferences("Config",MODE_PRIVATE);
		this.PosMacText.setText(preference.getString("device_address","74:F0:7D:A4:89:26"));
		this.PrinterMacText.setText(preference.getString("printer_address", "00:19:5D:23:FA:D9"));
        this.PrintNumber.setText(String.valueOf(preference.getInt("print_number", 2)));
        Log.i(TAG, "��ȡ��Pos��MAC��ַ:"+preference.getString("device_address",""));
        Log.i(TAG, "��ȡ��PrinterMAC��ַ:"+preference.getString("printer_address", ""));
        Log.i(TAG, "��ȡ�Ĵ�ӡ����:"+preference.getInt("print_number", 2));
	}
	
	/*��ȷ�ϰ�ťʱ�����û����뵽SharedPreferences*/
	protected void saveConfiguration()
	{
		SharedPreferences preference = this.getSharedPreferences("Config",MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString("device_address", this.PosMacStr);
		editor.putBoolean("BLUETOOTH_OK_FLAG", true);
		editor.putString("printer_address", this.PrinterMacStr);
		editor.putInt("print_number", this.PrintNumberInt);
		editor.commit();
		Log.i(TAG, "�����Pos����MAC��ַ:"+this.PosMacStr);
		Log.i(TAG, "�����Pos������BLUETOOTH_OK_FLAG:"+true);
		Log.i(TAG, "�����Printer��MAC��ַ:"+ this.PrinterMacStr);
		Log.i(TAG, "����Ĵ�ӡ����:"+this.PrintNumberInt);
	}
	
	/*��ȡ��ǰ�û������ֵ*/
	protected void getText()
	{
		this.PosMacStr = this.PosMacText.getText().toString().replace(" ", "");
		this.PrinterMacStr = this.PrinterMacText.getText().toString().replace(" ", "");
		String tempNumber =   this.PrintNumber.getText().toString().replace(" ", "");
		try
		{
			Integer.parseInt(tempNumber);
		}
		catch(Exception e)
		{
			Toast.makeText(this, "�����������ӡ����!", Toast.LENGTH_LONG).show();
			return;
		}
		this.PrintNumberInt =Integer.parseInt(tempNumber);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
	






}