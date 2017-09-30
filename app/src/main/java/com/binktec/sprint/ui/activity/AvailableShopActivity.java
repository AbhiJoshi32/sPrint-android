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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AvailShopFragmentListener;
import com.binktec.sprint.interactor.presenter.AvailableShopPresenterListener;
import com.binktec.sprint.modal.pojo.shop.Shop;
import com.binktec.sprint.presenter.AvailableShopPresenter;
import com.binktec.sprint.ui.fragment.AvailableShopFragment;

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

    boolean toOpenActivity = false;
    Class newActivityClass;

    View headerView;


    private AvailableShopPresenter availableShopPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_shop);
        ButterKnife.bind(this);
        availableShopPresenter = new AvailableShopPresenter(this);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        selectNavMenu();
        headerView =  navigationView.getHeaderView(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    public void initTransactionActivity() {
        loadFragment();
        setUpNavigationView();
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toOpenActivity = true;
                newActivityClass = SettingsActivity.class;
            }
        });
    }

    @Override
    public void showToast() {
        Toast.makeText(this, "Network Error", Toast.LENGTH_SHORT).show();
        AvailableShopFragment availableShopFragment = (AvailableShopFragment) getSupportFragmentManager().findFragmentById(R.id.shopFragment);
        if (availableShopFragment != null) {
            availableShopFragment.hideShopProgressBar();
        }
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
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        toOpenActivity = true;
                        newActivityClass = PrintJobActivity.class;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_start_print:
                        toOpenActivity = true;
                        newActivityClass = TransactionActivity.class;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_available_shops:
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        toOpenActivity = true;
                        newActivityClass = AboutUs.class;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_privacy_policy:
                        toOpenActivity = true;
                        newActivityClass = PrivacyPolicyActivity.class;
                        drawer.closeDrawers();
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (toOpenActivity) {
                    Intent intent = new Intent(AvailableShopActivity.this, newActivityClass);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent;
            intent = new Intent(AvailableShopActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }
}