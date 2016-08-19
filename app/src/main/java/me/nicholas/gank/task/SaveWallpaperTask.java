package me.nicholas.gank.task;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import me.nicholas.gank.App;
import me.nicholas.gank.R;
import me.nicholas.gank.utils.DateUtils;
import me.nicholas.gank.utils.SDCardUtils;
import me.nicholas.gank.utils.ToastUtils;

/**
 * Created by Nicholas on 2016/7/11.
 */
public class SaveWallpaperTask extends AsyncTask<String, Void, Bitmap> {

    private File file;
    private String flag;

    public static final String SAVE = "save";
    public static final String WALLPAPER = "wallpaper";

    @Override
    protected void onPreExecute() {

        String dir;

        if (SDCardUtils.isSDCardEnable()) {
            dir = SDCardUtils.getSDCardPath();
        } else {
            dir = SDCardUtils.getRootDirectoryPath();
        }

        File folder = new File(dir, "Meizhi");

        //建立文件夹
        if (!folder.exists()) {
            folder.mkdir();
        }

        file = new File(folder, "MZ" + DateUtils.getNowTime2Save() + ".JPG");
    }


    @Override
    protected Bitmap doInBackground(String... params) {

        flag = params[1];

        Bitmap bitmap = null;

        try {
            bitmap = Picasso.with(App.getContext()).load(params[0]).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        switch (flag) {
            case SAVE:
                if (bitmap != null) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        //100为不压缩
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();

                        //通知相册更新
                        Uri uri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                        App.getContext().sendBroadcast(intent);

                        ToastUtils.Short("已保存到" + file.getAbsolutePath());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    ToastUtils.Short(R.string.save_failure);
                }

                break;

            case WALLPAPER:
                if (bitmap != null) {
                    WallpaperManager wpManager = WallpaperManager.getInstance(App.getContext());
                    try {
                        wpManager.setBitmap(bitmap);
                        ToastUtils.Short(R.string.set_wallpaper_succeed);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtils.Short(R.string.set_wallpaper_failure);
                }

                break;
        }

    }

}
