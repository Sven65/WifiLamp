package xyz.mackan.wifilamp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputDialog {

    public interface InputDialogCallback<T, Bundle> {
        public void inputDialogCallback(T ret, Bundle meta);
        public void inputDialogCancelCallback(T ret, Bundle meta);

    }

    public InputDialog(){

    }

    public void getInput(Context context, View view, String title, final Bundle meta, final InputDialogCallback<String, Bundle> callback){
        getInput(context, view, title, meta, InputType.TYPE_CLASS_TEXT, callback);
    }

    /**
     * Prompts the user for text input
     * @param context The context to show
     * @param view The view to show the dialog in
     * @param title The title to use for the prompt
     * @param meta A bundle to use for metadata
     * @param callback The callbacks
     */
    public void getInput(Context context, View view, String title, final Bundle meta, int inputType, final InputDialogCallback<String, Bundle> callback){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        View viewInflated = LayoutInflater.from(context).inflate(R.layout.input_dialog, (ViewGroup) view, false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.dialogInput);

        input.setInputType(inputType);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        input.requestFocus();

        //if(input.requestFocus()) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
        //}

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value = input.getText().toString();
                dialog.dismiss();


                callback.inputDialogCallback(value, meta);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.inputDialogCancelCallback(null, meta);
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }
}
