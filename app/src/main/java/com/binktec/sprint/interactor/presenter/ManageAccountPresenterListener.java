package com.binktec.sprint.interactor.presenter;

public interface ManageAccountPresenterListener {

    void initializePrintJob(String displayName, String s);

    void openAuthActivity();

    void showToast(String s);

    void emailSentSuccessful();

    void emailSentUnsuccessful();
}
