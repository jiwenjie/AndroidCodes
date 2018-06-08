package com.kuky.restframework;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private File imgFile;

    @Override
    protected boolean enableTransparentStatus() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initActivity(Bundle savedInstanceState) {
        onRuntimePermissionsAsk(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
            @Override
            public void onGranted() {
                init();
            }

            @Override
            public void onDenied(List<String> deniedPermissions) {

            }
        });
    }

    @Override
    protected void setListener() {

    }

    private void init() {
        findViewById(R.id.get_categories).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .getCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.post_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .postNewCategory("new category")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.get_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .getCategory("15")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.update_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .updateCategory("15", "new name for category 22")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.delete_category).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .deleteCategory("15")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.EXPERT_BASE, null)
                        .create(RetrofitApi.class)
                        .userLogin("kuky_xs", "123456")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<UserLogin>() {
                            @Override
                            public void accept(UserLogin loginResult) throws Exception {
                                Log.w(TAG, "token: " + loginResult.getToken());
                                SharePreferenceUtils.saveString(MainActivity.this, "token", loginResult.getToken());
                                SharePreferenceUtils.saveInt(MainActivity.this, "pk", loginResult.getUser_id());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .reset("123456", "123456",
                                "123456",
                                "Token " + SharePreferenceUtils.getString(MainActivity.this, "token"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.get_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.avatar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: imgFile> " + imgFile);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
                MultipartBody.Part body = MultipartBody.Part.createFormData("avatar", imgFile.getName(), requestBody);

                String suffix = imgFile.getAbsolutePath().split("\\.")[1];

                RetrofitApiManager.provideClient(RetrofitApi.EXPERT_BASE, null)
                        .create(RetrofitApi.class)
                        .avatarUpload(body, suffix, "Token " + SharePreferenceUtils.getString(MainActivity.this, "token"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.w(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: pk = " + SharePreferenceUtils.getInt(MainActivity.this, "pk"));
                RetrofitApiManager.provideClient(RetrofitApi.EXPERT_BASE, null)
                        .create(RetrofitApi.class)
                        .getUserDetail(2, "Token " + SharePreferenceUtils.getString(MainActivity.this, "token"))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<User>() {
                            @Override
                            public void accept(User user) throws Exception {
                                Log.w(TAG, "accept: " + user.toString());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });

        findViewById(R.id.modified).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitApiManager.provideClient(RetrofitApi.DJANGO_BASE, null)
                        .create(RetrofitApi.class)
                        .modifiedUser(SharePreferenceUtils.getInt(MainActivity.this, "pk"),
                                "Token " + SharePreferenceUtils.getString(MainActivity.this, "token"),
                                "smile_xg", "12655870911", "65228976", "xx_xxx")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ResponseBody>() {
                            @Override
                            public void accept(ResponseBody responseBody) throws Exception {
                                Log.i(TAG, "accept: " + responseBody.string());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: ", throwable);
                            }
                        });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Bitmap bm = null;
                ContentResolver resolver = getContentResolver();
                try {
                    Uri originalUri = data.getData(); // 获得图片的uri
                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 得到bitmap图片
                    // 这里开始的第二部分，获取图片的路径：
                    String[] pro = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(originalUri, pro, null, null, null);
                    // 获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 将光标移至开头
                    cursor.moveToFirst();
                    // 最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    Log.e(TAG, "onActivityResult: path> " + path);
                    imgFile = new File(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
