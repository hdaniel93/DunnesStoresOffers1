package com.daniel.haughton93.dunnesstoresoffers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by danie on 15/12/2016.
 */

public class PdfAdapter extends ArrayAdapter<Pdf> {
    ArrayList<Pdf> pdfs;

    public PdfAdapter(Context context, ArrayList<Pdf> pdfs) {
        super(context, 0, pdfs);
        this.pdfs = pdfs;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Pdf pdf = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pdf, parent, false);
        }

        // Lookup view for data population

        TextView date = (TextView) convertView.findViewById(R.id.date);
        // Populate the data into the template view using the data object

        date.setText("Offers starting on " + pdf.returnDateInWords()  );
        //Button button = (Button) convertView.findViewById(R.id.buttonOpenPdf);

        // Return the completed view to render on screen
        return convertView;
    }
    public Pdf getItem(int position){
        return pdfs.get(position);
    }
}

