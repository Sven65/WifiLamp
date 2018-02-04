package xyz.mackan.wifilamp;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import xyz.mackan.wifilamp.Steps.StepConstants;
import xyz.mackan.wifilamp.Steps.StepData;

public class EffectFragment extends Fragment implements Button.OnClickListener, Button.OnTouchListener, InputDialog.InputDialogCallback, ColorDialog.ColorDialogCallback{

    DataPassListener mCallback;
    private ColorButton BUTTON_DATA;
    private LinkedHashMap<Integer, View> buttons = new LinkedHashMap<Integer, View>();

    float dX;
    float dY;
    int lastAction;

    private LockableScroll scroller;

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

    public static EffectFragment newInstance(Bundle data) {
        EffectFragment fragment = new EffectFragment();
        fragment.setArguments(data);
        return fragment;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (DataPassListener) activity;
        } catch (ClassCastException e) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_effects, container, false);

        addEffectButton = (Button) rootView.findViewById(R.id.addEffect);

        addEffectButton.setOnClickListener(this);

        tv = (TableLayout) rootView.findViewById(R.id.effectHolder);

        if(args != null){
            Log.wtf("WIFILAMP", "ARGS IS NOT NULL");
            BUTTON_DATA = (ColorButton) args.get("BUTTON_DATA");

            addEffectButtons(BUTTON_DATA, getContext());
        }

        scroller = (LockableScroll) getActivity().findViewById(R.id.settingsScroll);

        thisView = rootView;
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.addEffect){
            showEffectMenu();
        }else{
            showButtonMenu(v);
        }
    }

    @Override
    public void inputDialogCallback(Object ret, Object meta) {
        Bundle metaData = (Bundle) meta;
        String inData = (String) ret;
        StepData stepData = new StepData();
        String stepID = createTransactionID();

        if(!metaData.isEmpty()) {
            switch (metaData.getInt("type")){
                case 2:
                    String metaID = metaData.getString("ID");

                    stepData.duration = Integer.parseInt(inData);
                    if(metaID != null){
                        stepID = metaID;
                    }

                    steps.put(stepID, new Step(StepConstants.STEP_DELAY, stepData));

                    if(metaID != null){
                        addButton("Delay " + inData, stepID, Color.rgb(255, 0, 255), this.getContext(), true);
                    }else{
                        addButton("Delay " + inData, stepID, Color.rgb(255, 0, 255), this.getContext());
                    }


                    break;
            }
            mCallback.passData(steps);
        }
    }

    @Override
    public void inputDialogCancelCallback(Object ret, Object meta) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        View fl = (View) view.getParent();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = fl.getX() - event.getRawX();
                dY = fl.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                scroller.setEnableScrolling(false);
                break;

            case MotionEvent.ACTION_MOVE:
                fl.setY(event.getRawY() + dY);
                //fl.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                scroller.setEnableScrolling(true);
                if (lastAction == MotionEvent.ACTION_DOWN){
                    showButtonMenu(view);
                }else {
                    updateFrames();
                }
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void colorDialogCallback(Object ret, Bundle meta, int r, int g, int b) {
        StepData stepData = new StepData();
        String stepID = createTransactionID();

        stepData.r = r;
        stepData.g = g;
        stepData.b = b;

        String metaID = meta.getString("ID");

        if(metaID != null){
            stepID = metaID;
        }

        steps.put(stepID, new Step(StepConstants.STEP_SET_COLOR, stepData));

        if(metaID != null){
            addButton("Set color", stepID, Color.rgb(r, g, b), this.getContext(), true);
        }else {
            addButton("Set color", stepID, Color.rgb(r, g, b), this.getContext());
        }
        mCallback.passData(steps);
    }

    @Override
    public void colorDialogCancelCallback(Object ret) {

    }

    private String createTransactionID(){
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    public void addEffectButtons(ColorButton data, Context context){

        if(data != null){
            steps = data.steps;


            Iterator it = steps.entrySet().iterator();

            while(it.hasNext()){
                Map.Entry stepPair = (Map.Entry) it.next();

                Step step = (Step) stepPair.getValue();

                Log.wtf("WIFILAMP", "STEP KEY: "+stepPair.getKey()+" STEP TYPE: "+step.stepType);

                if(step.stepType == StepConstants.STEP_OFF){
                    addButton(getResources().getString(R.string.EFFECT_OFF), ""+stepPair.getKey(), Color.rgb(0, 0, 255), context);
                }else if(step.stepType == StepConstants.STEP_DELAY){
                    addButton(getResources().getString(R.string.EFFECT_DELAY)+" "+step.stepData.duration+" "+getResources().getString(R.string.seconds), ""+stepPair.getKey(), Color.rgb(0, 0, 255), context);
                }else if(step.stepType == StepConstants.STEP_SET_COLOR){
                    addButton(getResources().getString(R.string.EFFECT_SET_COLOR), ""+stepPair.getKey(), Color.rgb(step.stepData.r, step.stepData.g, step.stepData.b), context);
                }
            }

            //mCallback.passData(steps);
        }
    }

    public void addButton(String title, String tag, int color, Context context){
        addButton(title, tag, color, context, false);
    }

    public void addButton(String title, String tag, int color, Context context, boolean edit){
        if(tv != null) {

            if(edit){
                TableLayout containerParent = (TableLayout) getActivity().findViewById(R.id.effectHolder);
                Button btn = (Button) containerParent.findViewWithTag(tag);

                if(btn != null){
                    FrameLayout fl = (FrameLayout) btn.getParent();

                    fl.setBackgroundColor(color);

                    btn.setText(title);

                    btn.setTag(tag);

                    int index = buttons.size()+1;

                    Iterator it = buttons.entrySet().iterator();

                    while (it.hasNext()){
                        Map.Entry entryPair = (Map.Entry) it.next();

                        Button thisButton = (Button) entryPair.getValue();

                        if(thisButton.getTag() == tag){
                            index = (int) entryPair.getKey();
                        }
                    }

                    buttons.put(index, btn);
                }
            }else {

                FrameLayout fl = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.button_holder, null);

                fl.setBackgroundColor(color);

                Button btn = (Button) LayoutInflater.from(context).inflate(R.layout.held_button, null);

                Drawable buttonIcon = getContext().getResources().getDrawable(R.drawable.ic_more_vert_white_24dp);
                buttonIcon.setBounds(0, 0, 60, 60);
                btn.setCompoundDrawables(null, null, buttonIcon, null);

                btn.setText(title);

                btn.setTag(tag);

                btn.setOnClickListener(this);
                btn.setOnTouchListener(this);

                fl.addView(btn);

                tv.addView(fl);

                buttons.put(buttons.size() + 1, btn);
            }
        }
    }

    public void deleteButton(View v){
        FrameLayout container = (FrameLayout) v.getParent();
        TableLayout containerParent = (TableLayout) getActivity().findViewById(R.id.effectHolder);
        container.setLayoutTransition(null);

        if(containerParent != null){
            containerParent.removeView(container);
        }

        updateFrames();
    }

    public void showButtonMenu(final View v){
        // TODO: Add strings and logic to change the button type and metadata
        CharSequence options[] = new CharSequence[] {
                getResources().getString(R.string.EDIT),
                getResources().getString(R.string.delete)
        };

        final EffectFragment t = this;
        final Context c;
        c = this.getContext();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.action);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        // Edit

                        String stepID = ""+v.getTag();

                        showEffectMenu(stepID);
                    break;
                    case 1:
                        // Delete

                        new AlertDialog.Builder(c)
                        .setTitle(getResources().getString(R.string.confirm_delete_title))
                        .setMessage(getResources().getString(R.string.confirm_delete)+" "+getResources().getString(R.string.step)+"?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteButton(v);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                    break;
                }
            }
        });
        builder.show();
    }

    public void showEffectMenu(){
        showEffectMenu(null);
    }

    public void showEffectMenu(final String id){

        CharSequence effects[] = new CharSequence[] {
                getResources().getString(R.string.EFFECT_OFF),
                getResources().getString(R.string.EFFECT_DELAY),
                getResources().getString(R.string.EFFECT_SET_COLOR)
        };

        final EffectFragment t = this;
        final Context c;
        c = this.getContext();

        final Bundle meta = new Bundle();

        meta.putString("ID", id);

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

                        if(id != null){
                            stepID = id;
                        }

                        steps.put(stepID, new Step(StepConstants.STEP_OFF, new StepData()));

                        if(id != null) {
                            addButton("Turn off", stepID, Color.rgb(255, 0, 255), c, true);
                        }else{
                            addButton("Turn off", stepID, Color.rgb(255, 0, 255), c);
                        }
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
                        cd.getInput(c, meta, getView(), "Color", t);
                        break;
                }
            }
        });
        builder.show();
    }

    public void updateFrames(){
        //TODO: Make buttons re-render in their new positions and then save them in the steps
        //LinkedHashMap<String, Step> steps = new LinkedHashMap<String, Step>();
        List<View> buttonStuff = new ArrayList<View>();

        TableLayout rootTableLayout = (TableLayout) getActivity().findViewById(R.id.effectHolder);
        int count = rootTableLayout.getChildCount();
        for(int i=0;i<count;i++){
            View v = rootTableLayout.getChildAt(i);
            if(v instanceof FrameLayout){
                buttonStuff.add(v);
            }
        }

        Collections.sort(buttonStuff, new Comparator<View>() {
            public int compare(View button1, View button2) {
                float b1Y = button1.getY();
                float b2Y = button2.getY();
                return Integer.valueOf((int) b1Y).compareTo((int) b2Y);
            }
        });

        Iterator it = buttonStuff.iterator();

        LinkedHashMap<String, Step> tempSteps = new LinkedHashMap<String, Step>();

        while (it.hasNext()){
            FrameLayout container = (FrameLayout) it.next();

            Button childButton = (Button) container.getChildAt(0);

            ((ViewGroup) container.getParent()).removeView(container);

            Drawable background = container.getBackground();

            int color = Color.TRANSPARENT;
            if (background instanceof ColorDrawable){
                color = ((ColorDrawable) background).getColor();
            }

            String tag = (String) childButton.getTag();

            addButton(""+childButton.getText(), tag, color, getContext());

            Log.wtf("WIFILAMP", "TAG: "+tag);


            Step tStep = steps.get(tag);

            if(tStep != null){
                Log.wtf("WIFILAMP", "TAG: "+tag+" STEP TYPE: "+tStep.stepType);
                tempSteps.put(tag, steps.get(tag));
            }

            Log.wtf("WIFILAMP", "BTN TEXT: "+childButton.getText());
        }

        steps = tempSteps;

        mCallback.passData(steps);
    }



    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
