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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.ManageFragmentListener;
import com.binktec.sprint.interactor.presenter.ManageAccountPresenterListener;
import com.binktec.sprint.presenter.ManageAccountPresenter;
import com.binktec.sprint.ui.fragment.ManageAccountFragment;
import com.binktec.sprint.utility.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ManageAccountActivity extends AppCompatActivity implements ManageAccountPresenterListener,ManageFragmentListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Manage Accounts";
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.manage_acct_fragment)
    FrameLayout manageAcctFragment;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;


    private String urlNavHeaderBg;

    private TextView txtName;
    private ImageView imgNavHeaderBg;
    private ImageView imgProfile;
    private GoogleApiClient mGoogleApiClient;

    private ManageAccountPresenter manageAccountPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);
        ButterKnife.bind(this);

        urlNavHeaderBg = getString(R.string.background_url);
        View navHeader = navView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        manageAccountPresenter = new ManageAccountPresenter(this);
        manageAccountPresenter.appStart();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ManageAccountFragment availShopFragment = ManageAccountFragment.newInstance();
        ft.replace(R.id.manage_acct_fragment, availShopFragment);
        ft.commitAllowingStateLoss();
    }


    private void setUpNavigationView() {
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        drawerLayout.closeDrawers();
                        intent = new Intent(ManageAccountActivity.this, PrintJobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_start_print:
                        drawerLayout.closeDrawers();
                        intent = new Intent(ManageAccountActivity.this, TransactionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_available_shops:
                        intent = new Intent(ManageAccountActivity.this, AvailableShopActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_manage_accounts:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_settings:
                        drawerLayout.closeDrawers();
                        intent = new Intent(ManageAccountActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_about_us:
                        drawerLayout.closeDrawers();
                        intent = new Intent(ManageAccountActivity.this, AboutUs.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;
                    case R.id.nav_privacy_policy:
                        drawerLayout.closeDrawers();
                        intent = new Intent(ManageAccountActivity.this, PrivacyPolicyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;
                }
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
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

    private void selectNavMenu() {
        navView  .getMenu().getItem(2).setChecked(true);
    }

    private void loadNavHeader(String displayName, String photoUrl) {
        txtName.setText(displayName);
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);
        if (photoUrl != null) {
            Glide.with(this).load(photoUrl)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(this))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgProfile);
        }

        navView.getMenu().getItem(0).setActionView(R.layout.menu_dot);
    }

    @Override
    public void initializePrintJob(String displayName, String photoUrl) {
        setSupportActionBar(toolbar);
        loadNavHeader(displayName, photoUrl);
        setToolbarTitle();
        selectNavMenu();
        setUpNavigationView();
        loadFragment();
    }

    @Override
    public void openAuthActivity() {
        Intent intent = new Intent(ManageAccountActivity.this, PrintJobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void emailSentSuccessful() {
        ManageAccountFragment manageFrament = (ManageAccountFragment) getSupportFragmentManager().findFragmentById(R.id.manage_acct_fragment);
        manageFrament.enableBtn();
        showToast("Password reset mail has been sent to your email");
    }

    @Override
    public void emailSentUnsuccessful() {
        ManageAccountFragment manageFrament = (ManageAccountFragment) getSupportFragmentManager().findFragmentById(R.id.manage_acct_fragment);
        manageFrament.enableBtn();
        showToast("Unable to send email. Check net connectivity");
    }

    @Override
    public void changePassBtnClicked() {
        showToast("Sending reset password mail to your email");
        manageAccountPresenter.updatePassword();
    }

    @Override
    public void signOutBtnClicked() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    manageAccountPresenter.signOut();
                }
                else {
                    showToast("Unable to logout");
                    ManageAccountFragment manageFrament = (ManageAccountFragment) getSupportFragmentManager().findFragmentById(R.id.manage_acct_fragment);
                    manageFrament.enableBtn();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent intent;
            intent = new Intent(ManageAccountActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showToast("No Internet Connection");
    }
}
