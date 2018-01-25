package xyz.mackan.wifilamp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TableLayout;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ButtonFragment extends Fragment implements Button.OnClickListener, Button.OnLongClickListener {

    private ButtonFragment.OnFragmentInteractionListener mListener;

    private SeekBar redBar, greenBar, blueBar;
    private FileUtils fileUtils;


    private LinkedHashMap<String, ColorButton> buttonData = new LinkedHashMap<String, ColorButton>();

    public ButtonFragment() {
        // Required empty public constructor
    }

    private void addButtonsFromSaveFile(String data, View rootView){

        String buttons[] = data.split("\\r?\\n");

        Log.wtf("BUTTONS SAVE LENGTH", ""+buttons.length);

        for(String color : buttons){
            ColorButton cButton;

            String[] colorData = color.split(":");

            if(colorData.length >= 3) {

                int red = Integer.parseInt(colorData[0]);
                int green = Integer.parseInt(colorData[1]);
                int blue = Integer.parseInt(colorData[2]);

                String id = Long.toString(System.currentTimeMillis(), 36);

                cButton = new ColorButton(red, green, blue);

                if(colorData.length >= 4){

                    String name = "";
                    String[] nameParts;

                    // TODO Make a loop from colorData[3] to end and add to string "name" with ":" as delimiter

                    for(int i=3;i<colorData.length;i++){
                        name += colorData[i];
                    }

                    cButton = new ColorButton(red, green, blue, name);
                }

                buttonData.put("" + id, cButton);

                TableLayout ll = (TableLayout) rootView.findViewById(R.id.buttonTable);

                Button btn = new Button(this.getContext());
                btn.setText(cButton.name);
                btn.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                btn.setTag(id);

                btn.setOnClickListener(this);
                btn.setOnLongClickListener(this);

                ll.addView(btn);
            }
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

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void saveButtons(){
        String data = "";

        Log.wtf("WIFILAMP", "BUTTON LENGTH: "+buttonData.size());

        Iterator it = buttonData.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            ColorButton colorData = (ColorButton) pair.getValue();

            //it.remove();

            stringBuilder.append(colorData.r+":"+colorData.g+":"+colorData.b+" "+colorData.name+"\r\n");

        }

        data = stringBuilder.toString();

        data += "\r\n";

        Log.wtf("WIFILAMP", "WRITNG: "+data);

        fileUtils.writeToFile(Constants.FILE_NAME, "\r\n"+data, this.getContext());

        //fileUtils.writeObjectToFile(Constants.FILE_NAME, buttonData, this.getContext());
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

                String id = Long.toString(System.currentTimeMillis(), 36);

                Log.wtf("WIFILAMP", "ID: "+id);

                buttonData.put(id, new ColorButton(red, green, blue));

                TableLayout ll = (TableLayout) mainView.findViewById(R.id.buttonTable);

                Button btn = new Button(this.getContext());
                btn.setText(red+":"+green+":"+blue);
                btn.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                btn.setTag(id);

                btn.setOnClickListener(this);
                btn.setOnLongClickListener(this);

                ll.addView(btn);
                saveButtons();


                break;
            default:
                //Button currentButton = (Button) mainView.findViewById(view.getId());

                String viewTag = ""+view.getTag();

                ColorButton colorData = buttonData.get(viewTag);

                Log.wtf("WIFILAMP", ""+view.getTag());

                Log.wtf("WIFILAMP", ""+colorData);

                redBar.setProgress(colorData.r);
                greenBar.setProgress(colorData.g);
                blueBar.setProgress(colorData.b);
            break;
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

                ButtonFragment.this.startActivity(mainIntent);
            }
        };

        runnable.run();

        return true;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
