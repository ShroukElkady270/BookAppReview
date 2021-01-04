package com.example.mobileappproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;



public class PostDetailActivity extends AppCompatActivity {

    private Context mContext;
    private TextView titleTV, authorTV,quoteTV, rateTV, tagTV, reviewTV,datetimeTV, quoteinBar;
    private ImageButton playbtn, stopbtn;
    private ImageView bookcover;
    private MediaPlayer mediaPlayer;
    private Button deletebtn;
    private DatabaseHelper databaseHelper;
    private ImageUtils utils = new ImageUtils();

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle(R.string.PostContent);
        setContentView(R.layout.post_detail_v2);

        mContext=this;
        databaseHelper = new DatabaseHelper(this);

        titleTV = findViewById(R.id.detail_title);
        authorTV =findViewById(R.id.detail_author);
        quoteTV = findViewById(R.id.detail_quote);
        rateTV = findViewById(R.id.detail_rate);
        tagTV = findViewById(R.id.detail_tags);
        reviewTV = findViewById(R.id.detail_review);
        bookcover =findViewById(R.id.detail_bookcover);
        datetimeTV =findViewById(R.id.detail_datetime);
        deletebtn =findViewById(R.id.delete);
        quoteinBar = findViewById(R.id.quote_topbar);

        final String title = this.getIntent().getExtras().getString("title");
        String author = this.getIntent().getExtras().getString("author");
        String quote = this.getIntent().getExtras().getString("quote");
        String rate = this.getIntent().getExtras().getString("rate");
        String tag = this.getIntent().getExtras().getString("tag");
        String review = this.getIntent().getExtras().getString("review");
//        final String audiopath =this.getIntent().getExtras().getString("record");
        final String datetime = this.getIntent().getExtras().getString("datetime");
        byte[] bookcoverBLOB =this.getIntent().getExtras().getByteArray("bookcover");

        bookcover.setImageBitmap(ImageUtils.getImage(bookcoverBLOB));
        titleTV.setText(title);
        authorTV.setText("By: " + author);
        quoteTV.setText("' "+ quote+" '");
        quoteinBar.setText(quote);
        rateTV.setText(rate);
        tagTV.setText(tag);
        reviewTV.setText(review);
        datetimeTV.setText("Post Created at: "+datetime);

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseHelper.deleteData(title,datetime);
                createConfirm();

            }
        });


    }
    private void createConfirm(){
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Are you sure you want to delete the Post TAT !");
        alertDlg.setCancelable(false);

        alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent backintent = new Intent(mContext, postlistActivity.class);
                startActivity(backintent);
            }
        });

        alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDlg.create().show();
    }



}
