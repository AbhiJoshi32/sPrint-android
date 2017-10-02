package com.binktec.sprint.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.TransactionFragmentListener;
import com.binktec.sprint.interactor.presenter.TransactionPresenterListener;
import com.binktec.sprint.modal.pojo.FileDetail;
import com.binktec.sprint.modal.pojo.PrintDetail;
import com.binktec.sprint.modal.pojo.PrintTransaction;
import com.binktec.sprint.presenter.TransactionPresenter;
import com.binktec.sprint.ui.adapter.TransactionPagerAdapter;
import com.binktec.sprint.ui.fragment.ChooseFileFragment;
import com.binktec.sprint.ui.fragment.ChooseShopFragment;
import com.binktec.sprint.ui.fragment.PrintDetailFragment;
import com.binktec.sprint.ui.ui_utility.CustomViewPager;
import com.layer_net.stepindicator.StepIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionActivity extends AppCompatActivity implements TransactionPresenterListener, TransactionFragmentListener {
    @BindView(R.id.progressBar2)
    ProgressBar toolProgressBar;

    private boolean donePrintStatus = true;

    private static final String TAG_FILE = "Choose File";
    private static final String TAG_PRINT_DETAIL = "Print Details";
    private static final String TAG_SHOP = "Choose Shop";
    private static final int UPLOAD_CODE = 1000;

    boolean toOpenActivity = false;
    Class newActivityClass;

    private String TAG_CURR = TAG_FILE;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.step_indicator)
    StepIndicator stepIndicator;
    @BindView(R.id.viewPager)
    CustomViewPager viewPager;
    private TransactionPresenter transactionPresenter;

    TransactionPagerAdapter transactionPagerAdapter;

    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);
        transactionPresenter = new TransactionPresenter(this);
        headerView =  navigationView.getHeaderView(0);
        setSupportActionBar(toolbar);
        setToolbarTitle();
        selectNavMenu();
        setUpNavigationView();
        setUpViewPager();
        transactionPresenter.appStart();
    }

    @Override
    public void initTransactionActivity() {
        TAG_CURR = TAG_FILE;
        transactionPagerAdapter = new TransactionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(transactionPagerAdapter);
        stepIndicator.setupWithViewPager(viewPager);
        stepIndicator.setClickable(false);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.closeDrawers();
                toOpenActivity = true;
                newActivityClass = SettingsActivity.class;
            }
        });
    }

    @Override
    public void updateChooseFileFragment(List<FileDetail> chosenFiles) {
        ChooseFileFragment chooseFileFragment = (ChooseFileFragment)
                (getSupportFragmentManager().
                        findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + viewPager.getCurrentItem()));
        chooseFileFragment.updateFileList(chosenFiles);
    }

    @Override
    public void showPrintDetailError(String s) {
        PrintDetailFragment printDetailFrag = (PrintDetailFragment)
                (getSupportFragmentManager().
                        findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 1));
        printDetailFrag.showPagesTextError();
        viewPager.setCurrentItem(1, true);
    }

    @Override
    public void showFileError(String empty) {
        viewPager.setCurrentItem(0, true);
        toolProgressBar.setVisibility(View.GONE);
        showToastError("Please add file");
    }

    @Override
    public void updatePrintDetails(int size) {
        viewPager.setCurrentItem(1, true);
        PrintDetailFragment printDetailFrag = (PrintDetailFragment)
                (getSupportFragmentManager().
                        findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 1));
        if (printDetailFrag != null)
            printDetailFrag.updatePrintDetailFragment(size);
    }

    @Override
    public void proceedToShopSelection() {
        viewPager.setCurrentItem(2, true);
    }

    @Override
    public void updateShopFragment(List<PrintTransaction> printTransactions) {
        ChooseShopFragment chooseShopFragment = (ChooseShopFragment) getSupportFragmentManager().
                findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 2);
        if (chooseShopFragment != null) {
            chooseShopFragment.updateShopRecyclerView(printTransactions);
        }
    }

    @Override
    public void openPrintJobActivity() {
        Intent intent = new Intent(TransactionActivity.this, PrintJobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    public void disableDone() {
        showToastError("File is being processed");
        donePrintStatus = false;
        toolProgressBar.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    @Override
    public void enableDone() {
        donePrintStatus = true;
        toolProgressBar.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    @Override
    public void ongoingUpload() {
        showToastError("Let one upload complete to print again");
        openPrintJobActivity();
    }

    @Override
    public void FileError(List<FileDetail> chosenFiles) {
        donePrintStatus = true;
        viewPager.setCurrentItem(0, true);
        toolProgressBar.setVisibility(View.GONE);
        invalidateOptionsMenu();
        ChooseFileFragment chooseFileFragment = (ChooseFileFragment)
                (getSupportFragmentManager().
                        findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 0));
        chooseFileFragment.updateFileList(chosenFiles);
        showToastError("Some error in File. Check if the file is present");

    }

    private void setToolbarTitle() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(TAG_CURR);
        }
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_printing_job:
                        newActivityClass = PrintJobActivity.class;
                        toOpenActivity = true;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_start_print:
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_available_shops:
                        newActivityClass = AvailableShopActivity.class;
                        toOpenActivity = true;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_about_us:
                        drawer.closeDrawers();
                        newActivityClass = AboutUs.class;
                        toOpenActivity = true;
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_privacy_policy:
                        newActivityClass = PrivacyPolicyActivity.class;
                        toOpenActivity = true;
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
                    openActivity(newActivityClass);
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

    private void openActivity(Class newActivityClass) {
        Intent intent = new Intent(TransactionActivity.this, newActivityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setUpViewPager() {
        viewPager.setAllowedSwipeDirection(CustomViewPager.SwipeDirection.none);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        TAG_CURR = TAG_FILE;
                        break;
                    case 1:
                        TAG_CURR = TAG_PRINT_DETAIL;
                        break;
                    case 2:
                        TAG_CURR = TAG_SHOP;
                        break;
                }
                setToolbarTitle();
                invalidateOptionsMenu();
            }
        });
    }

    @Override
    public void onStart() {
        transactionPresenter.onStartCalled();
        super.onStart();
    }

    @Override
    public void onStop() {
        transactionPresenter.onStopCalled();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        transactionPresenter.onDestroyCalled();
        transactionPresenter = null;
        super.onDestroy();
    }

    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (TAG_CURR.equals(TAG_PRINT_DETAIL)) {
            viewPager.setCurrentItem(0, true);

        } else if (TAG_CURR.equals(TAG_SHOP)) {
            viewPager.setCurrentItem(1, true);
        } else {
            Intent intent = new Intent(TransactionActivity.this, PrintJobActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (TAG_CURR.equals(TAG_FILE)) {
            getMenuInflater().inflate(R.menu.choose_file, menu);
        }
        if (TAG_CURR.equals(TAG_PRINT_DETAIL)) {
            getMenuInflater().inflate(R.menu.choose_option, menu);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (TAG_CURR.equals(TAG_FILE)) {
            getMenuInflater().inflate(R.menu.choose_file, menu);
            menu.findItem(R.id.action_upload_add).setEnabled(donePrintStatus);
        }
        if (TAG_CURR.equals(TAG_PRINT_DETAIL)) {
            getMenuInflater().inflate(R.menu.choose_option, menu);
            menu.findItem(R.id.action_print_option_done).setEnabled(donePrintStatus);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (TAG_CURR.equals(TAG_FILE)) {
            if (id == R.id.action_upload_add) {
                item.setEnabled(false);
                startFileChooser();
            }
            if (id == R.id.action_upload_done) {
                transactionPresenter.doneChooseFile();
            }
        } else if (TAG_CURR.equals(TAG_PRINT_DETAIL)) {
            if (id == R.id.action_print_option_done) {
                donePrintDetail();
            }
        }
        return true;
    }

    private void donePrintDetail() {
        if (TAG_CURR.equals(TAG_PRINT_DETAIL)) {
            PrintDetailFragment printOptFrag = (PrintDetailFragment)
                    (getSupportFragmentManager().
                            findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 1));
            if (printOptFrag !=  null) {
                PrintDetail detail = printOptFrag.getSpninnerDetails();
                transactionPresenter.confirmPrintDetail(detail);
            }

        }
    }

    private void startFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), UPLOAD_CODE);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == UPLOAD_CODE && resultCode == RESULT_OK) {
            Uri selectedUri = data.getData();
            this.grantUriPermission(this.getPackageName(), selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(selectedUri, takeFlags);

            invalidateOptionsMenu();
            transactionPresenter.addFileDetailList(selectedUri, this);
        } else {
            invalidateOptionsMenu();
        }
    }

    @Override
    public void removeFile(int position) {
        transactionPresenter.removeSelectedFile(position);
    }

    @Override
    public void shopCardClicked(final PrintTransaction printTransaction) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.dismiss();
                        transactionPresenter.confirmTransaction(printTransaction);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    @Override
    public void initShopFragment() {
        transactionPresenter.getShopList();
    }

    @Override
    public void showToastError(String s) {
        Toast.makeText(TransactionActivity.this, s,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void shopRetrivalError() {
        ChooseShopFragment chooseShopFragment = (ChooseShopFragment) getSupportFragmentManager().
                findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + 2);
        if (chooseShopFragment != null) {
            chooseShopFragment.showConnectionError();
        }
    }
}