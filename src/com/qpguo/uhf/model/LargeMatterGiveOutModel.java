package com.qpguo.uhf.model;

/*������������ݿ⣬���ڴ洢����ķ�������*/
public class LargeMatterGiveOutModel
{
	private String LargeMatterId;//����������
	private String MatterId;//�������id
	private String IsGiveOut;//�Ƿ��ѷ���
	
	
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