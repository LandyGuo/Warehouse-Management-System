package com.qpguo.uhf.modelDAO;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	public  static final String DATABASENAME = "product";
	public  static int VERSION = 8; 
	//数据库中所含的表名称
	public static final String  TABLE_BASEDATA_NAME = "basedata";
	public static final String  TABLE_USERDATA_NAME = "userdata";
	public static final String  TABLE_PANDIANDATA_NAME = "pandiandata";
	public static final String  TABLE_PLANDATA_NAME = "plandata";
	public static final String  TABLE_POSITIONDATA_NAME = "position";
	public static final String  TABLE_LARGEMATTERDATA_NAME = "LargeMatter";
	/*新增的用于上传已分条执行的plandata数据的表*/
	public static final String TABLE_UPLOADPLANDATA_NAME = "uploadplandata";
	/*新增的用于大件上传数据的暂存库*/
	public static final String TABLE_UPLOADLARGEMATTERDATA_NAME = "uploadLargeMatter";
	/*新增的大件发卡数据库*/
	public static final String TABLE_LARGEMATTERGIVEOUT_NAME = "largeMatterGiveOut";
	//创建各表的SQL语句
	public static final String  CREATE_TABLE_BASEDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_BASEDATA_NAME
			+ "("
			+ "id integer,"
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
	public static final String CREATE_TABLE_USERDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_USERDATA_NAME 
			+"(" 
			+ "id integer primary key autoincrement," 
			+ "LoginID varchar(50)," // 用户名
			+ "Password varchar(50))"; // 密码
	public static final String  CREATE_TABLE_PANDIANDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_PANDIANDATA_NAME
			+"(" 
			+ "id integer primary key autoincrement,"
			+ "StorageId varchar(50)," // 储位
			+ "MatterId varchar(50)," // 物资编号
			+ "LabelCount varchar(50)," // 物资数量
			+ "RealCount varchar(100))"; // 物资实际数量
	public static final String CREATE_TABLE_PLANDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_PLANDATA_NAME
			+ "("
			+ "id integer primary key autoincrement,"
			+"IsExcuted integer,"//此字段仅用于手机客户端显示计划时用，不用从服务器端获取
			+ "InfoId integer,"
//			+ "StorageId varchar(50),"此字段弃用，因为返回的为可用储位的id列表
			+ "MatterId varchar(50),"
			+ "InOutCount integer,"
//			+ "StorageName integer,"此字段弃用
			+ "MatterName varchar(50),"
			+ "BillId varchar(50),"
//			+ "IsExcute integer,"//此字段弃用，因为客户端服务器端发过来的都
			    //是未执行完的计划，执行完的计划不再返回
			+ "IsHasLabel integer,"
			+ "LoginId varchar(50),"
