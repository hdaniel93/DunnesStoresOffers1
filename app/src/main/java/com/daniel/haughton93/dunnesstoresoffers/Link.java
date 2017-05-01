package com.daniel.haughton93.dunnesstoresoffers;

/**
 * Created by danie on 05/12/2016.
 */

public class Link {
    private String url;

    public Link(String url1){
        url = url1;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrlWithDomain(){
        return "http://www.dunnesstores.com" + url;
    }
    public String getFileNameOnly(){
        return url.substring(Math.max(url.length() - 12, 0));
       
    }

}

