package com.tmc.concentrationgame.views;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.tmc.concentrationgame.R;
import com.tmc.concentrationgame.models.FlickrPhoto;

import java.io.File;

/**
 * Created by sammy on 2/17/2018.
 */

public class MyView extends FrameLayout implements View.OnClickListener {
    private boolean touchEnabled = true;
    @Override
    public void onClick(View view) {
        if(myFlickrPhoto.getClickable()) {
            invalidate();
            animateItem();
            if (toggledListener != null) {
                toggledListener.OnToggled(this, touchOn);
            }
        }
    }

    public interface OnToggledListener {
        void OnToggled(MyView v, boolean touchOn);
    }

    boolean touchOn;
    private OnToggledListener toggledListener;
    private FlickrPhoto myFlickrPhoto;
    private ImageView belowImage;
    private ImageView topImage;
    private Context context;
    private ContextWrapper cw ;
    private File directory ;
    private AnimatorSet mSetRightOut;
    private AnimatorSet mSetLeftIn;
    private boolean mIsBackVisible = true;
    private Handler handler = new Handler();
    public Runnable hideBackImage = new Runnable() {
        @Override
        public void run() {
            topImage.setVisibility(INVISIBLE);
        }
    };
    public Runnable showBackImage = new Runnable() {
        @Override
        public void run() {
            topImage.setVisibility(VISIBLE);

        }
    };

    public MyView(Context context, int x, int y, FlickrPhoto flickrPhoto) {
        super(context);
        this.myFlickrPhoto = flickrPhoto;
        this.context = context;
        cw = new ContextWrapper(context);
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        LayoutInflater.from(context).inflate(R.layout.row_game, this, true);

        touchOn = false;
        loadAnimations();
        init(flickrPhoto);
    }
    private void init(FlickrPhoto flickrPhoto) {
        belowImage =  findViewById(R.id.below_image);
        topImage =  findViewById(R.id.top_image);
        topImage.setOnClickListener(this);
        File myImageFile = new File(directory, flickrPhoto.getPhotoId()+".jpg");
        Picasso.with(context).load(myImageFile).into(belowImage);
        flickrPhoto.setClickable(true);
        topImage.setVisibility(INVISIBLE);
    }
    private void loadAnimations() {
        mSetRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.out_animation);
        mSetLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.in_animation);
    }
    public void flipCard() {
        if (!mIsBackVisible) {
            mSetRightOut.setTarget(belowImage);
            mSetLeftIn.setTarget(topImage);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = true;
            handler.postDelayed(hideBackImage, getResources().getInteger(R.integer.anim_length_half));
        } else {
            mSetRightOut.setTarget(topImage);
            mSetLeftIn.setTarget(belowImage);
            mSetRightOut.start();
            mSetLeftIn.start();
            mIsBackVisible = false;
            handler.postDelayed(showBackImage, getResources().getInteger(R.integer.anim_length_half));

        }

    }


    private void animateItem(){
        flipCard();
    }


    public void setOnToggledListener(OnToggledListener listener){
        toggledListener = listener;
    }

    public String getImageId(){
        return myFlickrPhoto.getPhotoId();
    }

    public void closeOpenedImages(){
        if (mIsBackVisible && (myFlickrPhoto.getCompleted()==null || !myFlickrPhoto.getCompleted()))
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    flipCard();
                }
            }, getResources().getInteger(R.integer.anim_length));
    }
    public void setCompleted() {
        myFlickrPhoto.setCompleted(true);
    }
    public void setTouchDisabled(){
        myFlickrPhoto.setClickable(false);
    }
    public void setTouchEnabled(){
        myFlickrPhoto.setClickable(true);

    }
}
