package com.example.ilijaangeleski.example;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    //TODO handle permisssions
    //TODO write askyncTask Test
    //TODO list and play song

    private Button downloadBtn;
    private ProgressDialog progressDialog;
    private static final int progress_bar_type = 0;
    private static String downloadURL = "http://www.noiseaddicts.com/samples_1w72b820/2541.mp3";
    private static final String TAG = MainActivity.class.getName();
    private static final int MY_REQUEST_PERMISSION=0;
    private boolean permissionGranted=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_REQUEST_PERMISSION);
        }


        downloadBtn = (Button) findViewById(R.id.downloadBtn);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(permissionGranted){
                        onDownloadPressed();
                    }

                }
            });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_REQUEST_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    permissionGranted=true;
                }else{
                    permissionGranted=false;
                    Toast.makeText(getApplicationContext(),"This app requeires external storage permission",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void onDownloadPressed() {
        if (isNetworkConnected()) {
            downloadBtn.setEnabled(false);
            File file = new File(Environment.getExternalStorageDirectory().getPath()+"/jai_ho.mp3");

            if (file.exists()) {
                file.delete();
                Toast.makeText(getApplicationContext(), "File already exist. Will be overridden !", Toast.LENGTH_LONG).show();
            }

            new DownloadSongAsyncTask(this).execute(downloadURL);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet connection !", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type:
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Downloading Mp3 file. Please wait");
                progressDialog.setIndeterminate(false);
                progressDialog.setMax(100);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.show();
                return progressDialog;
            default:
                return null;
        }
    }

    private static class DownloadSongAsyncTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<MainActivity> activityWeakReference;

        public DownloadSongAsyncTask(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.showDialog(progress_bar_type);
            }
        }

        @Override
        protected Boolean doInBackground(String... f_url) {
            int count;
            boolean downloaded = false;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(),10*1024);
                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+"/jai_ho.mp3");
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                downloaded = true;

                try {
                    output.close();
                }catch (Exception ignored){
                }

                try {
                    output.flush();
                }catch (Exception ignored){
                }

                try {
                    input.close();
                }catch (Exception ignored){
                }

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return downloaded;
        }

        protected void onProgressUpdate(String... progress) {
            Log.d("Download", "progress = " + progress);
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.progressDialog.setProgress(Integer.parseInt(progress[0]));
            }
        }

        @Override
        protected void onPostExecute(Boolean downloaded) {
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.dismissDialog(progress_bar_type);
                activity.downloadBtn.setEnabled(true);
                Toast.makeText(activity.getApplicationContext(), "Download complete!", Toast.LENGTH_LONG).show();
            }
        }


    }
}