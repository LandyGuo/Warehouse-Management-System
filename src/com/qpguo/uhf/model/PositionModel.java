package com.qpguo.uhf.model;

public class PositionModel
{
	private int id;
	private int IsUpload = 0;//默认不需要上传
	private int PositionCode; // 存储位置编号
	private String PositionName; // 存储位置名称
	private String MatterId;
	private int TheCount;
	/**
	 * 是否已发卡
	 */
	private int IsGiveOut;
	/**
	 * 是否跟踪
	 */
	private int IsCatch;
	/**
	 * 是否有标签
	 */
	private int IsHasLabel;
	

	public PositionModel(int id, int isUpload, int positionCode,
			String positionName, String matterId, int theCount, int isGiveOut,
			int isCatch, int isHasLabel) 
	{
		super();
		this.id = id;
		IsUpload = isUpload;
		PositionCode = positionCode;
		PositionName = positionName;
		MatterId = matterId;
		TheCount = theCount;
		IsGiveOut = isGiveOut;
		IsCatch = isCatch;
		IsHasLabel = isHasLabel;
	}
	public int getId() 
	{
		return id;
	}
	public void setId(int id) 
	{
		this.id = id;
	}
	public int getPositionCode() 
	{
		return PositionCode;
	}
	public void setPositionCode(int positionCode) 
	{
		PositionCode = positionCode;
	}
	public String getPositionName() 
	{
		return PositionName;
	}
	public void setPositionName(String positionName) 
	{
		PositionName = positionName;
	}
	public String getMatterId() 
	{
		return MatterId;
	}
	public void setMatterId(String matterId)
	{
		MatterId = matterId;
	}
	public int getTheCount()
	{
		return TheCount;
	}
	public void setTheCount(int theCount) 
	{
		TheCount = theCount;
	}
	public int getIsGiveOut() 
	{
		return IsGiveOut;
	}
	public void setIsGiveOut(int isGiveOut) 
	{
		IsGiveOut = isGiveOut;
	}
	public int getIsCatch() 
	{
		return IsCatch;
	}
	public void setIsCatch(int isCatch) 
	{
		IsCatch = isCatch;
	}
	public int getIsHasLabel() 
	{
		return IsHasLabel;
	}
	public void setIsHasLabel(int isHasLabel) 
	{
		IsHasLabel = isHasLabel;
	}
	
	public String toString()
	{
		return 
				"id:"+id
				+"IsUpdate:"+IsUpload//是否需要回传
				+ "PositionCode:"+PositionCode // 位置名称
				+ "PositionName:"+PositionName// 对照编号
				+ "MatterId:"+MatterId// 物料编号
				+ "TheCount:"+TheCount //数量
				+ "IsGiveOut:"+IsGiveOut// 是否已发卡
				+ "IsCatch:" +IsCatch//是否跟踪
				+ "IsHasLabel:"+IsHasLabel;  //是否有标签;
	}
	public int getIsUpload() {
		return IsUpload;
	}
	public void setIsUpload(int isUpload) {
		IsUpload = isUpload;
	}

	
	
	
}
/*
			+ "id integer,"
			+ "PositionCode varchar(50)," // 位置名称
			+ "PositionName varchar(50),"// 对照编号
			+ "MatterId varchar(50),"// 物料编号
			+ "TheCount varchar(50)," //数量
			+ "IsGiveOut varchar(50),"// 是否已发卡
			+ "IsCatch varchar(50)," //是否跟踪
			+ "IsHasLabel varchar(50))";  //是否有标签;
*/