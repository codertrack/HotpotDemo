package wukai.hotpotdemo;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.lang.reflect.Method;

public class HotActivity extends AppCompatActivity {


	private WifiManager wifiManager;
	private boolean flag=false;


	private ServerThread serverThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hot);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		serverThread = new ServerThread();
	}

	public void openHotpot(View view) {
			setWifiApEnabled(true);
			serverThread.start();

	}

	/**
	 * 组播
	 * @param enabled
	 * @return
	 */
	public boolean setWifiApEnabled(boolean enabled) {
		if (enabled) {
			//wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
			wifiManager.setWifiEnabled(false);
		}
		try {

//			Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
//			method.setAccessible(true);
//			WifiConfiguration apConfig = (WifiConfiguration) method.invoke(wifiManager);
			//热点的配置类

			WifiConfiguration apConfig = new WifiConfiguration();
			//配置热点的名称(可以在名字后面加点随机数什么的)
			apConfig.SSID="16033333333";
			//配置热点的密码
			apConfig.preSharedKey="12345678";


			apConfig.hiddenSSID = true;
			apConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			apConfig.allowedPairwiseCiphers
					.set(WifiConfiguration.PairwiseCipher.CCMP);
			apConfig.status = WifiConfiguration.Status.ENABLED;
			//通过反射调用设置热点

			Method method1 = wifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			//返回热点打开状态

			boolean flag = (boolean) method1.invoke(wifiManager, apConfig, enabled);
			return flag;
		} catch (Exception e) {
			return false;
		}


	}





}
