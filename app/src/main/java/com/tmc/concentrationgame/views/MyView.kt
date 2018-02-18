package com.tmc.concentrationgame.views

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

import com.squareup.picasso.Picasso
import com.tmc.concentrationgame.R
import com.tmc.concentrationgame.interfaces.OnToggledListener
import com.tmc.concentrationgame.models.FlickrPhoto

import java.io.File

/**
 * Created by sammy on 2/17/2018.
 */

class MyView(@get:JvmName("getContext_") private val context: Context,  private val myFlickrPhoto: FlickrPhoto,
             @get:JvmName("getHandler_") private val handler: Handler) : FrameLayout(context), View.OnClickListener {

    private var toggledListener: OnToggledListener? = null
    private var belowImage: ImageView? = null
    private var topImage: ImageView? = null
    private val cw: ContextWrapper = ContextWrapper(context)
    private val directory: File
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var mIsBackVisible = true
    //Hiding the top image
    var hideBackImage: Runnable = Runnable { topImage!!.visibility = View.INVISIBLE }
    //Showing the top image
    var showBackImage: Runnable = Runnable { topImage!!.visibility = View.VISIBLE }

    val imageId: String
        get() = myFlickrPhoto.photoId
    //OnClick of each image
    override fun onClick(view: View) {
        if (myFlickrPhoto.clickable!!) {
            invalidate()
            animateItem()
            if (toggledListener != null) {
                toggledListener!!.OnToggled(this)
            }
        }
    }
    //Constructor
    init {
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        LayoutInflater.from(context).inflate(R.layout.row_game, this, true)

        loadAnimations()
        init(myFlickrPhoto)
    }
    //Constructor
    private fun init(flickrPhoto: FlickrPhoto) {
        belowImage = findViewById(R.id.below_image)
        topImage = findViewById(R.id.top_image)
        topImage!!.setOnClickListener(this)
        val myImageFile = File(directory, flickrPhoto.photoId + ".jpg")
        Picasso.with(context).load(flickrPhoto.photoUrl).into(belowImage)
        flickrPhoto.clickable = true
        topImage!!.visibility = View.INVISIBLE
    }
    //Loading the animation for the image
    private fun loadAnimations() {
        mSetRightOut = AnimatorInflater.loadAnimator(context, R.animator.out_animation) as AnimatorSet
        mSetLeftIn = AnimatorInflater.loadAnimator(context, R.animator.in_animation) as AnimatorSet
    }
    //Executing the animation
    fun flipCard() {
        if (!mIsBackVisible) {
            mSetRightOut!!.setTarget(belowImage)
            mSetLeftIn!!.setTarget(topImage)
            mSetRightOut!!.start()
            mSetLeftIn!!.start()
            mIsBackVisible = true
            handler.postDelayed(hideBackImage, resources.getInteger(R.integer.anim_length_half).toLong())
        } else {
            mSetRightOut!!.setTarget(topImage)
            mSetLeftIn!!.setTarget(belowImage)
            mSetRightOut!!.start()
            mSetLeftIn!!.start()
            mIsBackVisible = false
            handler.postDelayed(showBackImage, resources.getInteger(R.integer.anim_length_half).toLong())

        }

    }

    private fun animateItem() {
        flipCard()
    }

    //Setter for the interface
    fun setOnToggledListener(listener: OnToggledListener) {
        toggledListener = listener
    }
    //Closing opened images if they do not match
    fun closeOpenedImages() {
        if (mIsBackVisible && (myFlickrPhoto.completed == null || (!myFlickrPhoto.completed!!)))
            handler.postDelayed({ flipCard() }, resources.getInteger(R.integer.anim_length).toLong())
    }
    //Changing image state so it will remain opened
    fun setCompleted() {
        myFlickrPhoto.completed = true
    }
    //Disabling touch while animation
    fun setTouchDisabled() {
        myFlickrPhoto.clickable = false
    }
    //Enabling touch after animation has ended
    fun setTouchEnabled() {
        myFlickrPhoto.clickable = true

    }
}