package xyz.mackan.wifilamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.LinkedHashMap;

// TODO: Code for adding effects

public class ButtonSettingsFragment extends Fragment implements Button.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private ButtonSettingsFragment.OnFragmentInteractionListener mListener;

    private String BUTTON_ID = "";
    private ColorButton BUTTON_DATA;

    private SeekBar redBar, greenBar, blueBar;
    private TextView redText, greenText, blueText;

    private View rootView;

    public ButtonSettingsFragment() {
        // Required empty public constructor
    }

    public static ButtonSettingsFragment newInstance() {
        ButtonSettingsFragment fragment = new ButtonSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //Log.wtf("WIFILAMP", "ARG LEN: "+getArguments().size());

        Bundle bundle = getActivity().getIntent().getExtras();

        if(bundle != null){
            Log.wtf("WIFILAMP", "BUTTON "+bundle.get("BUTTON_ID"));
            BUTTON_ID = bundle.getString("BUTTON_ID");

            BUTTON_DATA = (ColorButton) bundle.get("BUTTON_DATA");

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (ButtonSettingsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_buttonsettings, container, false);

        EditText nameInput = (EditText) rootView.findViewById(R.id.nameInput);

        nameInput.setText(BUTTON_DATA.name);

        Button saveButton = (Button) rootView.findViewById(R.id.saveSettingsButton);
        Button deleteButton = (Button) rootView.findViewById(R.id.deleteButton);

        saveButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        redBar = (SeekBar) rootView.findViewById(R.id.seekBarRed);
        greenBar = (SeekBar) rootView.findViewById(R.id.seekBarGreen);
        blueBar = (SeekBar) rootView.findViewById(R.id.seekBarBlue);

        redText = (TextView) rootView.findViewById(R.id.textViewRed);
        greenText = (TextView) rootView.findViewById(R.id.textViewGreen);
        blueText = (TextView) rootView.findViewById(R.id.textViewBlue);

        redBar.setOnSeekBarChangeListener(this);
        greenBar.setOnSeekBarChangeListener(this);
        blueBar.setOnSeekBarChangeListener(this);

        redBar.setProgress(BUTTON_DATA.r);
        greenBar.setProgress(BUTTON_DATA.g);
        blueBar.setProgress(BUTTON_DATA.b);

        redText.setText(getString(R.string.red)+": "+BUTTON_DATA.r);
        greenText.setText(getString(R.string.green)+": "+BUTTON_DATA.g);
        blueText.setText(getString(R.string.blue)+": "+BUTTON_DATA.b);

        setColor(BUTTON_DATA.r, BUTTON_DATA.g, BUTTON_DATA.b);

        return rootView;
    }

    @Override
    public void onClick(View v){
        View rootView = ((Activity)this.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);

        Intent data = new Intent();

        switch(v.getId()){
            case R.id.saveSettingsButton:

                EditText name = (EditText) rootView.findViewById(R.id.nameInput);

                BUTTON_DATA.name = name.getText().toString();
                BUTTON_DATA.r = redBar.getProgress();
                BUTTON_DATA.g = greenBar.getProgress();
                BUTTON_DATA.b = blueBar.getProgress();

                LinkedHashMap<String, Step> stepData = ((ButtonSettings)this.getActivity()).getSteps();

                if(!stepData.isEmpty() && stepData != null){
                    BUTTON_DATA.steps = stepData;
                }

                data.putExtra("BUTTON_ID", BUTTON_ID);
                data.putExtra("BUTTON_DATA", BUTTON_DATA);
                data.putExtra("ACTION", Constants.SETTING_EDIT);



                getActivity().setResult(Activity.RESULT_OK, data);

                getActivity().finish();
            break;
            case R.id.deleteButton:
                // TODO: Add a "Really delete button" dialog
                data.putExtra("BUTTON_ID", BUTTON_ID);
                data.putExtra("BUTTON_DATA", BUTTON_DATA);
                data.putExtra("ACTION", Constants.SETTING_DELETE);

                getActivity().setResult(Activity.RESULT_OK, data);

                getActivity().finish();
            break;
        }
    }

    public void setColor(int r, int g, int b){

        View view = getView();
        if(view != null) {
            ImageView colorBox = (ImageView) getView().findViewById(R.id.colorBox);

            colorBox.setBackgroundColor(Color.rgb(r, g, b));
        }


        String title = String.format("Settings - %s - #%02x%02x%02x", BUTTON_DATA.name, r, g,b);

        android.support.v7.app.ActionBar actionBar = ((ButtonSettings)getActivity()).getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(r, g, b)));

        SpannableString s = new SpannableString(title);

        if((r*0.299 + g*0.587 + b*0.114) > 186){
            // black
            s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }else{
            //white
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        actionBar.setTitle(s);

    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){

        switch(seekBar.getId()){
            case R.id.seekBarRed:
                redText.setText(getString(R.string.red)+": "+progress);
            break;
            case R.id.seekBarGreen:
                greenText.setText(getString(R.string.green)+": "+progress);
            break;
            case R.id.seekBarBlue:
                blueText.setText(getString(R.string.blue)+": "+progress);
            break;
        }

        this.setColor(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress());
    }

    public void onStartTrackingTouch(SeekBar seekBar){

    }

    public void onStopTrackingTouch(SeekBar seekBar){

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
