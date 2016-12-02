package in.dsardy.pingerme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class HowtoPlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setBackgroundColor(Color.DKGRAY);
        toolbar.setDrawingCacheBackgroundColor(Color.DKGRAY);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabhowtoreturn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplication(),InfoActivity.class));
                finish();


            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
