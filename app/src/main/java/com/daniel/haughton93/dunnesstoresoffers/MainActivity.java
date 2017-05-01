package com.daniel.haughton93.dunnesstoresoffers;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLConnection;

import static android.R.attr.button;
import static android.R.id.list;
import static android.app.PendingIntent.getActivity;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity  implements ActivityCompat.OnRequestPermissionsResultCallback {

    //newest pdf in pos 0 in array
    ArrayList<Pdf> downloadedPdfsArrayList = new ArrayList<Pdf>();
    Context thisContext = this;

    private String thisAppDirectory =Environment.getExternalStorageDirectory()+  File.separator + "Android" + File.separator + "data" + File.separator+ "com.software.tatsu.dunnesstoresoffers";
    Button btnFetchData;
    Button btnRequestPermissions;
    ProgressDialog mProgressDialog;
    CharSequence textToastAlready = "You already have the latest offers!";
    CharSequence textToastSuccessful = "Download was successful!";
    //texttoast error occurs when offers cant be found on website,possibly due to internet connection or no offers are on website
    CharSequence textToastError = "no offers found!";
    CharSequence textToastPermissionsDeniedMessage = "You need to give storage permissions for this app to work!Close the app and open it again to be asked for permission again!";
    Toast toastPermissionsDeniedMessage;
    int duration = Toast.LENGTH_LONG;
    Toast toastAlready;
    Toast toastSuccessfull;
    Toast toastError;
    TextView textViewPermissions;

    private static final int REQUEST_WRITE_PERMISSION = 786;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

// instantiate it within the onCreate method


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //
            //permissions granted
            textViewPermissions.setVisibility(View.GONE);
            btnRequestPermissions.setVisibility(View.GONE);
            populateDownloadedPdfArrayListStartup();
            sortPdfArrayListByDate();
            final PdfAdapter adapter = new PdfAdapter(this,downloadedPdfsArrayList);
            ListView listView = (ListView) findViewById(R.id.listViewPdfs);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    openPdf( new File(thisAppDirectory,downloadedPdfsArrayList.get(position).getFileName()));
                }
            });
        }else{
            //if the user denies permissions,show a button to request permissions again and text explaining we need permissions
            textViewPermissions.setVisibility(View.VISIBLE);
            btnRequestPermissions.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewPermissions = (TextView) findViewById(R.id.textViewPermissions);
        btnRequestPermissions = (Button) findViewById(R.id.buttonRequestPermissions);
        btnRequestPermissions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

                }
            }
        });
        toastPermissionsDeniedMessage = Toast.makeText(this,textToastPermissionsDeniedMessage,duration);
        toastPermissionsDeniedMessage.setGravity(Gravity.CENTER,0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);}
        else{

            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                //old android versions,we have permissions

                populateDownloadedPdfArrayListStartup();
                sortPdfArrayListByDate();
                final PdfAdapter adapter = new PdfAdapter(this, downloadedPdfsArrayList);
                ListView listView = (ListView) findViewById(R.id.listViewPdfs);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // TODO Auto-generated method stub
                        openPdf(new File(thisAppDirectory, downloadedPdfsArrayList.get(position).getFileName()));
                    }
                });
            }else{

                textViewPermissions.setText("This app needs storage permissions to work.Give permissions in your settings or re-install the app");
                textViewPermissions.setVisibility(View.VISIBLE);
            }

        }
        //sets up android notifications for new offers,currently not in use
        //scheduleAlarm();


        toastAlready = Toast.makeText(this, textToastAlready, duration);
        toastAlready.setGravity(Gravity.CENTER, 0, 0);
        toastSuccessfull = Toast.makeText(this,textToastSuccessful,duration);
        toastSuccessfull.setGravity(Gravity.CENTER, 0, 0);
        toastError = Toast.makeText(this,textToastError,duration);
        toastError.setGravity(Gravity.CENTER, 0, 0);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("currently downloading");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        btnFetchData = (Button) findViewById(R.id.buttonCheckLatestOffers);







        btnFetchData.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new FindFile(thisContext,toastAlready).execute();


            }
        });



        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */













    private int createDirectory(){
        File folder = new File(thisAppDirectory);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            return 1;//folder is there
        }
        return 2;//couldnt create folder
    }
    //scan file is used to make sure a recently downloaded file is findable by the android OS immediately
    public void scanFile(Context ctxt, File f, String mimeType) {
        MediaScannerConnection
                .scanFile(ctxt, new String[]{f.getAbsolutePath()},
                        new String[]{mimeType}, null);
    }

    public boolean doesFileExist(File file) {
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public void openPdf(File file) {
        Intent intent = new Intent(getBaseContext(), ViewPdfActivity.class);
        intent.putExtra("fileLocation", file.getAbsolutePath());
        startActivity(intent);
    }
//check storage for downloaded offers we already have,put in arraylist for listview
    public void populateDownloadedPdfArrayListStartup() {
        createDirectory();//make sure dirictory exists
        File folder = new File(thisAppDirectory);
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                downloadedPdfsArrayList.add(i,new Pdf(listOfFiles[i].getName()));
            }
        }
    }
