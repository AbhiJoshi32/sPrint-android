package com.binktec.sprint.interactor.presenter;

public interface ManageAccountPresenterListener {
    public void initializePrintJob(String displayName, String s);

    public void openAuthActivity();

    void showToast(String s);

    void emailSentSuccessful();

    void emailSentUnsuccessful();
}
