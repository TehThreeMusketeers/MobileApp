package com.sunicola.setapp.objects;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by soaresbo on 15/02/2018.
 */

//FIXME: Not working
public class GroupType {
    int id;
    String name;
    HashMap<String,String> states = new HashMap<>();
    HashMap<String,String> variables = new HashMap<>();

    public GroupType(int id, String name, HashMap<String,String> states, HashMap<String,String> variables) {
        this.id = id;
        this.name = name;
        this.states = states;
        this.variables = variables;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getStates() {
        return states;
    }

    public void setStates(HashMap<String, String> states) {
        this.states = states;
    }

    public HashMap<String, String> getVariables() {
        return variables;
    }

    public void setVariables(HashMap<String, String> variables) {
        this.variables = variables;
    }
}
