package xyz.mackan.wifilamp;

import java.io.Serializable;

public class ColorButton implements Serializable{
    public int r, g, b;

    public String name;

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

    @Override
    public String toString() {
        return this.r+":"+this.g+":"+this.b;
    }
}
