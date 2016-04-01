package com.qpguo.uhf.utils;

/**
 * ��д��ǩʱ�Ĺ����࣬�������ֵĽ���ת��
 * @author guoqingpei
 *
 */
public class NumberConvert
{
	/**
	 *�˷�������д��ǩʱ����10���Ƶ�����(int��String)ת��Ϊ16���Ƶ�String
	 */
	public static String Decimal_int2Hex_String(Object number)
	{  
		String number1 = String.valueOf(number);
		return Integer.toHexString(Integer.parseInt(number1));
	}
	/**
	 *�˷������ڶ���ǩʱ����16���Ƶ�����(String)ת��Ϊ10���Ƶ�int
	 */
	public static int Hex_String2Decimal_int(String hexStr)
	{
		return Integer.parseInt(hexStr, 16);
	}
	/**
	 * �˷������ڸ���һ��ʮ�������ַ��������λ��0ֱ��ռ��HexWidth*4��λ��
	 */
	public static String hex_StringAutoComplete(String hex_String,int HexWidth)
	{
		//���������ʮ�������ַ�����λ��
		int currentBitWidth = hex_String.length();
		if(currentBitWidth>=HexWidth)
		{
			return hex_String;
		}
		StringBuffer sb =new StringBuffer(hex_String);
		for(int i=0;i<HexWidth-currentBitWidth;i++)
		{
			sb.insert(0, '0');
		}
		return sb.toString();
	}


}