package com.qpguo.uhf.utils;

import com.qpguo.uhf.model.BaseDataModel;
import com.qpguo.uhf.model.PositionModel;
import com.qpguo.uhf.modelDAO.BaseDataDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;

public class ExplainReadInfo
{

	/**
	 * 根据16进制储位信息查询该储位的所有信息
	 * @param readHexStorageId
	 * @return 查询不到该储位返回null
	 */
	public static  PositionModel getHexStorageIdInfo(String readHexStorageId)
	{
		//将16进制数据转化为10进制数据
		int StorageId = NumberConvert.Hex_String2Decimal_int(readHexStorageId);
		//查询position表,获取储位名称
		PositionModel pm = PositionDataDAO.findPositionData(String.valueOf(StorageId));
		return pm;
	}
	
	 /** 根据16进制物资信息查询该物资的所有信息
	 * @param readHexMatterId
	 * @return 查询不到该物资返回null
	 */
	public static BaseDataModel getMatterIdInfo(String readHexMatterId)
	{
		//将16进制数据转化为10进制数据
		int MatterId = NumberConvert.Hex_String2Decimal_int(readHexMatterId);
		//查询basedata表，获取该物资的相关信息
		return BaseDataDAO.findItem(String.valueOf(MatterId));
	}
	/**
	 * 将从标签中读取数量16进制信息转换为10进制表示
	 */
	public static int getNumberInfo(String readHexCount)
	{
		return NumberConvert.Hex_String2Decimal_int(readHexCount);
	}
	





}