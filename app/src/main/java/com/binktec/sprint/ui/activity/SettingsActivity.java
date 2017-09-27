package com.binktec.sprint.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.SettingFragmentListener;
import com.binktec.sprint.interactor.presenter.SettingPresenterListener;
import com.binktec.sprint.presenter.SettingPresenter;
import com.binktec.sprint.ui.fragment.SettingFragment;
import com.binktec.sprint.utility.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements SettingFragmentListener,SettingPresenterListener {

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

    private TextView txtName;
    private String urlNavHeaderBg;
    private ImageView imgNavHeaderBg;
    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        urlNavHeaderBg = getString(R.string.background_url);
        settingPresenter = new SettingPresenter(this);
        View navHeader = navView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);
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
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        Log.d(TAG, "Pritning job");
                        drawerLayout.closeDrawers();
                        intent = new Intent(SettingsActivity.this, PrintJobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_start_print:
                        drawerLayout.closeDrawers();
                        intent = new Intent(SettingsActivity.this, TransactionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Log.d(TAG, "start printing");
                        break;
                    case R.id.nav_available_shops:
                        intent = new Intent(SettingsActivity.this, AvailableShopActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.nav_manage_accounts:
                        intent = new Intent(SettingsActivity.this, ManageAccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        drawerLayout.closeDrawers();
                        Log.d(TAG, "manage");
                        break;
                    case R.id.nav_settings:
                        drawerLayout.closeDrawers();
                        intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Log.d(TAG, "Settings");
                        break;
                    case R.id.nav_about_us:
                        drawerLayout.closeDrawers();
                        intent = new Intent(SettingsActivity.this, AboutUs.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Log.d(TAG, "About Us");
                        return true;
                    case R.id.nav_privacy_policy:
                        drawerLayout.closeDrawers();
                        intent = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        Log.d(TAG, "privacy policy");
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
        navView  .getMenu().getItem(4).setChecked(true);
    }


    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(SettingsActivity.this, PrintJobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void openInstructionActivity() {
        Intent intent;
        intent = new Intent(SettingsActivity.this, InstructionActivity.class);
        startActivity(intent);
    }

    @Override
    public void openHelpActivity() {
        Intent intent;
        intent = new Intent(SettingsActivity.this, HelpActivity.class);
        startActivity(intent);
    }

    @Override
    public void openAuthActivity() {
        Intent intent;
        intent = new Intent(SettingsActivity.this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void initializePrintJob(String displayName, Uri photoUrl) {
        setSupportActionBar(toolbar);
        loadNavHeader(displayName, photoUrl);
        setToolbarTitle();
        selectNavMenu();
        setUpNavigationView();
        loadFragment();
    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SettingFragment settingFragment = SettingFragment.mewInstance();
        ft.replace(R.id.settingFragment, settingFragment);
        ft.commitAllowingStateLoss();
    }

    private void loadNavHeader(String displayName, Uri photoUrl) {
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
}