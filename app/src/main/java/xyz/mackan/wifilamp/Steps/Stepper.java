package xyz.mackan.wifilamp.Steps;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

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

    private String createTransactionID(){
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public void handleStep(Step stepData, final int step) throws ExecutionException, InterruptedException {
        if(stepData != null){
            Log.wtf("WIFILAMP", "STEPDATA NOT NULL");

            if(step < buttonData.steps.keySet().toArray().length) {

                final String m = "" + buttonData.steps.keySet().toArray()[step];

                Log.wtf("WIFILAMP", "ST: " + m);

                if (stepData.stepType == StepConstants.STEP_OFF) {
                    int res = new ChangeColorTask(context).execute(0, 0, 0).get();

                    Log.wtf("WIFILAMP", "OFFRES: "+res);

                    new Stepper(buttonData, context).handleStep(buttonData.steps.get(m), step + 1);
                } else if (stepData.stepType == StepConstants.STEP_DELAY) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                new Stepper(buttonData, context).handleStep(buttonData.steps.get(m), step + 1);
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }, stepData.stepData.duration);
                } else if (stepData.stepType == StepConstants.STEP_SET_COLOR) {
                    int res = new ChangeColorTask(context).execute(stepData.stepData.r, stepData.stepData.g, stepData.stepData.b).get();
                    new Stepper(buttonData, context).handleStep(buttonData.steps.get(m), step + 1);
                }
            }
        }else{
            Log.wtf("WIFILAMP", "STEPDATA NULL");
        }
    }

    public void doHandle(){
        try {
            //buttonData.steps.put(createTransactionID(), new Step(StepConstants.STEP_DELAY, new StepData(0)));
            handleStep(buttonData.steps.get(buttonData.steps.keySet().toArray()[0]), 0);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        try {
            handleStep(buttonData.steps.get(buttonData.steps.keySet().toArray()[0]), 0);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
