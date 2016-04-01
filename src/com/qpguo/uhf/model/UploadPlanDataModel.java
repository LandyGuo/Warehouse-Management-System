package com.qpguo.uhf.model;
/**
 *分条执行计划的数据模型 
 */

public  class UploadPlanDataModel
{
	private String InfoId;
	private String ExcutedTime;
	private String ExcutedNumber;
	private String LoginId;
	private String StorageId;
	private String InOutCount;
	
	
	

	public String getInfoId() {
		return InfoId;
	}

	public void setInfoId(String infoId) {
		InfoId = infoId;
	}

	public String getExcutedTime() {
		return ExcutedTime;
	}

	public void setExcutedTime(String excutedTime) {
		ExcutedTime = excutedTime;
	}

	public String getExcutedNumber() {
		return ExcutedNumber;
	}

	public void setExcutedNumber(String excutedNumber) {
		ExcutedNumber = excutedNumber;
	}

	public String getLoginId() {
		return LoginId;
	}

	public void setLoginId(String loginId) {
		LoginId = loginId;
	}

	public String getStorageId() {
		return StorageId;
	}

	public void setStorageId(String storageId) {
		StorageId = storageId;
	}

	public String getInOutCount() {
		return InOutCount;
	}

	public void setInOutCount(String inOutCount) {
		InOutCount = inOutCount;
	}

	public UploadPlanDataModel(String infoId, String excutedTime,
			String excutedNumber, String loginId, String storageId,
			String inOutCount) {
		super();
		InfoId = infoId;
		ExcutedTime = excutedTime;
		ExcutedNumber = excutedNumber;
		LoginId = loginId;
		StorageId = storageId;
		InOutCount = inOutCount;
	}

	@Override
	public String toString() {
		return "UploadPlanDataModel [InfoId=" + InfoId + ", ExcutedTime="
				+ ExcutedTime + ", ExcutedNumber=" + ExcutedNumber
				+ ", LoginId=" + LoginId + ", StorageId=" + StorageId
				+ ", InOutCount=" + InOutCount + "]";
	}


	
	


}