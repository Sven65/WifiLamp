package xyz.mackan.wifilamp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ButtonSettingsFragment extends Fragment implements Button.OnClickListener {
    private ButtonSettingsFragment.OnFragmentInteractionListener mListener;

    private String BUTTON_ID = "";
    private ColorButton BUTTON_DATA;

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

        saveButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v){
        View rootView = ((Activity)this.getContext()).getWindow().getDecorView().findViewById(android.R.id.content);

        switch(v.getId()){
            case R.id.saveSettingsButton:

                EditText name = (EditText) rootView.findViewById(R.id.nameInput);

                BUTTON_DATA.name = name.getText().toString();

                Intent data = new Intent();

                data.putExtra("BUTTON_ID", BUTTON_ID);
                data.putExtra("BUTTON_DATA", BUTTON_DATA);

                getActivity().setResult(Activity.RESULT_OK, data);

                getActivity().finish();
            break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
