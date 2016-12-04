package in.dsardy.pingerme;

import android.Manifest;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static in.dsardy.pingerme.ApplicationClass.REQUEST_CHECK_SETTINGS;
import static in.dsardy.pingerme.ApplicationClass.mGoogleApiClient;
import static in.dsardy.pingerme.ApplicationClass.mLocationRequest;
import static in.dsardy.pingerme.ApplicationClass.userloc;
import static in.dsardy.pingerme.MainActivity.isReg;
import static in.dsardy.pingerme.Register.pGen;
import static in.dsardy.pingerme.Register.pName;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    public static GoogleMap mMap;
    DatabaseReference people;
    DatabaseReference me;
    HashMap<String,Marker> markers;
    ChildEventListener childEventListener;
    TextView pInfo , pCP;
    Dialog dialog;
    FloatingActionButton fabSend, fabCancel;
  //  Player p;
    String phoneNo,message;
    EditText etCmsg;
    protected static final String TAG = "MapActivity";
    SharedPreferences spm;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarmap);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spm = PreferenceManager.getDefaultSharedPreferences(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        people = FirebaseDatabase.getInstance().getReference().child("players");
        markers = new HashMap<>();

        //Dialog

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.customdialog);
        pInfo = (TextView)dialog.findViewById(R.id.textViewabout);
        pCP = (TextView)dialog.findViewById(R.id.textViewcp);
        fabCancel=(FloatingActionButton)dialog.findViewById(R.id.fabCancelmsg);
        fabSend =(FloatingActionButton)dialog.findViewById(R.id.fabsendmsg);
        etCmsg = (EditText)dialog.findViewById(R.id.editTextcusmsg);

        //ad

        //initialize admob banner

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3922710053471966~6702676138");
        AdView mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest;
        adRequest = new AdRequest.Builder().addTestDevice("C046ECE47FD2AF40C0FE7CF1D02EF7A2").setLocation(userloc).build();
        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onStart() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStop() {
        super.onStop();

        if(childEventListener!=null)
        people.removeEventListener(childEventListener);

        markers.clear();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


       // LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
        /*Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(spm.getInt(isReg,0)==0){
                    finish();
                    startActivity(new Intent(MapsActivity.this,Register.class));
                }
            }
        });

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Player m = dataSnapshot.getValue(Player.class);

                String time = m.getTime();
                String diff = gettimeDiff(time);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(m.getLat(),m.getLang()));
                markerOptions.title(m.getName());
                markerOptions.snippet(diff);


                if(diff.contains("Realtime")||diff.contains("sec")||diff.contains("min")){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else{
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }


                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(m);
                markers.put(m.getMobile(),marker);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(m.getLat(),m.getLang()),17));


            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Player m = dataSnapshot.getValue(Player.class);
                String time = m.getTime();
                String diff = gettimeDiff(time);

                //remove previous one
                markers.get(m.getMobile()).remove();

                //get

                //new markeroptions
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(m.getLat(),m.getLang()));
                markerOptions.title(m.getName());
                markerOptions.snippet(diff);

                if(diff.contains("Realtime")||diff.contains("sec")||diff.contains("min")){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else{
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }



                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(m);
                markers.put(m.getMobile(),marker);



            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //set listener to database
        people.addChildEventListener(childEventListener);


        //marker click listener

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Player p = (Player) marker.getTag();


                pInfo.setText("Found here "+gettimeDiff(p.getTime())+"! "+p.getGender()+"/"+p.getAge());
                pCP.setText("CP: "+p.getIntrest());
                dialog.setTitle("Challenge @"+p.getName()+" now !");
                dialog.show();

                fabSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        phoneNo="0"+p.getMobile();
                        String gend = "";
                        if(spm.getString(pGen,"Male").equals("Male")){
                            gend ="him";
                        }else {
                            gend = "her";
                        }
                        message = ": '"+etCmsg.getText().toString()+"'  WARGASM: @"+spm.getString(pName,"someone")+" wants to play Wargasmm challenge with you! you can locate "+gend+" on the map in game. ";
                        sendSMSMessage();
                        dialog.cancel();

                    }
                });

                fabCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                return true;
            }
        });




    }

    protected void sendSMSMessage() {

        Snackbar.make(findViewById(R.id.map), " You will get a money deduction pop up if sms sent Successfully! , if not try again...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);




    }
    String gettimeDiff(String time){

        String startDateString = time;
        String diff = "" ;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate ;
        try {
            startDate = df.parse(startDateString);

            if(startDate!=null){

                Date endDate = new Date();

                long duration  = endDate.getTime() - startDate.getTime();
                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
                long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

                if(diffInSeconds==0){
                    return "Realtime!";
                }

                if(diffInSeconds<60){
                    diff = ""+diffInSeconds+" sec ago";
                }else if(diffInMinutes<60){
                    diff = ""+diffInMinutes+" min ago";
                }else if(diffInHours<24){
                    diff = ""+diffInHours+" hrs ago";
                }else {

                    long daysago = duration / (1000 * 60 * 60 * 24);
                    diff = ""+daysago+" days ago";
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;

    }



}
