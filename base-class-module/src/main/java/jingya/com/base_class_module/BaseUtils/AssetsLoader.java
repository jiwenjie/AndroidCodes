package jingya.com.base_class_module.BaseUtils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author kuky
 * @description asserts 文件加载工具类
 */
public class AssetsLoader {

    public static String getTextFromAssets(Context context, String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getText(inputStream);
    }

    private static String getText(InputStream inputStream) {
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder result = new StringBuilder();
        try {
            inputReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ;
            }


            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return result.toString();
    }


    public static Bitmap getImageFromAssets(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

}
