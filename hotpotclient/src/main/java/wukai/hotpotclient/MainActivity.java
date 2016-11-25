package wukai.hotpotclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private WifiManager wifiManager;

	private ClientThread clientThread;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		connectToHotpot();
	}

	private BroadcastReceiver mReceiver;;

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.ACTION_PICK_WIFI_NETWORK);
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		//intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		mReceiver = new WifiReceiver();
		registerReceiver(mReceiver,intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	/*连接到热点*/
	public void connectToHotpot() {

		String local = Formatter.formatIpAddress(wifiManager.getDhcpInfo().serverAddress);
		System.out.println(local);
		clientThread = new ClientThread(local);
		clientThread.start();


//		if (!wifiManager.isWifiEnabled()){
//			wifiManager.setWifiEnabled(true);
//		}
//
//		WifiConfiguration wifiConfig = this.setWifiParams("160333333333");
//		int wcgID = wifiManager.addNetwork(wifiConfig);
//		wifiManager.enableNetwork(wcgID, true);


	}


	/*当搜索到新的wifi热点时判断该热点是否符合规格*/
	public void onReceiveNewNetworks(List<ScanResult> wifiList) {
		for (ScanResult result : wifiList) {
			System.out.println(result.SSID);
			if ((result.SSID).contains("1603")){
				System.out.println("1603被搜索到了");
			}
		}
	}


	/**
	 * 搜索热点
	 * @param view
	 */
	public void searchAp(View view) {
		wifiManager.startScan();
	}

	/**
	 * 搜索热点
	 * @param view
	 */
	public void connAp(View view) {

		connectToHotpot();
	}

	public void send(View view) {
		if (clientThread!= null){
			clientThread.sendData("I'm super Man ".getBytes());
		}

	}


	/* 监听热点变化 */
	private final class WifiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				List<ScanResult> results = wifiManager.getScanResults();
				onReceiveNewNetworks(results);
			}else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")){

				ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				NetworkInfo activeInfo = manager.getActiveNetworkInfo();

				Toast.makeText(context,
						"wifi:"+wifiInfo.isConnected(),
						Toast.LENGTH_SHORT).show();

				if (wifiInfo.isConnected()){
					String local = Formatter.formatIpAddress(wifiManager.getDhcpInfo().serverAddress);
					System.out.println(local);
					clientThread = new ClientThread(local);
					clientThread.start();
				}
			}
		}
	}

	/*设置要连接的热点的参数*/
	public WifiConfiguration setWifiParams(String ssid) {
		WifiConfiguration apConfig = new WifiConfiguration();
		apConfig.SSID = "\"" + ssid + "\"";
		apConfig.preSharedKey = "\"12345678\"";
		apConfig.hiddenSSID = true;
		apConfig.status = WifiConfiguration.Status.ENABLED;
		apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		apConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		apConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		apConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		return apConfig;
	}


}