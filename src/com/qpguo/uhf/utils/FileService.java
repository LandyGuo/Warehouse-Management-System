package com.qpguo.uhf.utils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.util.EncodingUtils;

import android.content.Context;


public class FileService
{ 
	private Context context;
	public FileService(Context context)
	{
		super();
		this.context = context;
	}
/**写数据
 * 数据保存在/data/data/com.qpguo.uhf.application/file目录下
 * @param fileName
 * @param writeStr
 * @throws IOException
 */
	public void writeFile(String fileName,String writeStr) 
	{   
		try
		{
	        FileOutputStream fout =context.openFileOutput(fileName, Context.MODE_PRIVATE);   
	        fout.write(writeStr.getBytes());   
	        fout.close();     
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}   
//读数据  
	public String readFile(String fileName) 
	{   
		String res="";   
		try{   
		         FileInputStream fin =context.openFileInput(fileName);   
		         int length = fin.available();   
		         byte [] buffer = new byte[length];   
		         fin.read(buffer);       
		         res = EncodingUtils.getString(buffer, "UTF-8");   
		         fin.close();       
	     	  }   
	     catch(IOException e)
	     {   
	         e.printStackTrace();   
	     }   
	     return res;   
	}   
	//获取文件存储的绝对路径
	public String getFilePath(String fileName)
	{
		return this.context.getFilesDir().getAbsolutePath()+"/"+fileName;
	}
}