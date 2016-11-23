package in.dsardy.pingerme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;


import rb.popview.PopField;

import static android.R.drawable.ic_media_play;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener  {

    DrawerLayout drawer;
    ImageView girl;
    FloatingActionButton fab,fab2;
    LinearLayout Dashboard,Girlmsg;
    TextView TVtimeLeft,TVgirlMsg , TVscore, TVhighscore , TVlastscore, TVscore2;
    Boolean isPlaying,isGirlok,highScoreChangeDone,ya;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int score;
    CountDownTimer ct,mct;
    int TimerValue;
    PopField popField;
    private GoogleApiClient mGoogleApiClient;








    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    public static final String gamePref = "GamePref";
    public static final String gameType = "GameType";
    public static final String isReg = "isRegistered";


    public static final String lastScore = "LastScore";
    public static final String highScore = "HighScore";

    public static final String lastScore1 = "LastScore1";
    public static final String highScore1 = "HighScore1";

    public static final String lastScore2 = "LastScore2";
    public static final String highScore2 = "HighScore2";





    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the Google Api Client with access to the Play Games services
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();


        //sharedpref setup
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();

        /*if(sharedpreferences.getInt(isReg,0)==0){
            finish();
            startActivity(new Intent(this,Register.class));
        }*/

        editor.putInt(gameType,0);
        editor.commit();


        isPlaying = false;
        isGirlok = true;
        highScoreChangeDone = false;
        TimerValue = 10;


        setContentView(R.layout.activity_main);
        popField = PopField.attach2Window(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        //sensor setup
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        getUIref();





        girl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                    girl.setImageResource(R.drawable.pphappysm2);
                    TVgirlMsg.setText("I love you too :)");

                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    girl.setImageResource(R.drawable.pphappysm);
                    TVgirlMsg.setText("You Can Score much Better!");

                }
                return true;
            }
        });



        TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore,0));
        TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore,0));



        //pref
        editor.putInt(gameType,0);


        com.github.florent37.viewanimator.ViewAnimator.animate(fab).translationX(-200,0).alpha(0,1).accelerate().duration(1000).thenAnimate(fab).tada().duration(1000).thenAnimate(Girlmsg).tada().descelerate().duration(1000).start();


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
                    com.github.florent37.viewanimator.ViewAnimator.animate(TVtimeLeft).tada().descelerate().duration(2000).start();
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
                                com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(1500).start();
                                com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();

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
                                Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIlcfg65YJEAIQAg",score);
                                animateScore2();

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

                                    com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();
                                    com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(2000).start();




                                    Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIlcfg65YJEAIQAA",score);

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
                                    com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(1500).start();
                                    com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();


                                    Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIlcfg65YJEAIQAQ",score);

                                    TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore1,0));
                                    TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore1,0));
                                }


                                //score
                                animateScore2();
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
                        Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIlcfg65YJEAIQAA",score);

                        animateScore2();


                        score=0;
                        TVscore.setText("0.");
                        TVgirlMsg.setText("do Complete the challenge boi !");
                        com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(1500).start();
                        com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();


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
                        Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIlcfg65YJEAIQAQ",score);

                        animateScore2();
                        score=0;
                        TVscore.setText("0.");
                        TVgirlMsg.setText("haha!running away. See leaderboard");
                        com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(1500).start();
                        com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();


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
                        Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIlcfg65YJEAIQAg",score);

                        score=0;
                        TVscore.setText("0.");
                        TVgirlMsg.setText("its ok! This is Real mans stuff :)");
                        com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(1500).start();
                        com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();


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
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void getUIref() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Dashboard = (LinearLayout)findViewById(R.id.lldashbord);
        TVtimeLeft = (TextView)findViewById(R.id.tvTimeLeft);
        TVgirlMsg = (TextView)findViewById(R.id.tvGirlmsg);
        girl = (ImageView)findViewById(R.id.imageViewGirl);
        TVscore = (TextView)findViewById(R.id.tvScore);
        TVscore2 = (TextView)findViewById(R.id.textViewScore2);
        TVhighscore = (TextView)findViewById(R.id.tvHighScore);
        TVlastscore = (TextView)findViewById(R.id.tvLastScore);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        Girlmsg = (LinearLayout)findViewById(R.id.llGirlmsg);



    }

    public void animateScore2(){

        TVscore2.setText(""+score);
        TVscore2.setVisibility(View.VISIBLE);
        com.github.florent37.viewanimator.ViewAnimator.animate(TVscore2).translationX(-100,0).alpha(0,1).descelerate().duration(500).thenAnimate(TVscore2).tada().duration(1000).thenAnimate(TVscore2).alpha(1,0).duration(1000).onStop(new AnimationListener.Stop() {
            @Override
            public void onStop() {
                TVscore2.setVisibility(View.GONE);
            }
        }).start();
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
            com.github.florent37.viewanimator.ViewAnimator.animate(TVtimeLeft).tada().duration(1000).start();
            highScoreChangeDone = false;
            editor.putInt(gameType,0).commit();
            TVgirlMsg.setText("Check LeaderBoard!");


        } else if (id == R.id.nav_gallery) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore1,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore1,0));
            TimerValue=30;
            TVtimeLeft.setText("Time : " +30);
            com.github.florent37.viewanimator.ViewAnimator.animate(TVtimeLeft).tada().duration(1000).start();

            highScoreChangeDone = false;
            editor.putInt(gameType,1).commit();
            TVgirlMsg.setText("Challenge a friend ! its fun ;)");



        } else if (id == R.id.nav_slideshow) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore2,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore2,0));
            TVtimeLeft.setText("Time : " +3);
            com.github.florent37.viewanimator.ViewAnimator.animate(TVtimeLeft).tada().duration(1000).start();

            highScoreChangeDone = false;
            TimerValue=3;
            TVgirlMsg.setText("here is No Timer honey! start it ;)");


            editor.putInt(gameType,2).commit();


        } else if (id == R.id.nav_leaderboard) {


            if (isSignedIn()) {
                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),
                        100);
            } else {
                BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
            }



        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,MapsActivity.class));

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

            if(score==1){
                if(isGirlok==false && sensorEvent.values[0]==5){
                    girl.setImageResource(R.drawable.pphappysm);
                    isGirlok=true;
                }

            }

            switch (sharedpreferences.getInt(gameType,0)){
                case 0:{

                    if(score>sharedpreferences.getInt(lastScore,0)){
                        TVlastscore.setText("Last Score : "+score);
                    }

                    if(score>sharedpreferences.getInt(highScore,0)){
                        TVhighscore.setText("High Score : "+score);
                        if(highScoreChangeDone){
                            TVhighscore.setTextColor(Color.WHITE);
                            TVgirlMsg.setText("Damn! Its a new High Score!");

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
                            TVgirlMsg.setText("AAhh! Its a new High Score !");

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
                            TVgirlMsg.setText("AAhh! u nailed it !");

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



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        //The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    private static int RC_SIGN_IN = 9001;

    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInflow = true;
    private boolean mSignInClicked = false;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (mResolvingConnectionFailure) {
            // Already resolving
            return;
        }

        // If the sign in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInflow) {
            mAutoStartSignInflow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, getString(R.string.signin_other_error))) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button


        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);


    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.sign_in_button) {
            // start the asynchronous sign in flow
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }

    }
    private boolean isSignedIn() {
        return (mGoogleApiClient != null && mGoogleApiClient.isConnected());
    }


}
