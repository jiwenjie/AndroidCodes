package com.kuky.base_kt_module.BaseUtils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

/**
 * @author kuky
 * @description
 */
@SuppressLint("MissingPermission")
object NetworkUtils {

    fun isNetworkConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo?.isAvailable ?: false
    }

    fun isWifiConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI
    }

    fun isMobileConnected(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return if (networkInfo == null || networkInfo.type != ConnectivityManager.TYPE_MOBILE) false else networkInfo.isAvailable

    }

    fun getConnectedType(context: Context): Int {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo
        return if (networkInfo != null && networkInfo.isAvailable) networkInfo.type else -1
    }

    /**
     * 没有网络-0：WIFI网络-1：4G网络-4：3G网络-3：2G网络-2
     */
    fun getApnType(context: Context): Int {
        var netType = 0
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = manager.activeNetworkInfo ?: return netType

        val nType = networkInfo.type
        if (nType == ConnectivityManager.TYPE_WIFI) netType = 1
        else if (nType == ConnectivityManager.TYPE_MOBILE) {
            val nSubType = networkInfo.subtype
            val telManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            netType = if (nSubType == TelephonyManager.NETWORK_TYPE_LTE && !telManager.isNetworkRoaming) 4
            else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telManager.isNetworkRoaming) 3
            else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telManager.isNetworkRoaming) 2
            else 2
        }
        return netType
    }

    fun isGpsEnabled(context: Context): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
