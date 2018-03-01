/*
 * Copyright (c) 25. 2. 2018. Orber Soares Bom Jesus
 */

package com.sunicola.setapp.objects;


public class Group {
    String name;
    int id, groupType,state;

    public Group(){}
    public Group(int id, String name, int groupType, int state) {
        this.name = name;
        this.id = id;
        this.groupType = groupType;
        this.state = state;

    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getGroupType() {return groupType;}
    public void setGroupType(int groupType) {this.groupType = groupType;}

    public int getState() {return state;}
    public void setState(int state) {this.state = state;}
}
