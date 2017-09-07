package async;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.example.ilijaangeleski.example.MainActivity;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import static com.example.ilijaangeleski.example.MainActivity.progress_bar_type;

public class DownloadSongAsyncTask extends AsyncTask<String, String, Boolean> {

        private WeakReference<MainActivity> activityWeakReference;
        private Context context;
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

