package com.qpguo.uhf.model;

/**
 * 此模型用于大件基础数据模型和上传的大件信息模型
 * @author guoqingpei
 */
public class LargeMatterModel
{
	private String LargeMatterId;//大件自身编号
	private String OpeType;//操作类型
	private String MatterId;//物资id
	private String LoginId;//操作人，即登录者
	private String ExcuteTime;//执行时间
 
	
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