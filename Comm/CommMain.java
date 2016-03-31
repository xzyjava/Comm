package Comm;

import java.io.IOException;

class CommMain extends Thread
{
	
	public static void main(String[] args) throws IOException, Exception
	{ 
		/*Data2Html data2html = new Data2Html();
		for(int i=0;i<50;i++){	
			if(i>5)
				data2html.deleteFirstLine();
			sleep(3000);
			
			data2html.writeData(16+i,37+i,new SimpleDateFormat("HH:mm:ss"),new Date()); 		
		}*/
		SerialPortRun();
	}


	public static void SerialPortRun() {
		DSerialPort ds = new DSerialPort();
		ds.listPort();
		ds.selectPort("COM3");
		ds.Initialize();
//		ds.startRead(12);
		ds.startRead(0);
	}
	
}
