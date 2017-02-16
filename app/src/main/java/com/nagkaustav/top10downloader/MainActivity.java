package com.nagkaustav.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: AsyncTask begins");
        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=10/xml");
        Log.d(TAG, "onCreate: AsyncTask Ends");
    }

    //inner Class since this class will only be used by MainActivity class
    private class DownloadData extends AsyncTask<String, Void, String> {
        /**
         * String : URL of the rss feed
         * Void: progress bar which we wont need since download is small
         * String: XML we will retrieve from the url
         */
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            //executed after doInBackground is completed
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: parameter is " + s);
        }

        @Override
        protected String doInBackground(String... params) {
            //this method is executed on DownloadData.execute call
            Log.d(TAG, "doInBackground: begins with " + params[0]);
            String rssFeed = downloadXML(params[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlresult = new StringBuilder();

            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response was " + response);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);

                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlresult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }

                reader.close();

            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: SecurityException: " + e.getMessage());
            }
            return String.valueOf(xmlresult);
        }
    }

}
