package com.binktec.sprint.ui.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.binktec.sprint.R;
import com.binktec.sprint.ui.fragment.SettingFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "Settings";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.settings_frame)
    FrameLayout settingsFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(TAG);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        SettingFragment settingFragment = SettingFragment.mewInstance();
        ft.replace(R.id.settings_frame,settingFragment);
        ft.commitAllowingStateLoss();
    }
    @Override

    public void onBackPressed() {
        Intent intent;
        intent = new Intent(SettingsActivity.this, PrintJobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
