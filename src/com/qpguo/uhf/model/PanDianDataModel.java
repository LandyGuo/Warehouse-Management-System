package com.qpguo.uhf.model;

public class PanDianDataModel 
{
	/*盘点数据的四个属性*/
	private String StorageId;//储位id
	private String MatterId;//物件id
	private String LabelCount;//标签标明的数量
	private String RealCount;//实际数量
	
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