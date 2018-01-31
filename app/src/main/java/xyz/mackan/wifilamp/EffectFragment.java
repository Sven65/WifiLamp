package xyz.mackan.wifilamp;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class EffectFragment extends Fragment implements Button.OnClickListener{

    private EffectFragment.OnFragmentInteractionListener mListener;
    private View thisView;

    private Button addEffectButton;

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

        return rootView;
    }

    public void showEffectMenu(){
        CharSequence effects[] = new CharSequence[] {getResources().getString(R.string.EFFECT_BLINK)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(R.string.effect_type);
        builder.setItems(effects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.wtf("WIFILAMP", "EFFECT: "+which);
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
