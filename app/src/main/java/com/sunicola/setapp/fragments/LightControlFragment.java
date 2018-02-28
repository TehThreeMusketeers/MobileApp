/*
 * Copyright (c) 26. 2. 2018. Orber Soares Bom Jesus
 */

package com.sunicola.setapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import com.sunicola.setapp.R;

import java.io.DataOutputStream;
import java.net.Socket;


public class LightControlFragment extends Fragment {
    // Skeleton vars
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TriggerFragment.OnFragmentInteractionListener mListener;

    // light control vars
    // command strings
    private static final String ALL_OFF = "410";
    private static final String ALL_ON = "420";

    /**
     * Add Color Picker after view has been created
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        colorPicker = getView().findViewById(R.id.colorPicker);
        HSLColorPicker colorPicker = getView().findViewById(R.id.colorPicker);
        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                // Do whatever you want with the color
                final String hexColor = String.format("#%06X", (0xFFFFFF & color));
                sendMessage(COLOR_PREFIX+hexColor); //sends hex color
                // sendMessage(COLOR_PREFIX+color); sends raw color
            }
        });
    }

    private static final String BULB_1_OFF = "460";
    private static final String BULB_1_ON = "450";
    private static final String BULB_2_OFF = "480";
    private static final String BULB_2_ON = "470";
    private static final String BULB_3_OFF = "4a0";
    private static final String BULB_3_ON = "490";
    private static final String COLOR_PREFIX = "40";
    private static final String BRIGHTNESS_PREFIX = "4e";

    // UI elements
    private Button bulb1, bulb2, bulb3, all, off;
    private SeekBar colour, brightness;
    private HSLColorPicker colorPicker;

    // communication
    private Socket socket;
    private DataOutputStream outputStream;
    private String macAddress;
    String msg = "Android: ";
    private String currentBulb = "";

    // required constructor
    public LightControlFragment() {}


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
                sendMessage(currentBulb);
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

        /* Old Color code
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
        });*/

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
        Log.d(msg, command);
        /*try {
            outputStream.writeBytes("APP#" + macAddress + "#CMD#" + command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
