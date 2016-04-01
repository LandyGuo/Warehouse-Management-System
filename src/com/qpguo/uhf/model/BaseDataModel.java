package com.qpguo.uhf.model;

public class BaseDataModel
{
	private int id;
	private String classes;
	private String pid;
	private String name;
	private String type;
	private String unit;
	private String price;
	private String img;
	private String use;
	private String mark;
	private String piccode;
	
	public BaseDataModel(int id,String classes,String pid,String name,String type,
			String unit,String price,String img,String use,String mark,String piccode)
	{
		this.id=id;
		this.classes=classes;
		this.pid=pid;
		this.name=name;
		this.type=type;
		this.unit=unit;
		this.price=price;
		this.img=img;
		this.use=use;
		this.mark=mark;
		this.piccode=piccode;
	}
	
	public Integer getId() 
	{
		return id;
	}
	public void setId(Integer id) 
	{
		this.id = id;
	}
	public String getClasses() 
	{
		return classes;
	}
	public void setClasses(String classes) 
	{
		this.classes = classes;
	}
	public String getPid() 
	{
		return pid;
	}
	public void setPid(String pid) 
	{
		this.pid = pid;
	}
	public String getName() 
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getType() 
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getUnit() 
	{
		return unit;
	}
	public void setUnit(String unit) 
	{
		this.unit = unit;
	}
	public String getPrice() 
	{
		return price;
	}
	public void setPrice(String price) 
	{
		this.price = price;
	}
	public String getImg() 
	{
		return img;
	}
	public void setImg(String img) 
	{
		this.img = img;
	}
	public String getUse() 
	{
		return use;
	}
	public void setUse(String use) 
	{
		this.use = use;
	}
	public String getMark() 
	{
		return mark;
	}
	public void setMark(String mark) 
	{
		this.mark = mark;
	}
	public String getPiccode() 
	{
		return piccode;
	}
	public void setPiccode(String piccode) 
	{
		this.piccode = piccode;
	}
	public String toString()
	{
		return 	"id:"+id
		+"classes:"+classes
		+"pid:"+pid
		+"name:"+name
		+"type:"+type
		+"unit:"+unit
		+"price:"+price
		+"img:"+img
		+"use:"+use
		+"mark:"+mark
		+"piccode:"+piccode;
	}


}
/*
			+ "id integer primary key autoincrement,"
			+ "class varchar(100)," // 物资分类
			+ "pid varchar(100)," // 物资编号
			+ "name varchar(100)," // 物资名称
			+ "type varchar(100),"// 规格型号
			+ "unit varchar(100)," // 计量单位
			+ "price varchar(100),"// 目录价
			+ "img varchar(100),"// 图片
			+ "use varchar(100),"// 用途
			+ "mark varchar(100),"// 备注
			+ "piccode varchar(100))";// 图号;
*/