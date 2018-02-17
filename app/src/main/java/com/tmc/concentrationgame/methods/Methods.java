package com.tmc.concentrationgame.methods;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tmc.concentrationgame.models.FlickrPhoto;
import com.tmc.concentrationgame.utilities.Parameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by sammy on 2/17/2018.
 */

public class Methods {
    public static String contstructImage(FlickrPhoto flickrPhoto, Parameters.ImageSizes imageSize) {
        String baseUrl = "https://farm";
        baseUrl += flickrPhoto.getFarm();
        baseUrl += ".staticflickr.com/";
        baseUrl += flickrPhoto.getServer();
        baseUrl += "/";
        baseUrl += flickrPhoto.getPhotoId();
        baseUrl += "_";
        baseUrl += flickrPhoto.getSecret();
        baseUrl += "_";
        baseUrl += imageSize;
        baseUrl += ".jpg";

        return baseUrl;
    }

    public static void downloadImages(Context context, ArrayList<FlickrPhoto> flickrPhotos, Parameters.ImageSizes imageSize, Parameters.Levels level) {
        for (int i = 0; i < flickrPhotos.size(); i++) {
            Picasso.with(context).load(contstructImage(flickrPhotos.get(i), imageSize)).into(picassoImageTarget(context, "imageDir", flickrPhotos.get(i).getPhotoId() + ".jpg"));
        }
    }

    private static Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    public static int getNumberImagesRequired(Parameters.Levels level) {
        if (level == Parameters.Levels.EASY)
            return 8;
        else if (level == Parameters.Levels.MEDIUM)
            return 12;
        else return 16;
    }
}
