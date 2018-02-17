package com.tmc.concentrationgame.methods;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tmc.concentrationgame.interfaces.ImageDownloadedInterface;
import com.tmc.concentrationgame.models.FlickrPhoto;
import com.tmc.concentrationgame.utilities.Parameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import kotlin.jvm.Synchronized;

/**
 * Created by sammy on 2/17/2018.
 */

public class Methods {
    //Constructing image url for the flickr api
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

    //Firing the picasso target for each image
    @Synchronized
    public static void downloadImages(final Context context, final ArrayList<FlickrPhoto> flickrPhotos, final Parameters.ImageSizes imageSize, Parameters.Levels level, ImageDownloadedInterface imageDownloadedInterface) {
        for (int i = 0; i < flickrPhotos.size(); i++) {
            Picasso.with(context).load(contstructImage(flickrPhotos.get(i), imageSize)).into(
                    getTarget(context, "imageDir", flickrPhotos.get(i).getPhotoId() + ".jpg", imageDownloadedInterface
                    )
            );

        }
    }

    private static Target getTarget(final Context context, final String imageDir, final String imageName, final ImageDownloadedInterface imageDownloadedInterface) {

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        ContextWrapper cw = new ContextWrapper(context);
                        File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir

                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            imageDownloadedInterface.onComplete();

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
        return target;
    }

    //For each difficulty a number of images is assigned
    public static int getNumberImagesRequired(Parameters.Levels level) {
        if (level == Parameters.Levels.EASY)
            return 8;
        else if (level == Parameters.Levels.MEDIUM)
            return 12;
        else return 16;
    }
}
