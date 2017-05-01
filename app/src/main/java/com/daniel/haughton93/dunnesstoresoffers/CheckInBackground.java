package com.daniel.haughton93.dunnesstoresoffers;

import android.os.Environment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by danie on 24/12/2016.
 */

public class CheckInBackground

{
    public CheckInBackground(){}

    private String thisAppDirectory = Environment.getExternalStorageDirectory()+  File.separator + "Android" + File.separator + "data" + File.separator+ "com.software.tatsu.dunnesstoresoffers";

    public boolean isThereNewOffers(){
        boolean result= false;//return false if we have the newest offers already
         String pdfLink = "didnt work";
         Link foundLink = new Link("");



        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.dunnesstores.com/offer20/food-wine/fcp-category/home").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Elements links = doc.select("a[title=\"Download offers in store\"]");
            Element links = doc.select("a[title=\"Download offers in store\"]").first();
        if(links==null){
            //do nothing
        }else {
            foundLink = new Link(links.attr("href"));


            File file = new File(thisAppDirectory, foundLink.getFileNameOnly());
            if (file.exists() == true) {
                //do nothing,we already have the latest file
            } else {
                result = true;//there is new offers we dont already have
            }
        }
return result;
    }
}
