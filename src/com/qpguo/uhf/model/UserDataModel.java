package com.qpguo.uhf.model;

public class UserDataModel
{
	private String LoginID;
	private String Password;
	
	public UserDataModel(String LoginID,String Password)
	{
		this.LoginID=LoginID;
		this.Password=Password;
	}
	
	public String getLoginID() 
	{
		return LoginID;
	}
	public void setLoginID(String loginID) 
	{
		LoginID = loginID;
	}
	public String getPassword()
	{
		return Password;
	}
	public void setPassword(String password) 
	{
		Password = password;
	}
	public String toString()
	{
		return "LoginID:"+this.LoginID+"Password:"+this.Password;
	}

}
/*
			+ "id integer primary key autoincrement," 
			+ "LoginID varchar(50)," // ”√ªß√˚
			+ "Password varchar(50))"; // √‹¬Î
*/