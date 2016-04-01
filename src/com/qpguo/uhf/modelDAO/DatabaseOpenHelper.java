package com.qpguo.uhf.modelDAO;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseOpenHelper extends SQLiteOpenHelper
{
	public  static final String DATABASENAME = "product";
	public  static int VERSION = 8; 
	//���ݿ��������ı�����
	public static final String  TABLE_BASEDATA_NAME = "basedata";
	public static final String  TABLE_USERDATA_NAME = "userdata";
	public static final String  TABLE_PANDIANDATA_NAME = "pandiandata";
	public static final String  TABLE_PLANDATA_NAME = "plandata";
	public static final String  TABLE_POSITIONDATA_NAME = "position";
	public static final String  TABLE_LARGEMATTERDATA_NAME = "LargeMatter";
	/*�����������ϴ��ѷ���ִ�е�plandata���ݵı�*/
	public static final String TABLE_UPLOADPLANDATA_NAME = "uploadplandata";
	/*���������ڴ���ϴ����ݵ��ݴ��*/
	public static final String TABLE_UPLOADLARGEMATTERDATA_NAME = "uploadLargeMatter";
	/*�����Ĵ���������ݿ�*/
	public static final String TABLE_LARGEMATTERGIVEOUT_NAME = "largeMatterGiveOut";
	//���������SQL���
	public static final String  CREATE_TABLE_BASEDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_BASEDATA_NAME
			+ "("
			+ "id integer,"
			+ "class varchar(100)," // ���ʷ���
			+ "pid varchar(100)," // ���ʱ��
			+ "name varchar(100)," // ��������
			+ "type varchar(100),"// ����ͺ�
			+ "unit varchar(100)," // ������λ
			+ "price varchar(100),"// Ŀ¼��
			+ "img varchar(100),"// ͼƬ
			+ "use varchar(100),"// ��;
			+ "mark varchar(100),"// ��ע
			+ "piccode varchar(100))";// ͼ��;
	public static final String CREATE_TABLE_USERDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_USERDATA_NAME 
			+"(" 
			+ "id integer primary key autoincrement," 
			+ "LoginID varchar(50)," // �û���
			+ "Password varchar(50))"; // ����
	public static final String  CREATE_TABLE_PANDIANDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_PANDIANDATA_NAME
			+"(" 
			+ "id integer primary key autoincrement,"
			+ "StorageId varchar(50)," // ��λ
			+ "MatterId varchar(50)," // ���ʱ��
			+ "LabelCount varchar(50)," // ��������
			+ "RealCount varchar(100))"; // ����ʵ������
	public static final String CREATE_TABLE_PLANDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_PLANDATA_NAME
			+ "("
			+ "id integer primary key autoincrement,"
			+"IsExcuted integer,"//���ֶν������ֻ��ͻ�����ʾ�ƻ�ʱ�ã����ôӷ������˻�ȡ
			+ "InfoId integer,"
//			+ "StorageId varchar(50),"���ֶ����ã���Ϊ���ص�Ϊ���ô�λ��id�б�
			+ "MatterId varchar(50),"
			+ "InOutCount integer,"
//			+ "StorageName integer,"���ֶ�����
			+ "MatterName varchar(50),"
			+ "BillId varchar(50),"
//			+ "IsExcute integer,"//���ֶ����ã���Ϊ�ͻ��˷������˷������Ķ�
			    //��δִ����ļƻ���ִ����ļƻ����ٷ���
			+ "IsHasLabel integer,"
			+ "LoginId varchar(50),"
//			+ "ExcuteTime varchar(50),"���ֶ����ã����ϴ��������д��ֶ�
			+"NotExcutedNumbers integer,"//ExcutedNumbersδִ����ɵ���������ֵ 
			+"AvailableStorageIdList varchar(50),"//AvailableStorageIdList�ֶ����ڴ洢����������
			//�����п��ܵĴ�λ��id���á�;��������磺"id1;id2;id3;id4;"���������ĸ����ô�λ
			+"LabelInfo varchar(50),"//�洢���ô�λ�Ƿ��б�ǩ����Ϣ
			+"Workshop varchar(50),"//Workshop�ֶ�Ϊ��ӡ�ƻ����裬�������ϳ���
			+"GetPlanTime varchar(50),"//GetPlanTime�ƻ��´�ʱ��Ϊ��ӡ�ƻ����裬�硰2014��7��1�ա�
			+"GiveOutPerson varchar(50),"//GiveOutPerson�����ˣ�Ϊ��ӡ�ƻ����裬�硰������
			+"GetPerson varchar(50),"//GetPerson�����ˣ�Ϊ��ӡ�ƻ����裬�硰���ġ�
			+ "IsCatch integer)";
	/*���������ڴ洢��Ҫ�ϴ��ķ���ִ������*/
	public static final String CREATE_TABLE_UPLOADPLANDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_UPLOADPLANDATA_NAME
			+"("
			+"InfoId varchar(50),"//�ƻ���Ψһ��ʶ
			+"ExcutedTime varchar(50),"//�����ƻ��Ĳ���ִ��ʱ��
			+"ExcutedNumber varchar(50),"//�����ƻ��Ĵ˴�ִ������
			+"LoginId varchar(50),"//��½�ˣ����ƻ���ִ����
			+"InOutCount varchar(50),"//�����ƻ�
			+"StorageId varchar(50))";//�����ƻ�ִ�еĴ�λ
	
	public static final String CREATE_TABLE_POSITIONDATA = "CREATE TABLE IF NOT EXISTS "
			+TABLE_POSITIONDATA_NAME
			+	"("
			+ "id integer,"
			+"IsUpload integer,"//�����ֻ������ϴ��������ݵı�־λ�������ݽ����ֻ��ͻ���ʹ��
			//��Ҫ�ϴ�
			+ "PositionCode varchar(50)," // λ������
			+ "PositionName varchar(50),"// ���ձ��
			+ "MatterId varchar(50),"// ���ϱ��
			+ "TheCount varchar(50)," //����
			+ "IsGiveOut varchar(50),"// �Ƿ��ѷ���
			+ "IsCatch varchar(50)," //�Ƿ����
			+ "IsHasLabel varchar(50))";  //�Ƿ��б�ǩ;
	public static final String  CREATE_TABLE_LARGEMATTERDATA ="CREATE TABLE IF NOT EXISTS " 
			+TABLE_LARGEMATTERDATA_NAME
			+"("
			+"id integer primary key autoincrement,"
			+ "LargeMatterId varchar(50),"//���������
			+ "OpeType varchar(50),"//��������
			+ "MatterId varchar(50),"//����id����������������
			+ "LoginId varchar(50),"//�����ߣ�����¼��
			+ "ExcuteTime varchar(50))";//ִ��ʱ��		
	/*�ڴ����largematter��LargeMatterId��ExcuteTime�ֶ��Ͻ��������������ѯ����ʱ������*/
	public static final String CREATE_LARGEMATTER_INDEX ="CREATE INDEX IF NOT EXISTS indexa ON "
			+TABLE_LARGEMATTERDATA_NAME+"(LargeMatterId,ExcuteTime)";
	/*ɾ�������largematter��LargeMatterId�ֶ��ϵ�����*/
	public static final String DELETE_LARGEMATTER_INDEX = "DROP INDEX indexa";
	/*����ϴ����ݵ��ݴ��*/
	public static final String  CREATE_TABLE_UPLOADLARGEMATTERDATA ="CREATE TABLE IF NOT EXISTS " 
			+TABLE_UPLOADLARGEMATTERDATA_NAME
			+"("
			+ "LargeMatterId varchar(50),"//���������
			+ "OpeType varchar(50),"//��������
			+ "MatterId varchar(50),"//����id����������������
			+ "LoginId varchar(50),"//�����ߣ�����¼��
			+ "ExcuteTime varchar(50))";//ִ��ʱ��		
	/*��������������ݿ�*/
	public static final String CREATE_TABLE_LARGEMATTERGIVEOUT = "CREATE TABLE IF NOT EXISTS "
	       +TABLE_LARGEMATTERGIVEOUT_NAME
	       +"("
	       +"LargeMatterId varchar(50),"//���������
	       +"MatterId varchar(50),"//��������ʱ��
	       +"IsGiveOut varchar(50))";//�Ƿ��ѷ���
	public DatabaseOpenHelper(Context context)
	{
		super(context, DATABASENAME,null , VERSION);
	}
	/*���ݿ��ʼ��ʱ���Ƚ�����
	 * ��Ҫ�����ı���9����
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
		/*������䣺����LargeMatter������������ڲ�ѯ*/
		db.execSQL(CREATE_LARGEMATTER_INDEX);
		/*�������ݿ⣺�ϴ�������ݵ��ݴ��*/
		db.execSQL(CREATE_TABLE_UPLOADLARGEMATTERDATA);
		db.execSQL(CREATE_TABLE_PANDIANDATA);
		db.execSQL(CREATE_TABLE_PLANDATA);
		db.execSQL(CREATE_TABLE_POSITIONDATA);
		db.execSQL(CREATE_TABLE_USERDATA);
		db.execSQL(CREATE_TABLE_UPLOADPLANDATA);
		/*�������ݿ⣺����������ݿ�*/
		db.execSQL(CREATE_TABLE_LARGEMATTERGIVEOUT);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		//�汾����ʱֱ��ɾ���ɵ����ݿ⣬���½���
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_BASEDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PANDIANDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PLANDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_POSITIONDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_LARGEMATTERGIVEOUT_NAME);
//		/*ɾ�������֮ǰ��ɾ������*/
//		db.execSQL(DELETE_LARGEMATTER_INDEX);
		/*ɾ������ϴ����ݻ����*/
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_UPLOADLARGEMATTERDATA_NAME);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_LARGEMATTERDATA_NAME );
		db.execSQL("DROP TABLE IF EXISTS "+ TABLE_UPLOADPLANDATA_NAME);
		this.onCreate(db);
	}
	
}