package com.binktec.sprint.interactor.presenter;

public interface SettingPresenterListener {
    void openAuthActivity();

    void initializePrintJob();

    void emailSentSuccessful();

    void emailSentUnsuccessful();
}
