package com.rreay.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApplications {

    //TODO create log entries
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    // TODO create constructor. Generate constructor and select none
    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    // TODO create getter for ArrayList
    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    // TODO Write method to parse and manipulate data stream

    /**
     * Every time we get a new entry, we need to create a new feed entry object. Stored in FeedEntry.
     * inEntry makes sure we're not looking at the tags inside an entry, such as name under author.
     * textValue used to store the value of the current tag. Will assign to appropriate field
     **/
    public boolean parse(String xmlData) {
        boolean status = true;
        FeedEntry currentRecord = null; // added null because currentRecord below had an error demanding a value
        boolean inEntry = false;
        String textValue = "";

        /** Can look up Pull Parsing online. API provides Factory, will produce PullParser object for you.
         *  Class factories are common and used when you don't know which class will be used.
         *  **/
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData)); // StringReader is a class that treats a String like a stream.
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) { // this will cause it to parse until we reach the end
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        if ("entry".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        if (inEntry) {
                            if ("entry".equalsIgnoreCase(tagName)) { // checks to see if tagName is entry
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(tagName)) {
                                currentRecord.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(tagName)) {
                                currentRecord.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(tagName)) {
                                currentRecord.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(tagName)) {
                                currentRecord.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(tagName)) {
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;

                    default:
                        // nothing else to do.
                }
                eventType = xpp.next();
            }
//            for (FeedEntry app : applications) {
//                Log.d(TAG, "***********************");
//                Log.d(TAG, app.toString());
//            }

        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
