package com.example.mimurayuuya.mydiary;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mimurayuuya on 2017/07/31.
 */

public class MyUtils {

    public static Bitmap getImageFromByte(byte[] bytes){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes,0,bytes.length,opt);
        int bitmapSize = 1;
        if ((opt.outHeight * opt.outWidth) > 500000){
            double outSize = (double) (opt.outHeight * opt.outWidth) / 500000;
            bitmapSize = (int) (Math.sqrt(outSize) + 1);
        }

        opt.inJustDecodeBounds = false;
        opt.inSampleSize = bitmapSize;
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length,opt);
        return bmp;
    }

    public static byte[] getByteFromImage(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Bitmap getImageFromStream(ContentResolver resolver,Uri uri) throws IOException{
        InputStream in;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        in = resolver.openInputStream(uri);
        BitmapFactory.decodeStream(in,null,opt);
        in.close();
        int bitmapSize = 1;
        if((opt.outHeight * opt.outWidth) > 500000){
            double outSize = (double) (opt.outHeight * opt.outWidth) / 500000;
            bitmapSize = (int)(Math.sqrt(outSize) + 1);
        }

        opt.inJustDecodeBounds = false;
        opt.inSampleSize = bitmapSize;
        in = resolver.openInputStream(uri);
        Bitmap bmp = BitmapFactory.decodeStream(in,null,opt);
        in.close();
        return bmp;
    }

}
