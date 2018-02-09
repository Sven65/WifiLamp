package xyz.mackan.wifilamp.Steps;

import java.io.Serializable;

public class StepData implements Serializable{
    public Integer r, g, b, duration;

    public StepData(){

    }

    public void setB(Integer b) {
        this.b = b;
    }

    public void setR(Integer r) {
        this.r = r;
    }

    public void setG(Integer g) {
        this.g = g;
    }

    public StepData(Integer r, Integer g, Integer b){
        r = r;
        g = g;
        b = b;
    }

    public StepData(Integer duration){
        duration = duration;
    }

    public boolean isEmpty(){
        return (r == null || g == null || b == null || duration == null);
    }

}
