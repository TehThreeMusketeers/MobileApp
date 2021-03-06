/*
 * Copyright (c) 26. 2. 2018. Orber Soares Bom Jesus
 */

package com.sunicola.setapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sunicola.setapp.R;
import com.sunicola.setapp.app.AppConfig;
import com.sunicola.setapp.app.AppController;
import com.sunicola.setapp.storage.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;

import static com.sunicola.setapp.helper.Util.setHeaders;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotonFragment extends Fragment {
    private static final String ARG_DEV_ID = "param1";
    private static final String ARG_SRV_DEV_ID = "param2";
    private int WEEK = 7;
    private int MONTH = 30;
    private static final String TAG = PhotonFragment.class.getSimpleName();
    private static String sessionToken;
    private static String accessToken;
    private static Context _context = null;
    private TabHost tabHost;
    private RadioGroup radioGroup;
    private LinearLayout liveData;

    private String mPhotonId;
    private String srvDevId;
    private OnFragmentInteractionListener mListener;

    /**
     * Constructor needed empty
     */
    public PhotonFragment() {}

    /**
     * Method used to generate instances of photons
     * @param photonId
     * @param srvDevId
     * @return
     */
    public static PhotonFragment newInstance(String photonId, String srvDevId, Context context) {
        PhotonFragment fragment;
        fragment = new PhotonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DEV_ID, photonId);
        args.putString(ARG_SRV_DEV_ID, srvDevId);
        fragment.setArguments(args);
        _context = context;
        return fragment;
    }

    /**
     * On creation of view get sessiontoken
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhotonId = getArguments().getString(ARG_DEV_ID);
            srvDevId = getArguments().getString(ARG_SRV_DEV_ID);
        }
        SQLiteHandler db = new SQLiteHandler(getContext());
        HashMap<String, String> user = db.getUserDetails();
        ParticleCloudSDK.init(_context);
        sessionToken = user.get("session_token");
        accessToken = user.get("access_token");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Photon View");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photon, container, false);
    }

    /**
     * Once the view is created add elements
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Load data from particle sdk
        loadParticle();

        // Load live data
        //loadLive();

        //Radio Button Setup
        RadioButton rb;
        rb = getView().findViewById(R.id.weekRadioBtn);
        if (rb.isChecked()){
            loadData(WEEK);
        }

        // Radio Group
        radioGroup = getView().findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == 2131231045){
                    //Load Week
                    loadData(WEEK);
                }
                else if (checkedId == 2131230906){
                    //Load Month
                    loadData(MONTH);
                }
                Log.e("CLICKED",Integer.toString(checkedId));
            }
        });

        //Tabs Setup
        tabHost = getView().findViewById(R.id.tabHost);
        tabHost.setup();
        //Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Temperature");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Temperature");
        tabHost.addTab(spec);
        //Tab 2
        spec = tabHost.newTabSpec("Sound");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Sound");
        tabHost.addTab(spec);
        //Tab 3
        spec = tabHost.newTabSpec("Light");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Light");
        tabHost.addTab(spec);

        liveData = getView().findViewById(R.id.liveData);
    }

    /**
     * Uses Particle Cloud Api to get specific photon data
     */
    private void loadParticle() {
        //TextViews Setup
        TextView photonID = getView().findViewById(R.id.photonId);
        photonID.append(mPhotonId);
        TextView photonStatus = getView().findViewById(R.id.photonStatus);
        TextView photonLastHeard = getView().findViewById(R.id.photonLastHeard);

        Async.executeAsync(ParticleCloudSDK.getCloud(), new Async.ApiWork<ParticleCloud, Object>() {
            private ParticleDevice mDevice;
            @Override
            public Object callApi(@NonNull ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                sparkCloud.setAccessToken(accessToken);
                mDevice = sparkCloud.getDevice(mPhotonId);
                if (!mDevice.getStatus().equals("normal")){
                    photonStatus.append("OFF");
                }else {
                    photonStatus.append("ON");
                }
                //photonStatus.append(mDevice.getStatus());
                photonLastHeard.append(mDevice.getLastHeard().toString());
                return -1;
            }

            @Override
            public void onSuccess(@NonNull Object value) {
                Log.d(TAG,"Particle Cloud SDK success");
            }

            @Override
            public void onFailure(@NonNull ParticleCloudException e) {
                Log.e(TAG,"Particle Cloud error");
                Log.e(TAG, e.toString());
            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Gets data from server and creates graph
     * @param days
     * @return
     */
    public void loadData(int days) {
        // Tag used to cancel the request
        String tag_string_req = "req_allData";
        // Url used during request
        String[] dataTypes = {"/temp", "/sound", "/light"};
        for (String type: dataTypes) {
            String url = AppConfig.URL_DEVICES+srvDevId+type+"?since="+days;
            Log.e("URL", url);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                GraphView graphView = new GraphView(_context);
                                switch (type) {
                                    case "/temp":
                                        graphView = getView().findViewById(R.id.graphTemp);
                                        break;
                                    case "/sound":
                                        graphView = getView().findViewById(R.id.graphSound);
                                        break;
                                    case "/light":
                                        graphView = getView().findViewById(R.id.graphLight);
                                        break;
                                }
                                graphView.removeAllSeries();
                                int count = response.getInt("count");
                                Log.e(TAG,"Count from Server: " + count);
                                if (count<1){
                                    tabHost.setVisibility(View.GONE);
                                    radioGroup.setVisibility(View.GONE);
                                }liveData.setVisibility(View.GONE);//hides live data until implementation
                                DataPoint[] values = new DataPoint[days];
                                // Gets data from results and populates Data Point
                                JSONArray jsonArray = response.getJSONArray("results");
                                for (int i=0; i<days; i++) {
                                    double result = jsonArray.getJSONObject(i).getDouble("result");
                                    DataPoint v = new DataPoint(i, result);
                                    values[i] = v;
                                }
                                if (days == 7){
                                    BarGraphSeries<DataPoint> nodes = new BarGraphSeries<>(values);
                                    graphView.addSeries(nodes);
                                    // Sets spacing and title
                                    nodes.setSpacing(10);
                                    nodes.setTitle("Week View");
                                }
                                else if (days == 30){
                                    LineGraphSeries<DataPoint> nodes = new LineGraphSeries<>(values);
                                    graphView.addSeries(nodes);
                                    nodes.setTitle("Month View");
                                }
                                //General settings for graph view
                                graphSettings(graphView);

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Server Error: " + error.getMessage());
                            Toast.makeText(getContext(),
                                    "Issue getting all temp data from server", Toast.LENGTH_SHORT).show();
                        }
                    }
            )
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return setHeaders(sessionToken);
                }
            };
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(objectRequest, tag_string_req);
        }
    }

    /**
     * General Graph Settings
     * @param graphView
     */
    private void graphSettings(GraphView graphView) {
        // activate horizontal zooming and scrolling
        graphView.getViewport().setScalable(true);
        // activate horizontal scrolling
        graphView.getViewport().setScrollable(true);
        // activate horizontal and vertical zooming and scrolling
        graphView.getViewport().setScalableY(true);
        // activate vertical scrolling
        graphView.getViewport().setScrollableY(true);
        // add a legend
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}