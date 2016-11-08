package com.learn2crack;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, null);

        VideoView coverVideo = (VideoView) view.findViewById(R.id.cover_video);

        // VideoView mVideoView = new VideoView(this);

        String uriPath = "android.resource://com.learn2crack/" + R.raw.cover;
        Uri uri2 = Uri.parse(uriPath);
        coverVideo.setVideoURI(uri2);
        coverVideo.requestFocus();
        coverVideo.start();

        return view;

    }


}
