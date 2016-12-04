package in.dsardy.pingerme;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.vungle.publisher.VunglePub;

import rb.popview.PopField;

import static android.R.drawable.ic_media_play;
import static in.dsardy.pingerme.ApplicationClass.userloc;
import static in.dsardy.pingerme.Register.pMobile;
import static in.dsardy.pingerme.Register.pName;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SensorEventListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener,RewardedVideoAdListener{

    DrawerLayout drawer;
    ImageView girl;
    FloatingActionButton fab,fab2;
    LinearLayout Dashboard,Girlmsg;
    TextView TVtimeLeft,TVgirlMsg , TVscore, TVhighscore , TVlastscore, TVscore2,TVyoname,TVyomb,TVplayAd;
    Boolean isPlaying,isGirlok,highScoreChangeDone,ya;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int score;
    CountDownTimer ct,mct;
    int TimerValue;
    PopField popField;
    private GoogleApiClient mGoogleApiClient;
    ImageView rotatingLOGO;
    AdView mAdView;
    public static RatingDialog ratingDialog;



    private RewardedVideoAd rewardedVideoAd;
    InterstitialAd mInterstitialAd;
    // get the VunglePub instance
    final VunglePub vunglePub = VunglePub.getInstance();



    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    public static final String STATE_RESOLVING_ERROR = "resolving_state";
    //Request code to use when launching the activity to fix the connection to google API
    private static final int REQUEST_SOLVE_CONNEXION = 999;








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
                .setViewForPopups(findViewById(android.R.id.content))
                .build();





        //sharedpref setup
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedpreferences.edit();

        if(sharedpreferences.getInt(isReg,0)==0&&sharedpreferences.getInt("isskiped",0)==0){
            finish();
            startActivity(new Intent(this,Register.class));
        }

        editor.putInt(gameType,0);
        editor.commit();


        isPlaying = false;
        isGirlok = true;
        highScoreChangeDone = false;
        TimerValue = 10;


        setContentView(R.layout.activity_main);

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


        popField = PopField.attach2Window(this);
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        //keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        //sensor setup
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        getUIref();

        //initialize admob banner

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3922710053471966~6702676138");
        mAdView = (AdView) findViewById(R.id.adView);


        //video ad

        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(this);
        loadVideoAd();

        TVplayAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(rewardedVideoAd.isLoaded()){
                    rewardedVideoAd.show();
                }else {
                    if(vunglePub.isAdPlayable()){
                        vunglePub.playAd();
                        TVplayAd.setVisibility(View.GONE);
                        scoreReward();


                    }

                }
            }
        });
        vunglePub.init(this,"583f68f84beff6147e000456");


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3922710053471966/8155729737");












        girl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()== MotionEvent.ACTION_DOWN){
                    girl.setImageResource(R.drawable.pphappysm2);
                    TVgirlMsg.setText("love you too :)");

                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    girl.setImageResource(R.drawable.pphappysm);
                    TVgirlMsg.setText("You Can Score much Better!");

                }
                return true;
            }
        });

        Girlmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vunglePub.isAdPlayable())
                    vunglePub.playAd();
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
                ViewAnimator.animate(rotatingLOGO).shake().descelerate().duration(1000).start();

            }
        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(vunglePub.isAdPlayable()){
                    TVplayAd.setVisibility(View.VISIBLE);
                }




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

                                if(isSignedIn()){

                                    Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIycjWw84HEAIQAw",score);

                                }

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
                                    TVgirlMsg.setText("Try Harder next time !");

                                    com.github.florent37.viewanimator.ViewAnimator.animate(Girlmsg).flash().descelerate().duration(1000).start();
                                    com.github.florent37.viewanimator.ViewAnimator.animate(fab2).fall().descelerate().duration(2000).start();




                                    if(isSignedIn()){
                                        Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIycjWw84HEAIQAQ",score);
                                    }


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

                                    if(isSignedIn()){
                                        Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIycjWw84HEAIQAg",score);

                                    }



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
                        if(isSignedIn()){
                            Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIycjWw84HEAIQAQ",score);
                        }


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
                        if(isSignedIn()){
                            Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIycjWw84HEAIQAg",score);

                        }


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
                        if(isSignedIn()){
                            Games.Leaderboards.submitScore(mGoogleApiClient,"CgkIycjWw84HEAIQAw",score);
                        }


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

        View header=navigationView.getHeaderView(0);
        /*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
        TVyoname= (TextView)header.findViewById(R.id.textViewyoname);
        TVyomb = (TextView)header.findViewById(R.id.textViewypmb);
        rotatingLOGO = (ImageView)header.findViewById(R.id.imageViewlogo);
        TVyoname.setText("@"+sharedpreferences.getString(pName,"user"));
        TVyomb.setText(sharedpreferences.getString(pMobile,"9XXXXXXXXX"));

        TVyoname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedpreferences.getInt(isReg,0)!=1)
                startActivity(new Intent(MainActivity.this,Register.class));
            }
        });
    }

    private void scoreReward() {

        TVgirlMsg.setText("You are rewarded!");

        switch (sharedpreferences.getInt(gameType,0)){
            case 0:{
                score = sharedpreferences.getInt(lastScore,0);
                TVscore.setText(""+score+".");
                Log.e("Score........0."," lets seee");
                break;
            }
            case 1:{
                score = sharedpreferences.getInt(lastScore1,1);
                TVscore.setText(""+score+".");
                Log.e("Score........1."," lets seee");

                break;
            }
            case 2:{
                score = sharedpreferences.getInt(lastScore2,2);
                TVscore.setText(""+score+".");
                Log.e("Score........2."," lets seee");

                break;
            }
        }
        ViewAnimator.animate(Girlmsg).tada().descelerate().duration(1000).start();

        TVplayAd.setVisibility(View.GONE);
    }


    public  void loadVideoAd(){
        if(!rewardedVideoAd.isLoaded()){
            rewardedVideoAd.loadAd("ca-app-pub-3922710053471966/4109136530",new AdRequest.Builder().addTestDevice("C046ECE47FD2AF40C0FE7CF1D02EF7A2").build());
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("C046ECE47FD2AF40C0FE7CF1D02EF7A2")
                .build();

        mInterstitialAd.loadAd(adRequest);
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

        AdRequest adRequest;
        adRequest = new AdRequest.Builder().addTestDevice("C046ECE47FD2AF40C0FE7CF1D02EF7A2").setLocation(userloc).build();
        mAdView.loadAd(adRequest);

        requestNewInterstitial();
        vunglePub.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        vunglePub.onPause();
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
        TVplayAd = (TextView)findViewById(R.id.textViewplayAd);




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
            score=0;
            TVscore.setText("0.");
            TVgirlMsg.setText("Check LeaderBoard!");

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }


        } else if (id == R.id.nav_gallery) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore1,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore1,0));
            TimerValue=30;
            TVtimeLeft.setText("Time : " +30);
            com.github.florent37.viewanimator.ViewAnimator.animate(TVtimeLeft).tada().duration(1000).start();

            highScoreChangeDone = false;
            editor.putInt(gameType,1).commit();
            score=0;
            TVscore.setText("0.");
            TVgirlMsg.setText("Challenge a friend ! its fun ;)");

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }



        } else if (id == R.id.nav_slideshow) {
            TVlastscore.setText("Last Score : "+sharedpreferences.getInt(lastScore2,0));
            TVhighscore.setText("High Score : "+sharedpreferences.getInt(highScore2,0));
            TVtimeLeft.setText("Time : " +3);
            com.github.florent37.viewanimator.ViewAnimator.animate(TVtimeLeft).tada().duration(1000).start();

            highScoreChangeDone = false;
            TimerValue=3;
            TVgirlMsg.setText("here is No Timer honey! start it ;)");


            editor.putInt(gameType,2).commit();
            score=0;
            TVscore.setText("0.");

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }


        } else if (id == R.id.nav_leaderboard) {


            if (isSignedIn()) {
                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient),100);
            } else {
                BaseGameUtils.makeSimpleDialog(this, getString(R.string.leaderboards_not_available)).show();
            }



        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,MapsActivity.class));

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        } else if (id == R.id.nav_howto) {
            startActivity(new Intent(this,HowtoPlayActivity.class));

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }else if (id == R.id.nav_about) {

            startActivity(new Intent(this,InfoActivity.class));

            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

        }else if (id == R.id.nav_rate) {

            ratingDialog.show();


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
        Games.setViewForPopups(mGoogleApiClient, getWindow().getDecorView().findViewById(android.R.id.content));

        Log.e(" connrcted>>>>>>"," hmmmm");
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


        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }



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

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    @Override
    public void onRewardedVideoAdLoaded() {

        TVplayAd.setVisibility(View.VISIBLE);
        ViewAnimator.animate(TVplayAd).flash().descelerate().duration(1000).start();

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {


    }

    @Override
    public void onRewardedVideoAdClosed() {
        TVgirlMsg.setText("No Reward! watch it all...");
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

        scoreReward();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }
}
