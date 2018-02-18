package com.tmc.concentrationgame.methods;

import android.content.Context;

import com.tmc.concentrationgame.models.FlickrPhoto;
import com.tmc.concentrationgame.utilities.Parameters;

import java.util.ArrayList;

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
    public static boolean downloadImages(final Context context, final ArrayList<FlickrPhoto> flickrPhotos, final Parameters.ImageSizes imageSize, Parameters.Levels level) {
        for (int i = 0; i < flickrPhotos.size(); i++) {
            flickrPhotos.get(i).setPhotoUrl(contstructImage(flickrPhotos.get(i), imageSize));
        }
        return true;
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
