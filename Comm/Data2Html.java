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
 * @�ļ�����Data2Php.java
 * @author��л����
 * @���ڴ��ڼ�����ã�ÿһ��������ݣ�ʹ��IO��apacheվ��htdocs�ļ���Ŀ¼��������ݣ�������ҳ����������
 * */

public class Data2Html {
	OutputStreamWriter iniDataFile;
	private String path = "E:\\wamp\\apache\\htdocs\\temp_humi_data.html";
	
	int line = 0;
	/**
	 * @���췽�� ����ʼ����
	 * @�������� :��������ʱ����ʼ�����������ļ�����վ�㡣 վ��·����E:\\wamp\\apache\\htdocs
	 */
	public Data2Html() throws IOException {
		try {
			iniDataFile = new OutputStreamWriter(new FileOutputStream(path),"GBK");
		} catch (IOException e) {
			throw new IOException("�ļ������쳣");
		}
	}

	/**
	 * @�������� :writeData
	 * @�������� :����ʪ�ȣ�ʱ������¼��html�ļ�
	 * @����ֵ���� :void
	 * @param temp��humi��SimpleDateFormat sdf��Date d
	 * 			temp:�¶�����
	 * 			humi:ʪ������
	 * 			SimpleDateFormat sdf��Date d:ʱ���ʽ��
	 */
	public void writeData(int temp, int humi, SimpleDateFormat sdf, Date d)
			throws IOException {
		try {
			String outputhtml = "<html><body><h1>" + "   "+(++line)+":  +��ǰʱ�䣺" + sdf.format(d).toString()
					+ " ------1.�¶�:" + temp + "  ------2.ʪ�ȣ�" + humi
					+"</h1></body></html>";
			iniDataFile.write(outputhtml);
			iniDataFile.flush();
		} catch (IOException e) {
			throw new IOException("��������ʧ��" + e.getMessage());
		}
	}
	
	/**
	 * @�������� :deleteFirstLine
	 * @�������� :������������50��ʱ��ɾ����һ�����ݡ����ڽ�ʡ�ռ�
	 * @����ֵ���� :
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
