package jingya.com.base_class_module.BaseUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.math.BigDecimal;

/**
 * @author kuky
 * @description
 */
public class CleanMemoryCacheUtils {

    /**
     * 清空缓存
     *
     * @param context
     */
    public static void clearAllCaches(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     */
    public static String getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return getFormatSize(cacheSize);
    }

    private static boolean deleteDir(File cacheDir){
        if (cacheDir != null && cacheDir.isDirectory()) {
            String[] children = cacheDir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(cacheDir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return cacheDir == null || cacheDir.delete();
    }

    private static long getFolderSize(File cacheDir) {
        long size = 0;
        File[] fileList = cacheDir.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    size += getFolderSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    private static String getFormatSize(double cacheSize) {
        double kilobyte = cacheSize / 1024;
        if (kilobyte < 1) {
            return "0.00KB";
        }

        double megabyte = kilobyte / 1024;
        if (megabyte < 1) {
            BigDecimal decimal = new BigDecimal(Double.toString(kilobyte));
            return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }

        double gigabyte = megabyte / 1024;
        if (gigabyte < 1) {
            BigDecimal decimal = new BigDecimal(Double.toString(megabyte));
            return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }

        return new BigDecimal(gigabyte).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
    }
}
