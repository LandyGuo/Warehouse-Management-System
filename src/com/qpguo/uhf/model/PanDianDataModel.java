package com.qpguo.uhf.model;

public class PanDianDataModel 
{
	/*�̵����ݵ��ĸ�����*/
	private String StorageId;//��λid
	private String MatterId;//���id
	private String LabelCount;//��ǩ����������
	private String RealCount;//ʵ������
	
	public PanDianDataModel(String StorageId,String MatterId,String LabelCount,String RealCount)
	{
		this.StorageId = StorageId;
		this.MatterId = MatterId;
		this.LabelCount = LabelCount;
		this.RealCount = RealCount;
	}
	
	public String getStorageId() 
	{
		return StorageId;
	}
	public void setStorageId(String storageId) 
	{
		StorageId = storageId;
	}
	public String getMatterId() 
	{
		return MatterId;
	}
	public void setMatterId(String matterId)
	{
		MatterId = matterId;
	}
	public String getLabelCount()
	{
		return LabelCount;
	}
	public void setLabelCount(String labelCount)
	{
		LabelCount = labelCount;
	}
	public String getRealCount() 
	{
		return RealCount;
	}
	public void setRealCount(String realCount) 
	{
		RealCount = realCount;
	}	
	public String toString()
	{
		return "StorageId:"+this.StorageId 
					+"RealCount:"+this.RealCount
					+"MatterId:"+this.MatterId
					+ "LabelCount:"+this.LabelCount;
	}	
}