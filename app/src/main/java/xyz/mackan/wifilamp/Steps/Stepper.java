package xyz.mackan.wifilamp.Steps;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import java.util.Iterator;
import java.util.Map;

import xyz.mackan.wifilamp.ChangeColorTask;
import xyz.mackan.wifilamp.ColorButton;
import xyz.mackan.wifilamp.Step;

public class Stepper extends AsyncTask<Integer, Void, Integer>{
    final static int SUCCESS = 0;
    final static int FAILURE = 1;

    private ColorButton buttonData;
    private Context context;

    public Stepper(ColorButton bData, Context ctx){
        buttonData = bData;
        context = ctx;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        //new ChangeColorTask(context).execute(0, 0, 0);

        Iterator it = buttonData.steps.entrySet().iterator();

        try {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                Step stepData = (Step) pair.getValue();

                if(stepData.stepType == StepConstants.STEP_OFF){
                    new ChangeColorTask(context).execute(0, 0, 0);
                }else if(stepData.stepType == StepConstants.STEP_DELAY){
                    Thread.currentThread();
                    Thread.sleep(stepData.stepData.duration);
                    //wait(stepData.stepData.duration);
                }else if(stepData.stepType == StepConstants.STEP_SET_COLOR){
                    new ChangeColorTask(context).execute(stepData.stepData.r, stepData.stepData.g, stepData.stepData.b);
                }
            }


            return SUCCESS;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            return FAILURE;
        }
    }
}
