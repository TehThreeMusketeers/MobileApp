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

public class ActuatorsFragment extends Fragment {
    // Skeleton vars
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TriggerFragment.OnFragmentInteractionListener mListener;

    // light control vars
    // command strings
    private static final String ALL_OFF = "410";
    private static final String ALL_ON = "420";
    private static final String GR_1_OFF = "460";
    private static final String GR_1_ON = "450";
    private static final String GR_2_OFF = "480";
    private static final String GR_2_ON = "470";
    private static final String GR_3_OFF = "4a0";
    private static final String GR_3_ON = "490";
    private static final String GR_4_OFF = "4b0";
    private static final String GR_4_ON = "4c0";
    private static final String COLOR_PREFIX = "40";
    private static final String BRIGHTNESS_PREFIX = "4e";

    // UI elements for light control
    private Button btn1, btn2, btn3, btn4, all, off;
    private SeekBar colour, brightness;

    // photon communication - call to cloud exposed function
    private ParticleCloud cloud;
    String msg = "Android: ";
    private String currentBulb = "";

    // kettle control vars
    // command strings
    private static final String K_HELLOKETTLE = "HELLOKETTLE";
    private static final String K_BASE_COMMAND = "set sys output ";
    private static final String K_100 = "0x80";
    private static final String K_95 = "0x2";
    private static final String K_80 = "0x4000";
    private static final String K_65 = "0x200";
    private static final String K_WARM = "0x8";
    private static final String K_WARM5 = "0x8005";
    private static final String K_WARM10 = "0x8010";
    private static final String K_WARM20 = "0x8020";
    private static final String K_ON = "0x4";
    private static final String K_OFF = "0x0";

    // UI elements for Kettle control
    private Button kettle_on, kettle_off, kettle_65, kettle_80, kettle_95, kettle_100, kettle_w5, kettle_w10, kettle_w20;

    // required constructor
    public ActuatorsFragment() {}

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
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cloud = ParticleCloudSDK.getCloud();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.actuators_layout, container, false);

        // LIGHT CONTROL
        // add light control UI elements
        btn1 = view.findViewById(R.id.button_group_one);
        btn1.setOnClickListener(onButtonClickListener(GR_1_ON));
        btn2 = view.findViewById(R.id.button_group_two);
        btn2.setOnClickListener(onButtonClickListener(GR_2_ON));
        btn3 = view.findViewById(R.id.button_group_three);
        btn3.setOnClickListener(onButtonClickListener(GR_3_ON));
        btn4 = view.findViewById(R.id.button_group_four);
        btn4.setOnClickListener(onButtonClickListener(GR_4_ON));
        all = view.findViewById(R.id.button_all);
        all.setOnClickListener(onButtonClickListener(ALL_ON));
        off = view.findViewById(R.id.button_off);
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (currentBulb){
                    case GR_1_ON:
                        sendMessage(GR_1_OFF);
                        btn1.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                    case GR_2_ON:
                        sendMessage(GR_2_OFF);
                        btn2.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                    case GR_3_ON:
                        sendMessage(GR_3_OFF);
                        btn3.setBackgroundColor(Color.parseColor("#ff00ddff"));
                        break;
                    case GR_4_ON:
                        sendMessage(GR_4_OFF);
                        btn4.setBackgroundColor(Color.parseColor("#ff00ddff"));
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

        // KETTLE CONTROL
        // add kettle control UI elements
        kettle_on = view.findViewById(R.id.button_kettle_on);
        kettle_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_HELLOKETTLE);
                tellKettle(K_ON);
                kettle_on.setBackgroundColor(Color.parseColor("#ffff0000"));
            }
        });
        kettle_off = view.findViewById(R.id.button_kettle_off);
        kettle_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_OFF);
                kettle_on.setBackgroundColor(Color.parseColor("#ff00ddff"));
            }
        });
        kettle_65 = view.findViewById(R.id.button_65);
        kettle_65.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_ON);
                tellKettle(K_65);
            }
        });
        kettle_80 = view.findViewById(R.id.button_80);
        kettle_80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_ON);
                tellKettle(K_80);
            }
        });
        kettle_95 = view.findViewById(R.id.button_95);
        kettle_95.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_ON);
                tellKettle(K_95);
            }
        });
        kettle_100 = view.findViewById(R.id.button_100);
        kettle_100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_ON);
                tellKettle(K_100);
            }
        });
        kettle_w5 = view.findViewById(R.id.button_w5);
        kettle_w5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_WARM);
                tellKettle(K_WARM5);
            }
        });
        kettle_w10 = view.findViewById(R.id.button_w10);
        kettle_w10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_WARM);
                tellKettle(K_WARM10);
            }
        });
        kettle_w20 = view.findViewById(R.id.button_w20);
        kettle_w20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tellKettle(K_WARM);
                tellKettle(K_WARM20);
            }
        }); // end of kettle UI section

        return view;
    }

    // kettle click handler
    private void tellKettle(String command) {
        Log.d(msg, command);
        List<String> lst = new ArrayList<String>();
        lst.add(command);

      /*  Async.executeAsync(cloud, new Async.ApiProcedure<ParticleCloud>() {
            @Override
            public Void callApi(@NonNull ParticleCloud particleCloud) throws ParticleCloudException, IOException {
                ParticleDevice myDevice = ParticleCloudSDK.getCloud().getDevice("1c0036001447343433313338");
                try {
                    int i = myDevice.callFunction("setKettle", lst);
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
        }); */
    }

    // light control listener
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
            btn1.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn3.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn4.setBackgroundColor(Color.parseColor("#ff00ddff"));
            all.setBackgroundColor(Color.parseColor("#ffff0000")); // red
        } else if (currentBulb.equals(GR_1_ON)) {
            btn1.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            btn2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn3.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn4.setBackgroundColor(Color.parseColor("#ff00ddff"));
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        } else if (currentBulb.equals(GR_2_ON)) {
            btn1.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn2.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            btn3.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn4.setBackgroundColor(Color.parseColor("#ff00ddff"));
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        } else if (currentBulb.equals(GR_3_ON)) {
            btn1.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn3.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            btn4.setBackgroundColor(Color.parseColor("#ff00ddff"));
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        } else if (currentBulb.equals(GR_4_ON)) {
            btn1.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn2.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn3.setBackgroundColor(Color.parseColor("#ff00ddff"));
            btn4.setBackgroundColor(Color.parseColor("#ffff0000")); // red
            all.setBackgroundColor(Color.parseColor("#ff00ddff"));
        }
    }

    // light control command handler
    private void sendMessage(String command){
        Log.d(msg, command);
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
