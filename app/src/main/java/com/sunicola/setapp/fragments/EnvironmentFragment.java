package com.sunicola.setapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;

import com.sunicola.setapp.R;
import com.sunicola.setapp.app.MyAdapter;
import com.sunicola.setapp.app.UserEnvironment;
import com.sunicola.setapp.helper.APICalls;
import com.sunicola.setapp.helper.Util;
import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.storage.SQLiteHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EnvironmentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EnvironmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnvironmentFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private UserEnvironment ue;
    private Button btn;
    private Button clearBtn;
    private boolean clicked = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Bitmap drawing;

    public EnvironmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnvironmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnvironmentFragment newInstance(String param1, String param2) {
        EnvironmentFragment fragment = new EnvironmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getActivity().setTitle("User Environment");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_environment, container, false);

        ue = (UserEnvironment)v.findViewById(R.id.userEnvironment);

        btn = (Button) v.findViewById(R.id.button4);
        btn.setOnClickListener(this);

        clearBtn = (Button) v.findViewById(R.id.btn_clear);
        clearBtn.setOnClickListener(this);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // instantiates api, db and list objects
        APICalls apiCalls = new APICalls(getContext());
        SQLiteHandler db = new SQLiteHandler(getContext());
        Util util = new Util(getContext());
        List<String> photonNameList = new ArrayList<>();

        //updates db using api calls
        apiCalls.updateAllPhotons();
        apiCalls.updateAllDeviceTypes();


        Photon[] photonArray = db.getAllPhotonsArr();


        mAdapter = new MyAdapter(photonArray);
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.button4: {

                if(!clicked){  //when the user clicks customize
                    btn.setText(R.string.ue_btn_done);
                    ue.setDrawing();
                    clicked = true;
                }else if (clicked){  //when the user clicks done
                    btn.setText(R.string.ue_btn_customize);
                    ue.setDrawingCacheEnabled(true);
                    drawing = ue.getDrawingCache();
                    ue.unsetDrawing();
                    clicked = false;
                }
                break;
            }

            case R.id.btn_clear: {
                ue.erase();

            }
        }


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
