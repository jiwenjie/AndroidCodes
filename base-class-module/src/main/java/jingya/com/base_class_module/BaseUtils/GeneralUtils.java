package jingya.com.base_class_module.BaseUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author Kuky
 */
public class GeneralUtils {

    /**
     * 设置 textView 的图片
     *
     * @param context
     * @param view
     * @param drawId
     * @param direction
     */
    @SuppressLint("NewApi")
    public static void setTextDrawable(Context context, TextView view, int drawId, String direction) {
        Drawable drawable = context.getResources().getDrawable(drawId, null);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (direction) {
            case "left":
                view.setCompoundDrawables(drawable, null, null, null);
                break;

            case "top":
                view.setCompoundDrawables(null, drawable, null, null);
                break;

            case "right":
                view.setCompoundDrawables(null, null, drawable, null);
                break;

            case "bottom":
                view.setCompoundDrawables(null, null, null, drawable);
                break;

            default:
                break;
        }
    }

    public static void setTextDrawable(Context context, TextView view, Bitmap bitmap, String direction) {
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (direction) {
            case "left":
                view.setCompoundDrawables(drawable, null, null, null);
                break;

            case "top":
                view.setCompoundDrawables(null, drawable, null, null);
                break;

            case "right":
                view.setCompoundDrawables(null, null, drawable, null);
                break;

            case "bottom":
                view.setCompoundDrawables(null, null, null, drawable);
                break;

            default:
                break;
        }
    }

    public static void setTextDrawable(Context context, TextView view, Drawable drawable, String direction) {
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        switch (direction) {
            case "left":
                view.setCompoundDrawables(drawable, null, null, null);
                break;

            case "top":
                view.setCompoundDrawables(null, drawable, null, null);
                break;

            case "right":
                view.setCompoundDrawables(null, null, drawable, null);
                break;

            case "bottom":
                view.setCompoundDrawables(null, null, null, drawable);
                break;

            default:
                break;
        }
    }

    public static void setTextDrawable(Context context, TextView view, Drawable drawable, String direction, int width, int height) {
        drawable.setBounds(0, 0, width, height);
        switch (direction) {
            case "left":
                view.setCompoundDrawables(drawable, null, null, null);
                break;

            case "top":
                view.setCompoundDrawables(null, drawable, null, null);
                break;

            case "right":
                view.setCompoundDrawables(null, null, drawable, null);
                break;

            case "bottom":
                view.setCompoundDrawables(null, null, null, drawable);
                break;

            default:
                break;
        }
    }


    /**
     * 判断能否转 integer
     *
     * @param str
     * @return
     */
    public static boolean canString2Integer(String str) {
        try {
            Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * #.## 表示会去掉小数点后最后的0 例如 1.20 就会变成1.2
     * <p>
     * 0.00 表示不会去掉小数点后的0 例如 1.20 依然是 1.20
     *
     * @param nowDouble
     * @param textFormat
     * @return
     */
    public static String getSavePoint(double nowDouble, String textFormat) {
        return new DecimalFormat(textFormat).format(nowDouble);
    }

    /**
     * 设置 activity 透明度
     *
     * @param f      透明度，0代表全透明，0.5代表半透明，1代表完不透明
     * @param window 当前 activity 的 getWindow
     */
    public static void setWindowAlpha(float f, Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = f;
        window.setAttributes(lp);
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param view    触发显示软件盘的view
     */
    public static void showSoftInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param view    触发控件
     */
    public static void hideKeyboard(Context context, View view) {
        Window window = ((Activity) context).getWindow();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (window.getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 根据图片路径、屏幕宽高获取 bitmap
     *
     * @param path 图片路径
     * @param rqsW 屏幕宽
     * @param rqsH 屏幕高
     * @return
     */
    public static Bitmap compressBitmap(String path, int rqsW, int rqsH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int ow = options.outWidth;
        int oh = options.outHeight;
        int size = (ow * oh) / (rqsW * rqsH);
        if (size % 2 != 0) {
            size++;
        }
        options.inSampleSize = size;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 根据图片路径、屏幕宽高获取 bitmap
     *
     * @param path 图片路径
     * @param rqsW 屏幕宽
     * @param rqsH 屏幕高
     * @return
     */
    public static Bitmap compressBitmapForNewGoods(String path, int rqsW, int rqsH, int compressSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int ow = options.outWidth;
        int oh = options.outHeight;
        options.inSampleSize = calculateInSampleSize(options, rqsW, rqsH, ow, oh) * compressSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 计算压缩值
     *
     * @param options BitmapFactory.Options
     * @param rqsW    屏幕宽
     * @param rqsH    屏幕高
     * @param width   图片宽
     * @param height  图片高
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int rqsW, int rqsH, int width, int height) {
        int inSampleSize = 1;
        if (rqsW == 0 || rqsH == 0)
            return 1;
        if (height > rqsH || width > rqsW) {
            final int heightRatio = Math.round((float) height / (float) rqsH);
            final int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 图片的质量压缩（尺寸不变－即图片像素点不变，空间变小）
     *
     * @param bitmap
     * @return
     */
    public static Bitmap bitmapQualityCompress(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(bais, null, null);
    }

    /**
     * 传入一个Bitmap获得指定大小的图片
     *
     * @param bm        所要转换的 bitmap
     * @param newWidth  新的宽
     * @param newHeight 新的高
     * @return 指定宽高的bitmap
     */
    public static Bitmap getScaledBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        /**
         * 计算缩放比例
         */
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        /**
         *  取得想要缩放的matrix参数
         */
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        /**
         *  得到新的图片
         */
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    }

    /**
     * 拨打电话
     */
    @SuppressLint("MissingPermission")
    public static void makeCall(Context context, String phoneNumber) {
        Intent phoneIntent = new Intent(Intent.ACTION_CALL);
        phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        phoneIntent.setData(Uri.parse("tel:" + phoneNumber + ""));
        try {
            context.startActivity(phoneIntent);
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

    /**
     * 判断一个字符串是否是英文字母
     *
     * @param s
     * @return
     */
    public static boolean isEnglish(String s) {
        char c = s.charAt(0);
        int i = (int) c;
        if ((i >= 65 && i <= 90) || (i >= 97 && i <= 122)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取本地视频缩略图
     *
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoThumbnail(String videoPath) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(videoPath);
        return media.getFrameAtTime();
    }

    /**
     * 启动另一个 app
     *
     * @param context
     * @param packageName
     */
    public static void toStartOtherApplication(Context context, String packageName) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        List<ResolveInfo> resolveInfoList = context.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveInfoList.iterator().next();

        if (resolveinfo != null) {
            String package_name = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            ComponentName cn = new ComponentName(package_name, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    /**
     * KITKAT之前版本获取路径
     *
     * @param context
     * @param data
     * @return
     */
    public static String getPathByUri(Context context, Uri data) {
        String filename = null;
        if (data.getScheme().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(data, new String[]{"_data"}, null, null, null);
            if (cursor.moveToFirst()) {
                filename = cursor.getString(0);
            }
            cursor.close();
        } else if (data.getScheme().compareTo("file") == 0) {// file:///开头的uri
            filename = data.toString().replace("file://", "");// 替换file://
        }
        return filename;
    }


    /**
     * KITKAT之后版本获取路径
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
