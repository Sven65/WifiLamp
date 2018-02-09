package xyz.mackan.wifilamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import xyz.mackan.wifilamp.Steps.StepConstants;
import xyz.mackan.wifilamp.Steps.StepData;
import xyz.mackan.wifilamp.Steps.Stepper;

import static android.app.Activity.RESULT_OK;

public class ButtonFragment extends Fragment implements Button.OnClickListener, Button.OnLongClickListener, InputDialog.InputDialogCallback{

    private ButtonFragment.OnFragmentInteractionListener mListener;

    private SeekBar redBar, greenBar, blueBar;
    private FileUtils fileUtils;
    private View thisView;

    public LinkedHashMap<String, ColorButton> buttonData = new LinkedHashMap<String, ColorButton>();

    public ButtonFragment() {
        // Required empty public constructor
    }

    private String createTransactionID(){
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    private void addButtonsFromSaveFile(String data, View rootView){

        try {
            JSONObject jsonObj = new JSONObject(data);

            // Getting JSON Array node
            JSONArray buttons = jsonObj.getJSONArray("buttons");

            // looping through All buttons
            for (int i = 0; i < buttons.length(); i++) {
                JSONObject button = buttons.getJSONObject(i);

                String name = button.getString("name");
                int r = button.getInt("r");
                int g = button.getInt("g");
                int b = button.getInt("b");

                ColorButton cButton = new ColorButton(r, g, b, name);

                if(button.get("steps") != null){
                    JSONArray steps = button.getJSONArray("steps");
                    LinkedHashMap<String, Step> stepData = new LinkedHashMap<String, Step>();


                    for (int x = 0; x < steps.length(); x++) {
                        StepData stepD = new StepData();
                        JSONObject row = steps.getJSONObject(x);

                        int stepType = row.getInt("stepType");

                        if(stepType == StepConstants.STEP_DELAY){
                            stepD.duration = row.getInt("duration");
                        }else if(stepType == StepConstants.STEP_SET_COLOR){

                            stepD.setR(row.getInt("r"));
                            stepD.setG(row.getInt("g"));
                            stepD.setB(row.getInt("b"));
                        }

                        Step step = new Step(stepType, stepD);

                        Log.wtf("WIFILAMP", "STEP TYPE: "+step.stepType+" RGB: "+step.stepData.r+":"+step.stepData.g+":"+step.stepData.b);

                        stepData.put(createTransactionID(), step);
                    }

                    cButton.setSteps(stepData);
                }

                String id = createTransactionID();

                buttonData.put(id, cButton);

                TableLayout ll = (TableLayout) rootView.findViewById(R.id.buttonTable);

                FrameLayout fl = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.button_holder, null);

                fl.setBackgroundColor(Color.rgb(r, g, b));

                Button btn = (Button)LayoutInflater.from(getContext()).inflate(R.layout.held_button, null);

                btn.setText(cButton.name);

                btn.setTag(id);

                btn.setOnClickListener(this);
                btn.setOnLongClickListener(this);

                fl.addView(btn);

                ll.addView(fl);
            }
        } catch (final JSONException e) {
            Log.e("WIFIBUTTON", "Json parsing error: " + e.getMessage());
        }

        Log.wtf("WIFILAMP", "BUTTON LENGTH FROM SAVE: "+buttonData.size());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ButtonFragment newInstance() {
        ButtonFragment fragment = new ButtonFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_buttongrid, container, false);

        Button saveButton = (Button) rootView.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(this);

        fileUtils = new FileUtils();

        if(fileUtils.fileExists(Constants.FILE_NAME, this.getContext())) {

            String data = fileUtils.readFromFile(Constants.FILE_NAME, this.getContext());

            Log.wtf("WIFILAMP", "DATA: " + data);

            addButtonsFromSaveFile(data, rootView);
        }

        thisView = rootView;

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void saveButtons(){
        Iterator it = buttonData.entrySet().iterator();
        JSONObject object = new JSONObject();
        JSONArray dataArray = new JSONArray();


        try {
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();

                ColorButton colorData = (ColorButton) pair.getValue();

                // Todo: Effect stuff

                JSONObject buttonJSON = new JSONObject();

                buttonJSON.put("name", colorData.name);
                buttonJSON.put("r", colorData.r);
                buttonJSON.put("g", colorData.g);
                buttonJSON.put("b", colorData.b);

                dataArray.put(buttonJSON);

                if(colorData.steps != null){
                    JSONArray steps = new JSONArray();

                    Iterator stepIT = colorData.steps.entrySet().iterator();

                    while(stepIT.hasNext()){
                        Map.Entry stepPair = (Map.Entry) stepIT.next();

                        Step step = (Step) stepPair.getValue();


                        steps.put(step.toJSON());
                    }

                    buttonJSON.put("steps", steps);
                }
            }

            object.put("buttons", dataArray);

            Log.wtf("WIFILAMP", "BUTTONS: \n"+object.toString(4));

            fileUtils.writeToFile(Constants.FILE_NAME, object.toString(), this.getContext());
        }catch(JSONException e){

        }
    }

