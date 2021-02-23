package gropal.in;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import Config.BaseURL;
import Config.SharedPref;
import Fonts.CustomTypefaceSpan;
import Fonts.LatoBLack;
import Fragment.Contact_Us_Fragment;
import Fragment.Category_fragment;
import Fragment.Login_Fragment;
import Fragment.About_us_fragment;
import Fragment.Help_Support_fragment;
import Fragment.Empty_cart_fragment;
import Fragment.Home_fragment;
import Fragment.Account_fragment;
import Fragment.Cart_fragment;
import Fragment.Reward_fragment;
import Fragment.Update_Profile_Fragment;
import Fragment.Shop_Now_fragment;
import Fragment.Terms_and_Privacy_fragment;
import Fragment.Wallet_fragment;
import Fragment.Location_fragment;
import Fragment.My_Order_Fragment;
import Service.LocationUpdatesService;
import gropal.in.networkconnectivity.NetworkError;
import util.ConnectivityReceiver;
import util.Constants;
import util.CustomVolleyJsonRequest;
import util.DatabaseHandler;
import util.GPSTracker;
import util.HamburgerDrawable;
import util.PreferenceUtility;
import util.Session_management;
import util.UIUtility;
import util.UtilityHelper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private NavController navController;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView totalBudgetCount, totalBudgetCount2, totalBudgetCount3, tv_name, powerd_text;
    private ImageView iv_profile;
    private DatabaseHandler dbcart;
    private String user_address;
    private BottomNavigationView bottomNavigationView;
    private Session_management sessionManagement;
    private Menu nav_menu;
    private AutoCompleteTextView atv_location;
    ImageView imageView;
    ImageView img_edit_loc;
    TextView mTitle;
    Toolbar toolbar;
    String whatsappnum, callnum;
    String language = "";
    LinearLayout My_Cart;
    int padding = 0;
    private TextView txtRegId;
    NavigationView navigationView;
    LinearLayout Change_Store;
    String Store_Count;
    GPSTracker tracker;

    SharedPreferences sharedPreferences, callprefrences;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor calleditor;
    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;


    @Override
    protected void attachBaseContext(Context newBase) {


        newBase = LocaleHelper.onAttach(newBase);

        super.attachBaseContext(newBase);
    }


    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myReceiver = new MyReceiver();
        setBottomNavigationView();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        tracker = new GPSTracker(this);
        // tracker.getLocation();

        // Check that the user hasn't revoked permissions by going to Settings.
        // if (UtilityHelper.requestingLocationUpdates(this)) {
        if (!checkPermissions()) {
            requestPermissions();
        }
        //}

        //forceCrash();
        initializeMap();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("MYTAG", "This is your Firebase token" + token);

        sharedPreferences = getSharedPreferences("lan", Context.MODE_PRIVATE);

        callprefrences = getSharedPreferences("calling", Context.MODE_PRIVATE);

        editor = sharedPreferences.edit();

        calleditor = callprefrences.edit();

        editor.clear();

        editor.putString("language", "english");

        editor.apply();

        editor.commit();

        make_number();


        if (getIntent().getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

                if (key.equals("MainActivity") && value.equals("True")) {
                    Fragment fm = new Login_Fragment();
                    Bundle args = new Bundle();
                    args.putString("value", value);
                    fm.setArguments(args);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();

                }

            }
            subscribeToPushService();
        }

        Store_Count = SharedPref.getString(MainActivity.this, BaseURL.KEY_STORE_COUNT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPadding(padding, toolbar.getPaddingTop(), padding, toolbar.getPaddingBottom());
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLocScreen();
            }
        });

        setSupportActionBar(toolbar);
        for (int i = 0; i < toolbar.getChildCount(); i++) {

            View view = toolbar.getChildAt(i);

            if (view instanceof AutoCompleteTextView) {
                atv_location = (AutoCompleteTextView) view;
            } else if (view instanceof ImageView) {
                img_edit_loc = (ImageView) view;
                img_edit_loc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateToLocScreen();
                    }
                });

            } else if (view instanceof TextView) {
                TextView textView = (TextView) view;

                Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "Font/Bold.ttf");
                textView.setTypeface(myCustomFont);

            }

        }
        setToolbarTitle("");

     /*  if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }*/


        dbcart = new DatabaseHandler(this);


        checkConnection();

        sessionManagement = new Session_management(MainActivity.this);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        increaseHamburgerSize(toggle);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }

        View headerView = navigationView.getHeaderView(0);
        // navigationView.getBackground().setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY);
        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();
        View header = ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0);
        Change_Store = (LinearLayout) header.findViewById(R.id.change_store_btn);
        if (sessionManagement.isLoggedIn()) {
            //show order/rewards/wallet/cart icons
            // viewpa.setVisibility(View.VISIBLE);
        }


        if (Store_Count.equals("1")) {
            Change_Store.setVisibility(View.INVISIBLE);
        } else if (Store_Count.equals("2")) {
            Change_Store.setVisibility(View.VISIBLE);
            Change_Store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SelectStore.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
        iv_profile = (ImageView) header.findViewById(R.id.iv_header_img);
        tv_name = (TextView) header.findViewById(R.id.tv_header_name);

        iv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManagement.isLoggedIn()) {
                    Fragment fm = new Update_Profile_Fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                } else {
                    Fragment fm = new Login_Fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();

                }
            }
        });

        updateHeader();

        sideMenu();


        if (savedInstanceState == null) {
            Fragment fm = new Home_fragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentPanel, fm, "Home_fragment")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
        getSupportFragmentManager().

                addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            Fragment fr = getSupportFragmentManager().findFragmentById(R.id.contentPanel);

                            final String fm_name = fr.getClass().getSimpleName();
                            Log.e("backstack: ", ": " + fm_name);
                            if (fm_name.contentEquals("Home_fragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                toggle.setDrawerIndicatorEnabled(true);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                toggle.syncState();

                            } else if (fm_name.contentEquals("My_order_fragment") ||
                                    fm_name.contentEquals("Thanks_fragment")) {
                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                toggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toggle.syncState();

                                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Fragment fm = new Home_fragment();
                                        FragmentManager fragmentManager = getSupportFragmentManager();
                                        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                                                .addToBackStack(null).commit();
                                    }
                                });
                            } else {

                                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

                                toggle.setDrawerIndicatorEnabled(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                toggle.syncState();

                                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        onBackPressed();
                                    }
                                });
                            }

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                });


