package com.qpguo.uhf.model;

public class PlanDataModel
{
	//plandata表项的属性
	private int InfoId;//计划的唯一标识
	private int IsExcuted;//用于客户端本地显示时判断
	private String MatterId;
	private int InOutCount;
	private String MatterName;
	private String BillId;
	private int IsHasLabel;//是否有标签
	private String LoginId;//执行人
	private int NotExcutedNumbers;//计划未执行数
	private String AvailableStorageIdList;//可用储位Id列表"22;23;24;25"
	private String LabelInfo;//可用储位是否有标签的信息
	private String Workshop;//发料车间
	private String GetPlanTime;//计划下达时间
	private String GiveOutPerson;//发料人
	private String GetPerson;//收料人
	private int IsCatch;//是否跟踪
	

	public PlanDataModel(int infoId, int isExcuted, String matterId,
			int inOutCount, String matterName, String billId, int isHasLabel,
			String loginId, int notExcutedNumbers,
			String availableStorageIdList, String labelInfo, String workshop,
			String getPlanTime, String giveOutPerson, String getPerson,
			int isCatch) {
		super();
		InfoId = infoId;
		IsExcuted = isExcuted;
		MatterId = matterId;
		InOutCount = inOutCount;
		MatterName = matterName;
		BillId = billId;
		IsHasLabel = isHasLabel;
		LoginId = loginId;
		NotExcutedNumbers = notExcutedNumbers;
		AvailableStorageIdList = availableStorageIdList;
		LabelInfo = labelInfo;
		Workshop = workshop;
		GetPlanTime = getPlanTime;
		GiveOutPerson = giveOutPerson;
		GetPerson = getPerson;
		IsCatch = isCatch;
	}
	public int getInfoId() 
	{
		return InfoId;
	}
	public void setInfoId(int infoId) 
	{
		InfoId = infoId;
	}
	public String getMatterId()
	{
		return MatterId;
	}
	public void setMatterId(String matterId)
	{
		MatterId = matterId;
	}
	public int getInOutCount() 
	{
		return InOutCount;
	}
	public void setInOutCount(int inOutCount) 
	{
		InOutCount = inOutCount;
	}
	public String getMatterName()
	{
		return MatterName;
	}
	public void setMatterName(String matterName)
	{
		MatterName = matterName;
	}
	public String getBillId()
	{
		return BillId;
	}
	public void setBillId(String billId) 
	{
		BillId = billId;
	}
	public int getIsHasLabel()
	{
		return IsHasLabel;
	}
	public void setIsHasLabel(int isHasLabel)
	{
		IsHasLabel = isHasLabel;
	}
	public String getLoginId() 
	{
		return LoginId;
	}
	public void setLoginId(String loginId)
	{
		LoginId = loginId;
	}
	public String getAvailableStorageIdList() 
	{
		return AvailableStorageIdList;
	}
	public void setAvailableStorageIdList(String availableStorageIdList) 
	{
		AvailableStorageIdList = availableStorageIdList;
	}
	public String getWorkshop() 
	{
		return Workshop;
	}
	public void setWorkshop(String workshop) 
	{
		Workshop = workshop;
	}
	public String getGetPlanTime()
	{
		return GetPlanTime;
	}
	public void setGetPlanTime(String getPlanTime) 
	{
		GetPlanTime = getPlanTime;
	}
	public String getGiveOutPerson()
	{
		return GiveOutPerson;
	}
	public void setGiveOutPerson(String giveOutPerson)
	{
		GiveOutPerson = giveOutPerson;
	}
	public String getGetPerson() 
	{
		return GetPerson;
	}
	public void setGetPerson(String getPerson)
	{
		GetPerson = getPerson;
	}
	public int getIsCatch() 
	{
		return IsCatch;
	}
	public void setIsCatch(int isCatch) 
	{
		IsCatch = isCatch;
	}

	public int getIsExcuted() 
	{
		return IsExcuted;
	}
	public void setIsExcuted(int isExcuted) 
	{
		IsExcuted = isExcuted;
	}

	public int getNotExcutedNumbers()
	{
		return NotExcutedNumbers;
	}
	public void setNotExcutedNumbers(int notExcutedNumbers) 
	{
		NotExcutedNumbers = notExcutedNumbers;
	}
	public String getLabelInfo() {
		return LabelInfo;
	}
	public void setLabelInfo(String labelInfo) {
		LabelInfo = labelInfo;
	}
	@Override
	public String toString() {
		return "PlanDataModel [InfoId=" + InfoId + ", IsExcuted=" + IsExcuted
				+ ", MatterId=" + MatterId + ", InOutCount=" + InOutCount
				+ ", MatterName=" + MatterName + ", BillId=" + BillId
				+ ", IsHasLabel=" + IsHasLabel + ", LoginId=" + LoginId
				+ ", NotExcutedNumbers=" + NotExcutedNumbers
				+ ", AvailableStorageIdList=" + AvailableStorageIdList
				+ ", LabelInfo=" + LabelInfo + ", Workshop=" + Workshop
				+ ", GetPlanTime=" + GetPlanTime + ", GiveOutPerson="
				+ GiveOutPerson + ", GetPerson=" + GetPerson + ", IsCatch="
				+ IsCatch + "]";
	}
	
	


	
	
}


/*
 * 数据模型字段与表中字段相对应
 *
+ "id integer primary key autoincrement,"
+ "InfoId varchar(50),"
+ "StorageId varchar(50),"
+ "MatterId varchar(50),"
+ "InOutCount varchar(50),"
+ "StorageName varchar(50),"
+ "MatterName varchar(50),"
+ "BillId varchar(50),"
+ "IsExcute varchar(50),"
+ "IsHasLabel varchar(50),"
+ "LoginId varchar(50),"
+ "ExcuteTime varchar(50),"
+ "IsCatch varchar(50))";
*/