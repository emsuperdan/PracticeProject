package com.example.annotation.annotation.bean;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class DiffMeta {

    public Set<Class> subClass = new HashSet<>();


    public Set<Class> getSubClass() {
        return subClass;
    }

    public void add(Class className){
        subClass.add(className);
    }
}
