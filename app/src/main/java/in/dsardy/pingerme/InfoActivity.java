package in.dsardy.pingerme;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.codemybrainsout.ratingdialog.RatingDialog;

import static in.dsardy.pingerme.MainActivity.ratingDialog;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setDrawingCacheBackgroundColor(Color.DKGRAY);

        ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .title("How much will you rate this game?")
                .titleTextColor(R.color.black)
                .positiveButtonText("Not Now")
                .positiveButtonTextColor(R.color.white)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.colorAccent)
                .positiveButtonBackgroundColor(R.color.colorAccent)
                .negativeButtonBackgroundColor(R.color.colorAccent)
                .onRatingChanged(new RatingDialog.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {

                    }
                }).build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabrate);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ratingDialog.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
