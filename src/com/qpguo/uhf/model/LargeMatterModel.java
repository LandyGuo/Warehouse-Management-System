package com.qpguo.uhf.model;

/**
 * ��ģ�����ڴ����������ģ�ͺ��ϴ��Ĵ����Ϣģ��
 * @author guoqingpei
 */
public class LargeMatterModel
{
	private String LargeMatterId;//���������
	private String OpeType;//��������
	private String MatterId;//����id
	private String LoginId;//�����ˣ�����¼��
	private String ExcuteTime;//ִ��ʱ��
 
	
	public LargeMatterModel(String largeMatterId, String opeType,
			String matterId, String loginId, String excuteTime)
	{
		super();
		LargeMatterId = largeMatterId;
		OpeType = opeType;
		MatterId = matterId;
		LoginId = loginId;
		ExcuteTime = excuteTime;
	}
	
	public String getLargeMatterId() 
	{
		return LargeMatterId;
	}
	public void setLargeMatterId(String largeMatterId)
	{
		LargeMatterId = largeMatterId;
	}
	public String getOpeType()
	{
		return OpeType;
	}
	public void setOpeType(String opeType)
	{
		OpeType = opeType;
	}
	public String getMatterId() 
	{
		return MatterId;
	}
	public void setMatterId(String matterId)
	{
		MatterId = matterId;
	}
	public String getLoginId() 
	{
		return LoginId;
	}
	public void setLoginId(String loginId) 
	{
		LoginId = loginId;
	}
	public String getExcuteTime() 
	{
		return ExcuteTime;
	}
	public void setExcuteTime(String excuteTime) 
	{
		ExcuteTime = excuteTime;
	}

	@Override
	public String toString()
	{
		return "LargeMatterModel [LargeMatterId=" + LargeMatterId
				+ ", OpeType=" + OpeType + ", MatterId=" + MatterId
				+ ", LoginId=" + LoginId + ", ExcuteTime=" + ExcuteTime + "]";
	}
	

}
/*
			+ "LargeMatterId varchar(50),"
			+ "OpeType varchar(50),"
			+ "MatterId varchar(50),"
			+ "LoginId varchar(50),"
			+ "ExcuteTime varchar(50),"
			+ "ExtendInfo varchar(100)," 
*/