package xyz.mackan.wifilamp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class ChangeColorTask extends AsyncTask<Integer, Void, Integer> {

    final static int SUCCESS = 0;
    final static int FAILURE = 1;

    final int PORT = 2390;
    final int RESPONSE_PORT = 55056;

    final String LOG_TAG = ChangeColorTask.class.getSimpleName();

    Context mContext;

    public ChangeColorTask(Context context){
        mContext = context;
    }

    public void onPostExecute(Integer result) {
        if (result == SUCCESS){
            Log.v(LOG_TAG, "success");
        } else {
            Log.e(LOG_TAG, "error");
        }
    }

    public Integer doInBackground(Integer... params){
        long BeforeTime = System.currentTimeMillis();

        int red = params[0];
        int green = params[1];
        int blue = params[2];

        // scale colors from max 255 to max 1023
        red = (int) ((red / 255.0) * 1023);
        green = (int) ((green / 255.0) * 1023);
        blue = (int) ((blue / 255.0) * 1023);

        Log.v(LOG_TAG, "red: " + red + ", green: " + green + ", blue: " + blue);

        DatagramSocket socket = null;

        InetAddress address;
        try {
            String ipAddrString = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0)
                    .getString(Constants.PREFERENCES_IP_ADDR, "");
            // remove slash at the beginning
            ipAddrString = ipAddrString.substring(1);
            address = InetAddress.getByName(ipAddrString);
        } catch (Exception e) {
            Log.w(LOG_TAG, "No valid IP in SharedPreferences");
            e.printStackTrace();

            View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar snackbar = Snackbar.make(rootView, mContext.getString(R.string.no_valid_ip), Snackbar.LENGTH_LONG);

            snackbar.show();

            return null;
        }

        SharedPreferences preferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);
        preferences.edit()
                .putInt(Constants.PREFERENCES_RED, params[0])
                .putInt(Constants.PREFERENCES_GREEN, params[1])
                .putInt(Constants.PREFERENCES_BLUE, params[2])
                .apply();

        // packet as a string
        String packetContents = red + ":" + green + ":" + blue;

        try {
            // open a socket
            socket = new DatagramSocket(RESPONSE_PORT);
            // packet contents
            byte[] bytes = packetContents.getBytes();
            // send a packet with above contents to specified ip and port
            Log.v(LOG_TAG, "sending packet to " + address.toString());
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            socket.send(packet);

            // listen for a response
            byte[] response = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(response, response.length);
            socket.setSoTimeout(1000);

            String text = "";
            try {
                Log.v(LOG_TAG, "Listening for a response");
                socket.receive(responsePacket);
                text = new String(response, 0, responsePacket.getLength());
                Log.v(LOG_TAG, "Received packet.  contents: " + text);
            } catch (SocketTimeoutException e) {
                Log.w(LOG_TAG, "Socket timed out");

                return FAILURE;
            }

            if(text.equals("acknowledged")) {
                return SUCCESS;
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Error in ChangeColorTask doInBackground()");
            e.printStackTrace();

            return FAILURE;
        } finally {
            if(socket != null) {
                socket.close();
            }
        }

        return FAILURE;
    }
}