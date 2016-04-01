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
	//三个编辑框
	private EditText PosMacText;//手持机MAC
	private EditText PrinterMacText;//蓝牙打印机MAC
	private EditText PrintNumber;//默认打印份数
	//确认按钮
	private Button saveButton;
	//获取用户当前输入值
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
	
	/*获取所有控件*/
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
	/*确定按钮的监听事件*/
	class Listener implements OnClickListener
	{
		public void onClick(View v)
		{
			Button btn = (Button)v;
			switch(btn.getId())
			{
			case R.id.saveButton:
				getText();//获取用户输入
				saveConfiguration();//保存
				Toast.makeText(ConfigActivity.this , "保存成功!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	}
	/*初始化界面时显示从preference中读取的值*/
	protected void setHint()
	{
		SharedPreferences preference = this.getSharedPreferences("Config",MODE_PRIVATE);
		this.PosMacText.setText(preference.getString("device_address","74:F0:7D:A4:89:26"));
		this.PrinterMacText.setText(preference.getString("printer_address", "00:19:5D:23:FA:D9"));
        this.PrintNumber.setText(String.valueOf(preference.getInt("print_number", 2)));
        Log.i(TAG, "读取的Pos机MAC地址:"+preference.getString("device_address",""));
        Log.i(TAG, "读取的PrinterMAC地址:"+preference.getString("printer_address", ""));
        Log.i(TAG, "读取的打印份数:"+preference.getInt("print_number", 2));
	}
	
	/*点确认按钮时保存用户输入到SharedPreferences*/
	protected void saveConfiguration()
	{
		SharedPreferences preference = this.getSharedPreferences("Config",MODE_PRIVATE);
		SharedPreferences.Editor editor = preference.edit();
		editor.putString("device_address", this.PosMacStr);
		editor.putBoolean("BLUETOOTH_OK_FLAG", true);
		editor.putString("printer_address", this.PrinterMacStr);
		editor.putInt("print_number", this.PrintNumberInt);
		editor.commit();
		Log.i(TAG, "保存的Pos机的MAC地址:"+this.PosMacStr);
		Log.i(TAG, "保存的Pos机连接BLUETOOTH_OK_FLAG:"+true);
		Log.i(TAG, "保存的Printer的MAC地址:"+ this.PrinterMacStr);
		Log.i(TAG, "保存的打印份数:"+this.PrintNumberInt);
	}
	
	/*获取当前用户输入的值*/
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
			Toast.makeText(this, "请重新输入打印份数!", Toast.LENGTH_LONG).show();
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