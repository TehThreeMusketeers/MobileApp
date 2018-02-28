/*
 * Copyright (c) 26. 2. 2018. Orber Soares Bom Jesus
 */

package com.sunicola.setapp.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.sunicola.setapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotonFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PhotonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotonFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mPhotonId;
    private OnFragmentInteractionListener mListener;
    public PhotonFragment() {}

    public static PhotonFragment newInstance(String photonId) {
        PhotonFragment fragment = new PhotonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, photonId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhotonId = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Photon View");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //TextViews Setup
        TextView photonID = getView().findViewById(R.id.photonId);
        photonID.append(mPhotonId);

        //Radio Button Setup
        RadioGroup radioGroup = getView().findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == 2131230921){
                            loadTemp(1);
                            loadSound(1);
                            loadHumid(1);
                        }
                        if (checkedId == 2131230922){
                            loadTemp(2);
                            loadSound(2);
                            loadHumid(2);
                        }
                    }

                });

        //Tabs Setup
        TabHost tabHost = getView().findViewById(R.id.tabHost);
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
        spec = tabHost.newTabSpec("Humidity");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Humidity");
        tabHost.addTab(spec);

        loadTemp(1);
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
     * Loads Temperature graph and data
     * @param i
     */
    private void loadTemp(int i){
        GraphView graphView = getView().findViewById(R.id.graphTemp);
        // Month Data
        if (i == 1){
            DataPoint[] values = new DataPoint[30];
            for (int xi=1; xi<=30; xi++) {
                Integer yi = 30; // data to be added
                DataPoint v = new DataPoint(xi, yi);
                values[xi-1] = v;
            }
            graphView.removeAllSeries();
            //BarGraphSeries<DataPoint> monthData = new BarGraphSeries<>(values);
            BarGraphSeries<DataPoint> monthData = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            graphView.addSeries(monthData);

            monthData.setSpacing(10);
            monthData.setTitle("Month View");
        }

        //Week Data
        else if (i==2){
            DataPoint[] values = new DataPoint[7];
            for (int xi=1; xi<=7; xi++) {
                Integer yi = 7; // data to be added
                DataPoint v = new DataPoint(xi, yi);
                values[xi-1] = v;
            }
            graphView.removeAllSeries();
            //BarGraphSeries<DataPoint> weekData = new BarGraphSeries<>(values);
            LineGraphSeries<DataPoint> weekData = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            graphView.addSeries(weekData);

            //weekData.setSpacing(10);
            weekData.setTitle("Week View");
        }
        graphSettings(graphView);
    }

    private void loadSound(int i) {
        GraphView graphView = getView().findViewById(R.id.graphSound);
        // Month Data
        if (i == 1){
            DataPoint[] values = new DataPoint[30];
            for (int xi=1; xi<=30; xi++) {
                Integer yi = 30; // data to be added
                DataPoint v = new DataPoint(xi, yi);
                values[xi-1] = v;
            }
            graphView.removeAllSeries();
            //BarGraphSeries<DataPoint> monthData = new BarGraphSeries<>(values);
            BarGraphSeries<DataPoint> monthData = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });graphView.addSeries(monthData);

            monthData.setSpacing(10);
            monthData.setTitle("Month View");
        }

        //Week Data
        else if (i==2){
            DataPoint[] values = new DataPoint[7];
            for (int xi=1; xi<=7; xi++) {
                Integer yi = 7; // data to be added
                DataPoint v = new DataPoint(xi, yi);
                values[xi-1] = v;
            }
            graphView.removeAllSeries();
            //BarGraphSeries<DataPoint> weekData = new BarGraphSeries<>(values);
            LineGraphSeries<DataPoint> weekData = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });graphView.addSeries(weekData);

            //weekData.setSpacing(10);
            weekData.setTitle("Week View");
        }
        graphSettings(graphView);
    }

    private void loadHumid(int i) {
        GraphView graphView = getView().findViewById(R.id.graphHumid);
        // Month Data
        if (i == 1){
            DataPoint[] values = new DataPoint[30];
            for (int xi=1; xi<=30; xi++) {
                Integer yi = 30; // data to be added
                DataPoint v = new DataPoint(xi, yi);
                values[xi-1] = v;
            }
            graphView.removeAllSeries();
            //BarGraphSeries<DataPoint> monthData = new BarGraphSeries<>(values);
            BarGraphSeries<DataPoint> monthData = new BarGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });
            graphView.addSeries(monthData);

            monthData.setSpacing(10);
            monthData.setTitle("Month View");
        }

        //Week Data
        else if (i==2){
            DataPoint[] values = new DataPoint[7];
            for (int xi=1; xi<=7; xi++) {
                Integer yi = 7; // data to be added
                DataPoint v = new DataPoint(xi, yi);
                values[xi-1] = v;
            }
            graphView.removeAllSeries();
            //BarGraphSeries<DataPoint> weekData = new BarGraphSeries<>(values);
            LineGraphSeries<DataPoint> weekData = new LineGraphSeries<>(new DataPoint[] {
                    new DataPoint(0, 1),
                    new DataPoint(1, 5),
                    new DataPoint(2, 3),
                    new DataPoint(3, 2),
                    new DataPoint(4, 6)
            });graphView.addSeries(weekData);

            //weekData.setSpacing(10);
            weekData.setTitle("Week View");
        }
        graphSettings(graphView);
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
    }
}
