package xyz.mackan.wifilamp;

import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import xyz.mackan.wifilamp.Steps.StepConstants;

public class Step {
    public int stepType;
    public Bundle stepData;

    public Step(int stepType, Bundle data){
        this.stepType = stepType;
        this.stepData = data;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject stepJSON = new JSONObject();

        stepJSON.put("stepType", this.stepType);

        if(!stepData.isEmpty()){
            if(stepType == StepConstants.STEP_DELAY){
                stepJSON.put("duration", stepData.get("duration"));
            }else if(stepType == StepConstants.STEP_SET_COLOR){
                stepJSON.put("r", stepData.get("r"));
                stepJSON.put("g", stepData.get("g"));
                stepJSON.put("b", stepData.get("b"));
            }
        }

        return stepJSON;
    }
}
