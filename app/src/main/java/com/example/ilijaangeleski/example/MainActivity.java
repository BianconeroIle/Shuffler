package com.example.ilijaangeleski.example;
import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import async.DownloadSongAsyncTask;

public class MainActivity extends AppCompatActivity {

    //TODO handle permisssions
    //TODO write askyncTask Test
    //TODO list and play song
    public ProgressDialog progressDialog;
    public static final int progress_bar_type = 0;
    public Button downloadBtn;


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

}