//        if (sessionManagement.getUserDetails().
//                get(BaseURL.KEY_ID) != null && !sessionManagement.getUserDetails().
//                get(BaseURL.KEY_ID).equalsIgnoreCase())
//
//        {
//            MyFirebaseRegister fireReg = new MyFirebaseRegister(this);
//            fireReg.RegisterUser(sessionManagement.getUserDetails().get(BaseURL.KEY_ID));
//        }

    }

    private void increaseHamburgerSize(ActionBarDrawerToggle mDrawerToggle) {
        mDrawerToggle.setDrawerArrowDrawable(new HamburgerDrawable(this));
    }

    private void setDrawerIconSize() {
        //custom drawer icon
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_bar);

    }


    public void updateHeader() {
        if (sessionManagement.isLoggedIn()) {
            String getname = sessionManagement.getUserDetails().get(BaseURL.KEY_NAME);
            String getimage = sessionManagement.getUserDetails().get(BaseURL.KEY_IMAGE);
            String getemail = sessionManagement.getUserDetails().get(BaseURL.KEY_EMAIL);
            SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(this);
            String previouslyEncodedImage = shre.getString("image_data", "");
            if (!previouslyEncodedImage.equalsIgnoreCase("")) {
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                iv_profile.setImageBitmap(bitmap);
            }
/*
            if(getimage!=null&&!getimage.equals(""))
            Glide.with(this)
                    .load(BaseURL.IMG_PROFILE_URL + getimage)
                    .placeholder(R.drawable.ic_logo_grocel)
                    .crossFade()
                    .into(iv_profile);
*/
            tv_name.setText(getname);

        }
    }

    private void initializeMap()
    {
        /*Runnable initMap = () -> {
            MapView mapView = new MapView(MainActivity.this);
            mapView.onCreate(null);
        };
        new Thread(initMap).start();*/
        MapView mapView = (MapView) findViewById(R.id.mapView);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.d(TAG, "onMapReady");
            }
        });

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "Font/Bold.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    public void sideMenu() {

        if (sessionManagement.isLoggedIn()) {
            //tv_number.setVisibility(View.VISIBLE);
            nav_menu.findItem(R.id.nav_logout).setVisible(true);

            // nav_menu.findItem(R.id.nav_user).setVisible(true);
        } else {

            //tv_number.setVisibility(View.GONE);
            tv_name.setText(getResources().getString(R.string.login_register));
            tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment fm = new Login_Fragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                }
            });
            nav_menu.findItem(R.id.nav_logout).setVisible(false);

            //            nav_menu.findItem(R.id.nav_user).setVisible(false);
        }
    }

    public void setFinish() {
        finish();
    }

    public void setCartCounter(String totalitem) {
        try {
            totalBudgetCount.setText(totalitem);
        } catch (Exception e) {

        }
    }
    private void setFragmentTitle(String title) {
        getSupportActionBar().setTitle(title);
        img_edit_loc.setVisibility(View.GONE);
        toolbar.setClickable(false);
    }
    private void setCurrentLocTitle(){
            String user_address = getLastLocation();
            getSupportActionBar().setTitle("Deliver to " + user_address);

        img_edit_loc.setVisibility(View.VISIBLE);
        toolbar.setClickable(true);

    }

    public void setToolbarTitle(String title) {
        String selectedPlace = PreferenceUtility.getString(this, Constants.selected_place, "");
        if (!title.isEmpty()) {
            setFragmentTitle(title);
        } else if (selectedPlace == null || selectedPlace.isEmpty()) {
           // if(locationUtility != null) {
             //   getSupportActionBar().setTitle("Deliver to " + locationUtility.getLastLocation());
            //}

            //toolbar.setClickable(true);
            setCurrentLocTitle();
        } else {
            getSupportActionBar().setTitle("Deliver to " + selectedPlace);
            toolbar.setClickable(true);
            img_edit_loc.setVisibility(View.VISIBLE);

        }
    }

    /*public void setToolbarTitle(String title) {
        String selectedPlace = PreferenceUtility.getString(this, Constants.selected_place, "");
        if (!title.isEmpty()) {
            setFragmentTitle(title);
        } else if (selectedPlace == null || selectedPlace.isEmpty()) {
            if(tracker != null) {
                //  getSupportActionBar().setTitle("Deliver to " + tracker.getAddress());
            }

            toolbar.setClickable(true);
        } else {
            getSupportActionBar().setTitle("Deliver to " + selectedPlace);
            toolbar.setClickable(true);
        }
    }*/


    @Override
    public void onBackPressed() {
        if (atv_location != null)
            atv_location.setVisibility(View.GONE);

        /*if (img_edit_loc != null)
            img_edit_loc.setVisibility(View.VISIBLE);

        setToolbarTitle("");*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem item = menu.findItem(R.id.action_cart);
        item.setVisible(true);

        View count = item.getActionView();
        count.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(item.getItemId(), 0);
            }
        });

        totalBudgetCount = (TextView) count.findViewById(R.id.actionbar_notifcation_textview);
        totalBudgetCount.setText("" + dbcart.getCartCount());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            UIUtility.showAlert("MainAct", "back button", this);
        }

        if (id == R.id.action_cart) {
            if (dbcart.getCartCount() > 0) {
                Fragment fm = new Cart_fragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            } else {
                Fragment fm = new Empty_cart_fragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                        .addToBackStack(null).commit();
            }
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
//
//
//
//
//                recreate();
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }


    private void navigateToLocScreen() {
        atv_location.setVisibility(View.VISIBLE);
        img_edit_loc.setVisibility(View.GONE);

        Fragment fm = new Location_fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                .addToBackStack(null).commit();
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }

    private void setBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        //  CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavigationBehaviour());

        LatoBLack latoBLack = new LatoBLack(this);
        latoBLack.applyLatoBlackFont(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fm = null;
                Bundle args = new Bundle();

                if (id == R.id.navigation_home) {
                    fm = new Home_fragment();

                } else if (id == R.id.navigation_oder) {
                    if (sessionManagement.isLoggedIn()) {
                        fm = new My_Order_Fragment();
                    } else {
                        fm = new Login_Fragment();
                    }

                } else if (id == R.id.navigation_reward) {
                    if (sessionManagement.isLoggedIn()) {
                        fm = new Reward_fragment();
                    } else {
                        fm = new Login_Fragment();
                    }

                } else if (id == R.id.navigation_wallet) {
                    if (sessionManagement.isLoggedIn()) {
                        fm = new Wallet_fragment();
                    } else {
                        fm = new Login_Fragment();
                    }

                } else if (id == R.id.navigation_account) {
                    fm = new Account_fragment();
                   /* if (sessionManagement.isLoggedIn()) {
                        fm = new Edit_profile_fragment();
                    } else {
                        fm = new Login_Fragment();
                    }*/
                }

                if (fm != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                            .addToBackStack(null).commit();
                }
                return true;
            }
        });
    }


    @SuppressLint("ResourceType")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fm = null;
        Bundle args = new Bundle();

        if (id == R.id.nav_home) {
            fm = new Home_fragment();

        } else if (id == R.id.nav_contact) {
            fm = new Contact_Us_Fragment();
        } else if (id == R.id.nav_aboutus) {
            fm = new About_us_fragment();
            // args.putString("url", BaseURL.GET_ABOUT_URL);
            //args.putString("title", getResources().getString(R.string.nav_about));
            fm.setArguments(args);
        } else if (id == R.id.nav_policy) {
            fm = new Terms_and_Privacy_fragment();
            fm.setArguments(args);
        } else if (id == R.id.nav_review) {
            reviewOnApp();
        } else if (id == R.id.nav_help) {
            fm = new Help_Support_fragment();
            args.putString("url", BaseURL.GET_SUPPORT_URL);
            args.putString("title", getResources().getString(R.string.nav_terms));
            fm.setArguments(args);
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_logout) {
            sessionManagement.logoutSession();
            finish();
        } else if (id == R.id.nav_categories) {
            if (sessionManagement.isLoggedIn()) {
                fm = new Category_fragment();
            } else {
                fm = new Category_fragment();
                //fm = new Login_Fragment();
            }
        } else if (id == R.id.nav_brands) {

        } else if (id == R.id.nav_order) {
            if (sessionManagement.isLoggedIn()) {
                fm = new My_Order_Fragment();
            } else {
                fm = new Login_Fragment();
            }
        } else if (id == R.id.nav_rewards) {
            if (sessionManagement.isLoggedIn()) {
                fm = new Reward_fragment();
            } else {
                fm = new Login_Fragment();
            }
        } else if (id == R.id.nav_wallet) {
            if (sessionManagement.isLoggedIn()) {
                fm = new Wallet_fragment();
            } else {
                fm = new Login_Fragment();
            }

        }


        if (fm != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.contentPanel, fm)
                    .addToBackStack(null).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        try {
            super.startActivityForResult(intent, requestCode);
        } catch (Exception ignored) {
        }

    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi friends i am using ." + " http://play.google.com/store/apps/details?id=" + getPackageName() + " APP");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    public void reviewOnApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }


    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;

        if (!isConnected) {
            Intent intent = new Intent(MainActivity.this, NetworkError.class);
            startActivity(intent);
        }
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(BaseURL.PREFS_NAME, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id: " + regId);
        if (!TextUtils.isEmpty(regId)) {
            // txtRegId.setText("Firebase Reg Id: " + regId);
        } else {
            // txtRegId.setText("Firebase Reg Id is not received yet!");
        }
    }

    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");

        Log.d("Tecmanic", "Subscribed");