//			+ "ExcuteTime varchar(50),"此字段弃用，在上传表中需有此字段
			+"NotExcutedNumbers integer,"//ExcutedNumbers未执行完成的数量绝对值 
			+"AvailableStorageIdList varchar(50),"//AvailableStorageIdList字段用于存储服务器返回
			//的所有可能的储位的id，用“;”相隔，如："id1;id2;id3;id4;"最多可能有四个可用储位
			+"LabelInfo varchar(50),"//存储可用储位是否有标签的信息
			+"Workshop varchar(50),"//Workshop字段为打印计划所需，表明领料车间
			+"GetPlanTime varchar(50),"//GetPlanTime计划下达时间为打印计划所需，如“2014年7月1日”
			+"GiveOutPerson varchar(50),"//GiveOutPerson发料人，为打印计划所需，如“张三”
			+"GetPerson varchar(50),"//GetPerson领料人，为打印计划所需，如“李四”
			+ "IsCatch integer)";
	/*新增表，用于存储需要上传的分条执行数据*/
	public static final String CREATE_TABLE_UPLOADPLANDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_UPLOADPLANDATA_NAME
			+"("
			+"InfoId varchar(50),"//计划的唯一标识
			+"ExcutedTime varchar(50),"//该条计划的部分执行时间
			+"ExcutedNumber varchar(50),"//该条计划的此次执行数量
			+"LoginId varchar(50),"//登陆人，即计划的执行人
			+"InOutCount varchar(50),"//出入库计划
			+"StorageId varchar(50))";//该条计划执行的储位
	
	public static final String CREATE_TABLE_POSITIONDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_POSITIONDATA_NAME
			+	"("
			+ "id integer,"
			+"IsUpload integer,"//用于手机本地上传发卡数据的标志位，此数据仅供手机客户端使用
			//不要上传
			+ "PositionCode varchar(50)," // 位置名称
			+ "PositionName varchar(50),"// 对照编号
			+ "MatterId varchar(50),"// 物料编号
			+ "TheCount varchar(50)," //数量
			+ "IsGiveOut varchar(50),"// 是否已发卡
			+ "IsCatch varchar(50)," //是否跟踪
			+ "IsHasLabel varchar(50))";  //是否有标签;
	public static final String  CREATE_TABLE_LARGEMATTERDATA ="CREATE TABLE IF NOT EXISTS " 
			+TABLE_LARGEMATTERDATA_NAME
			+"("
			+"id integer primary key autoincrement,"
			+ "LargeMatterId varchar(50),"//大件自身编号
			+ "OpeType varchar(50),"//操作类型
			+ "MatterId varchar(50),"//物资id，表明其物资类型
			+ "LoginId varchar(50),"//操作者，即登录人
			+ "ExcuteTime varchar(50))";//执行时间		
	/*在大件表largematter的LargeMatterId和ExcuteTime字段上建立索引，方便查询并按时间排序*/
	public static final String CREATE_LARGEMATTER_INDEX ="CREATE INDEX IF NOT EXISTS indexa ON "
			+TABLE_LARGEMATTERDATA_NAME+"(LargeMatterId,ExcuteTime)";
	/*删除大件表largematter的LargeMatterId字段上的索引*/
	public static final String DELETE_LARGEMATTER_INDEX = "DROP INDEX indexa";
	/*大件上传数据的暂存库*/
	public static final String  CREATE_TABLE_UPLOADLARGEMATTERDATA ="CREATE TABLE IF NOT EXISTS " 
			+TABLE_UPLOADLARGEMATTERDATA_NAME
			+"("
			+ "LargeMatterId varchar(50),"//大件自身编号
			+ "OpeType varchar(50),"//操作类型
			+ "MatterId varchar(50),"//物资id，表明其物资类型
			+ "LoginId varchar(50),"//操作者，即登录人
			+ "ExcuteTime varchar(50))";//执行时间		
	/*创建大件发卡数据库*/
	public static final String CREATE_TABLE_LARGEMATTERGIVEOUT = "CREATE TABLE IF NOT EXISTS "
	       +TABLE_LARGEMATTERGIVEOUT_NAME
	       +"("
	       +"LargeMatterId varchar(50),"//大件自身编号
	       +"MatterId varchar(50),"//大件的物资编号
	       +"IsGiveOut varchar(50))";//是否已发卡
	public DatabaseOpenHelper(Context context)
	{
		super(context, DATABASENAME,null , VERSION);
	}
	/*数据库初始化时首先建立表
	 * 需要建立的表有9个：
	 * basedata,userdata,pandiandata,
	 * plandata,position,LargeMatter
	 * uploadplandata,uploadLargeMatter
	 * largeMatterGiveOut
	 */
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL(CREATE_TABLE_BASEDATA);
		db.execSQL(CREATE_TABLE_LARGEMATTERDATA);
		/*新增语句：创建LargeMatter表的索引，便于查询*/
		db.execSQL(CREATE_LARGEMATTER_INDEX);
		/*新增数据库：上传大件数据的暂存库*/
		db.execSQL(CREATE_TABLE_UPLOADLARGEMATTERDATA);
		db.execSQL(CREATE_TABLE_PANDIANDATA);
		db.execSQL(CREATE_TABLE_PLANDATA);
		db.execSQL(CREATE_TABLE_POSITIONDATA);
		db.execSQL(CREATE_TABLE_USERDATA);
		db.execSQL(CREATE_TABLE_UPLOADPLANDATA);
		/*新增数据库：大件发卡数据库*/
		db.execSQL(CREATE_TABLE_LARGEMATTERGIVEOUT);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		//版本更新时直接删除旧的数据库，重新建表
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_BASEDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PANDIANDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PLANDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_POSITIONDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_LARGEMATTERGIVEOUT_NAME);
//		/*删除大件表之前先删除索引*/
//		db.execSQL(DELETE_LARGEMATTER_INDEX);
		/*删除大件上传数据缓存库*/
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_UPLOADLARGEMATTERDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_LARGEMATTERDATA_NAME );
		db.execSQL("DROP TABLE IF EXISTS "+ TABLE_UPLOADPLANDATA_NAME);
		this.onCreate(db);
	}
	
}