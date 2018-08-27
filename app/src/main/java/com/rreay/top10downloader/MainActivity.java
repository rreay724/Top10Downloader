package com.rreay.top10downloader;

import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";  // changed limit=10 to limit=%d. A way of specifying an integer value that will be replaced by an actual value by the String.format method.
    private int feedLimit = 10;
    private String feedCachedUrl = "INVALIDATED";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = findViewById(R.id.xmlListView);

        // since we are using a bundle, this will check that the bundle isn't null
        if(savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        // passes the string provided in feedUrl as well as the feedLimit
        downloadUrl(String.format(feedUrl, feedLimit));

//        Log.d(TAG, "onCreate: starting AsyncTask");
//        DownloadData downloadData = new DownloadData();
//        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"); // can change limit= to 25 or 30 or anything to list top apps
//        Log.d(TAG, "onCreate: done");

    }

    // onCreateOptionsMenu is called when it's time to inflate the activities menu. That's create the menu objects from the xml file.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        // Write code to set correct menu limit once you've restored the feedLimit value
        if(feedLimit == 10){
            menu.findItem(R.id.mnu10).setChecked(true);
        }else{
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    // This is the Override method that shows information on the screen when you make selections from the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnuAlbums:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topalbums/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit; // since feedLimit = 10, if activated 35 - 10 is 25
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " feedLimit unchanged");
                }
                break;
            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;
            default: // this line is important and should always be added in. Possible to create submenus and android triggers a call to this method when the sub menus open.
                return super.onOptionsItemSelected(item);

        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void downloadUrl(String feedUrl) {
        // add code in "if" statement below to prevent from url downloading unnecessarily
        if (!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            Log.d(TAG, "downloadUrl: starting AsyncTask");
            DownloadData downloadData = new DownloadData();
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl; // this checks to confirm we're not redownloading the same url again
            Log.d(TAG, "onCreate: done");
        }else{
            Log.d(TAG, "downloadUrl: URL not changed");
        }

    }

    /**
     * This will create a new class in Main Activity for the AsyncTask.
     * String - pass url to RSS feed, Void - used if you want to display a progress bar, will not be using,
     * String - type of the result we want to get back. All of our XML will be in a String
     * <p>
     * Will implement the doInBackground method. Tells Android what code we want to run in the background
     * <p>
     * Will also implement the onPostExecute method that Android will call when the job is done so that
     * we are notified when the task is finished.
     **/


    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";


        /**
         * Will enter ArrayAdapter under onPostExecute method
         **/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s); // "s" is the XML that the android framework has sent after downloading in the doInBackground method

            // Create a new ArrayAdapter object and entered 3 parameters - Context, resource containing text view, list of objects to display. Last line is to use setAdapter method to tell adapter what it should use to get it's data
//            ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<>(
//                    MainActivity.this, R.layout.list_item, parseApplications.getApplications());
//            listApps.setAdapter(arrayAdapter);
            // the below line will use the FeedAdapter set in FeedAdapter.java and send it to list_record.xml
            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);
        }


        /**
         * String... - The ... is called an ellipsis. It allows you to pass any number of objects of a specific type,
         * in this case they are type String.
         * <p>
         * Added String rssFeed code. When using ellipsis, the values get passed into the method as an array, an array of Strings in this case.
         * Because we're only calling this method with a single parameter, we don't bother writing code to get more than one string. The one we're interested in
         * is String 0.
         * <p>
         * Use Log.e because it is logging the message as an error instead of the debug level.
         * <p>
         * downloadXML will run on the background thread
         **/
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        /**
         * Start by opening http connection which we use to access the stream of data coming through a url. Will use input stream reader to read from stream.
         * When dealing with a data stream that is coming from a slow device or connection, it's good to use a buffered reader. Buffers the data
         * coming in from the stream. A block of data is read into the buffer in memory, and our program can read from the buffer.
         * <p>
         * Added a try block. When dealing with data from an external source, there are lots of things that can go wrong. Connection may drop,
         * device may not be connected to the internet, apple site might be down, URL may be invalid. Try block wraps up section of code and capture
         * exceptions that occur while it's executed.
         * <p>
         * 2 different types of exceptions. Checked exceptions must be dealt with or code won't compile. First checked exception is the URL. Error claiming malformed
         * URL exception. Runtime exception indicates problem with code, such as null point exception. Will use catch block to deal with exception
         * <p>
         * url.openConnection, getResponseCode, getInputStream errors - related to an IO exception. Add another catch block. The order
         * you catch exceptions is important. e.getMessage gives us more information in relation to what the error was.
         * <p>
         * The BufferedReader buffers the inputStreamReader and the bufferedReader is what we will use to read the XML.
         * <p>
         * One more exception to deal with. Android requires app to be granted permissions to do certain things, such as access the internet.
         * Read data in an append it to the Buffer. Will set up a character array that is filled with characters from the buffer.
         * <p>
         * NOTE: ADDED INTERNET ACCESS PERMISSION IN MANIFEST FILE
         **/

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();

            try {
                URL url = new URL(urlPath);         // used catch block to clear error here (MalformedURL)
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader reader = new BufferedReader(inputStreamReader);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                /** Below is the input buffer character array to store 500 characters. If you expect a larger download, would increase more than 500.
                 *  The while loop will keep going around until the end of the input stream is reached.
                 *  When end of input stream is reached, the loop terminates and we close the BufferedReader.
                 *  reader.close() will automatically close the inputStreamReader and the input stream **/
                int charsRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charsRead = reader.read(inputBuffer);
                    if (charsRead < 0) {
                        break;
                    }
                    if (charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                    }
                }
                reader.close();

                return xmlResult.toString();
            } catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception. Needs permission? " + e.getMessage());
//                e.printStackTrace(); // will print the entire stack trace in the catch block
            }
            return null;
        }
    }
}
