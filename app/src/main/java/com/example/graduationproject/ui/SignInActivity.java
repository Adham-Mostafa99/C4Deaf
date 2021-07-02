package com.example.graduationproject.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;

import com.example.graduationproject.R;
import com.example.graduationproject.models.CompleteInfo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignInActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 10;
    public static final int FACEBOOK = 2;
    public static final int GOOGLE = 3;
    public static final String TYPE = "type";

    public static final String TAG = "SignInActivity";

    @BindView(R.id.sign_in_button)
    Button signIn;
    @BindView(R.id.sign_in_email_edit_text)
    TextInputLayout signInEmail;
    @BindView(R.id.sign_in_password_edit_text)
    TextInputLayout signInPassword;
    @BindView(R.id.sign_in_forgot_pass)
    TextView signInForgotPass;

    @BindView(R.id.sign_in_create_account)
    TextView signInCreateAccount;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    @BindView(R.id.sign_in_by_google_button)
    CircleButton signInByGoogleButton;
    @BindView(R.id.sign_in_by_facebook_button)
    CircleButton signInByFacebookButton;
    @BindView(R.id.sign_in_by_twitter_button)
    CircleButton signInByTwitterButton;

    GoogleSignInClient mGoogleSignInClient;
    // Initialize Facebook Login
    CallbackManager mCallbackManager;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.btn_arrow_back)
    CircleImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        hideSystemUI();
        initializeFirebase();
        configGoogle();

        mCallbackManager = CallbackManager.Factory.create();

        backButton.setOnClickListener(v -> onBackPressed());

        //sign in by email and password
        signIn.setOnClickListener(v -> {
            String email = getEmail();
            String pass = getPassword();

            //check validate of email and password
            if (isInputValid(email, pass)) {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //get instance from current user
                                currentUser = mAuth.getCurrentUser();
                                signInToChat(currentUser);
                            } else {
                                Toast.makeText(getApplicationContext()
                                        , task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //forgetting password
        signInForgotPass.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), RestUserPasswordActivity.class));
        });

        signInByGoogleButton.setOnClickListener(v -> {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignIn.getClient(getApplicationContext(), gso).signOut();
            googleSignIn();
        });

        signInByFacebookButton.setOnClickListener(v -> {
            facebookLogin();
        });

        signInByTwitterButton.setOnClickListener(v -> {
            //TODO will add feature
        });

        //not have an account and want to create one
        signInCreateAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)
                .putExtra(SignUpActivity.COMPLETE_USER_INFO_INTENT_EXTRA, new CompleteInfo())));
    }

    public String getEmail() {
        return signInEmail.getEditText().getText().toString().trim();
    }

    public String getPassword() {
        return signInPassword.getEditText().getText().toString().trim();
    }

    //initialize Firebase objects
    public void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    /**
     * check validate of email and password
     *
     * @param email user email address to sign in
     * @param pass  user password to sign in
     * @return true if data is valid and false otherwise
     */
    public boolean isInputValid(@NonNull String email, String pass) {
        if (email.isEmpty()) {
            signInEmail.setError("Enter your Email");
            signInEmail.requestFocus();
        } else if (pass.isEmpty()) {
            signInPassword.setError("Enter your Password");
            signInPassword.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        signInToChat(currentUser);
//    }
//
    public void signInToChat(FirebaseUser currentUser) {
        if (currentUser != null) {
            currentUser.reload();
            if (currentUser.isEmailVerified()) {
                Toast.makeText(getApplicationContext(), "Sign in success", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), ChatMenuActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            } else {
                Toast.makeText(getApplicationContext(), "please verify your email", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hideSystemUI() {
        Window window = this.getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30
            window.setDecorFitsSystemWindows(false);
        } else {

            this.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LogIn_or_SignUp.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    public void configGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;

                            finish();
                            startActivity(new Intent(getApplicationContext(), SignUpActivity.class)
                                    .putExtra(SignUpActivity.COMPLETE_USER_INFO_INTENT_EXTRA
                                            , new CompleteInfo(user.getUid(), user.getEmail(), user.getDisplayName()
                                                    , user.getPhotoUrl().toString(), user.getPhoneNumber()))
                                    .putExtra(TYPE, GOOGLE));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                            Toast.makeText(getApplicationContext(), "Error occurs", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


    public void facebookLogin() {
        LoginButton loginButton = new LoginButton(this);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.performClick();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(@NonNull AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;

                            finish();
                            startActivity(new Intent(getApplicationContext(), SignUpActivity.class)
                                    .putExtra(SignUpActivity.COMPLETE_USER_INFO_INTENT_EXTRA
                                            , new CompleteInfo(user.getUid(), user.getEmail(), user.getDisplayName()
                                                    , user.getPhotoUrl().toString(), user.getPhoneNumber()))
                                    .putExtra(TYPE, FACEBOOK));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


//    @Override
//    protected void onPause() {
//        signOut();
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        signOut();
//        super.onStop();
//    }

//    @Override
//    protected void onDestroy() {
//        signOut();
//        super.onDestroy();
//    }
//
//    public void signOut() {
//        mAuth.signOut();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        GoogleSignIn.getClient(getApplicationContext(), gso).signOut();
//        LoginManager.getInstance().logOut();//facebook
//    }
}