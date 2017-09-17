package com.binktec.sprint.utility;

public class Constants {

    public static final String vitInvalid = "Only vit email is allowed";
    public static final String emptyEmailError = "Input Email";
    public static final String emptyPassError = "Input Password";
    public static final String weakPassword = "Password too short";
    public static final String passNotMatch = "Password do not match";

    public static final String vitRegex = "^[a-z0-9](\\.?[a-z0-9]){5,}@vit\\.ac\\.in$";

    public static final String sendVerificationSuccess = "We have sent a verification e-mail. Verify your credentials and then login again.";
    public static final String sendVerificationFailed = "Sending email verification was unsuccessful. Try again later";
    public static final String pagesTextRegex = "^\\d[\\d-,]*\\d$|\\d+";
}