//        Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d("Tecmanic", token);
        //      Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
    }


//

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // register reciver

    }

    private void make_number() {
        String tag_json_obj = "json_category_req";
        Map<String, String> params = new HashMap<String, String>();
        params.put("parent", "");

        CustomVolleyJsonRequest jsonObjReq = new CustomVolleyJsonRequest(Request.Method.GET,
                BaseURL.GET_Call, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {
                    if (response != null && response.length() > 0) {

                        JSONArray jsonArray = response.getJSONArray("numbers");


                        JSONObject jsonObject = jsonArray.getJSONObject(0);


                        whatsappnum = jsonObject.getString("whatsapp");

                        callnum = jsonObject.getString("calling");

                        calleditor.putString("whatsapp", whatsappnum);

                        calleditor.putString("call", callnum);

                        calleditor.commit();


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_time_out), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    public AutoCompleteTextView getLocationEditText() {
        return atv_location;
    }


    /**
     * Receiver for broadcasts sent by {@link LocationUpdatesService}.
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
            if (location != null) {
                Toast.makeText(MainActivity.this, UtilityHelper.getLocationText(location),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

   /* @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(UtilityHelper.KEY_REQUESTING_LOCATION_UPDATES)) {
           // setButtonsState(sharedPreferences.getBoolean(UtilityHelper.KEY_REQUESTING_LOCATION_UPDATES, false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton.setEnabled(false);
            mRemoveLocationUpdatesButton.setEnabled(true);
        } else {
            mRequestLocationUpdatesButton.setEnabled(true);
            mRemoveLocationUpdatesButton.setEnabled(false);
        }
    }*/

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };


    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            startLocationPermissionRequest();

            /*showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });*/

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
              /*  showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });*/
            }
        }
    }

    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    public String getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            user_address = tracker.getAddress(mLastLocation);
                            //UIUtility.showLongToast("Lat  Long"+ address,getApplicationContext());
                            getSupportActionBar().setTitle(user_address);
                            toolbar.setClickable(true);


                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            UIUtility.showLongToast("no location detected",getApplicationContext());
                        }
                    }
                });

        return user_address;
    }

}
