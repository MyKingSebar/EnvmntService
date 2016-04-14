package com.youngsee.envservice;

import com.youngsee.envmnt.EnvMntManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;

public class EnvmntService extends Service {
	
	public static final String UPDATA_MONITOR_DEVID_ACTION = "com.ys.intent.action.EnvmntManager";
	
	private EnvmntReceiver mEnvmntReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {		
		super.onCreate(); 
		initReceiver();
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATA_MONITOR_DEVID_ACTION);
		filter.setPriority(Integer.MAX_VALUE);
		mEnvmntReceiver = new EnvmntReceiver();
		registerReceiver(mEnvmntReceiver, filter);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
	
			return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mEnvmntReceiver);
	 	EnvMntManager.getInstance().destroy();
		super.onDestroy();
	}
	
	private class EnvmntReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent != null && intent.getAction().equals(UPDATA_MONITOR_DEVID_ACTION)) {
				Bundle bundle = intent.getExtras();
				String cpuId = bundle.getString("CpuId", "FFFFFFFFFFFFFFFF");
				String mac = bundle.getString("Mac", "000000");
				String envTerm = bundle.getString("term", "ys");
				String envTermGroup = bundle.getString("termGroup", "youngsee");
				EnvMntManager.getInstance().updateMonitorDevice(cpuId, mac, envTerm, envTermGroup);
			}
		}
	}
}
