package com.tmc.concentrationgame.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmc.concentrationgame.R;
import com.tmc.concentrationgame.methods.Methods;
import com.tmc.concentrationgame.models.FlickrJsonResponse;
import com.tmc.concentrationgame.models.FlickrPhotoWrapper;
import com.tmc.concentrationgame.server.ApiUtils;
import com.tmc.concentrationgame.server.RetrofitServices;
import com.tmc.concentrationgame.utilities.Parameters;
import com.tmc.concentrationgame.views.MyView;

import java.util.Collections;
import java.util.Random;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sammy on 2/17/2018.
 */

public class SinglePlayerFragment extends Fragment implements MyView.OnToggledListener {

    private Parameters.Levels level;
    private RetrofitServices mService;
    private int numberOfImagesRequired;
    private Handler handler = new Handler();
    private GridLayout myGridLayout;
    private TextView scoreTextView;
    private MyView[] myViews;
    private String firstImageId, secondImageId;
    private int score = 0;
    private FlickrPhotoWrapper flickrPhotoWrapper;
    private CountDownTimer countDownTimer;
    private ProgressBar progressBar;
    private TextView timeTextView;
    private Runnable startGame;
    private int numOfCol;
    private int numOfRow = 4;
    private int MARGIN = 4;
    private int gridWidth;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private long seconds = 0;
    public SinglePlayerFragment() {
        // Required empty public constructor
    }

    public static SinglePlayerFragment newInstance(Parameters.Levels level) {
        SinglePlayerFragment fragment = new SinglePlayerFragment();
        Bundle args = new Bundle();
        args.putSerializable(Parameters.Companion.getLEVEL_IDENTIFIER(), level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            level = (Parameters.Levels) getArguments().getSerializable(Parameters.Companion.getLEVEL_IDENTIFIER());
        }
        numberOfImagesRequired = Methods.getNumberImagesRequired(level);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_player, container, false);
        progressBar = view.findViewById(R.id.progressBar);

        myGridLayout = view.findViewById(R.id.mygrid);
        scoreTextView = view.findViewById(R.id.score);
        timeTextView = view.findViewById(R.id.timeTextView);
        scoreTextView.setText(getResources().getString(R.string.score) + String.valueOf(score));

        return view;
    }

    @Override
    public void OnToggled(MyView v, boolean touchOn) {

        //get the id string
        String idString = v.getImageId();
        if (!TextUtils.isEmpty(firstImageId)) {
            for (int i = 0; i < myViews.length; i++) {
                myViews[i].setTouchDisabled();
            }
            secondImageId = idString;
            myGridLayout.setClickable(false);
            compare(firstImageId, secondImageId);
            firstImageId = "";
            secondImageId = "";
        } else {
            firstImageId = idString;
        }


    }

    private void compare(String firstImageId, String secondImageId) {
        if (firstImageId.equals(secondImageId)) {
            for (int i = 0; i < flickrPhotoWrapper.getPhoto().size(); i++) {
                if (flickrPhotoWrapper.getPhoto().get(i).getPhotoId().equals(firstImageId)) {
                    flickrPhotoWrapper.getPhoto().get(i).setCompleted(true);
                    myViews[i].setCompleted();
                }
            }
            score++;
            scoreTextView.setText(getResources().getString(R.string.score) + String.valueOf(score));
            if (score == numberOfImagesRequired) {
                showEndGameDialog(true);
            }
        } else {

            for (int i = 0; i < myViews.length; i++) {
                myViews[i].closeOpenedImages();
                myViews[i].setTouchDisabled();

            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < myViews.length; i++) {
                    myViews[i].setTouchEnabled();
                }
            }
        }, 400);
