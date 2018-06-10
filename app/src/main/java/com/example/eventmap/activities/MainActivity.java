package com.example.eventmap.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmap.R;
import com.example.eventmap.adapter.CustomInfoWindowAdapter;
import com.example.eventmap.models.Event;
import com.example.eventmap.models.PlaceInfo;
import com.example.eventmap.models.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity
    implements OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleMap.OnMarkerClickListener {

    private String currentUserIdInFirebase;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mMapLayer;
    private RelativeLayout mAddBtnLayer;
    private NavigationView mNavigationLayer;
    private ActionBarDrawerToggle mToggle;
    private LinearLayout mSignInLayer;

    private SignInButton SignIn;
    private Button SignOutBtn;
    private FloatingActionButton placePickerIcon;
    private TextView nameView;
    private TextView emailView;

    private static final int REQ_CODE = 9001;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String TAG = "MainActivity";



    // Maps
    private GoogleApiClient googleApiClient;
    private GoogleApiClient placeGoogleApiClient;
    private GoogleMap mMap;
    private PlaceInfo mPlace;
    private Marker mMarker;
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private Location mLastKnownLocation;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private final LatLng mDefaultLocation = new LatLng(37.427711, -122.170563);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    HashMap<Marker, ArrayList<Event>> markersList = new HashMap<Marker, ArrayList<Event>>();

    // Keys for storing activity state.
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        // Set tool bar
        mToolbar = (Toolbar)findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set views
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        mMapLayer = (LinearLayout)findViewById(R.id.mapLayer);
        mAddBtnLayer = (RelativeLayout)findViewById(R.id.addButtonLayer);
        mNavigationLayer = (NavigationView)findViewById(R.id.navigationLayer);
        mSignInLayer = (LinearLayout)findViewById(R.id.signInLayer);
        placePickerIcon = (FloatingActionButton) findViewById(R.id.placePickerIcon);

        // Database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String name = currentUser.getDisplayName();
                    String email = currentUser.getEmail();
                    String imgUrl = currentUser.getPhotoUrl().toString();
                    System.out.println(name+" "+email+" "+imgUrl);
                    // Signed in successfully, show authenticated UI.
                    updateUI(true);
                    nameView.setText(name);
                    emailView.setText(email);
                    addUserIntoDatabase(email);
                } else {
                    updateUI(false);
                }
            }
        };

        mNavigationLayer.setNavigationItemSelectedListener(this);

        // Signin
        SignIn = (SignInButton)findViewById(R.id.loginBtn);
        SignIn.setSize(SignInButton.SIZE_STANDARD);
        SignIn.setOnClickListener(this);

        // Set profile
        View navigationHeaderView = mNavigationLayer.getHeaderView(0);
        nameView = navigationHeaderView.findViewById(R.id.nameView);
        emailView = navigationHeaderView.findViewById(R.id.emailView);

        // SignOut
        SignOutBtn = navigationHeaderView.findViewById(R.id.signOutBtn);

        // Navbar toggle
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        // Initiate visibility
        mMapLayer.setVisibility(View.GONE);
        mNavigationLayer.setVisibility(View.GONE);
        mAddBtnLayer.setVisibility(View.GONE);

        // Google sign in
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(MainActivity.this, "You got an error", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleApiClient.connect();

        placeGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();

        placeGoogleApiClient.connect();

        // Signout
        SignOutBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                mAuth.signOut();
            }
        });
        // PlacePicker
        placePickerIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage() );
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage() );
                }
            }
        });

        // Google map
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void addUserIntoDatabase(final String email){
        // Check the database for existing users
        final DatabaseReference usersRef = mDatabase.child("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean createANewUserNode = true;
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String currentNodeEmail = dsp.child("email").getValue().toString();
                    System.out.println("current email on firebase: " + currentNodeEmail);
                    if (currentNodeEmail.equals(email)) {
                        createANewUserNode = false;
                        System.out.println("found the user");
                        String key = dsp.getKey();
                        System.out.println("key:" + key);
                        currentUserIdInFirebase = key;
                    }
                }

                if (createANewUserNode){
                    System.out.println("Creating a new node");
                    User newUser = new User(email);
                    currentUserIdInFirebase = usersRef.push().getKey();
                    usersRef.child(currentUserIdInFirebase).setValue(newUser);
                } else {
                    System.out.println("Leave");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });



    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        map.setOnMarkerClickListener(this);
        addMarkerOnMap(mMap);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MainActivity.this, markersList));

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, myProfile.class);
            intent.putExtra("currentUserId", currentUserIdInFirebase);
            startActivity(intent);
        } else if (id == R.id.nav_hosted) {
            Intent intent = new Intent(MainActivity.this, myEvents.class);
            intent.putExtra("currentUserId", currentUserIdInFirebase);
            startActivity(intent);
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(MainActivity.this, notificationActivity.class);
            intent.putExtra("currentUserId", currentUserIdInFirebase);
            startActivity(intent);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.loginBtn:
                signIn();
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    private void updateUI (boolean isLogin){
        if (isLogin){
            mMapLayer.setVisibility(View.VISIBLE);
            mNavigationLayer. setVisibility(View.VISIBLE);
            mAddBtnLayer.setVisibility(View.VISIBLE);

            mSignInLayer.setVisibility(View.GONE);
        } else {
            mMapLayer.setVisibility(View.GONE);
            mNavigationLayer.setVisibility(View.GONE);
            mAddBtnLayer.setVisibility(View.GONE);

            mSignInLayer.setVisibility(View.VISIBLE);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct){
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(true);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                }
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                //Log error
            }

        } else if (requestCode == PLACE_PICKER_REQUEST){
            System.out.println("in place picker request");
            System.out.println("result code: " + resultCode);
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                System.out.println("toast msg: " + toastMsg);
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(placeGoogleApiClient, place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    /* Google map */

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void addMarkerOnMap(GoogleMap map){

        // Get all events from database
        DatabaseReference allEventDatabase = mDatabase.child("events");
        allEventDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    Event e = eventSnapshot.getValue(Event.class);
                    LatLng thisEventLatLng = new LatLng(Double.parseDouble(e.getLatitude()), Double.parseDouble(e.getLongitude()));
                    Marker markerForThisEvent = mMap.addMarker(new MarkerOptions().position(thisEventLatLng).title(e.getPlaceName()));
                    boolean findSameLatLng = false;
                    for (Marker key : markersList.keySet() ) {
                        LatLng thisKeyLatLng = new LatLng(key.getPosition().latitude, key.getPosition().longitude);
                        if (thisKeyLatLng.equals(thisEventLatLng)){
                            findSameLatLng = true;
                            ArrayList<Event> currentVal = markersList.get(key);
                            currentVal.add(e);
                            markersList.put(key, currentVal);
                        }
                    }
                    if (!findSameLatLng){
                        ArrayList<Event> newVal = new ArrayList<Event>();
                        newVal.add(e);
                        markersList.put(markerForThisEvent, newVal);
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // For debug
        for (Marker markers: markersList.keySet()){
            String key = markers.toString();
            ArrayList<Event> events = markersList.get(key);
            System.out.println("key: " + key + "value count: " + events);
        }
        Iterator it = markersList.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry pair = (HashMap.Entry)it.next();
            if (marker.equals(pair.getKey())){
                // show info window using pair.getValue()
                if(marker.isInfoWindowShown()){
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
            }

            it.remove(); // avoids a ConcurrentModificationException
        }

        return true;
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<PlaceLikelihoodBufferResponse> placeResult =
                    mPlaceDetectionClient.getCurrentPlace(null);
            placeResult.addOnCompleteListener
                (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getCount();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        mLikelyPlaceNames = new String[count];
                        mLikelyPlaceAddresses = new String[count];
                        mLikelyPlaceAttributions = new String[count];
                        mLikelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                            // Build a list of likely places to show the user.
                            mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                            mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                    .getAddress();
                            mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                    .getAttributions();
                            mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Release the place likelihood buffer, to avoid memory leaks.
                        likelyPlaces.release();

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        openPlacesDialog();

                    } else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                    }
                });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                String markerSnippet = mLikelyPlaceAddresses[which];
                if (mLikelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                mMap.addMarker(new MarkerOptions()
                        .title(mLikelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(mLikelyPlaceNames, listener)
                .show();
    }



    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            System.out.println("In callback function. ");
            if (!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();;
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());
                Log.d(TAG, "onResult: place: " + mPlace.toString());

                String placeName = place.getName().toString();
                String placeAddress = place.getAddress().toString();
                double placeLatitude = place.getLatLng().latitude;
                double placeLongitude = place.getLatLng().longitude;

                Intent intent = new Intent(MainActivity.this, createEvent.class);
                intent.putExtra("currentUserId", currentUserIdInFirebase);
                intent.putExtra("placeName", placeName);
                intent.putExtra("placeAddress", placeAddress);
                intent.putExtra("placeLatitude", String.valueOf(placeLatitude));
                intent.putExtra("placeLongitude", String.valueOf(placeLongitude));
                startActivity(intent);

            } catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
            }
        }
    };
}
