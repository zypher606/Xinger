package io.github.froger.xinger.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.BindString;
import io.github.froger.xinger.R;
import io.github.froger.xinger.model.Response;
import io.github.froger.xinger.model.User;
import io.github.froger.xinger.network.NetworkUtil;
import io.github.froger.xinger.ui.utils.CircleTransformation;
import io.github.froger.xinger.utils.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Miroslaw Stanek on 15.07.15.
 */
public class BaseDrawerActivity extends BaseActivity {

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;
    @BindString(R.string.user_profile_photo)
    String profilePhoto;

    //Cannot be bound via Butterknife, hosting view is initialized later (see setupHeader() method)
    private ImageView ivMenuUserProfilePhoto;



    // Initiating navigation drawer values
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;

    private CompositeSubscription mSubscriptions;


    private TextView drawerMenuProfileName;

    SessionManager manager;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);
        bindViews();
        setupHeader();


        // Sidenav initiation
        mSubscriptions = new CompositeSubscription();
        initViews();
        initSharedPreferences();
        loadSidenavProfile();


        /*****************************/
        manager = new SessionManager();
        /*****************************/

    }


    private void initViews() {
        View headerView = vNavigation.getHeaderView(0);
        drawerMenuProfileName = (TextView) headerView.findViewById(R.id.drawerMenuProfileName);
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
    }

    private void loadSidenavProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {
        Log.d("Fetched username", user.getName());
        drawerMenuProfileName.setText(user.getName());
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                //showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            //showSnackBarMessage("Network Error !");
        }
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

    private void setupHeader() {
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });


        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                Intent intent;
                switch (id) {
                    case R.id.menu_feed:

                        intent = new Intent(BaseDrawerActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        Log.d("Clicked: ", "Menu feed");
                        break;
                    case R.id.menu_direct:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.menu_news:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.menu_popular:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.menu_photos_nearby:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;

                    case R.id.menu_photo_you_liked:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;

                    case R.id.menu_settings:
                        intent = new Intent(BaseDrawerActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.menu_logout:
                        manager.setPreferences(BaseDrawerActivity.this, "status", "0");
                        intent = new Intent(BaseDrawerActivity.this, UserAuthenticationActivity.class);
                        startActivity(intent);
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
            }

        });





        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivMenuUserProfilePhoto);
    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                overridePendingTransition(0, 0);
            }
        }, 200);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


/*
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View v = getWindow().getCurrentFocus();


        if (id == R.id.menu_feed) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    int[] startingLocation = new int[2];
//                    v.getLocationOnScreen(startingLocation);
//                    startingLocation[0] += v.getWidth() / 2;
//                    DashboardActivity.startDashboardFromLocation(startingLocation, BaseDrawerActivity.this);
//                    overridePendingTransition(0, 0);
//                }
//            }, 200);
            Log.d("Clicked:", "Menu feed");
        } else if (id == R.id.menu_direct) {

        } else if (id == R.id.menu_news) {

        } else if (id == R.id.menu_popular) {

        } else if (id == R.id.menu_photos_nearby) {

        } else if (id == R.id.menu_photo_you_liked) {

        } else if (id == R.id.menu_settings) {

        } else if (id == R.id.menu_about) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }

}
