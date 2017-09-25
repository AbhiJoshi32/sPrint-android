package com.binktec.sprint.ui.activity;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.PrintJobFragmentListener;
import com.binktec.sprint.interactor.presenter.PrintJobPresenterListener;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.presenter.PrintJobPresenter;
import com.binktec.sprint.ui.adapter.PrintJobPagerAdapter;
import com.binktec.sprint.ui.fragment.HistoryFragment;
import com.binktec.sprint.ui.fragment.ProgressFragment;
import com.binktec.sprint.utility.CircleTransform;
import com.binktec.sprint.utility.SessionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrintJobActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener, PrintJobPresenterListener, PrintJobFragmentListener {

    private static final String TAG = "Print Jobs";
    private static final int WRITE_STORAGE_PERMISSION_CODE = 24;
    private String urlNavHeaderBg;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.print_job_pager)
    ViewPager printJobPager;

    private TextView txtName;
    private ImageView imgNavHeaderBg;
    private ImageView imgProfile;

    PrintJobPagerAdapter printJobPagerAdapter;
    private PrintJobPresenter printJobPresenter;

    private int STORAGE_PERMISSION_CODE = 23;

    private ProgressFragment progressFragment;
    private HistoryFragment historyFragment;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_job);
        context = this;
        SessionManager.initInstance(this);
        urlNavHeaderBg = getApplicationContext().getString(R.string.background_url);
        ButterKnife.bind(this);
        View navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        printJobPresenter = new PrintJobPresenter(this);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        setUpTabLayout();
        setUpNavigationView();

        if (!isReadStorageAllowed()) {
            requestStoragePermission();
        }
        if (!isWriteStorageAllowed()) {
            requestWriteStoragePermission();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        printJobPresenter.appStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"on Pause Called");
    }

    private void setUpTabLayout() {
        printJobPagerAdapter = new PrintJobPagerAdapter(getSupportFragmentManager());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        printJobPager.setAdapter(printJobPagerAdapter);
        tabLayout.setupWithViewPager(printJobPager);
    }

    private void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG);
        }
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
        navigationView.getMenu().getItem(0).setActionView(R.layout.menu_dot);
    }

    @Override
    public void openAuthActivity() {
        Intent intent = new Intent(PrintJobActivity.this, AuthActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void initializePrintJob(String displayName, Uri photoUrl) {
        selectNavMenu();
        loadNavHeader(displayName, photoUrl);
    }

    @Override
    public void showToastError(String s) {

    }

    @Override
    public void progressItemInserted(final PrintJobDetail transactionDetail, final int i) {
        Log.d(TAG,"preogress inserted isfinishing" + isFinishing());
        if (isFinishing()) {

            Intent intent = new Intent(PrintJobActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            progressFragment = (ProgressFragment) printJobPagerAdapter.getRegisteredFragment(0);
            if (progressFragment != null) {
                progressFragment.insertProgressRecyclerView(transactionDetail, i);
            }
        }
    }

    @Override
    public void progressItemChanged(PrintJobDetail changedTransaction, int changedIndex) {
        Log.d(TAG,"preogress changed isfinishing" + isFinishing());
        if (isFinishing()) {
            Intent intent = new Intent(PrintJobActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            progressFragment = (ProgressFragment) printJobPagerAdapter.getRegisteredFragment(0);
            if (progressFragment != null) {
                progressFragment.changeProgressRecyclerView(changedTransaction, changedIndex);
            }
        }
    }

    @Override
    public void progressItemRemoved(int removeIndex) {
        if (isFinishing()) {
            Intent intent = new Intent(PrintJobActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            progressFragment = (ProgressFragment) printJobPagerAdapter.getRegisteredFragment(0);
            if (progressFragment != null) {
                progressFragment.removeProgressRecyclerView(removeIndex);
            }
        }
    }

    @Override
    public void initProgressList(List<PrintJobDetail> progressPrintJobDetails) {
        progressFragment = (ProgressFragment) printJobPagerAdapter.getRegisteredFragment(0);
        if (progressFragment != null) {
            progressFragment.initProgressRecyclerView(progressPrintJobDetails);
        }
    }

    @Override
    public void initHistoryList(List<PrintJobDetail> historyPrintJobDetails) {
        historyFragment = (HistoryFragment) printJobPagerAdapter.getRegisteredFragment(1);
        Log.d(TAG,"History Fragment" + historyFragment);
        if (historyFragment != null) {
            historyFragment.initHistoryRecyclerView(historyPrintJobDetails);
        }
    }

    @Override
    public void historyItemInserted(final PrintJobDetail historyDetail, final int i) {
        if (isFinishing()) {
            Intent intent = new Intent(PrintJobActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } else {
            historyFragment = (HistoryFragment) printJobPagerAdapter.getRegisteredFragment(1);
            if (historyFragment != null) {
                Log.d(TAG, "history item to be inserted");
                historyFragment.addHistoryRecyclerView(historyDetail, i);
            }
            if (historyDetail.getStatus().equals("Printed")) {
                showPrintConfirmDialog();
            } else if (historyDetail.getStatus().equals("Rejected")) {
                showRejectDialog();
            }
        }
    }

    @Override
    public void showPrintConfirmDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        printJobPager.setCurrentItem(1);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setMessage("Your Document is printed").setPositiveButton("Check History", dialogClickListener)
                    .setNegativeButton("Ok", dialogClickListener).show();
    }

    @Override
    public void showRejectDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        printJobPager.setCurrentItem(1);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your Document is rejected").setPositiveButton("Check History", dialogClickListener)
                .setNegativeButton("Ok", dialogClickListener).show();
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        Log.d(TAG, "Pritning job");
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_start_print:
                        drawer.closeDrawers();
                        intent = new Intent(PrintJobActivity.this, TransactionActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_available_shops:
                        drawer.closeDrawers();
                        Log.d(TAG, "Available shop");
                        intent = new Intent(PrintJobActivity.this, AvailableShopActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_manage_accounts:
                        drawer.closeDrawers();
                        Log.d(TAG, "Manage account");
                        intent = new Intent(PrintJobActivity.this, ManageAccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_settings:
                        drawer.closeDrawers();
                        intent = new Intent(PrintJobActivity.this, SettingsActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_about_us:
                        drawer.closeDrawers();
                        Log.d(TAG, "About Us");
                        intent = new Intent(PrintJobActivity.this, AboutUs.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
                    case R.id.nav_privacy_policy:
                        drawer.closeDrawers();
                        Log.d(TAG, "privacy policy");
                        Log.d(TAG, "About Us");
                        intent = new Intent(PrintJobActivity.this, PrivacyPolicyActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        break;
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        printJobPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On resumed called");
    }

    @Override
    public void onStop() {
        super.onStop();
        printJobPresenter.onStopCalled();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        printJobPresenter = null;
        Log.d(TAG, "On destroy called");
    }

    @Override
    public void printCardClicked() {
        Log.d(TAG, "Print Caed Clicked");
    }

    @Override
    public void initProgressFragment() {
        printJobPresenter.getPrintJobList();
    }

    @Override
    public void initHistoryFragment() {
        printJobPresenter.getHistoryList();
    }

    @Override
    public void cancelUpload(final PrintJobDetail printJobDetail) {
        try {
            DialogInterface.OnClickListener cancelDialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            printJobPresenter.cancelUpload(printJobDetail);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to cancel?").setPositiveButton("Yes", cancelDialogClickListener)
                    .setNegativeButton("No", cancelDialogClickListener).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.print_float_button)
    public void onViewClicked() {
        Intent intent;
        intent = new Intent(PrintJobActivity.this, TransactionActivity.class);
        startActivity(intent);
    }

    private void requestWriteStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_PERMISSION_CODE);
    }

    private boolean isWriteStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            Log.d(TAG, "Grant result is for read " + grantResults[0]);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == WRITE_STORAGE_PERMISSION_CODE) {
            Log.d(TAG, "Grant result is for write " + grantResults[0]);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

}