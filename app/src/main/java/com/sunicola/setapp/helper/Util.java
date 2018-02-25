package com.sunicola.setapp.helper;

import android.content.Context;
import android.util.Log;

import com.sunicola.setapp.objects.Group;
import com.sunicola.setapp.storage.SQLiteHandler;

import java.util.HashMap;
import java.util.List;

/**
 * Created by soaresbo on 14/02/2018.
 */
public class Util {
    APICalls api;
    Context _context;
    SQLiteHandler db;

    public Util(Context _context){
        this._context = _context;
        api = new APICalls(_context);
        db = new SQLiteHandler(_context);
    }

    /**
     * Checks table of all device types and returns type of id parsed
     * 1 -> sensor for example
     * @param id
     * @return
     */
    public String convertDevId(String id){
        api.updateAllDeviceTypes();
        HashMap<String,String> allTypes = new HashMap<>(db.getAllDeviceTypes());
        return allTypes.get(id);
    }

    //TODO: Needs to be completed
    /**
     * Return String with name of group type
     * @param id
     * @return
     */
    public String convertGroupId(String id){
        //api.updateAllDeviceGroups();
        return id;
    }
}
