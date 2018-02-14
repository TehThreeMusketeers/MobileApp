package com.sunicola.setapp.objects;

/**
 * Created by soaresbo on 14/02/2018.
 */

public class Group {
    String name;
    int id, groupType;

    public Group(){}
    public Group(String name, int id, int groupType) {
        this.name = name;
        this.id = id;
        this.groupType = groupType;
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getGroupType() {return groupType;}
    public void setGroupType(int groupType) {this.groupType = groupType;}
}
