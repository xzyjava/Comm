package Comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

/**
 * @�ļ����� :SerialPort.java
 * @author :л����
 * @���ڴ���ͨ�š�
 */

class DSerialPort extends Thread implements Runnable, SerialPortEventListener {

	private String appName = "����ͨѶ����";
	private int timeout = 10000;// open �˿�ʱ�ĵȴ�ʱ��
	private int threadTime = 0;

	private CommPortIdentifier commPort;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;

	private static int temperature;
	private static int humidity;


	public int getTemperature() {
		return temperature;
	}

	public int getHumidity() {
		return humidity;
	}
	/**
	 * @�������� :listPort
	 * @�������� :�г����п��õĴ���
	 * @����ֵ���� :void
	 */
	@SuppressWarnings("rawtypes")
	public void listPort() {
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();

		System.out.println("now to list all Port of this PC��" + en);

		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(cpid.getName() + ", "
						+ cpid.getCurrentOwner());
			}
		}
	}

	/**
	 * @�������� :selectPort
	 * @�������� :ѡ��һ���˿ڣ����磺COM1
	 * @����ֵ���� :void
	 * @param portName
	 */
	@SuppressWarnings("rawtypes")
	public void selectPort(String portName) {

		this.commPort = null;
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();

		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL
					&& cpid.getName().equals(portName)) {
				this.commPort = cpid;
				break;
			}
		}

		openPort();
	}

	/**
	 * @�������� :openPort
	 * @�������� :��SerialPort
	 * @����ֵ���� :void
	 */
	private void openPort() {
		if (commPort == null)
			log(String.format("�޷��ҵ�����Ϊ'%1$s'�Ĵ��ڣ�", commPort.getName()));
		else {
			log("�˿�ѡ��ɹ�����ǰ�˿ڣ�" + commPort.getName() + ",����ʵ���� SerialPort:");

			try {
				serialPort = (SerialPort) commPort.open(appName, timeout);
				log("ʵ�� SerialPort �ɹ���");
			} catch (PortInUseException e) {
				throw new RuntimeException(String.format("�˿�'%1$s'����ʹ���У�",
						commPort.getName()));
			}
		}
	}

	/**
	 * @�������� :Initialize
	 * @�������� :��ʼ���˿�
	 * @����ֵ���� :void
	 */
	public void Initialize() {
		try {
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			System.out.println("������Ϊ��115200");
		} catch (UnsupportedCommOperationException e) {
			throw new RuntimeException("�˿ڳ�ʼ��ʧ����" + e.getMessage());
		}
	}

	/**
	 * @�������� :checkPort
	 * @�������� :���˿��Ƿ���ȷ����
	 * @����ֵ���� :void
	 */
	private void checkPort() {
		if (commPort == null)
			throw new RuntimeException("û��ѡ��˿ڣ���ʹ�� "
					+ "selectPort(String portName) ����ѡ��˿�");

		if (serialPort == null) {
			throw new RuntimeException("SerialPort ������Ч��");
		}
	}

	/**
	 * @�������� :write
	 * @�������� :��˿ڷ������ݣ����ڵ��ô˷���ǰ ��ѡ��˿ڣ���ȷ��SerialPort�����򿪣�
	 * @����ֵ���� :void
	 * @param message
	 */
	public void write(String message) {
		checkPort();

		try {
			outputStream = new BufferedOutputStream(
					serialPort.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("��ȡ�˿ڵ�OutputStream����" + e.getMessage());
		}

		try {
			outputStream.write(message.getBytes());
			log("��Ϣ���ͳɹ���");
		} catch (IOException e) {
			throw new RuntimeException("��˿ڷ�����Ϣʱ����" + e.getMessage());
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @�������� :startRead
	 * @�������� :��ʼ�����Ӷ˿��н��յ�����
	 * @����ֵ���� :void
	 * @param time
	 *            ��������Ĵ��ʱ�䣬��λΪ�룬0 ����һֱ����
	 */
	public void startRead(int time) {
		checkPort();

		try {
			inputStream = new BufferedInputStream(serialPort.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("��ȡ�˿ڵ�InputStream����" + e.getMessage());
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			throw new RuntimeException(e.getMessage());
		}

		serialPort.notifyOnDataAvailable(true);

		log(String.format("��ʼ��������'%1$s'������--------------", commPort.getName()));
		if (time > 0) {
			this.threadTime = time * 1000;
			Thread t = new Thread(this);
			t.start();
			log(String.format("����������%1$d���رա�������", threadTime));
		}
	}

	/**
	 * @�������� :close
	 * @�������� :�ر� SerialPort
	 * @����ֵ���� :void
	 */
	public void close() {
		serialPort.close();
		serialPort = null;
		commPort = null;
	}

	public void log(String msg) {
		System.out.println(appName + " --> " + msg);
	}

	/**
	 * @�������� :serialEvent
	 * @�������� :���ݽ��յļ���������
	 * @����ֵ���� :void
	 * @param SerialPortEvent 
	 * 			����ʱ���ֽڴ��룬�����жϻ�ȡ�������Ӧ�Ĵ���ʽ
	 */
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		switch (arg0.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,ͨѶ�ж� */
		case SerialPortEvent.OE:/* Overrun error����λ���� */
		case SerialPortEvent.FE:/* Framing error����֡���� */
		case SerialPortEvent.PE:/* Parity error��У����� */
		case SerialPortEvent.CD:/* Carrier detect���ز���� */
		case SerialPortEvent.CTS:/* Clear to send��������� */
		case SerialPortEvent.DSR:/* Data set ready�������豸���� */
		case SerialPortEvent.RI:/* Ring indicator������ָʾ */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*
												 * Output buffer is
												 * empty��������������
												 */
			break;
		case SerialPortEvent.DATA_AVAILABLE:/*
											 * Data available at the serial
											 * port���˿��п������ݡ������������飬������ն�
											 */
			byte[] readBuffer = new byte[1024];
			try {
				sleep(1000);
				getData(readBuffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	/**
	 * @�������� :getDate
	 * @�������� :���ݴ������������ڻ�ȡ������ȥ�����˿ո�֮��
	 * 			   ��ȡ�����ò��ֽ�����㣬�ֱ�������̨ ��������ݿ����ݴ洢���Լ�html�ļ������ɡ�
	 * @����ֵ���� :void
	 * @param readBuffer
	 * 			�����ݴ�������ȡ���ֽ����飨������������ݣ�
	 */
	public void getData(byte[] readBuffer) {
		String s2 = " ";
		String readStr = "";
		try {

			while (inputStream.available() > 0) {
				inputStream.read(readBuffer);
				readStr += new String(readBuffer).trim();
			}

			s2 = new String(readBuffer).trim();

			log("���յ��˿ڷ�������(����Ϊ" + readStr.length() + ")��" + readStr);
			log(s2);
			//������ȡ����
			calculaterData(s2);
			
			try {
				MySQL_JDBC.getTopUserInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
		}
	}


	

	
	/**
	 * @�������� :calculaterData
	 * @�������� :����Ϊ8λ�ַ�������5λΪʪ��ʮλ������λΪʪ�ȸ�λ����7λΪ�¶�ʮλ����8λΪ�¶ȸ�λ��
	 * 				 ��ȡ�ַ������������Ӧ����ʪ��ֵ������̨��
	 * @����ֵ���� :void
	 * @param s2
	 * 			�����ݴ�������ȡ���ֽ����飨������������ݣ���ȥ�����˿ո�
	 */
	private void calculaterData(String s2) {
		// ?011 2255
		String s = s2.substring(3,7);
		char[] data = s.toCharArray();

		humidity = ((int) data[2] - 48) * 10 + ((int) data[3] - 48);

		temperature = ((int) data[0] - 48) * 10 + ((int) data[1] - 48);

		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		System.out.println("��ǰ�����¶�Ϊ��" + temperature);
		System.out.println("��ǰ����ʪ��Ϊ��" + humidity);
	}


	@Override
	public void run() {
		try {
			Thread.sleep(threadTime);
			serialPort.close();
			log(String.format("�˿�''�����ر��ˣ�", commPort.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}