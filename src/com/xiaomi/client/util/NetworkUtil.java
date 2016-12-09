
package com.xiaomi.client.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.xiaomi.client.ClientApplication;

public class NetworkUtil {
    public static boolean isNetWorkConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static int getActiveNetworkType(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        if (info != null) {
            return info.getType();
        }
        return -1;
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info;
        }
        return null;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static String getWifiSSID(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null &&
                networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public static String getMacAddress() {
        WifiManager mWifiManager = null;
        String mMacAddress = null;
        if (mMacAddress == null) {
            if (mWifiManager == null) {
                mWifiManager = (WifiManager) ClientApplication.instance.getSystemService(
                        Context.WIFI_SERVICE);
            }
            WifiInfo info = mWifiManager.getConnectionInfo();
            if (info != null) {
                mMacAddress = info.getMacAddress();
            }
        }
        return null == mMacAddress ? "" : mMacAddress;
    }

    public static String getnetworkTypeStr() {
        NetworkInfo info = NetworkUtil.getNetworkInfo(ClientApplication.instance);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return "wifi";
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                final int subType = info.getSubtype();
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return "2g";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "3g";
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4g";
                    default:
                        return "unknown";
                }
            }
            if (info.getType() == ConnectivityManager.TYPE_BLUETOOTH) {
                return "bluetooth";
            }
            return "others";
        }
        return "unknown";
    }
}
