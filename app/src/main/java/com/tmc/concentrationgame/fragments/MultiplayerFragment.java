package com.tmc.concentrationgame.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tmc.concentrationgame.R;
import com.tmc.concentrationgame.activities.MainActivity;
import com.tmc.concentrationgame.interfaces.OnToggledListener;
import com.tmc.concentrationgame.methods.Methods;
import com.tmc.concentrationgame.models.FlickrJsonResponse;
import com.tmc.concentrationgame.models.FlickrPhotoWrapper;
import com.tmc.concentrationgame.server.ApiUtils;
import com.tmc.concentrationgame.server.RetrofitServices;
import com.tmc.concentrationgame.utilities.Parameters;
import com.tmc.concentrationgame.views.MyView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sammy on 2/17/2018.
 */

public class MultiplayerFragment extends Fragment implements OnToggledListener {

    private TextView firstPlayerScoreTextView;
    private TextView secondPlayerScoreTextView;

    
    private Boolean playerOneTurn = true;
    private Parameters.Levels level;
    private RetrofitServices mService;
    private Handler handler = new Handler();
    private GridLayout myGridLayout;
    private ArrayList<MyView> myViews = new ArrayList<>();
    private String firstImageId, secondImageId;
    private FlickrPhotoWrapper flickrPhotoWrapper;
    private Runnable startGame;

    private int numberOfImagesRequired;
    private int numOfCol;
    private int numOfRow = 4;
    private int MARGIN = 4;
    private int gridWidth;
    private int firstPlayerScore = 0;
    private int secondPlayerScore = 0;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    public MultiplayerFragment() {
        // Required empty public constructor
    }

    public static MultiplayerFragment newInstance(Parameters.Levels level) {
        MultiplayerFragment fragment = new MultiplayerFragment();
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
        View view = inflater.inflate(R.layout.fragment_multiplayer, container, false);
        myGridLayout = (GridLayout)view.findViewById(R.id.mygrid);
        firstPlayerScoreTextView = (TextView) view.findViewById(R.id.firstPlayerScore);
        secondPlayerScoreTextView = (TextView) view.findViewById(R.id.secondPlayerScore);
        secondPlayerScoreTextView.setText(getResources().getString(R.string.secondPlayerScore) + String.valueOf(secondPlayerScore));
        firstPlayerScoreTextView.setText(getResources().getString(R.string.firstPlayerScore) + String.valueOf(firstPlayerScore));


        return view;
    }

