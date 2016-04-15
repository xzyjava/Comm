package Comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @文件名：Data2Php.java
 * @author：谢智勇
 * @用于串口计算调用，每一次输出数据，使用IO在apache站点htdocs文件夹目录下添加数据，用于网页端数据生成
 * */

public class Data2Html {
	OutputStreamWriter iniDataFile;
	private String path = "E:\\wamp\\apache\\htdocs\\temp_humi_data.html";
	
	int line = 0;
	/**
	 * @构造方法 ：初始化该
	 * @功能描述 :创建对象时，初始化，创建空文件夹于站点。 站点路径：E:\\wamp\\apache\\htdocs
	 */
	public Data2Html() throws IOException {
		try {
			iniDataFile = new OutputStreamWriter(new FileOutputStream(path),"GBK");
		} catch (IOException e) {
			throw new IOException("文件创建异常");
		}
	}

	/**
	 * @方法名称 :writeData
	 * @功能描述 :将温湿度，时间数据录入html文件
	 * @返回值类型 :void
	 * @param temp，humi，SimpleDateFormat sdf，Date d
	 * 			temp:温度数据
	 * 			humi:湿度数据
	 * 			SimpleDateFormat sdf，Date d:时间格式。
	 */
	public void writeData(int temp, int humi, SimpleDateFormat sdf, Date d)
			throws IOException {
		try {
			String outputhtml = "<html><body><h1>" + "   "+(++line)+":  +当前时间：" + sdf.format(d).toString()
					+ " ------1.温度:" + temp + "  ------2.湿度：" + humi
					+"</h1></body></html>";
			iniDataFile.write(outputhtml);
			iniDataFile.flush();
		} catch (IOException e) {
			throw new IOException("数据输入失败" + e.getMessage());
		}
	}
	
	/**
	 * @方法名称 :deleteFirstLine
	 * @功能描述 :当数据量超过50行时，删除第一行数据。用于节省空间
	 * @返回值类型 :
	 * @param 
	 */
	public void deleteFirstLine()throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		BufferedWriter bf = new BufferedWriter(new FileWriter(path));
		String line = br.readLine();
		StringBuilder sb = new StringBuilder();
		int index = 0;
		while(index < 5){
			line = br.readLine();
			bf.write(sb.toString()+"\r\n");
			bf.flush();
			index ++;
			sb.append(line);
		}
		bf.close();
		br.close();
		sb.delete(0, sb.length());
	}
}
