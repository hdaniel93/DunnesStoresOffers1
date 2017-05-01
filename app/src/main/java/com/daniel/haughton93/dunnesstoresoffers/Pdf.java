package com.daniel.haughton93.dunnesstoresoffers;

/**
 * Created by danie on 15/12/2016.
 */

public class Pdf  {
    private String fileName;
    public Pdf(String fileName1){
        fileName = fileName1;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileNameWithoutExtension(){
        //remove ".pdf" from end"
        return fileName.substring(0,9);
    }
    public String returnDateInWords(){
        //date in filename is dd-mm-yy.pdf
        String result ="";
        //check day date
        result = result + fileName.substring(0,2)+"/";
        String month = fileName.substring(3,5);
        result = result + month +"/";
        //if(month.equals("01")){
        //    result = result + "January";
        //}else if(month.equals("02")){
        //    result = result + "February";
        //}else if(month.equals("03")){
        //    result = result + "March";
       // }else if(month.equals("04")){
       //     result = result + "April";
       // }else if(month.equals("05")){
          //  result = result + "May";
        //}else if(month.equals("06")){
          //  result = result + "June";
       // }else if(month.equals("07")){
          //  result = result + "July";
       // }else if(month.equals("08")){
           // result = result + "August";
       // }else if(month.equals("09")){
           // result = result + "September";
       // }else if(month.equals("10")){
           // result = result + "October";
        //}else if(month.equals("11")){
          //  result = result + "November";
       // }else if(month.equals("12")){
           // result = result + "December";
       // }
        String year = "20" + fileName.substring(6,8);
        result = result + year;

        return result;
    }
    public boolean isThisPdfNewer(Pdf pdf){
        //compares dates as ints in yymmdd
        boolean result =false;
        String thisPdfDate = fileName.substring(6,8)+ fileName.substring(3,5) + fileName.substring(0,2);
        String otherPdfDate = pdf.getFileName().substring(6,8) + pdf.getFileName().substring(3,5) + pdf.getFileName().substring(0,2);
        int thisPdfDateInt = Integer.parseInt(thisPdfDate);
        int otherPdfDateInt = Integer.parseInt(otherPdfDate);
        if(thisPdfDateInt> otherPdfDateInt){
            result = true;//this pdf is newer
        }

        return result;
    }
}
