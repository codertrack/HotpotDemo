package wukai.hotpotdemo;

import android.content.Context;
import android.text.style.ReplacementSpan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wukai on 16/7/8.
 */
public class ServerThread extends Thread {


	private ServerSocket serverSocket;
	private boolean flag = true;
	List<ConnectedThread> conns = new ArrayList<>();

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(10000);
			while (flag) {
				///阻塞方法
				Socket socket = serverSocket.accept();
				if (socket != null) {
					ConnectedThread t = new ConnectedThread(socket);
					conns.add(t);
					t.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public void stopServer(){
		flag = false;
		
		
		for (ConnectedThread thread:conns){
			thread.cancel();
		}
	}

	public void stopServer2(){
		flag = false;
		for (ConnectedThread thread:conns){
			thread.cancel();
		}
	}

	class ConnectedThread extends Thread {
		private Socket socket;
		private InputStream iis;
		private OutputStream oos;
		private boolean exit = true;
		public ConnectedThread(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {

			try {
				iis = socket.getInputStream();
				oos = socket.getOutputStream();
				byte[] buf = new byte[2048];

				while (exit) {
					int len = 0;

					if ((len = iis.read(buf)) != -1) {
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
						System.out.println("received=" + received);
						String reply = "Hi" + received;
						oos.write(reply.getBytes());
						oos.flush();
					}


				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		public void cancel(){
			exit = false;
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
}
