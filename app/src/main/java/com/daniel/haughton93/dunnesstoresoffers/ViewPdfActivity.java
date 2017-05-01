package com.daniel.haughton93.dunnesstoresoffers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.github.barteksc.pdfviewer.*;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;



public class ViewPdfActivity extends AppCompatActivity implements OnPageChangeListener{
    Integer pageNumber = 0;

    String pdfFileName = "test";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        //ListView listView = (ListView) findViewById(R.id.listViewPdfs)
        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
        String fileLocation = getIntent().getStringExtra("fileLocation");
        File file = new File(fileLocation);
       pdfFileName= file.getName();
        pdfView.fromFile(file).defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)

                .scrollHandle(new DefaultScrollHandle(this))
                .load();
        setTitle(pdfFileName);
    }







    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }
}
