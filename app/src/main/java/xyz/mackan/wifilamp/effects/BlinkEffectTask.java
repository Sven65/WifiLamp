package xyz.mackan.wifilamp.effects;

import android.content.Context;
import android.os.AsyncTask;

import xyz.mackan.wifilamp.ChangeColorTask;

public class BlinkEffectTask extends AsyncTask{
    private Context mContext = null;

    private BlinkEffect effectData;

    public BlinkEffectTask(Context mContext, BlinkEffect effectData){
        this.mContext = mContext;
        this.effectData = effectData;
    }

    @Override
    public Integer doInBackground(Object[] objects) {
        for(int i=0;i<this.effectData.blinks;i++){
            try{
                new ChangeColorTask(mContext).execute(this.effectData.blinkTo[0], this.effectData.blinkTo[1], this.effectData.blinkTo[2]);
                Thread.sleep(this.effectData.offTime);
                new ChangeColorTask(mContext).execute(this.effectData.blinkFrom[0], this.effectData.blinkFrom[1], this.effectData.blinkFrom[2]);
                Thread.sleep(this.effectData.onTime);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return 1;
    }
}
