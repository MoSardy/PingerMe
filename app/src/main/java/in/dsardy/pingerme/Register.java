package in.dsardy.pingerme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.pm.Signature;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import swarajsaaj.smscodereader.interfaces.OTPListener;
import swarajsaaj.smscodereader.receivers.OtpReader;

import static in.dsardy.pingerme.MainActivity.isReg;

public class Register extends AppCompatActivity implements VerificationListener,OTPListener{

    LinearLayout Enterdetails,OTPvarify;
    EditText firstname , mobile , otp ;
    ImageButton sendOTP , varifyOTP;
    Verification mVerification;
    SharedPreferences.Editor ed;
    ProgressDialog progressDialog;
    TextView tvcmsg,tvomsg;


    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setUI();
        OtpReader.bind(this,"WARGSM");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(" Just a sec...");

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        ed = sp.edit();


        sendOTP.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               //hide key board
               InputMethodManager inputManager = (InputMethodManager)
                       getSystemService(Context.INPUT_METHOD_SERVICE);

               inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                       InputMethodManager.HIDE_NOT_ALWAYS);
               //getdetails

               String fn,mb;
               fn = firstname.getText().toString();
               mb = mobile.getText().toString();
               if(fn.isEmpty()||mb.isEmpty()){
                   Toast.makeText(getApplicationContext(),"Something Missing!",Toast.LENGTH_LONG).show();
                   ViewAnimator.animate(Enterdetails).flash().duration(500).start();
               }else {

                   sendOTPnow(mb);


               }

               tvcmsg.setVisibility(View.VISIBLE);
               ViewAnimator.animate(tvcmsg).flash().duration(1000).start();


           }
       });



        varifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String myotp = otp.getText().toString();

                if(myotp.isEmpty()){
                }else {
                    progressDialog.show();
                    mVerification.verify(myotp); //verifying otp for given number
                }


            }
        });

        //animate card1
        ViewAnimator.animate(Enterdetails).translationY(-200,0).alpha(0,1).descelerate().duration(1000).thenAnimate(sendOTP).tada().descelerate().duration(2000).start();


    }

    private void sendOTPnow(String mb) {

        mVerification = SendOtpVerification.createSmsVerification(this,mb, this,"91");

        mVerification.initiate(); //sending otp on given number

    }

    private void setUI() {
        Enterdetails = (LinearLayout)findViewById(R.id.llLogin);
        OTPvarify = (LinearLayout)findViewById(R.id.llotpenter);
        firstname = (EditText)findViewById(R.id.editTextFirstName);
        mobile = (EditText)findViewById(R.id.editTextMobile);
        otp = (EditText)findViewById(R.id.editTextOTP);
        sendOTP = (ImageButton)findViewById(R.id.imageButtonSendOTP);
        varifyOTP = (ImageButton)findViewById(R.id.imageButtonVarifyOtp);
        tvcmsg = (TextView)findViewById(R.id.textViewcmsg);
        tvomsg = (TextView)findViewById(R.id.textViewMsg);
    }

    @Override
    public void onInitiated(String response) {

        ViewAnimator.animate(Enterdetails).translationX(0,200).alpha(1,0).accelerate().duration(500).onStop(new AnimationListener.Stop() {
            @Override
            public void onStop() {
                Enterdetails.setVisibility(View.GONE);
                OTPvarify.setVisibility(View.VISIBLE);
                ViewAnimator.animate(OTPvarify).translationX(-200,0).alpha(0,1).descelerate().duration(500).start();

            }
        }).start();

    }

    @Override
    public void onInitiationFailed(Exception paramException) {

        Toast.makeText(getApplicationContext(),"Something went Wrong! Try again.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onVerified(String response) {

        progressDialog.hide();
        ed.putInt(isReg,1);
        ed.commit();
        finish();
        startActivity(new Intent(this,ProximityCheck.class));
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Toast.makeText(getApplicationContext(),"Something went Wrong! Try again.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void otpReceived(String smsText) {

        //Do whatever you want to do with the text
        String timedOTP = smsText.substring(0,4);
        otp.setText(timedOTP);
        tvomsg.setText("Got it! press go to verify...");
        ViewAnimator.animate(tvomsg).flash().descelerate().duration(500).thenAnimate(varifyOTP).pulse().accelerate().duration(1000).start();


    }
}
