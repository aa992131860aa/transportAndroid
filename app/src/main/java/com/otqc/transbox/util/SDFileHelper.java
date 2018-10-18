package com.otqc.transbox.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SDFileHelper {

    private Context context;

    public SDFileHelper() {
    }

    public SDFileHelper(Context context) {
        super();
        this.context = context;
    }

    //Glide保存图片

    public void savePicture(final String fileName, String url) {
        Glide.with(context).load(url).asBitmap().toBytes().into(new SimpleTarget<byte[]>() {
            @Override

            public void onResourceReady(byte[] bytes, GlideAnimation<? super byte[]> glideAnimation) {
                try {

                    savaFileToSD(fileName, bytes);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }

        });
    }

    //往SD卡写入文件的方法
    public void downloadLocal(final String fileName, final String u) {
        new Thread(
        ) {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection con = null;
                try {
                    url = new URL(u);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setReadTimeout(5000);
                    con.setDoInput(true);
                    //InputStream in = con.getInputStream();
                    File parent = Environment.getExternalStorageDirectory();
                    File file = new File(parent, fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    InputStream in = con.getInputStream();
                    Log.e("SDFileHelper", "run: path---" + file.getAbsolutePath());

                    byte ch[] = new byte[2 * 1024];
                    int len;
                    if (fos != null) {
                        while ((len = in.read(ch)) != -1) {
                            fos.write(ch, 0, len);
                        }
                        in.close();
                        fos.close();
                    }
            /*BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            BufferedWriter bw = null;
            String s;
            while ((s = br.readLine()) != null){
                bw = new BufferedWriter(new OutputStreamWriter(fos));
                //以字符串的形式写入数据，无法读取图片
                bw.write(s);
            }
            br.close();
            bw.close();*/
//                    final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (bitmap != null) {
//                                iv.setImageBitmap(bitmap);
//                            }
//                        }
//                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    public void download(final String fileName, String url) {
        //获得图片的地址


        //Target
        Target target = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // String imageName = System.currentTimeMillis() + ".png";

                String filePath = null;
                try {
                    filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/boxNo/" + fileName;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File dir1 = new File(filePath);
                if (!dir1.exists()) {
                    dir1.mkdirs();
                }


                FileOutputStream ostream = null;
                try {
                    ostream = new FileOutputStream(dir1);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        //Picasso下载
        Picasso.get().load(url).into(target);

    }


    public void savaFileToSD(String filename, byte[] bytes) throws Exception {
        //如果手机已插入sd卡,且app具有读写sd卡的权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/boxNo";
            File dir1 = new File(filePath);
            if (!dir1.exists()) {
                dir1.mkdirs();
            }
            filename = filePath + "/" + filename;
            //这里就不要用openFileOutput了,那个是往手机内存中写数据的
            FileOutputStream output = new FileOutputStream(filename);
            output.write(bytes);
            //将bytes写入到输出流中
            output.close();
            //关闭输出流
//            Toast.makeText(context, "图片已成功保存到" + filePath, Toast.LENGTH_SHORT).show();
//            Log.e("开启:", filename);
        } else Toast.makeText(context, "SD卡不存在或者不可读写", Toast.LENGTH_SHORT).show();
    }

    public String getFilePath(String fileName) {
        try {
            return Environment.getExternalStorageDirectory().getCanonicalPath() + "/boxNo/" + fileName;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}