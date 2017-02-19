package com.nagkaustav.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView listApps;

    private int feedLimit = 10;
    private  String TOP_10_FREE_APP = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private  String TOP_25_PAID_APP = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
    private  String TOP_10_SONGS = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);
        downloadURL(String.format(TOP_10_FREE_APP, feedLimit));
    }


    //time to inflate activity menu. (Create menu object from xml file)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);

        //set the correct menu limit once you have restored the feedlimit value (orientation change)
        if(feedLimit == 10){
            menu.findItem(R.id.menu10).setChecked(true);
        }else{
            menu.findItem(R.id.menu25).setChecked(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String feedURL=TOP_10_FREE_APP;

        switch (id) {
            case R.id.menuFree:
                feedURL = TOP_10_FREE_APP;
                break;
            case R.id.menuPaid:
                feedURL = TOP_25_PAID_APP;
                break;
            case R.id.menuSongs:
                feedURL = TOP_10_SONGS;
                break;
            case R.id.menu10:
            case R.id.menu25:
                if (!(item.isChecked())) {
                    item.setCheckable(true);
                    feedLimit = 35 - feedLimit;
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedlist to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + "feedLimit unchanged");
                }
                break;
            default:
                //calls this when submenus are involved
                return super.onOptionsItemSelected(item);
        }
        downloadURL(String.format(feedURL, feedLimit));
        return true;
    }

    private void downloadURL(String feedURL) {
        Log.d(TAG, "downloadURL: AsyncTask begins");
        DownloadData downloadData = new DownloadData();
        downloadData.execute(feedURL);
        Log.d(TAG, "downloadURL: AsyncTask Ends");
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
            //   Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications()
//            );

            FeedAdapter feedAdapter = new FeedAdapter(
                    MainActivity.this, R.layout.list_record, parseApplications.getApplications()
            );
            listApps.setAdapter(feedAdapter);
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
