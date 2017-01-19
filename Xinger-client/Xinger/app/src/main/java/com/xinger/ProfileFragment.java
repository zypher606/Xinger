package com.xinger;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xinger.model.Response;
import com.xinger.model.User;
import com.xinger.network.NetworkUtil;
import com.xinger.utils.Constants;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ProfileFragment extends Fragment implements TextureView.SurfaceTextureListener{



    /***********************************************************/
    /*           Video constants initiated                     */
    /***********************************************************/
    private static final String TAG = MainActivity.class.getName();
    private MediaPlayer mMediaPlayer;
    private TextureView mTextureView;
    private float mVideoWidth;
    private float mVideoHeight;
    private static final String FILE_NAME = "cover_x.MP4";



    //Fetching profile data
    private TextView profileName;
    private String mEmail;
    private SharedPreferences mSharedPreferences;
    private CompositeSubscription mSubscriptions;
    private String mToken;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, null);




        mSubscriptions = new CompositeSubscription();
        calculateVideoSize();
        initView(view);

        initSharedPreferences();
        loadProfile();

        return view;

    }


    private void calculateVideoSize() {
        try {
            AssetFileDescriptor afd = getActivity().getAssets().openFd(FILE_NAME);
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(
                    afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            String height = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever
                    .extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            mVideoHeight = Float.parseFloat(height);
            mVideoWidth = Float.parseFloat(width);






        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (NumberFormatException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void initView(View view) {

        
//        View view = inflater.inflate(R.layout.fragment_profile, null);
        
        mTextureView = (TextureView) view.findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);



    

        Display display =getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int coverFrameHeight = Math.round((mVideoHeight / mVideoWidth) * (size.x));
        int coverFrameWidth = size.x;


        FrameLayout rootView = (FrameLayout) view.findViewById(R.id.rootView);
        ViewGroup.LayoutParams cover_layer_param = rootView.getLayoutParams();
        cover_layer_param.height = coverFrameHeight;



        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(coverFrameWidth, coverFrameHeight));





        //Initiate profile details
        profileName = (TextView) view.findViewById(R.id.profileUserName);



    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
        Log.d("token", mToken);
        Log.d("My email", mEmail);
    }


    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }


    private void handleResponse(User user) {
        Log.d("Ftched username", user.getName());
        profileName.setText(user.getName());
    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
//                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

//            showSnackBarMessage("Network Error !");
        }
    }
//
//    private void showSnackBarMessage(String message) {
//
//        Snackbar.make(getActivity().findViewById(R.id.activity_profile),message,Snackbar.LENGTH_SHORT).show();
//
//    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        mSubscriptions.unsubscribe();
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
        Surface surface = new Surface(surfaceTexture);

        try {
            AssetFileDescriptor afd = getActivity().getAssets().openFd(FILE_NAME);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource("http://127.0.0.1:3000/");
//            mMediaPlayer
//                    .setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.setSurface(surface);
            mMediaPlayer.setLooping(true);

            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });

        } catch (IllegalArgumentException e) {
            Log.d(TAG, e.getMessage());
        } catch (SecurityException e) {
            Log.d(TAG, e.getMessage());
        } catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    
    

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


}
