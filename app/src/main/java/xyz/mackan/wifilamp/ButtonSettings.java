package xyz.mackan.wifilamp;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.ImageView;

import java.util.LinkedHashMap;

public class ButtonSettings extends AppCompatActivity implements ButtonSettingsFragment.OnFragmentInteractionListener, EffectFragment.DataPassListener{

    public LinkedHashMap<String, Step> steps = new LinkedHashMap<String, Step>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttonsettings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //String BUTTON_ID = getIntent().getStringExtra("BUTTON_ID");

        ColorButton data = (ColorButton) getIntent().getExtras().get("BUTTON_DATA");

        getSupportActionBar().setTitle(getString(R.string.settings)+" - "+data.name);

        ImageView colorBox = (ImageView) findViewById(R.id.colorBox);

        colorBox.setBackgroundColor(Color.rgb(data.r, data.g, data.b));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Title bar back press triggers onBackPressed()
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Both navigation bar back press and title bar back press will trigger this method
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void passData(LinkedHashMap<String, Step> data) {
        steps = data;
    }

    public LinkedHashMap<String, Step> getSteps(){
        return steps;
    }
}
