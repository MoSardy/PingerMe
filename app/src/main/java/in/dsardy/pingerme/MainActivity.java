package in.dsardy.pingerme;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.R.drawable.ic_media_play;
import static in.dsardy.pingerme.R.drawable.dashbordbg3;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener {

    DrawerLayout drawer;
    ImageView girl;
    FloatingActionButton fab,fab2;
    LinearLayout Dashboard;
    TextView TVtimeLeft,TVgirlMsg , TVscore, TVhighscore , TVlastscore;
    Boolean isPlaying,isGirlok,highScoreChangeDone;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int score;
    CountDownTimer ct,mct;
    int TimerValue;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String gamePref = "GamePref";
    public static final String gameType = "GameType";

    public static final String lastScore = "LastScore";
    public static final String highScore = "HighScore";

    public static final String lastScore1 = "LastScore1";
    public static final String highScore1 = "HighScore1";

    public static final String lastScore2 = "LastScore2";
    public static final String highScore2 = "HighScore2";





    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isPlaying = false;
        isGirlok = false;
        highScoreChangeDone = false;
        TimerValue = 10;




        //sensor setup
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        getUIref();


        //sharedpref setup
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();
        TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore,0));
        TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore,0));



        //pref
        editor.putInt(gameType,0);





        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);

            }
        });



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isPlaying==false){
                    isPlaying = true;
                    fab.setImageResource(R.drawable.ic_pause_black_24dp);
                    Dashboard.setBackgroundResource(R.drawable.dashbordbg3sm);
                    TVtimeLeft.setTextColor(Color.WHITE);
                    TVhighscore.setTextColor(Color.BLACK);

                    if(sharedpreferences.getInt(gameType,0)==2){
                        Snackbar.make(view, "You Stop , You Loose ,still u got 3sec countdown if u stop !", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        TVgirlMsg.setText("its MARATHON baibee ;) ");

                        mct= new CountDownTimer(4000,1000) {
                            @Override
                            public void onTick(long l) {
                                TVtimeLeft.setText("Time : " + l/ 1000);

                            }

                            @Override
                            public void onFinish() {

                                isPlaying=false;
                                Dashboard.setBackgroundResource(R.drawable.dashbordbg2);
                                TVtimeLeft.setText("Time : "+3);
                                TVtimeLeft.setTextColor(Color.BLACK);
                                TVgirlMsg.setText("your fingers getting stronger");
                                fab.setImageResource(ic_media_play);
                                girl.setImageResource(R.drawable.pphappysm);
                                isGirlok=true;

                                editor.putInt(lastScore2,score);
                                if(sharedpreferences.getInt(highScore2,0)<score){
                                    editor.putInt(highScore2,score);
                                }
                                editor.apply();
                                TVscore.setText("0.");
                                TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore2,0));
                                TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore2,0));
                                score=0;

                            }
                        }.start();

                    }else {
                        Snackbar.make(view, "Move your Fingers in front of Proximity Sensor!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        TVgirlMsg.setText("Lets see what u got! ;) ");


                        //start countdown
                        ct = new CountDownTimer(TimerValue*1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                TVtimeLeft.setText("Time : " + millisUntilFinished / 1000);
                            }

                            public void onFinish() {
                                isPlaying=false;
                                Dashboard.setBackgroundResource(R.drawable.dashbordbg2);
                                TVtimeLeft.setText("Time : "+TimerValue);
                                TVtimeLeft.setTextColor(Color.BLACK);
                                fab.setImageResource(ic_media_play);
                                girl.setImageResource(R.drawable.pphappysm);
                                isGirlok=true;

                                //pref
                                if(sharedpreferences.getInt(gameType,0)==0){
                                    editor.putInt(lastScore,score);
                                    if(sharedpreferences.getInt(highScore,0)<score){
                                        editor.putInt(highScore,score);
                                    }
                                    editor.apply();
                                    TVscore.setText("0.");
                                    TVgirlMsg.setText("Try Hard next time !");

                                    TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore,0));
                                    TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore,0));

                                }else {
                                    editor.putInt(lastScore1,score);
                                    if(sharedpreferences.getInt(highScore1,0)<score){
                                        editor.putInt(highScore1,score);
                                    }
                                    editor.apply();
                                    TVscore.setText("0.");
                                    TVgirlMsg.setText("Damn! Try Harder next time !");

                                    TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore1,0));
                                    TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore1,0));
                                }


                                //score
                                score=0;


                            }
                        }.start();

                    }






                }else {
                    isPlaying=false;
                    Dashboard.setBackgroundResource(R.drawable.dashbordbg2);
                    TVtimeLeft.setTextColor(Color.BLACK);
                    TVtimeLeft.setText("Time : "+TimerValue);
                    fab.setImageResource(ic_media_play);
                    girl.setImageResource(R.drawable.pphappysm);
                    isGirlok=true;

                    //pref
                    if(sharedpreferences.getInt(gameType,0)==0){
                        ct.cancel();

                        editor.putInt(lastScore,score);
                        if(sharedpreferences.getInt(highScore,0)<score){
                            editor.putInt(highScore,score);
                        }
                        editor.apply();

                        //score
                        score=0;
                        TVscore.setText("0.");
                        TVgirlMsg.setText("do Complete the challenge boi !");

                        TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore,0));
                        TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore,0));

                    } else if(sharedpreferences.getInt(gameType,0)==1){
                        ct.cancel();

                        editor.putInt(lastScore1,score);
                        if(sharedpreferences.getInt(highScore1,0)<score){
                            editor.putInt(highScore1,score);
                        }
                        editor.apply();

                        //score
                        score=0;
                        TVscore.setText("0.");
                        TVgirlMsg.setText("haha!running away. See leaderboard");

                        TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore1,0));
                        TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore1,0));
                    }else {

                        mct.cancel();
                        editor.putInt(lastScore2,score);
                        if(sharedpreferences.getInt(highScore2,0)<score){
                            editor.putInt(highScore2,score);
                        }
                        editor.apply();

                        //score
                        score=0;
                        TVscore.setText("0.");
                        TVgirlMsg.setText("its ok! This is Real mans stuff :)");

                        TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore2,0));
                        TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore2,0));

                    }
                }
            }
        });



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void getUIref() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Dashboard = (LinearLayout)findViewById(R.id.lldashbord);
        TVtimeLeft = (TextView)findViewById(R.id.tvTimeLeft);
        TVgirlMsg = (TextView)findViewById(R.id.tvGirlmsg);
        girl = (ImageView)findViewById(R.id.imageViewGirl);
        TVscore = (TextView)findViewById(R.id.tvScore);
        TVhighscore = (TextView)findViewById(R.id.tvHighScore);
        TVlastscore = (TextView)findViewById(R.id.tvLastScore);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore,0));
            TimerValue=10;
            TVtimeLeft.setText("Time : " +10);
            highScoreChangeDone = false;
            editor.putInt(gameType,0).commit();
            TVgirlMsg.setText("Check LeaderBoard!");


        } else if (id == R.id.nav_gallery) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore1,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore1,0));
            TimerValue=30;
            TVtimeLeft.setText("Time : " +30);
            highScoreChangeDone = false;
            editor.putInt(gameType,1).commit();
            TVgirlMsg.setText("Challenge a friend ! its fun ;)");



        } else if (id == R.id.nav_slideshow) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore2,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore2,0));
            TVtimeLeft.setText("Time : " +3);
            highScoreChangeDone = false;
            TimerValue=3;
            TVgirlMsg.setText("here is No Timer honey! start it ;)");


            editor.putInt(gameType,2).commit();


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if(isPlaying){

        if(isGirlok==true){
            girl.setImageResource(R.drawable.ppsadsm);
            isGirlok=false;
        }else {
            girl.setImageResource(R.drawable.pphappysm);
            isGirlok=true;
        }
            score++;
            TVscore.setText(score +".");

            switch (sharedpreferences.getInt(gameType,0)){
                case 0:{

                    if(score>sharedpreferences.getInt(lastScore,0)){
                        TVlastscore.setText("Last Score : "+score);
                    }

                    if(score>sharedpreferences.getInt(highScore,0)){
                        TVhighscore.setText("High Score : "+score);
                        if(highScoreChangeDone){
                            TVhighscore.setTextColor(Color.WHITE);
                            Dashboard.setBackgroundResource(R.drawable.dashbordbg4sm);
                        }
                        highScoreChangeDone = true;

                    }

                    break;

                }
                case 1:{

                    if(score>sharedpreferences.getInt(lastScore1,0)){
                        TVlastscore.setText("Last Score : "+score);
                    }

                    if(score>sharedpreferences.getInt(highScore1,0)){
                        TVhighscore.setText("High Score : "+score);
                        if(highScoreChangeDone){
                            TVhighscore.setTextColor(Color.WHITE);
                            Dashboard.setBackgroundResource(R.drawable.dashbordbg4sm);
                        }
                        highScoreChangeDone = true;

                    }

                    break;

                }
                case 2:{

                    mct.cancel();

                    if(score>sharedpreferences.getInt(lastScore2,0)){
                        TVlastscore.setText("Last Score : "+score);
                    }

                    if(score>sharedpreferences.getInt(highScore2,0)){
                        TVhighscore.setText("High Score : "+score);
                        if(highScoreChangeDone){
                            TVhighscore.setTextColor(Color.WHITE);
                            Dashboard.setBackgroundResource(R.drawable.dashbordbg4sm);
                        }
                        highScoreChangeDone = true;

                    }

                    mct.start();

                    break;

                }
            }




        }




    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