//make the arraylist which populates listview sorted by date
    public void sortPdfArrayListByDate(){
        if(downloadedPdfsArrayList.size()<2){
            return;// dont do anything,only one/zero items,nothing to sort
        }
        if(downloadedPdfsArrayList.get(0).isThisPdfNewer(downloadedPdfsArrayList.get(1)) ==true){
            return;//is already in order
        }
        //if we get this far,the newest pdf is in the 2nd position,swap them
        Pdf copy0 = new Pdf(downloadedPdfsArrayList.get(0).getFileName());
        Pdf copy1 = new Pdf(downloadedPdfsArrayList.get(1).getFileName());
        downloadedPdfsArrayList.set(0,copy1);
        downloadedPdfsArrayList.set(1,copy0);

    }
    //used to add downloaded offers to arraylist which populates listview
    public void addPdfToArrayList(Pdf pdf){
        //we use this method after successfully downloading a pdf
    //move "new" pdf to top of arraylist as it is the newest,move the other one down
        if(downloadedPdfsArrayList.size()==2){
            //delete the oldest file
            File file = new File(thisAppDirectory,downloadedPdfsArrayList.get(1).getFileName());
            file.delete();
            scanFile(this, file, null);//scan so it doesnt show in file explorer

        }if(downloadedPdfsArrayList.size()>0){
            //if theres already something in the arraylist
        Pdf copy = new Pdf(downloadedPdfsArrayList.get(0).getFileName());
        downloadedPdfsArrayList.clear();
        downloadedPdfsArrayList.add(pdf);
        downloadedPdfsArrayList.add(copy);}
        else{
            downloadedPdfsArrayList.add(pdf);
        }

    }
//reload listview of offers
    public void refreshListView(){
      PdfAdapter adapter = new PdfAdapter(this,downloadedPdfsArrayList);

        ListView listView = (ListView) findViewById(R.id.listViewPdfs);
        listView.setAdapter(adapter);
    }
//schedule background process to check for new offers outisde the app
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
// Set the alarm to start at 9:10 pm
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 11);



//repeat every four days
        alarm.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY * 4,pIntent);
    }



    //check if there is new offers
    private  class FindFile extends AsyncTask<Void, Integer, Void> {
        private Link foundLink = new Link("");
        private Toast toast;
        Context context;
        boolean alreadyThere = false;


        public FindFile(Context context1,Toast toast1) {
            context = context1;
            toast = toast1;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            btnFetchData.setText("searching for latest offers....");


        }

        @Override
        protected Void doInBackground(Void... params) {



            try {


                Document doc = Jsoup.connect("http://www.dunnesstores.com/offer20/food-wine/fcp-category/home").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
                Element links = doc.select("a[title=\"Download offers in store\"]").first();
                if(links==null){
                    //there are no offers at all showing on the website,do nothing
                }else {
                    foundLink = new Link(links.attr("href"));
                    createDirectory();//make sure folder exists
                    File file = new File(thisAppDirectory, foundLink.getFileNameOnly());
                    //check if the latest offers on dunnesstores.com are already on the phone
                    if (doesFileExist(file) == true) {
                        alreadyThere = true;

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            btnFetchData.setText("check for latest offers");

            if(alreadyThere==true) {
                //we have the latest offers,no need to do anything,display a message telling the user
                toast.show();
            }else{
                // we dont have the latest offers,download it
                new DownloadFile(context,foundLink,toastSuccessfull,toastError).execute();
            }
        }


    }
    //used for downloading new offers
    private  class DownloadFile extends AsyncTask<Void, Integer, Void> {
        private Link foundLink; // the link to the pdf we are going to download
        String fileLocation = "";
        Context context;
        Toast toast;
        Toast toastError;


        public DownloadFile(Context context1,Link foundLink1,Toast toast1,Toast toastError1) {
            context = context1;
            foundLink = foundLink1;
            toast = toast1;
            toastError=toastError1;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(foundLink.getUrlWithDomain());
                createDirectory();//make sure the dunnes stores folder exists
                File file = new File(thisAppDirectory, foundLink.getFileNameOnly());


                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();


                fileLocation = file.toString();
                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);

                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);


                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    publishProgress((int) (downloadedSize * 100 / totalSize));

                }
                //close the output stream when done
                //add the file to the arraylist for the listview
                scanFile(context, file, null);
                addPdfToArrayList( new Pdf(foundLink.getFileNameOnly()));
                sortPdfArrayListByDate();
                fileOutput.close();

                publishProgress((int) 0);
                toast.show();//show success toast to user




            } catch (IOException e) {
                //delete the file if an error occured in the event it only half downloaded
                File file = new File(thisAppDirectory,foundLink.getFileNameOnly());
                file.delete();
                scanFile(context, file, null);
                toastError.show();//display error toast to user
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();
            refreshListView();

        }
        //used for download progress bar
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

    }
}


