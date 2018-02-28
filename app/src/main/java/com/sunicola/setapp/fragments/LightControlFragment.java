package com.sunicola.setapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;

import com.sunicola.setapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;
import io.particle.android.sdk.utils.Async;

/**
 * Created by Cristian on 23/02/2018.
 */

public class LightControlFragment extends Fragment {
    // Skeleton vars
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TriggerFragment.OnFragmentInteractionListener mListener;

    // light control vars
    // command strings
    private static final String ALL_OFF = "410";
    private static final String ALL_ON = "420";
    private static final String BULB_1_OFF = "460";
    private static final String BULB_1_ON = "450";
    private static final String BULB_2_OFF = "480";
    private static final String BULB_2_ON = "470";
    private static final String BULB_3_OFF = "4a0";
    private static final String  BULB_3_ON = "490";
    private static final String COLOR_PREFIX = "40";
    private static final String BRIGHTNESS_PREFIX = "4e";

    // UI elements
    private Button bulb1, bulb2, bulb3, all, off;
    private SeekBar colour, brightness;

    // photon communication - call to cloud exposed function
    private ParticleCloud cloud;
    String msg = "Android: ";
    private String currentBulb = "";

    // required constructor
    public LightControlFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TriggerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TriggerFragment newInstance(String param1, String param2) {
        TriggerFragment fragment = new TriggerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cloud = ParticleCloudSDK.getCloud();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lights_control, container, false);
        bulb1 = view.findViewById(R.id.button_bulb_one);
        bulb1.setOnClickListener(onButtonClickListener(BULB_1_ON));
        bulb2 = view.findViewById(R.id.button_bulb_two);
        bulb2.setOnClickListener(onButtonClickListener(BULB_2_ON));
        bulb3 = view.findViewById(R.id.button_bulb_three);
        bulb3.setOnClickListener(onButtonClickListener(BULB_3_ON));
        all = view.findViewById(R.id.button_all);
        all.setOnClickListener(onButtonClickListener(ALL_ON));
        off = view.findViewById(R.id.button_off);
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentBulb){
                    case BULB_1_ON:
                        sendMessage(BULB_1_OFF);
                        bulb1.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                    case BULB_2_ON:
                        sendMessage(BULB_2_OFF);
                        bulb2.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                    case BULB_3_ON:
                        sendMessage(BULB_3_OFF);
                        bulb3.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                    case ALL_ON:
                        sendMessage(ALL_OFF);
                        all.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                }
            }
        });
        colour = view.findViewById(R.id.seek_colour);
        colour.setMax(255);
        colour.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sendMessage(COLOR_PREFIX + Integer.toHexString(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        brightness = view.findViewById(R.id.seek_brightness);
        brightness.setMax(20);
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sendMessage(BRIGHTNESS_PREFIX + Integer.toHexString(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        return view;
    }

    //test
    private View.OnClickListener onButtonClickListener(final String command){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(command);
                currentBulb = command;
                updateGraphicalInterface();
            }
        };
    }

    private void updateGraphicalInterface() {
        if(currentBulb.equals(ALL_ON)){
            all.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            bulb1.setBackgroundColor(Color.parseColor("#ff00ddff"));
            bulb2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            bulb3.setBackgroundColor(Color.parseColor("#ff00ddff"));
        } else if (currentBulb.equals(BULB_1_ON)) {
            bulb1.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            bulb2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            bulb3.setBackgroundColor(Color.parseColor("#ff00ddff"));
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        } else if (currentBulb.equals(BULB_2_ON)) {
            bulb1.setBackgroundColor(Color.parseColor("#ff00ddff")); // red
            bulb2.setBackgroundColor(Color.parseColor("#ffff0000"));
            bulb3.setBackgroundColor(Color.parseColor("#ff00ddff"));
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        } else if (currentBulb.equals(BULB_3_ON)) {
            bulb1.setBackgroundColor(Color.parseColor("#ff00ddff"));
            bulb2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            bulb3.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        }
    }

    private void sendMessage(String command){
        //Log.d(msg, command);
        List<String> lst = new ArrayList<String>();
        lst.add(command);

        Async.executeAsync(cloud, new Async.ApiProcedure<ParticleCloud>() {
            @Override
            public Void callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                ParticleDevice myDevice = ParticleCloudSDK.getCloud().getDevice("1c0036001447343433313338");
                try {
                    int i = myDevice.callFunction("setLight", lst);
                    //Log.d(msg, String.valueOf(i));
                } catch (io.particle.android.sdk.cloud.ParticleDevice.FunctionDoesNotExistException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException exception) {
                Log.d(msg, "Call failed.");
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TriggerFragment.OnFragmentInteractionListener) {
            mListener = (TriggerFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}