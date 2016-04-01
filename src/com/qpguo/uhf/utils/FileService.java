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
/**д����
 * ���ݱ�����/data/data/com.qpguo.uhf.application/fileĿ¼��
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
//������  
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
	//��ȡ�ļ��洢�ľ���·��
	public String getFilePath(String fileName)
	{
		return this.context.getFilesDir().getAbsolutePath()+"/"+fileName;
	}
}