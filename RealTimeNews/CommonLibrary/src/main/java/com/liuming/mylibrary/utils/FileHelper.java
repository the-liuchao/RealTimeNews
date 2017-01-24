package com.liuming.mylibrary.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * Created by sll on 2015/3/7.
 */
public class FileHelper<T> {

    private Context context;

    public FileHelper(Context context) {
        this.context = context;
    }

    public boolean hasSDCard() {
        boolean mHasSDcard = false;
        if (Environment.MEDIA_MOUNTED.endsWith(Environment.getExternalStorageState())) {
            mHasSDcard = true;
        } else {
            mHasSDcard = false;
        }

        return mHasSDcard;
    }

    public String getSdcardPath() {

        if (hasSDCard())
            return Environment.getExternalStorageDirectory().getAbsolutePath();

        return "/sdcard/";
    }

    /**
     * 将集合写入sd卡
     * @param fileName 文件名
     * @param list  集合
     * @return true 保存成功
     */
    public boolean writeListIntoSDcard(String fileName,List<T> list){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
            File sdFile  = new File(sdCardDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(sdFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(list);//写入
                fos.close();
                oos.close();
                return true;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }
    /**
     * 读取sd卡对象
     * @param fileName 文件名
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> readListFromSdCard(String fileName){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //检测sd卡是否存在
            List<T> list;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File sdFile = new File(sdCardDir,fileName);
            try {
                FileInputStream fis = new FileInputStream(sdFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                list = (List<T>) ois.readObject();
                fis.close();
                ois.close();
                return list;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (OptionalDataException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }

    public String stringFromAssetsFile(String fileName) {
        AssetManager manager = context.getAssets();
        InputStream file;
        try {
            file = manager.open(fileName);
            byte[] data = new byte[file.available()];
            file.read(data);
            file.close();
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String length(long length) {
        String str = "0B";
        if (length == 0) {
            return str;
        }
        if (length < 1048576) {
            return Math.round(((double) length) / 1024) + "KBbyte";
        }
        return Math.round(((double) length) / 1048576) + "MBbyte";
    }


    /**
     * 复制assets文件到指定目录
     *
     * @param fileName 文件名
     * @param filePath 目录
     */
    public void copyAssets(String fileName, String filePath) {
        InputStream inputStream;
        try {
            inputStream = context.getResources().getAssets().open(fileName);// assets文件夹下的文件
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "/" + fileName);// 保存到本地的文件夹下的文件
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean copy(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            return false;
        }
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(oldFile);
            outputStream = new FileOutputStream(newFile);
            byte[] buffer = new byte[4096];
            while (inputStream.read(buffer) != -1) {
                outputStream.write(buffer);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean exist(String url) {
        File file = new File(url);
        return file.exists();
    }


    public void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


