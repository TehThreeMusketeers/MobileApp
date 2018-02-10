package com.sunicola.setapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.sunicola.setapp.R;
import com.sunicola.setapp.helper.APICalls;
import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.storage.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by soaresbo on 09/02/2018.
 */

public class PhotonListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static final String TAG = PhotonListFragment.class.getSimpleName();
    private APICalls apiCalls;
    private SQLiteHandler db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.photon_list_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("All Devices");
        getListView().setOnItemClickListener(this);

        apiCalls = new APICalls(getContext());
        db = new SQLiteHandler(getContext());
        List<String> mainList = new ArrayList<>();

        apiCalls.getAllPhotons();
        List<Photon> photonList = db.getAllPhotons();
        for (Photon photon: photonList) {
            mainList.add(photon.getDeviceId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mainList);
        setListAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
    }
}
