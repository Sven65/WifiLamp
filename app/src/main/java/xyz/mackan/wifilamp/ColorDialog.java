package xyz.mackan.wifilamp;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorDialog implements SeekBar.OnSeekBarChangeListener{
    private TextView redText, greenText, blueText;
    private SeekBar redBar, greenBar, blueBar;
    private ImageView colorBox;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setColorBox(redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ColorDialogCallback<T, Integer> {
        public void colorDialogCallback(T ret, Bundle meta, int r, int g, int b);
        public void colorDialogCancelCallback(T ret);
    }

    public ColorDialog(){

    }

    private void setColorBox(int r, int g, int b){
        colorBox.setBackgroundColor(Color.rgb(r, g, b));
    }

    /**
     * Prompts the user for color input
     * @param context The context to show
     * @param meta The metadata to return
     * @param view The view to show the dialog in
     * @param title The title to use for the prompt
     * @param callback The callbacks
     */
    public void getInput(Context context, final Bundle meta, View view, String title, final ColorDialogCallback<Integer, Integer> callback){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.color_dialog, (ViewGroup) view, false);
        // Set up the input
        colorBox = (ImageView) viewInflated.findViewById(R.id.colorBox);

        redBar = (SeekBar) viewInflated.findViewById(R.id.seekBarRed);
        greenBar = (SeekBar) viewInflated.findViewById(R.id.seekBarGreen);
        blueBar = (SeekBar) viewInflated.findViewById(R.id.seekBarBlue);

        redText = (TextView) viewInflated.findViewById(R.id.textViewRed);
        greenText = (TextView) viewInflated.findViewById(R.id.textViewGreen);
        blueText = (TextView) viewInflated.findViewById(R.id.textViewBlue);

        redBar.setOnSeekBarChangeListener(this);
        greenBar.setOnSeekBarChangeListener(this);
        blueBar.setOnSeekBarChangeListener(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();


                callback.colorDialogCallback(0, meta, redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.colorDialogCancelCallback(null);
                dialog.cancel();
            }
        });

        builder.show();
    }
}
