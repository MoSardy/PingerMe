package in.dsardy.pingerme;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

import rb.popview.PopField;

public class ProximityCheck extends AppCompatActivity implements SensorEventListener {

    TextView pCheckGuide , pnwMsg,cscore, wecome;
    RelativeLayout Scorebg;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int pscore;
    PopField popField;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_check);

        pCheckGuide = (TextView)findViewById(R.id.textViewpcmsg);
        pnwMsg = (TextView)findViewById(R.id.textViewpnwmsg);
        cscore = (TextView)findViewById(R.id.textViewsc);
        wecome = (TextView)findViewById(R.id.textViewwc);
        Scorebg = (RelativeLayout)findViewById(R.id.rlSc);

        pscore = -1;
        popField = PopField.attach2Window(this);


        ViewAnimator.animate(wecome).alpha(0,1).translationY(100,0).descelerate().duration(1000).thenAnimate(wecome).flash().duration(500).onStop(new AnimationListener.Stop() {
            @Override
            public void onStop() {
                wecome.setVisibility(View.GONE);
                Scorebg.setVisibility(View.VISIBLE);
                ViewAnimator.animate(Scorebg).alpha(0,1).descelerate().duration(1000).onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        pCheckGuide.setVisibility(View.VISIBLE);
                        ViewAnimator.animate(pCheckGuide).alpha(0,1).accelerate().duration(500).thenAnimate(pCheckGuide).tada().duration(1000).start();
                    }
                }).start();
            }
        }).start();

        //sensor setup
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


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
    public void onSensorChanged(SensorEvent sensorEvent) {

        pscore++;
        cscore.setText(""+pscore);

        if(pscore>8){
            pnwMsg.setVisibility(View.VISIBLE);
        }

        if(pscore>15){
           pCheckGuide.setVisibility(View.GONE);
            pnwMsg.setVisibility(View.GONE);
        }

        if(pscore>= 20){
            mSensorManager.unregisterListener(this);
            popField.popView(Scorebg);

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity( new Intent(getApplication(),MainActivity.class));
                    finish();
                }
            }, 1000);



        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
