package xyz.mackan.wifilamp;

import java.io.Serializable;

import xyz.mackan.wifilamp.effects.EffectData;

public class ColorButton implements Serializable{
    public int r, g, b;

    public String name;
    public EffectData effectData;

    public ColorButton(int r, int g, int b, String name, EffectData effectData){
        this.r = r;
        this.g = g;
        this.b = b;

        this.name = name;
        this.effectData = effectData;
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

    @Override
    public String toString() {
        return this.r+":"+this.g+":"+this.b;
    }
}