    @Override
    public void OnToggled(MyView v) {

        //get the id string
        String idString = v.getImageId();
        if (!TextUtils.isEmpty(firstImageId)) {
            for (int i = 0; i < myViews.size(); i++) {
                myViews.get(i).setTouchDisabled();
            }
            secondImageId = idString;
            compare(firstImageId, secondImageId);
            firstImageId = "";
            secondImageId = "";
        } else {
            firstImageId = idString;
        }


    }
    private void compare(String firstImageId, String secondImageId){
        if (firstImageId.equals(secondImageId)){
            for (int i =0; i<flickrPhotoWrapper.getPhoto().size(); i++){
                if(flickrPhotoWrapper.getPhoto().get(i).getPhotoId().equals(firstImageId)){
                    flickrPhotoWrapper.getPhoto().get(i).setCompleted(true);
                    myViews.get(i).setCompleted();
                }
            }
            int score = 0;
            switch (level){
                case EASY:
                    score++;
                    break;
                case MEDIUM:
                    score+=2;
                    break;
                case HARD:
                    score+=3;
                    break;
            }
            if(playerOneTurn) {
                firstPlayerScore+=score;
                firstPlayerScoreTextView.setText(getResources().getString(R.string.firstPlayerScore) + String.valueOf(firstPlayerScore));

            }else{
                secondPlayerScore+=score;
                secondPlayerScoreTextView.setText(getResources().getString(R.string.secondPlayerScore) + String.valueOf(secondPlayerScore));
            }
            if (firstPlayerScore + secondPlayerScore == numberOfImagesRequired) {
                if(firstPlayerScore>secondPlayerScore) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.player_one_won), Toast.LENGTH_SHORT).show();
                    showEndGameDialog(true);
                }
                else if (firstPlayerScore<secondPlayerScore) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.player_two_won), Toast.LENGTH_SHORT).show();
                    showEndGameDialog(false);
                }
                else if(firstPlayerScore==secondPlayerScore) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.tie),Toast.LENGTH_SHORT).show();
                    ((MainActivity)getActivity()).showPlayAgainDialog(false);
                }
            }
        }else {

            for (int i =0; i<myViews.size(); i++){
                myViews.get(i).closeOpenedImages();
                myViews.get(i).setTouchDisabled();
            }
            if (playerOneTurn)
                playerOneTurn = false;
            else playerOneTurn = true;
            showPlayerTurn(playerOneTurn);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < myViews.size(); i++) {
                    myViews.get(i).setTouchEnabled();
                }
            }
        }, getContext().getResources().getInteger(R.integer.anim_length));
    }
    private void showPlayerTurn(boolean playerOneTurn){
        if (playerOneTurn) {
            Toast.makeText(getActivity(), getResources().getString(R.string.player_one_turn), Toast.LENGTH_SHORT).show();
            firstPlayerScoreTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
            secondPlayerScoreTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
        }
        else{
            Toast.makeText(getActivity(), getResources().getString(R.string.player_two_turn), Toast.LENGTH_SHORT).show();
            firstPlayerScoreTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
            secondPlayerScoreTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        }

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
        myViews = new ArrayList<>(numOfCol * numOfRow);
        for (int yPos = 0; yPos < numOfRow; yPos++) {
            for (int xPos = 0; xPos < numOfCol; xPos++) {
                final MyView tView = new MyView(getActivity(), flickrPhotoWrapper.getPhoto().get((yPos * numOfCol) + xPos),handler);
                tView.setOnToggledListener(this);

                myViews.add(tView);
                myGridLayout.addView(tView);
                handler.postDelayed(tView.getHideBackImage(), getResources().getInteger(R.integer.anim_length_half));
                startGame = new Runnable() {
                    @Override
                    public void run() {
                        tView.flipCard();

                    }
                };
                handler.postDelayed(startGame, getResources().getInteger(R.integer.loading_time));
            }
        }
        onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int pWidth = myGridLayout.getWidth();
                int pHeight = myGridLayout.getHeight();

                if (level == Parameters.Levels.EASY) {
                    pWidth = gridWidth * 4 + 20;
                } else if (level == Parameters.Levels.MEDIUM) {
                    pWidth = gridWidth * 6 + 30;
                }


                int w = pWidth / numOfCol;
                int h = pHeight / numOfRow;

                for (int yPos = 0; yPos < numOfRow; yPos++) {
                    for (int xPos = 0; xPos < numOfCol; xPos++) {
                        GridLayout.LayoutParams params =
                                (GridLayout.LayoutParams) myViews.get(yPos * numOfCol + xPos).getLayoutParams();
                        params.width = w - 2 * MARGIN;
                        params.height = h - 2 * MARGIN;
                        params.setMargins(MARGIN, MARGIN, MARGIN, MARGIN);
                        if (myViews != null && !myViews.isEmpty())
                            myViews.get(yPos * numOfCol + xPos).setLayoutParams(params);
                    }
                }

            }
        };
        myGridLayout.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);


    }

    private void showEndGameDialog(boolean firstPlayerWon) {
        if (getActivity() != null) {
            if (firstPlayerWon)
            ((MainActivity) getActivity()).showNameDialog(firstPlayerScore);
            else
            ((MainActivity) getActivity()).showNameDialog(secondPlayerScore);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null)
            handler.removeCallbacks(startGame);
    }

}
