package xyz.mackan.wifilamp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
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

import static android.app.Activity.RESULT_OK;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ButtonFragment extends Fragment implements Button.OnClickListener, Button.OnLongClickListener, AlertDialogCallback{

    private ButtonFragment.OnFragmentInteractionListener mListener;

    private SeekBar redBar, greenBar, blueBar;
    private FileUtils fileUtils;
    private View thisView;

    private String mString = "";


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

                // TODO: Add code to check for effect

                if(button.get("steps") != null){
                    JSONArray steps = button.getJSONArray("steps");
                    LinkedHashMap<String, Step> stepData = new LinkedHashMap<String, Step>();
                    StepData stepD = new StepData();

                    for (int x = 0; x < steps.length(); x++) {
                        JSONObject row = steps.getJSONObject(x);

                        int stepType = row.getInt("stepType");

                        if(stepType == StepConstants.STEP_DELAY){
                            stepD.duration = row.getInt("duration");
                        }else if(stepType == StepConstants.STEP_SET_COLOR){
                            stepD.r = row.getInt("r");
                            stepD.g = row.getInt("g");
                            stepD.b = row.getInt("b");
                        }

                        Step step = new Step(stepType, stepD);

                        stepData.put(createTransactionID(), step);
                    }

                    cButton.steps = stepData;
                }

                String id = createTransactionID();

                buttonData.put("" + id, cButton);

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

    private void getInput(String title, final String buttonID, final AlertDialogCallback<String, String> callback){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.input_dialog, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.dialogInput);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                dialog.dismiss();


                callback.alertDialogCallback(value, buttonID);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.alertDialogCancelCallback(null, buttonID);
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void alertDialogCancelCallback(Object ret, Object buttonID){
        String bID = buttonID.toString();

        Button buttonView = (Button) getView().getRootView().findViewWithTag(bID);

        ViewGroup layout = (ViewGroup) buttonView.getParent();

        if(layout != null){
            layout.removeView(buttonView);
        }

        buttonData.remove(bID);

        saveButtons();
    }

    @Override
    public void alertDialogCallback(Object ret, Object buttonID) {

        Log.wtf("WIFILAMP",buttonID.toString());

        String bID = buttonID.toString();
        String name = ret.toString();

        ColorButton currentButton = (ColorButton) buttonData.get(bID);
        Button buttonView = (Button) getView().getRootView().findViewWithTag(buttonID);

        currentButton.name = name;

        buttonView.setText(name);

        buttonData.put(bID, currentButton);



        saveButtons();
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

                getInput("Name", id, this);

                break;
            default:
                //Button currentButton = (Button) mainView.findViewById(view.getId());

                String viewTag = ""+view.getTag();

                ColorButton colorData = buttonData.get(viewTag);

                Log.wtf("WIFILAMP", ""+view.getTag());

                Log.wtf("WIFILAMP", ""+colorData);

                // TODO: Add code to check if the button has an effect and trigger it if it does

                redBar.setProgress(colorData.r);
                greenBar.setProgress(colorData.g);
                blueBar.setProgress(colorData.b);
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

                mainIntent.putExtra("BUTTON_ID", ""+thisButton.getTag());
                mainIntent.putExtra("BUTTON_DATA", buttonData.get(""+thisButton.getTag()));

                startActivityForResult(mainIntent, Constants.GET_BUTTON_SETTINGS);


            }
        };

        runnable.run();

        return true;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
