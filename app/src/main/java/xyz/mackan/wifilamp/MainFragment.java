package xyz.mackan.wifilamp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainFragment extends Fragment implements DiscoverTask.DiscoverCallback, SeekBar.OnSeekBarChangeListener, Button.OnClickListener{
    final String LOG_TAG = MainFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    SeekBar redBar, greenBar, blueBar;
    Button connectButton;
    TextView textView, redText, greenText, blueText;
    ImageView colorBox;

    View rootView;

    FrameLayout cbHolder;

    int currentColor;
    boolean stopScroll = false;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        colorBox = (ImageView) rootView.findViewById(R.id.colorBox);

        // seekbars
        redBar = (SeekBar) rootView.findViewById(R.id.seekBarRed);
        greenBar = (SeekBar) rootView.findViewById(R.id.seekBarGreen);
        blueBar = (SeekBar) rootView.findViewById(R.id.seekBarBlue);
        redBar.setOnSeekBarChangeListener(this);
        blueBar.setOnSeekBarChangeListener(this);
        greenBar.setOnSeekBarChangeListener(this);

        redBar.setEnabled(false);
        greenBar.setEnabled(false);
        blueBar.setEnabled(false);

        connectButton = (Button) rootView.findViewById(R.id.button);

        connectButton.setOnClickListener(this);

        cbHolder = (FrameLayout) rootView.findViewById(R.id.connectButtonHolder);

        textView = (TextView) rootView.findViewById(R.id.textView);

        redText = (TextView) rootView.findViewById(R.id.textViewRed);
        greenText = (TextView) rootView.findViewById(R.id.textViewGreen);
        blueText = (TextView) rootView.findViewById(R.id.textViewBlue);

        redText.setText(getString(R.string.red)+": 0");
        greenText.setText(getString(R.string.green)+": 0");
        blueText.setText(getString(R.string.blue)+": 0");

        setColorBox(0,0,0, "Wifi Lamp - Not connected");

        searchForDevices();



        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view){
        Log.wtf("WIFILAMP", "ID: "+view.getId());
        switch(view.getId()){
            case R.id.button:
                //connectButton.setVisibility(View.INVISIBLE);
                cbHolder.setVisibility(View.INVISIBLE);
                searchForDevices();
            break;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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



    // methods for seek bar listener

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
        // colors changed so send packet to led strip
        changeColor();
    }

    public void onStartTrackingTouch(SeekBar seekBar){

    }

    public void onStopTrackingTouch(SeekBar seekBar){

    }



    /**
     * method for Discover task callback
     */
    public void onFoundDevice(){
        // we found a lamp!
        // what do we do now?

        // for debug only
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);
        String ipString = preferences.getString(Constants.PREFERENCES_IP_ADDR, "");
        Log.v(LOG_TAG, "onDeviceFound() ipAddr: " + ipString);

        textView.setText(getString(R.string.connected_to)+" "+ipString);

        redBar.setEnabled(true);
        greenBar.setEnabled(true);
        blueBar.setEnabled(true);

        int savedRed = preferences.getInt(Constants.PREFERENCES_RED, 0);
        int savedGreen = preferences.getInt(Constants.PREFERENCES_GREEN, 0);
        int savedBlue = preferences.getInt(Constants.PREFERENCES_BLUE, 0);

        redBar.setProgress(savedRed);
        greenBar.setProgress(savedGreen);
        blueBar.setProgress(savedBlue);

        this.changeColor();
    }


    /**
     * Start DiscoverTask to search for devices on local network.
     */
    public void searchForDevices(){
        Log.v(LOG_TAG, "starting DiscoverTask");
        new DiscoverTask(getContext(), this).execute(null, null);
    }

    public void setColorBox(int r, int g, int b){
        String title = String.format("#%02x%02x%02x", r, g,b);

        setColorBox(r, g, b, title);
    }

    public void setColorBox(int r, int g, int b, String title){
        colorBox.setBackgroundColor(Color.rgb(r, g, b));

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

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

    /**
     * Gets color data from seekbars and starts ChangeColorTask
     * to send a packet and change the color of the LEDs
     */
    public void changeColor(){
        int r = redBar.getProgress();
        int g = greenBar.getProgress();
        int b = blueBar.getProgress();

        redText.setText(getString(R.string.red)+": "+r);
        greenText.setText(getString(R.string.green)+": "+g);
        blueText.setText(getString(R.string.blue)+": "+b);

        setColorBox(r, g, b);


        new ChangeColorTask(getContext()).execute(r, g, b);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
