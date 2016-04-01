package com.qpguo.uhf.model;

public class PositionModel
{
	private int id;
	private int IsUpload = 0;//Ĭ�ϲ���Ҫ�ϴ�
	private int PositionCode; // �洢λ�ñ��
	private String PositionName; // �洢λ������
	private String MatterId;
	private int TheCount;
	/**
	 * �Ƿ��ѷ���
	 */
	private int IsGiveOut;
	/**
	 * �Ƿ����
	 */
	private int IsCatch;
	/**
	 * �Ƿ��б�ǩ
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
				+"IsUpdate:"+IsUpload//�Ƿ���Ҫ�ش�
				+ "PositionCode:"+PositionCode // λ������
				+ "PositionName:"+PositionName// ���ձ��
				+ "MatterId:"+MatterId// ���ϱ��
				+ "TheCount:"+TheCount //����
				+ "IsGiveOut:"+IsGiveOut// �Ƿ��ѷ���
				+ "IsCatch:" +IsCatch//�Ƿ����
				+ "IsHasLabel:"+IsHasLabel;  //�Ƿ��б�ǩ;
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
			+ "PositionCode varchar(50)," // λ������
			+ "PositionName varchar(50),"// ���ձ��
			+ "MatterId varchar(50),"// ���ϱ��
			+ "TheCount varchar(50)," //����
			+ "IsGiveOut varchar(50),"// �Ƿ��ѷ���
			+ "IsCatch varchar(50)," //�Ƿ����
			+ "IsHasLabel varchar(50))";  //�Ƿ��б�ǩ;
*/