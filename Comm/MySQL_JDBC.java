package Comm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * @文件名称 :MySQL_JDBC.java
 * @author :谢智勇 
 * @用于串口数据连接，使用JDBC传入MySQL数据库
 */
public class MySQL_JDBC {
	static int sum = 0;
public static void getTopUserInfo() throws Exception {
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Date d = new Date();
	String time = sdf.format(d);
	//创建DSerialPort对象
	DSerialPort ds = new DSerialPort();
	
	/**
	 * jdbc四大配置参数：
	 * 1.driverClassName:com.mysql.jdbc.Driver
	 * 2.url:jdbc:mysql://localhost:3306/mydb
	 * 3.username:root
	 * 4.password:123
	 */
	
	Class.forName("com.mysql.jdbc.Driver");//加载驱动类(注册驱动类)
	String mySqlUrl = "jdbc:mysql://localhost:3306/temp_humi_data";
	String username = "root";
	String password = "";
	
	//得到连接对象
	Connection con = DriverManager.getConnection(mySqlUrl, username, password);
		
	/*对数据库做增、删、改
	 * 1.通过Connection对象创建Statement
	 *   Statement语句的发送器，它的功能就是向数据库发送sql语句！
	 * 2.调用他的int executeUpdate(String sql),返回影响了几行
	 */
	//通过Connection 得到Statement;
	Statement stmt = con.createStatement();
	sum++;
	String sql1 = "INSERT INTO temp_humi(id,temperature,humidity,time)" + 
	" VALUES('" + sum +"','" + ds.getTemperature() + "','" + ds.getHumidity() + "','" + time + "')" ;
	stmt.executeUpdate(sql1);
	if(sum >= 20){
		String sql2 = "DELETE FROM temp_humi WHERE id="+(sum-19);
		stmt.executeUpdate(sql2);
	}
	System.out.println("信息存入数据库成功！");
	System.out.println();
	}
}
