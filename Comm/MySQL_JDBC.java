package Comm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @�ļ����� :MySQL_JDBC.java
 * @author :л���� 
 * @���ڴ����������ӣ�ʹ��JDBC����MySQL���ݿ�
 */
public class MySQL_JDBC {
	static int sum = 0;
public static void getTopUserInfo() throws Exception {
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Date d = new Date();
	String time = sdf.format(d);
	//����DSerialPort����
	DSerialPort ds = new DSerialPort();
	
	/**
	 * jdbc�Ĵ����ò�����
	 * 1.driverClassName:com.mysql.jdbc.Driver
	 * 2.url:jdbc:mysql://localhost:3306/mydb
	 * 3.username:root
	 * 4.password:123
	 */
	
	Class.forName("com.mysql.jdbc.Driver");//����������(ע��������)
	String mySqlUrl = "jdbc:mysql://localhost:3306/temp_humi_data";
	String username = "root";
	String password = "";
	
	//�õ����Ӷ���
	Connection con = DriverManager.getConnection(mySqlUrl, username, password);
		
	/*�����ݿ�������ɾ����
	 * 1.ͨ��Connection���󴴽�Statement
	 *   Statement���ķ����������Ĺ��ܾ��������ݿⷢ��sql��䣡
	 * 2.��������int executeUpdate(String sql),����Ӱ���˼���
	 */
	//ͨ��Connection �õ�Statement;
	Statement stmt = con.createStatement();
	sum++;
	String sql1 = "INSERT INTO temp_humi(id,temperature,humidity,time)" + 
	" VALUES('" + sum +"','" + ds.getTemperature() + "','" + ds.getHumidity() + "','" + time + "')" ;
	stmt.executeUpdate(sql1);
	if(sum >= 20){
		String sql2 = "DELETE FROM temp_humi WHERE id="+(sum-19);
		stmt.executeUpdate(sql2);
	}
	System.out.println("��Ϣ�������ݿ�ɹ���");
	System.out.println();
	}
}
