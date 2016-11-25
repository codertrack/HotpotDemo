package wukai.hotpotclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;

/**
 * Created by wukai on 16/7/8.
 */
public class ClientThread extends Thread {


	private String ip;
	public ClientThread(String ip) {
		this.ip = ip;

	}

	private Socket socket;

	private InputStream iis;

	private boolean flag = true;

	private OutputStream oos;
	@Override
	public void run() {
		try {
			socket = new Socket(ip,10000);

			if (socket != null &&socket.isConnected()){

				iis = socket.getInputStream();
				oos = socket.getOutputStream();
				byte[] buf = new byte[2048];

				while (flag){
					int len =0;

					if ((len = iis.read(buf)) != -1){
						byte[] content = new byte[len];

						//对象复制
						System.arraycopy(
								buf,//复制哪个数组
								0,//从源对象的哪个位置开始复制
								content,//复制到哪个对象中
								0,//从对象中哪个位置开始写
								len//写多少个内容进去
						);

						String received = new String(content);
						System.out.println("received="+received);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public void sendData(byte[] data){
		if (oos != null){
			try {
				oos.write(data);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void cancel(){
		flag = false;
		try {
			iis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
