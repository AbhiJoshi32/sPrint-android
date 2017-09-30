package com.binktec.sprint.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.SettingFragmentListener;
import com.binktec.sprint.interactor.presenter.SettingPresenterListener;
import com.binktec.sprint.presenter.SettingPresenter;
import com.binktec.sprint.ui.fragment.SettingFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements SettingFragmentListener,SettingPresenterListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Settings";
    private SettingPresenter settingPresenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.settingFragment)
    FrameLayout settingFragment;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private GoogleApiClient mGoogleApiClient;

    boolean toOpenActivity = false;
    Class newActivityClass;

    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        settingPresenter = new SettingPresenter(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        headerView =  navView.getHeaderView(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        settingPresenter.appStart();
    }


    private void setUpNavigationView() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        toOpenActivity = true;
                        newActivityClass = PrintJobActivity.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_start_print:
                        toOpenActivity = true;
                        newActivityClass = TransactionActivity.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_available_shops:
                        toOpenActivity = true;
                        newActivityClass = AvailableShopActivity.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        toOpenActivity = true;
                        newActivityClass = AboutUs.class;
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_privacy_policy:
                        toOpenActivity = true;
                        newActivityClass = PrivacyPolicyActivity.class;
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (toOpenActivity) {
                    openNewActivity(newActivityClass);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    private void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent;
            intent = new Intent(SettingsActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public void openAuthActivity() {
        Intent intent;
        intent = new Intent(SettingsActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void initializePrintJob() {
        setSupportActionBar(toolbar);
        setToolbarTitle();
        setUpNavigationView();
        loadFragment();
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });
    }

    @Override
    public void emailSentSuccessful() {
        showToast("Sending reset password mail to your email");
    }

    @Override
    public void emailSentUnsuccessful() {
        showToast("Unable to send email. Check net connectivity");
    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SettingFragment settingFragment = SettingFragment.mewInstance();
        ft.replace(R.id.settingFragment, settingFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void openNewActivity(Class activityClass) {
        Intent intent;
        intent = new Intent(SettingsActivity.this, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void changePassClicked() {
        showToast("Sending reset password mail to your email");
        settingPresenter.updatePassword();
    }

    @Override
    public void logoutClicked() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    settingPresenter.logout();
                } else {
                    showToast("Unable to Logout");
                }
            }
        });
    }

    private void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}