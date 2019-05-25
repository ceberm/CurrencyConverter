package com.example.tarea_3_cesar_bermudez;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tarea_3_cesar_bermudez.Fragments.CurrencyFragment;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    private static final String key = "463ad430cb38bc671ced5da29fbce731";

    //private static final String url = "http://www.apilayer.net/api/live?access_key=463ad430cb38bc671ced5da29fbce731&format=1";

    private static final String url = "http://www.apilayer.net/api/live?access_key=463ad430cb38bc671ced5da29fbce731&format=1&currencies=USD,EGP,CRC,TRY,EUR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setupUI();

        //solo como un FYI siempre se de be hacer un thread como una tarea asincrona

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Estoy en onStart");

        Context context = getApplicationContext();
        CharSequence text = "";
        int duration = Toast.LENGTH_SHORT;

        if(hasInternetAccess()){
            DownloadWebPageTask task = new DownloadWebPageTask();
            task.execute(new String[] {url});
        }else{
            text="No Internet Access";
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public boolean hasInternetAccess(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();

    }
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mProgress;

        @Override
        protected  void onPreExecute(){
            super.onPreExecute();
            mProgress = new ProgressDialog(MainActivity.this);
            mProgress.setCancelable(false);
            mProgress.setMessage("Downloading API content");
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... url) {
            OkHttpClient client = new OkHttpClient();

            Request request = null;

            try{
                request = new Request.Builder()
                        .url(url[0])
                        .build();
            }catch(Exception e){
                Log.i(TAG,"Error in the URL");
                return "Download Failed";
            }

            Response response = null;

            try{
                response = client.newCall(request).execute();
                if(response.isSuccessful()){
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            mProgress.dismiss();

            try {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                CurrencyFragment showFragment = new CurrencyFragment();
                Bundle bundle = new Bundle();
                JSONObject jsonResponse = new JSONObject(result);
                bundle.putString("jsonResponse", jsonResponse.toString());
                showFragment.setArguments(bundle);
                ft.replace(android.R.id.content, showFragment);
                ft.addToBackStack(null); //Add fragment in back stack
                ft.commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}
