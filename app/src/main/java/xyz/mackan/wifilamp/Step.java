package xyz.mackan.wifilamp;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import xyz.mackan.wifilamp.Steps.StepConstants;
import xyz.mackan.wifilamp.Steps.StepData;

public class Step implements Serializable{
    public int stepType;
    public StepData stepData;

    public Step(int stepType, StepData data){
        this.stepType = stepType;
        this.stepData = data;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject stepJSON = new JSONObject();

        stepJSON.put("stepType", this.stepType);


        if(stepType == StepConstants.STEP_DELAY){
            stepJSON.put("duration", stepData.duration);
        }else if(stepType == StepConstants.STEP_SET_COLOR){
            stepJSON.put("r", stepData.r);
            stepJSON.put("g", stepData.g);
            stepJSON.put("b", stepData.b);
        }

        return stepJSON;
    }
}