    @Override
    public void onClick(View view){
        View mainView = ((Activity)this.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);

        redBar = (SeekBar) mainView.findViewById(R.id.seekBarRed);
        greenBar = (SeekBar) mainView.findViewById(R.id.seekBarGreen);
        blueBar = (SeekBar) mainView.findViewById(R.id.seekBarBlue);

        switch(view.getId()){
            case R.id.saveButton:

                int red = redBar.getProgress();
                int green = greenBar.getProgress();
                int blue = blueBar.getProgress();

                String id = createTransactionID();//Long.toString(System.currentTimeMillis()*10, 36);

                ColorButton cButton = new ColorButton(red, green, blue);

                Log.wtf("WIFILAMP", "ID: "+id);

                TableLayout ll = (TableLayout) mainView.findViewById(R.id.buttonTable);

                FrameLayout fl = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.button_holder, null);

                fl.setBackgroundColor(Color.rgb(red, green, blue));

                Button btn = (Button)LayoutInflater.from(getContext()).inflate(R.layout.held_button, null);

                buttonData.put(id, cButton);

                btn.setText(red+":"+green+":"+blue);

                btn.setTag(id);

                btn.setOnClickListener(this);
                btn.setOnLongClickListener(this);

                fl.addView(btn);
                saveButtons();

                ll.addView(fl);

                //getInput("Name", id, this);

                Bundle meta = new Bundle();

                meta.putString("BUTTON_ID", id);

                InputDialog inputDialog = new InputDialog();
                inputDialog.getInput(getContext(), getView(), "Name", meta, this);

                break;
            default:
                //Button currentButton = (Button) mainView.findViewById(view.getId());

                String viewTag = ""+view.getTag();

                final ColorButton colorData = buttonData.get(viewTag);

                Log.wtf("WIFILAMP", ""+view.getTag());

                Log.wtf("WIFILAMP", ""+colorData);

                redBar.setProgress(colorData.r);
                greenBar.setProgress(colorData.g);
                blueBar.setProgress(colorData.b);

                if(colorData.steps != null) {
                    if (colorData.steps.size() > 0) {
                        Step lastStep = null;

                        Iterator iterator = colorData.steps.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry pair = (Map.Entry) iterator.next();

                            lastStep = (Step) pair.getValue();
                        }

                        /*if(lastStep != null) {

                            if (lastStep.stepType == StepConstants.STEP_OFF) {
                                if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 24) {
                                    redBar.setProgress(0, true);
                                    greenBar.setProgress(0, true);
                                    blueBar.setProgress(0, true);
                                }else{
                                    redBar.setProgress(0);
                                    greenBar.setProgress(0);
                                    blueBar.setProgress(0);
                                }
                            }else if(lastStep.stepType == StepConstants.STEP_SET_COLOR){
                                if(Integer.valueOf(android.os.Build.VERSION.SDK) >= 24){
                                    redBar.setProgress(lastStep.stepData.r, true);
                                    greenBar.setProgress(lastStep.stepData.g, true);
                                    blueBar.setProgress(lastStep.stepData.b, true);
                                }else{
                                    redBar.setProgress(lastStep.stepData.r);
                                    greenBar.setProgress(lastStep.stepData.g);
                                    blueBar.setProgress(lastStep.stepData.b);
                                }

                            }

                        }*/

                        Stepper stepper = new Stepper(colorData, getContext());
                        //stepper.execute();
                        stepper.doHandle();
                    }
                }


            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == Constants.GET_BUTTON_SETTINGS){
            if(resultCode == RESULT_OK){

                View rootView = ((Activity) this.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);

                int ACTION = data.getExtras().getInt("ACTION");

                Button thisButton = (Button) rootView.findViewWithTag(data.getExtras().getString("BUTTON_ID"));

                if(ACTION == Constants.SETTING_EDIT) {

                    ColorButton iButtonData = (ColorButton) data.getExtras().get("BUTTON_DATA");

                    FrameLayout fl = (FrameLayout) thisButton.getParent();

                    buttonData.put(data.getExtras().getString("BUTTON_ID"), iButtonData);

                    thisButton.setText(iButtonData.name);

                    if(fl != null) {

                        fl.setBackgroundColor(Color.rgb(iButtonData.r, iButtonData.g, iButtonData.b));

                    }

                    saveButtons();
                }else if(ACTION == Constants.SETTING_DELETE){
                    buttonData.remove(data.getExtras().getString("BUTTON_ID"));

                    ViewGroup layout = (ViewGroup) thisButton.getParent();

                    if(layout != null){
                        layout.removeView(thisButton);
                    }

                    saveButtons();
                }


            }
        }
    }

    @Override
    public boolean onLongClick(View view){

        View rootView = ((Activity)this.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);

        final Button thisButton = (Button) view;

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(getActivity(), ButtonSettings.class);

                String tag = ""+thisButton.getTag();

                Log.wtf("BUTTONFRAG", "BID: "+tag);

                ColorButton cb = buttonData.get(tag);

                mainIntent.putExtra("BUTTON_ID", tag);
                mainIntent.putExtra("BUTTON_DATA", cb);

                startActivityForResult(mainIntent, Constants.GET_BUTTON_SETTINGS);


            }
        };

        runnable.run();

        return true;
    }

    @Override
    public void inputDialogCallback(Object ret, Object meta) {
        Bundle metaData = (Bundle) meta;
        String buttonID = (String) metaData.getString("BUTTON_ID");

        if(!metaData.isEmpty()) {

            String name = ret.toString();

            ColorButton currentButton = (ColorButton) buttonData.get(buttonID);
            Button buttonView = (Button) getView().getRootView().findViewWithTag(buttonID);

            currentButton.name = name;

            buttonView.setText(name);

            buttonData.put(buttonID, currentButton);

            saveButtons();
        }
    }

    @Override
    public void inputDialogCancelCallback(Object ret, Object meta) {
        Bundle metaData = (Bundle) meta;
        String buttonID = (String) metaData.getString("BUTTON_ID");

        buttonData.remove(buttonID);

        View rootView = ((Activity) this.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);


        Button thisButton = (Button) rootView.findViewWithTag(buttonID);

        ViewGroup layout = (ViewGroup) thisButton.getParent();

        if(layout != null){
            layout.removeView(thisButton);
        }

        saveButtons();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
