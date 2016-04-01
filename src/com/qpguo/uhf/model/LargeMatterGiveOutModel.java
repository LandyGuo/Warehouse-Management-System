package com.qpguo.uhf.model;

/*大件发卡的数据库，用于存储大件的发卡数据*/
public class LargeMatterGiveOutModel
{
	private String LargeMatterId;//大件自增编号
	private String MatterId;//大件物资id
	private String IsGiveOut;//是否已发卡
	
	
	public LargeMatterGiveOutModel(String largeMatterId, String matterId,
			String isGiveOut)
	{
		super();
		LargeMatterId = largeMatterId;
		MatterId = matterId;
		IsGiveOut = isGiveOut;
	}
	public String getLargeMatterId()
	{
		return LargeMatterId;
	}
	public void setLargeMatterId(String largeMatterId) 
	{
		LargeMatterId = largeMatterId;
	}
	public String getMatterId() 
	{
		return MatterId;
	}
	public void setMatterId(String matterId)
	{
		MatterId = matterId;
	}
	public String getIsGiveOut()
	{
		return IsGiveOut;
	}
	public void setIsGiveOut(String isGiveOut) 
	{
		IsGiveOut = isGiveOut;
	}
	@Override
	public String toString() 
	{
		return "LargeMatterGiveOutModel [LargeMatterId=" + LargeMatterId
				+ ", MatterId=" + MatterId + ", IsGiveOut=" + IsGiveOut + "]";
	}
	
}