package com.sunicola.setapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.sunicola.setapp.R;
import com.sunicola.setapp.helper.APICalls;
import com.sunicola.setapp.helper.Util;
import com.sunicola.setapp.objects.Photon;
import com.sunicola.setapp.storage.SQLiteHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by soaresbo on 09/02/2018.
 */

// TODO: Fix the entire class to ensure it works properly as user increases number of phtons
public class PhotonListFragment
        extends ListFragment
        implements AdapterView.OnItemClickListener {
    private static final String TAG = PhotonListFragment.class.getSimpleName();
    private APICalls apiCalls;
    private SQLiteHandler db;
    private Util util;
    int[] images = {0,0,R.drawable.boardphoton, R.drawable.photon};
    ArrayList<HashMap<String, String>> data = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //loadListView();
        loadDualList();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG,"Clicked" + position);
    }

    public void loadDualList(){
        getActivity().setTitle("All Devices");

        // instantiates api, db and list objects
        apiCalls = new APICalls(getContext());
        db = new SQLiteHandler(getContext());
        util = new Util(getContext());
        List<String> photonIdList = new ArrayList<>();
        List<String> photonTypeList = new ArrayList<>();


        //updates db using api calls
        apiCalls.updateAllPhotons();
        List<Photon> photonList = db.getAllPhotons();
        for (Photon photon: photonList) {
            photonIdList.add(photon.getDeviceId());
            photonTypeList.add(photon.getDeviceType());
        }

        //MAP
        HashMap<String, String> map;
        for (int i=0; i<photonIdList.size(); i++){
            map = new HashMap<>();
            map.put("devId", photonIdList.get(i));
            //map.put("devType", util.convertId(photonTypeList.get(i)));
            map.put("image", Integer.toString(images[Integer.parseInt(photonTypeList.get(i))]));
            data.add(map);
        }

        //KEYS IN MAP
        String[] from = {"devId","image"};

        //IDS OF VIEWS
        int[] to = {R.id.devId,R.id.photonImg};

        //ADAPTER
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), data, R.layout.photon_list_fragment, from, to);
        setListAdapter(adapter);

        ListView list = getListView();
        list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list.setOnItemClickListener(this);

    }
}