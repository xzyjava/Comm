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
 * @文件名称 :SerialPort.java
 * @author :谢智勇
 * @用于串口通信。
 */

class DSerialPort extends Thread implements Runnable, SerialPortEventListener {

	private String appName = "串口通讯测试";
	private int timeout = 10000;// open 端口时的等待时间
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
	 * @方法名称 :listPort
	 * @功能描述 :列出所有可用的串口
	 * @返回值类型 :void
	 */
	@SuppressWarnings("rawtypes")
	public void listPort() {
		CommPortIdentifier cpid;
		Enumeration en = CommPortIdentifier.getPortIdentifiers();

		System.out.println("now to list all Port of this PC：" + en);

		while (en.hasMoreElements()) {
			cpid = (CommPortIdentifier) en.nextElement();
			if (cpid.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				System.out.println(cpid.getName() + ", "
						+ cpid.getCurrentOwner());
			}
		}
	}

	/**
	 * @方法名称 :selectPort
	 * @功能描述 :选择一个端口，比如：COM1
	 * @返回值类型 :void
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
	 * @方法名称 :openPort
	 * @功能描述 :打开SerialPort
	 * @返回值类型 :void
	 */
	private void openPort() {
		if (commPort == null)
			log(String.format("无法找到名字为'%1$s'的串口！", commPort.getName()));
		else {
			log("端口选择成功，当前端口：" + commPort.getName() + ",现在实例化 SerialPort:");

			try {
				serialPort = (SerialPort) commPort.open(appName, timeout);
				log("实例 SerialPort 成功！");
			} catch (PortInUseException e) {
				throw new RuntimeException(String.format("端口'%1$s'正在使用中！",
						commPort.getName()));
			}
		}
	}

	/**
	 * @方法名称 :Initialize
	 * @功能描述 :初始化端口
	 * @返回值类型 :void
	 */
	public void Initialize() {
		try {
			serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			System.out.println("波特率为：115200");
		} catch (UnsupportedCommOperationException e) {
			throw new RuntimeException("端口初始化失敗：" + e.getMessage());
		}
	}

	/**
	 * @方法名称 :checkPort
	 * @功能描述 :检查端口是否正确连接
	 * @返回值类型 :void
	 */
	private void checkPort() {
		if (commPort == null)
			throw new RuntimeException("没有选择端口，请使用 "
					+ "selectPort(String portName) 方法选择端口");

		if (serialPort == null) {
			throw new RuntimeException("SerialPort 对象无效！");
		}
	}

	/**
	 * @方法名称 :write
	 * @功能描述 :向端口发送数据，请在调用此方法前 先选择端口，并确定SerialPort正常打开！
	 * @返回值类型 :void
	 * @param message
	 */
	public void write(String message) {
		checkPort();

		try {
			outputStream = new BufferedOutputStream(
					serialPort.getOutputStream());
		} catch (IOException e) {
			throw new RuntimeException("获取端口的OutputStream出错：" + e.getMessage());
		}

		try {
			outputStream.write(message.getBytes());
			log("信息发送成功！");
		} catch (IOException e) {
			throw new RuntimeException("向端口发送信息时出错：" + e.getMessage());
		} finally {
			try {
				outputStream.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @方法名称 :startRead
	 * @功能描述 :开始监听从端口中接收的数据
	 * @返回值类型 :void
	 * @param time
	 *            监听程序的存活时间，单位为秒，0 则是一直监听
	 */
	public void startRead(int time) {
		checkPort();

		try {
			inputStream = new BufferedInputStream(serialPort.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException("获取端口的InputStream出错：" + e.getMessage());
		}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			throw new RuntimeException(e.getMessage());
		}

		serialPort.notifyOnDataAvailable(true);

		log(String.format("开始监听来自'%1$s'的数据--------------", commPort.getName()));
		if (time > 0) {
			this.threadTime = time * 1000;
			Thread t = new Thread(this);
			t.start();
			log(String.format("监听程序将在%1$d秒后关闭。。。。", threadTime));
		}
	}

	/**
	 * @方法名称 :close
	 * @功能描述 :关闭 SerialPort
	 * @返回值类型 :void
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
	 * @方法名称 :serialEvent
	 * @功能描述 :数据接收的监听处理函数
	 * @返回值类型 :void
	 * @param SerialPortEvent 
	 * 			串口时间字节传入，用于判断获取数据相对应的处理方式
	 */
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		switch (arg0.getEventType()) {
		case SerialPortEvent.BI:/* Break interrupt,通讯中断 */
		case SerialPortEvent.OE:/* Overrun error，溢位错误 */
		case SerialPortEvent.FE:/* Framing error，传帧错误 */
		case SerialPortEvent.PE:/* Parity error，校验错误 */
		case SerialPortEvent.CD:/* Carrier detect，载波检测 */
		case SerialPortEvent.CTS:/* Clear to send，清除发送 */
		case SerialPortEvent.DSR:/* Data set ready，数据设备就绪 */
		case SerialPortEvent.RI:/* Ring indicator，响铃指示 */
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:/*
												 * Output buffer is
												 * empty，输出缓冲区清空
												 */
			break;
		case SerialPortEvent.DATA_AVAILABLE:/*
											 * Data available at the serial
											 * port，端口有可用数据。读到缓冲数组，输出到终端
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
	 * @方法名称 :getDate
	 * @功能描述 :数据处理函数，将串口获取的数据去除两端空格之后，
	 * 			   获取其有用部分解码计算，分别做控制台 输出，数据库数据存储，以及html文件的生成。
	 * @返回值类型 :void
	 * @param readBuffer
	 * 			由数据处理方法获取的字节数组（串口输入的数据）
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

			log("接收到端口返回数据(长度为" + readStr.length() + ")：" + readStr);
			log(s2);
			//数据提取计算
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
	 * @方法名称 :calculaterData
	 * @功能描述 :输入为8位字符串，第5位为湿度十位，第六位为湿度个位，第7位为温度十位，第8位为温度个位。
	 * 				 截取字符串，并输出对应的温湿度值至控制台。
	 * @返回值类型 :void
	 * @param s2
	 * 			由数据处理方法获取的字节数组（串口输入的数据）并去除两端空格
	 */
	private void calculaterData(String s2) {
		// ?011 2255
		String s = s2.substring(3,7);
		char[] data = s.toCharArray();

		humidity = ((int) data[2] - 48) * 10 + ((int) data[3] - 48);

		temperature = ((int) data[0] - 48) * 10 + ((int) data[1] - 48);

		System.out.println(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		System.out.println("当前空气温度为：" + temperature);
		System.out.println("当前空气湿度为：" + humidity);
	}


	@Override
	public void run() {
		try {
			Thread.sleep(threadTime);
			serialPort.close();
			log(String.format("端口''监听关闭了！", commPort.getName()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}