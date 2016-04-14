package com.youngsee.envservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;

import com.youngsee.common.FileUtils;
import com.youngsee.common.Logger;
import android.app.Application;
import android.os.Environment;
import android.text.TextUtils;

public class EnvmntApplication extends Application {
	private static EnvmntApplication INSTANCE = null;
	private static byte[] mEthMac = null;
	private static String mCpuId = null;
	public static final String POSTER_PACKAGENAME = "com.youngsee.envservice";
	
	public static EnvmntApplication getInstance() {

		return INSTANCE;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		INSTANCE = this;
	}

	private static String getEnvmntFileName() {
		StringBuilder sb = new StringBuilder();
		sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
		sb.append(File.separator);
		sb.append("envmnt");
		sb.append(File.separator);
		// 创建目录
		if (!FileUtils.isExist(sb.toString())) {
			FileUtils.createDir(sb.toString());
		}
		sb.append("envmnt.txt");
		try {
			FileUtils.createFile(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	// 固定用网口的MAC地址做为与服务器通信的Device_ID
	public static synchronized byte[] getEthMacAddress() {
		if (mEthMac != null) {
			return mEthMac;
		}
		mEthMac =FileUtils.readSDFile(getEnvmntFileName());
		if (mEthMac.length==0) {
			try {
				NetworkInterface intf = null;
				if ((intf = NetworkInterface.getByName("eth0")) != null) {
					mEthMac = intf.getHardwareAddress();
				}
				FileUtils.writeSDFileData(getEnvmntFileName(), mEthMac, true);
			} catch (SocketException ex) {
				Logger.e("Get MacAddress has error, the msg is: "
						+ ex.toString());
			}catch (Exception ex){
				Logger.e("Get MacAddress has error, the msg is: "
						+ ex.toString());
			}
		}
		return mEthMac;
	}

	public static String getEthFormatMac() {
		byte[] mac = getEthMacAddress();
		if (mac != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02x", mac[i]));
				if (i < 5) {
					sb.append(":");
				}
			}
			return sb.toString();
		}
		return "";
	}

	public static String getCpuId() {
		if (TextUtils.isEmpty(mCpuId)) {
			BufferedReader reader = null;
			String line = null;
			try {
				reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
				while ((line = reader.readLine()) != null) {
					String[] subStr = line.split(":");
					if (subStr[0].trim().equals("Serial")) {
						mCpuId = subStr[1].trim();
						break;
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return mCpuId;
	}

	
}