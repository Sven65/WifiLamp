package xyz.mackan.wifilamp;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class ColorButton implements Serializable{
    public int r, g, b;

    public String name;
    public LinkedHashMap<String, Step> steps = new LinkedHashMap<String, Step>();

    public ColorButton(int r, int g, int b, String name, LinkedHashMap<String, Step> steps){
        this.r = r;
        this.g = g;
        this.b = b;

        this.name = name;
        this.steps = steps;
    }

    public ColorButton(int r, int g, int b, String name){
        this.r = r;
        this.g = g;
        this.b = b;

        this.name = name;
    }

    public ColorButton(int r, int g, int b){
        this.r = r;
        this.g = g;
        this.b = b;

        this.name = r+":"+g+":"+b;
    }

    public void setSteps(LinkedHashMap<String, Step> steps) {
        this.steps = steps;
    }

    @Override
    public String toString() {
        return this.r+":"+this.g+":"+this.b;
    }
}
