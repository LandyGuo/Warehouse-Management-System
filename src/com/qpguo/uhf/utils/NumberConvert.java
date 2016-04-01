package com.qpguo.uhf.utils;

/**
 * 读写标签时的工具类，用于数字的进制转换
 * @author guoqingpei
 *
 */
public class NumberConvert
{
	/**
	 *此方法用于写标签时，把10进制的数字(int或String)转换为16进制的String
	 */
	public static String Decimal_int2Hex_String(Object number)
	{  
		String number1 = String.valueOf(number);
		return Integer.toHexString(Integer.parseInt(number1));
	}
	/**
	 *此方法用于读标签时，把16进制的数字(String)转换为10进制的int
	 */
	public static int Hex_String2Decimal_int(String hexStr)
	{
		return Integer.parseInt(hexStr, 16);
	}
	/**
	 * 此方法用于给定一个十六进制字符串将其高位补0直到占有HexWidth*4的位宽
	 */
	public static String hex_StringAutoComplete(String hex_String,int HexWidth)
	{
		//计算给定的十六进制字符串的位数
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