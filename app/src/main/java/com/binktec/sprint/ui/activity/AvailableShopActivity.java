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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AvailShopFragmentListener;
import com.binktec.sprint.interactor.presenter.AvailableShopPresenterListener;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.binktec.sprint.presenter.AvailableShopPresenter;
import com.binktec.sprint.ui.fragment.AvailableShopFragment;
import com.binktec.sprint.utility.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableShopActivity extends AppCompatActivity implements AvailShopFragmentListener, AvailableShopPresenterListener {

    private static final String TAG = "Available Shops";
    @BindView(R.id.progressBar2)
    ProgressBar progressBar2;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    private String urlNavHeaderBg;

    private TextView txtName;
    private ImageView imgNavHeaderBg;
    private ImageView imgProfile;

    private AvailableShopPresenter availableShopPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_shop);
        ButterKnife.bind(this);
        urlNavHeaderBg = getString(R.string.background_url);
        availableShopPresenter = new AvailableShopPresenter(this);
        View navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        availableShopPresenter.appStart();
    }

    @Override
    public void getShops() {
        availableShopPresenter.retrieveShopList();
    }

    @Override
    public void updateShopList(List<Shop> apiShops) {
        AvailableShopFragment availableShopFragment = (AvailableShopFragment) getSupportFragmentManager().findFragmentById(R.id.shopFragment);
        if (availableShopFragment != null) {
            availableShopFragment.updateShopRecyclerView(apiShops);
        }
    }

    @Override
    public void initTransactionActivity(String displayName, String photoUrl) {
        setSupportActionBar(toolbar);
        loadNavHeader(displayName, photoUrl);
        setToolbarTitle();
        selectNavMenu();
        setUpNavigationView();
        loadFragment();
    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AvailableShopFragment availShopFragment = AvailableShopFragment.newInstance();
        ft.replace(R.id.shopFragment, availShopFragment);
        ft.commitAllowingStateLoss();
    }


    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        drawer.closeDrawers();
                        intent = new Intent(AvailableShopActivity.this, PrintJobActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.nav_start_print:
                        drawer.closeDrawers();
                        intent = new Intent(AvailableShopActivity.this, TransactionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_available_shops:
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_manage_accounts:
                        drawer.closeDrawers();
                        intent = new Intent(AvailableShopActivity.this, ManageAccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        drawer.closeDrawers();
                        intent = new Intent(AvailableShopActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_about_us:
                        drawer.closeDrawers();
                        intent = new Intent(AvailableShopActivity.this, AboutUs.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        drawer.closeDrawers();
                        intent = new Intent(AvailableShopActivity.this, PrivacyPolicyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        return true;
                }
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();
    }

    private void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(2).setChecked(true);
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

        navigationView.getMenu().getItem(0).setActionView(R.layout.menu_dot);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent;
            intent = new Intent(AvailableShopActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }
}