//        myGridLayout.setClickable(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mService = ApiUtils.INSTANCE.getSoService();
        gridWidth = (int) getResources().getDimension(R.dimen.unit_92);

        getPhotos();
    }

    public void getPhotos() {
        mService.getPhotos(numberOfImagesRequired, Parameters.Companion.getONLY_PHOTOS()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<FlickrJsonResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.e("hi", "hi");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("hi", "hi");
                    }

                    @Override
                    public void onNext(FlickrJsonResponse flickrPhotoWrapper) {
                        displayGame(flickrPhotoWrapper.getPhotos());
                    }
                });
    }

    private void displayGame(FlickrPhotoWrapper flickrPhotoWrapper) {
        this.flickrPhotoWrapper = flickrPhotoWrapper;
        Methods.downloadImages(getActivity(), flickrPhotoWrapper.getPhoto(), Parameters.ImageSizes.q, level);

        flickrPhotoWrapper.getPhoto().addAll(flickrPhotoWrapper.getPhoto());

        long seed = System.nanoTime();
        Collections.shuffle(flickrPhotoWrapper.getPhoto(), new Random(seed));


        numOfCol = flickrPhotoWrapper.getPhoto().size() / 4;
        myGridLayout.setColumnCount(numOfCol);
        myGridLayout.setRowCount(numOfRow);
        myViews = new MyView[numOfCol * numOfRow];
        for (int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                final MyView tView = new MyView(getActivity(), xPos, yPos, flickrPhotoWrapper.getPhoto().get((yPos * numOfCol) + xPos));
                tView.setOnToggledListener(this);

                myViews[yPos * numOfCol + xPos] = tView;
                myGridLayout.addView(tView);
                handler.postDelayed(tView.hideBackImage, getResources().getInteger(R.integer.anim_length_half));
                startGame = new Runnable() {
                    @Override
                    public void run() {
                        tView.flipCard();
                        if (isAdded())
                            startTimer(getResources().getInteger(R.integer.timer));
                    }
                };
                handler.postDelayed(startGame, getResources().getInteger(R.integer.loading_time));
            }
        }
        onGlobalLayoutListener =  new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {


                int pWidth = myGridLayout.getWidth();
                int pHeight = myGridLayout.getHeight();

                if (level == Parameters.Levels.EASY) {
                    pWidth = gridWidth * 4 + 20;
                }else if(level == Parameters.Levels.MEDIUM){
                    pWidth = gridWidth * 6 + 30;
                }


                int w = pWidth / numOfCol;
                int h = pHeight / numOfRow;

                for (int yPos = 0; yPos < numOfRow; yPos++) {
                    for (int xPos = 0; xPos < numOfCol; xPos++) {
                        GridLayout.LayoutParams params =
                                (GridLayout.LayoutParams) myViews[yPos * numOfCol + xPos].getLayoutParams();
                        params.width = w - 2 * MARGIN;
                        params.height = h - 2 * MARGIN;
                        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                        myViews[yPos * numOfCol + xPos].setLayoutParams(params);
                    }
                }

            }
        };
        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);


    }

    private void showEndGameDialog(boolean isWon) {
        countDownTimer.cancel();

        if (getActivity() != null) {
            String title = getResources().getString(R.string.time_is_up);
            if (isWon) {

                title = getResources().getString(R.string.you_win);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(title)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startGameTask();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }
    }

    private void startGameTask() {

        score = 0;
        firstImageId = "";
        secondImageId = "";
        myViews = new MyView[numOfCol * numOfRow];
        mService = ApiUtils.INSTANCE.getSoService();
        flickrPhotoWrapper = null;
        myGridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        getPhotos();
    }

    private void startTimer(final int minuti) {
        countDownTimer = new CountDownTimer(60 * minuti * 1000, 1000) {
            // 500 means, onTick function will be called at every 500 milliseconds

            @Override
            public void onTick(long leftTimeInMilliseconds) {
                seconds = leftTimeInMilliseconds / 1000;
                progressBar.setProgress((int) seconds);
                timeTextView.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
//                     format the textview to show the easily readable format

            }

            @Override
            public void onFinish() {
                showEndGameDialog(false);
                timeTextView.setText("STOP");

            }
        }.start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (handler != null)
            handler.removeCallbacks(startGame);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null)
            countDownTimer.cancel();
        if (handler != null)
            handler.removeCallbacks(startGame);
    }
}
