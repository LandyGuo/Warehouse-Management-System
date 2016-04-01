package com.qpguo.uhf.utils;

import com.qpguo.uhf.model.BaseDataModel;
import com.qpguo.uhf.model.PositionModel;
import com.qpguo.uhf.modelDAO.BaseDataDAO;
import com.qpguo.uhf.modelDAO.PositionDataDAO;

public class ExplainReadInfo
{

	/**
	 * ����16���ƴ�λ��Ϣ��ѯ�ô�λ��������Ϣ
	 * @param readHexStorageId
	 * @return ��ѯ�����ô�λ����null
	 */
	public static  PositionModel getHexStorageIdInfo(String readHexStorageId)
	{
		//��16��������ת��Ϊ10��������
		int StorageId = NumberConvert.Hex_String2Decimal_int(readHexStorageId);
		//��ѯposition��,��ȡ��λ����
		PositionModel pm = PositionDataDAO.findPositionData(String.valueOf(StorageId));
		return pm;
	}
	
	 /** ����16����������Ϣ��ѯ�����ʵ�������Ϣ
	 * @param readHexMatterId
	 * @return ��ѯ���������ʷ���null
	 */
	public static BaseDataModel getMatterIdInfo(String readHexMatterId)
	{
		//��16��������ת��Ϊ10��������
		int MatterId = NumberConvert.Hex_String2Decimal_int(readHexMatterId);
		//��ѯbasedata����ȡ�����ʵ������Ϣ
		return BaseDataDAO.findItem(String.valueOf(MatterId));
	}
	/**
	 * ���ӱ�ǩ�ж�ȡ����16������Ϣת��Ϊ10���Ʊ�ʾ
	 */
	public static int getNumberInfo(String readHexCount)
	{
		return NumberConvert.Hex_String2Decimal_int(readHexCount);
	}
	





}