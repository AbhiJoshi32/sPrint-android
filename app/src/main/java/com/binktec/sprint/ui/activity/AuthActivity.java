package com.binktec.sprint.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.binktec.sprint.R;
import com.binktec.sprint.interactor.fragment.AuthFragmentListener;
import com.binktec.sprint.interactor.presenter.AuthPresenterListener;
import com.binktec.sprint.presenter.AuthPresenter;
import com.binktec.sprint.ui.fragment.EmailVerFragment;
import com.binktec.sprint.ui.fragment.ForgotPasswordFragment;
import com.binktec.sprint.ui.fragment.LoginFragment;
import com.binktec.sprint.ui.fragment.RegisterFragment;
import com.binktec.sprint.utility.Constants;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity implements
        AuthFragmentListener, AuthPresenterListener , GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG_LOGIN = "Login";
    private static final String TAG_REG = "Registration";
    private static final String TAG_EMAIL_VER = "Email Verification";
    private static final String TAG_FORGOT_PASSWORD = "Forgot Password";
    private String TAG_CURR = TAG_LOGIN;

    private String toastErr;
    private static final int RC_SIGN_IN = 9001;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private EmailVerFragment emailVerFragment;
    private ForgotPasswordFragment forgotPasswordFragment;

    private FragmentManager fragmentManager;
    private AuthPresenter authPresenter;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG_CURR,"create auth activity");
        setContentView(R.layout.activity_auth);
        authPresenter = new AuthPresenter(this);
        // [START initialize_auth]
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onBackPressed() {
        if (TAG_CURR.equals(TAG_EMAIL_VER)) {
            try {
                signOut();
            }catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!TAG_CURR.equals(TAG_LOGIN)){
            TAG_CURR = TAG_LOGIN;
            loadAuthFragment();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragmentManager = getSupportFragmentManager();
        checkLoginStatus();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void loadAuthFragment() {
        Fragment loadFragment = getAuthFragment();
        Log.d(TAG_CURR,"The fragment that is being loaded is");
        Log.d(TAG_CURR,loadFragment.toString());
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.authenticateFrame, loadFragment, TAG_CURR);
        if (!TAG_CURR.equals(TAG_LOGIN)) {
            fragmentTransaction.addToBackStack(TAG_CURR);
        } else {
            int count = fragmentManager.getBackStackEntryCount();
            if (count>0) {
              fragmentManager.popBackStack();
            }
        }
        fragmentTransaction.commit();
    }

    private Fragment getAuthFragment() {
        Fragment fragment = new Fragment();
        switch (TAG_CURR) {
            case TAG_LOGIN:
                loginFragment = LoginFragment.newInstance();
                fragment = loginFragment;
                break;
            case TAG_REG:
                registerFragment = RegisterFragment.newInstance();
                fragment = registerFragment;
                break;
            case TAG_EMAIL_VER:
                emailVerFragment = EmailVerFragment.newInstance();
                fragment = emailVerFragment;
                break;
            case TAG_FORGOT_PASSWORD:
                forgotPasswordFragment = ForgotPasswordFragment.newInstance();
                fragment = forgotPasswordFragment;
                break;
        }
        return fragment;
    }

    private Fragment getAuthCurrentFrag () {
        return getSupportFragmentManager().
                findFragmentById(R.id.authenticateFrame);
    }

    @Override
    public void registerBtnClicked(String email, String password, String retypePassword) {
        authPresenter.validateRegister(email,password,retypePassword);
    }

    @Override
    public void signInTextClicked() {
        TAG_CURR = TAG_LOGIN;
        loadAuthFragment();
    }

    @Override
    public void googleSignInClicked() {
        Log.d(TAG_CURR,"Google Sign In clicked");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            try {
                Log.d(TAG_CURR, String.valueOf(result.getStatus().getStatusCode()));
            } catch (Exception e){
                e.printStackTrace();
            }
            if (result.isSuccess()) {
                Log.d(TAG_CURR,"Google Sign In was sucessful");
                showProgressBar();
                GoogleSignInAccount account = result.getSignInAccount();
                authPresenter.verifyGoogleSignInEmail(account);
            } else {
                Log.d(TAG_CURR,"Google sign In returned");
            }
        }
    }

    @Override
    public void loginBtnClicked(String email, String password) {
        authPresenter.validateLogin(email,password);
    }

    @Override
    public void registerTextClicked() {
        TAG_CURR = TAG_REG;
        loadAuthFragment();
    }

    @Override
    public void resendVerBtnClicked() {
        sendEmailVerification();
    }

    @Override
    public void logOutBtnClicked() {
        signOut();
    }

    @Override
    public void forgotPassBtnClicked(String email) {
        resetPassword(email);
    }

    @Override
    public void forgotPassTxtClicked() {
        Log.d(TAG_CURR,"Forgot Pass clicked");
        TAG_CURR = TAG_FORGOT_PASSWORD;
        loadAuthFragment();
    }

    @Override
    public void invalidRegisterCred(String errorElement, String error) {
        registerFragment = (RegisterFragment) getAuthCurrentFrag();
        switch (errorElement) {
            case "email":
                registerFragment.registerShowEmailError(error);
                break;
            case "password":
                registerFragment.registerShowPasswordError(error);
                break;
            case "retypePassword":
                registerFragment.registerShowRetypePasswordError(error);
                break;
        }

    }

    @Override
    public void invalidLoginCred(String errorElement, String error) {
        loginFragment = (LoginFragment) getAuthCurrentFrag();
        switch (errorElement) {
            case "email":
                loginFragment.showEmailError(error);
                break;
            case  "password":
                loginFragment.showPasswordError(error);
                break;
        }
    }

    @Override
    public void validRegisterCred(String email, String password) {
        showProgressBar();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG_CURR, "createUserWithEmail:success");
                            authPresenter.syncUser();
                            sendEmailVerification();
                        } else {
                            Log.w(TAG_CURR, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthException e){
                                String err = e.getErrorCode();
                                Log.d(TAG_CURR, err);
                                toastErr = e.getMessage();

                            }catch (FirebaseNetworkException e){
                                toastErr = e.getMessage();
                            }catch(Exception e) {
                                Log.e(TAG_CURR, e.getMessage());
                            }
                            showToastError(toastErr);
                            hideProgressBar();
                        }
                    }
                });
    }

    @Override
    public void validLoginCred(String email, String password) {
        showProgressBar();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG_CURR, "createUserWithEmail:success");
                    authPresenter.syncUser();
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {
                        if (user.isEmailVerified()) {
                            startMainActivity();
                        }
                        else {
                            TAG_CURR = TAG_EMAIL_VER;
                            hideProgressBar();
                            loadAuthFragment();
                        }
                    }
                } else {
                    hideProgressBar();
                    Log.w(TAG_CURR, "createUserWithEmail:failure", task.getException());

                    try {
                        throw task.getException();
                    } catch (FirebaseAuthException e){
                        String err = e.getErrorCode();
                        Log.d(TAG_CURR, err);
                        toastErr = e.getMessage();

                    }catch (FirebaseNetworkException e){
                        toastErr = e.getMessage();
                    }catch(Exception e) {
                        Log.e(TAG_CURR, e.getMessage());
                    }
                    showToastError(toastErr);
                }
                hideProgressBar();
            }
        });
    }

    @Override
    public void validGoogleAcc(GoogleSignInAccount account) {
        firebaseAuthWithGoogle(account);
    }

    @Override
    public void invalidGoogleAcc() {
        hideProgressBar();
        showToastError(Constants.vitInvalid);
        signOut();
    }

    public void signOut() {
        Log.d(TAG_CURR,"Signing Out");
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
        }
        // Google sign out
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        }
        TAG_CURR = TAG_LOGIN;
        loadAuthFragment();
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressBar();
        Log.d(TAG_CURR, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mGoogleApiClient.disconnect();
                            Log.d(TAG_CURR, "signInWithCredential:success");
                            authPresenter.syncUser();
                            startMainActivity();
                        } else {
                            Log.w(TAG_CURR, "signInWithCredential:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthException e){
                                String err = e.getErrorCode();
                                Log.d(TAG_CURR, err);
                                toastErr = e.getMessage();

                            }catch (FirebaseNetworkException e){
                                toastErr = e.getMessage();
                            }catch(Exception e) {
                                Log.e(TAG_CURR, e.getMessage());
                                toastErr = "Unknown Error";
                            }
                            showToastError(toastErr);
                            hideProgressBar();
                        }
                    }
                });
    }


    private void startMainActivity() {
        Log.d(TAG_CURR,"opening Main Activity");
        Intent intent = new Intent(AuthActivity.this, PrintJobActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showToastError(String toastErr) {
        Toast.makeText(AuthActivity.this,toastErr,
                Toast.LENGTH_SHORT).show();
    }

    private void hideProgressBar() {
        if (TAG_CURR.equals(TAG_LOGIN)) {
            loginFragment = (LoginFragment) getAuthCurrentFrag();
            loginFragment.hideLoginFragProgressBar();
        }
        else if (TAG_CURR.equals(TAG_REG)) {
            registerFragment = (RegisterFragment) getAuthCurrentFrag();
            registerFragment.hideRegFragProgressBar();
        }
    }

    private void showProgressBar() {
        if (TAG_CURR.equals(TAG_LOGIN)) {
            loginFragment = (LoginFragment) getAuthCurrentFrag();
            loginFragment.showLoginFragProgressBar();
        }
        else if (TAG_CURR.equals(TAG_REG)) {
            registerFragment = (RegisterFragment) getAuthCurrentFrag();
            registerFragment.showRegFragProgressBar();
        }
    }

    private void sendEmailVerification() {
        Log.d(TAG_CURR,"Sending ver cost");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (TAG_CURR.equals(TAG_EMAIL_VER)) {
                                emailVerFragment = (EmailVerFragment) getAuthCurrentFrag();
                                if (task.isSuccessful()) {
                                    Log.d(TAG_CURR, "Sending email successful");
                                    emailVerFragment.emailVerSendSuccessful();
                                } else {
                                    Log.e(TAG_CURR, "sendEmailVerification", task.getException());
                                    emailVerFragment.emailVerSendUnsuccessful();
                                }
                            }
                            else {
                                TAG_CURR = TAG_EMAIL_VER;
                                loadAuthFragment();
                            }
                            hideProgressBar();
                        }
                    });
        }
    }

    private void checkLoginStatus() {
        Log.d(TAG_CURR,"Check Login status called");
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            if (user.isEmailVerified()){
                startMainActivity();
            }
            else {
                TAG_CURR = TAG_LOGIN;
            }
        } else {
            TAG_CURR = TAG_LOGIN;
        }
        loadAuthFragment();
    }

    private void resetPassword(String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (TAG_CURR.equals(TAG_FORGOT_PASSWORD)) {
                            if (task.isSuccessful()) {
                                Log.d(TAG_CURR, "Email sent.");
                                forgotPasswordFragment = (ForgotPasswordFragment) getAuthCurrentFrag();
                                forgotPasswordFragment.showConfirmText();
                            } else {
                                showToastError("Some error occurred");
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG_CURR, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

}