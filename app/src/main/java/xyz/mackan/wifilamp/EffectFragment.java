package xyz.mackan.wifilamp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import java.util.LinkedHashMap;
import java.util.UUID;

import xyz.mackan.wifilamp.Steps.StepConstants;

public class EffectFragment extends Fragment implements Button.OnClickListener, InputDialog.InputDialogCallback, ColorDialog.ColorDialogCallback{

    DataPassListener mCallback;

    public interface DataPassListener{
        public void passData(LinkedHashMap<String, Step> data);
    }

    private EffectFragment.OnFragmentInteractionListener mListener;
    private View thisView;
    private TableLayout tv;

    private Button addEffectButton;

    public LinkedHashMap<String, Step> steps = new LinkedHashMap<String, Step>();

    public EffectFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EffectFragment.
     */

    public static EffectFragment newInstance() {
        EffectFragment fragment = new EffectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_effects, container, false);

        thisView = rootView;

        addEffectButton = (Button) rootView.findViewById(R.id.addEffect);

        addEffectButton.setOnClickListener(this);

        tv = (TableLayout) rootView.findViewById(R.id.effectHolder);

        return rootView;
    }

    private String createTransactionID(){
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public void addButton(String title, String tag, int color){
        if(tv != null) {

            FrameLayout fl = (FrameLayout) LayoutInflater.from(this.getContext()).inflate(R.layout.button_holder, null);

            fl.setBackgroundColor(color);

            Button btn = (Button) LayoutInflater.from(this.getContext()).inflate(R.layout.held_button, null);

            btn.setText(title);

            btn.setTag(tag);

            btn.setOnClickListener(this);

            fl.addView(btn);

            tv.addView(fl);
        }
    }

    public void showEffectMenu(){

        CharSequence effects[] = new CharSequence[] {
                getResources().getString(R.string.EFFECT_OFF),
                getResources().getString(R.string.EFFECT_DELAY),
                getResources().getString(R.string.EFFECT_SET_COLOR)
        };

        final EffectFragment t = this;
        final Context c;
        c = this.getContext();

        final Bundle meta = new Bundle();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.effect_type);
        builder.setItems(effects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        // Turn off
                        //InputDialog id = new InputDialog();
                        //id.getInput(c, getView(), "Duration", null, t);

                        String stepID = createTransactionID();

                        steps.put(stepID, new Step(StepConstants.STEP_OFF, new Bundle()));
                        addButton("Turn off", stepID, Color.rgb(255, 0, 255));
                        mCallback.passData(steps);
                    break;
                    case 1:


                        meta.putInt("type", 2);

                        InputDialog id = new InputDialog();
                        id.getInput(c, getView(), "Duration", meta, t);

                    break;
                    case 2:
                        meta.putInt("type", 3);
                        ColorDialog cd = new ColorDialog();
                        cd.getInput(c, getView(), "Color", t);
                    break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addEffect){
            showEffectMenu();
        }
    }

    @Override
    public void inputDialogCallback(Object ret, Object meta) {
        Bundle metaData = (Bundle) meta;
        String inData = (String) ret;
        Bundle stepData = new Bundle();
        String stepID = createTransactionID();

        if(!metaData.isEmpty()) {
            switch (metaData.getInt("type")){
                case 2:
                    stepData.putInt("duration", Integer.parseInt(inData));
                    steps.put(stepID, new Step(StepConstants.STEP_DELAY, stepData));
                    addButton("Delay " + inData, stepID, Color.rgb(255, 0, 255));
                break;
            }
            mCallback.passData(steps);
        }
    }

    @Override
    public void inputDialogCancelCallback(Object ret, Object meta) {

    }

    @Override
    public void colorDialogCallback(Object ret, int r, int g, int b) {
        Bundle stepData = new Bundle();
        String stepID = createTransactionID();

        stepData.putInt("r", r);
        stepData.putInt("g", g);
        stepData.putInt("b", b);
        steps.put(stepID, new Step(StepConstants.STEP_SET_COLOR, stepData));

        addButton("Set color", stepID, Color.rgb(r, g, b));
        mCallback.passData(steps);
    }

    @Override
    public void colorDialogCancelCallback(Object ret) {

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
