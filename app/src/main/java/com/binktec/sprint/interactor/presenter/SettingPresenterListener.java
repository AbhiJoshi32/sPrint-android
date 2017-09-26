package com.binktec.sprint.interactor.presenter;

import android.net.Uri;

public interface SettingPresenterListener {
    void openAuthActivity();

    void initializePrintJob(String displayName, Uri photoUrl);
}
