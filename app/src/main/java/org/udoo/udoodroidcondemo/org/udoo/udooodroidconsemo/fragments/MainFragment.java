package org.udoo.udoodroidcondemo.org.udoo.udooodroidconsemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.udoo.udoodroidcondemo.MainActivity;
import org.udoo.udoodroidcondemo.R;


public class MainFragment extends Fragment {

    private TextView debugTextView;
    private ImageButton voiceButton;
    private ImageView faceImage;
    private Animation animationFadeIn;
    private Animation animationFadeOut;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        // Inflate the layout for this fragment
        debugTextView = (TextView) view.findViewById(R.id.textView);
//	    debug_tv.setVisibility(View.GONE);

        // faceImage = (ImageView) getActivity().findViewById(R.id.face_imageView);

        animationFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fadein);
        animationFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fadeout);

        voiceButton = (ImageButton) view.findViewById(R.id.voice_imageButton);
        voiceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).speak("Good morning sir, would you like a coffee?");
                new CountDownTimer(3000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        //do nothing, just let it tick
                    }

                    public void onFinish() {
                        ((MainActivity) getActivity()).startRecognition();
                    }
                }.start();
            }
        });
//        getActivity().onViewInflated(view);
        return view;
    }

    public void setNewFace(int resourceID){
        faceImage.startAnimation(animationFadeOut);
        faceImage.setVisibility(View.INVISIBLE);
        faceImage.setImageResource(resourceID);
        faceImage.startAnimation(animationFadeIn);
        faceImage.setVisibility(View.VISIBLE);
    }

    public void setVoiceButtonBackgroundResource(int resId){
        voiceButton.setBackgroundResource(resId);
    }

    public void setDebugText(String text){
        debugTextView.setText(text);
    }


}
