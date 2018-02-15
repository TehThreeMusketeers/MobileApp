package com.sunicola.setapp.objects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by soaresbo on 15/02/2018.
 */

public class GroupType {
    int id;
    String name;
    ArrayList<HashMap<Integer, String>> statesList = new ArrayList<>();
    ArrayList<HashMap<Integer, String>> variablesList = new ArrayList<>();
    HashMap<Integer,String> states = new HashMap<>();
    HashMap<Integer,String> variables = new HashMap<>();

    public GroupType() {
        id = 1;
        name = "Burglar Alarm";

        states.put(1,"ARMED");
        states.put(2,"DISARMED");
        statesList.add(states);

        variables.put(4,"light");
        variables.put(5,"sound");
        variables.put(6,"motion");
        variablesList.add(variables);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<HashMap<Integer, String>> getStatesList() {
        return statesList;
    }

    public ArrayList<HashMap<Integer, String>> getVariablesList() {
        return variablesList;
    }

    public HashMap<Integer, String> getStates() {
        return states;
    }

    public HashMap<Integer, String> getVariables() {
        return variables;
    }
}
