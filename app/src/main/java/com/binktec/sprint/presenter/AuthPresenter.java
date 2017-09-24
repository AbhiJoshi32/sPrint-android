package com.binktec.sprint.presenter;

import android.util.Log;

import com.binktec.sprint.interactor.modal.SyncListener;
import com.binktec.sprint.interactor.modal.UserModalListener;
import com.binktec.sprint.interactor.presenter.AuthPresenterListener;
import com.binktec.sprint.modal.api.SyncApi;
import com.binktec.sprint.modal.api.UserApi;
import com.binktec.sprint.modal.pojo.PrintJobDetail;
import com.binktec.sprint.modal.pojo.User;
import com.binktec.sprint.utility.Constants;
import com.binktec.sprint.utility.Misc;
import com.binktec.sprint.utility.SessionManager;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AuthPresenter implements UserModalListener, SyncListener {
    private AuthPresenterListener authPresenterListener;
    private FirebaseAuth firebaseAuth;
    private UserApi userApi;
    private FirebaseUser firebaseUser;

    private static final Pattern p = Pattern.compile(Constants.vitRegex);
    private Matcher m;
    private static final String TAG = "Auth Presenter";

    public AuthPresenter(AuthPresenterListener authPresenterListener) {
        this.authPresenterListener = authPresenterListener;
        firebaseAuth = FirebaseAuth.getInstance();
        userApi = new UserApi();
    }



    public void validateRegister(String email, String password, String retypePassword) {
        m = p.matcher(email);
        if (email.isEmpty()) {
            authPresenterListener.invalidRegisterCred("email",Constants.emptyEmailError);
        } else if (password.isEmpty()) {
            authPresenterListener.invalidRegisterCred("password",Constants.emptyPassError);
        } else if (!m.matches()) {
            authPresenterListener.invalidRegisterCred("email",Constants.vitInvalid);
        } else if (password.length() < 6) {
            authPresenterListener.invalidRegisterCred("password",Constants.weakPassword);
        }else if (!password.equals(retypePassword)) {
            authPresenterListener.invalidRegisterCred("retypePassword",Constants.passNotMatch);
        }else {
            authPresenterListener.validRegisterCred(email,password);
        }
    }

    public void validateLogin(String email, String password) {
        m = p.matcher(email);
        if (email.isEmpty()) {
            authPresenterListener.invalidLoginCred("email",Constants.emptyEmailError);
        } else if (password.isEmpty()) {
            authPresenterListener.invalidLoginCred("password",Constants.emptyPassError);
        } else if (!m.matches()) {
            authPresenterListener.invalidLoginCred("email",Constants.vitInvalid);
        } else if (password.length() < 6) {
            authPresenterListener.invalidLoginCred("password",Constants.weakPassword);
        } else {
            authPresenterListener.validLoginCred(email,password);
        }
    }

    public void verifyGoogleSignInEmail(GoogleSignInAccount account) {
        String email = account.getEmail();
        if (email != null) {
            m = p.matcher(email);
            if (m.matches()) {
                authPresenterListener.validGoogleAcc(account);
            } else {
                authPresenterListener.invalidGoogleAcc();
            }
        }
        else {
            authPresenterListener.invalidGoogleAcc();
        }
    }

    public void syncUser() {
        firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User();
        user.setEmailId(firebaseUser.getEmail());
        user.setDateOfJoin(Misc.getDate());
        user.setRequestToken(FirebaseInstanceId.getInstance().getToken());
        user.setUid(firebaseUser.getUid());
        SessionManager.saveUser(user);
        userApi.syncUserWithBackEnd(user,this);
    }

    @Override
    public void syncDatabaseValues(User user) {
        SessionManager.saveUser(user);
        SyncApi syncApi = new SyncApi(this,firebaseUser.getUid());
        syncApi.syncProgressJobs();
        syncApi.syncHistoryJobs();
    }

    @Override
    public void initSessions() {
        List<PrintJobDetail> emptyJobDetails = new ArrayList<>();
        List<String> emptyId = new ArrayList<>();
        SessionManager.saveApiPrintJob(emptyJobDetails);
        SessionManager.saveHistoryPrintJob(emptyJobDetails);
        SessionManager.saveHistoryIds(emptyId);
        SessionManager.saveTrasactionIds(emptyId);

        authPresenterListener.openInstructionActivity();
    }

    @Override
    public void setProgressSession(List<PrintJobDetail> printJobDetailList, List<String> transactionIds) {
        Log.d(TAG,"Progress sync" + printJobDetailList);
        List<PrintJobDetail> emptyJobDetails = new ArrayList<>();
        List<String> emptyId = new ArrayList<>();
        if (printJobDetailList != null && transactionIds != null) {
            SessionManager.saveApiPrintJob(printJobDetailList);
            SessionManager.saveTrasactionIds(transactionIds);
        } else {
            SessionManager.saveApiPrintJob(emptyJobDetails);
            SessionManager.saveTrasactionIds(emptyId);
        }
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SessionManager.saveProgressSyncDate(currentDateTimeString);
        if (SessionManager.getHistoryPrintJobDetail() != null) {
            authPresenterListener.openMainActity();
        }
    }

    @Override
    public void setHistorySession(List<PrintJobDetail> printJobDetailList, List<String> historyIds) {
        Log.d(TAG,"History sync" + printJobDetailList + "history id is" + historyIds);
        if (printJobDetailList != null && historyIds != null) {
            SessionManager.saveHistoryPrintJob(printJobDetailList);
            SessionManager.saveHistoryIds(historyIds);
        } else {
            List<PrintJobDetail> emptyJobDetails = new ArrayList<>();
            List<String> emptyId = new ArrayList<>();
            SessionManager.saveHistoryPrintJob(emptyJobDetails);
            SessionManager.saveHistoryIds(emptyId);
        }
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        SessionManager.saveHistorySyncDate(currentDateTimeString);
        if (SessionManager.getApiPrintJobDetail() != null) {
            authPresenterListener.openMainActity();
        }
    }

    public void registerUser() {
        firebaseUser = firebaseAuth.getCurrentUser();
        User user = new User();
        user.setEmailId(firebaseUser.getEmail());
        user.setDateOfJoin(Misc.getDate());
        user.setRequestToken(FirebaseInstanceId.getInstance().getToken());
        user.setUid(firebaseUser.getUid());
        SessionManager.saveUser(user);
        userApi.registerToBackend(user);
    }
}