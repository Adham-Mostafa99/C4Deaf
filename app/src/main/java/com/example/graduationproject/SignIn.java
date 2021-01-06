package com.example.graduationproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignIn extends AppCompatActivity{

    @BindView(R.id.sign_up_profile_image)
    CircleImageView profileImage;
    @BindView(R.id.bottom_background)
    LinearLayout bottomBackground;
    @BindView(R.id.sign_in_button)
    Button signIn;
    @BindView(R.id.sign_in_email)
    EditText signInEmail;
    @BindView(R.id.sign_in_password)
    EditText signInPassword;
    @BindView(R.id.sign_in_forgot_pass)
    TextView signInForgotPass;
    @BindView(R.id.sign_in_by_google_button)
    SignInButton signInByGoogleButton;
    @BindView(R.id.sign_in_by_fb_button)
    Button signInByFbButton;
    @BindView(R.id.sign_in_create_account)
    TextView signInCreateAccount;

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;
    //declare_auth
    private FirebaseAuth mAuth;

    //check to see if the user is currently signed in
    //@Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user != null){
//            updateUI(user);
//
//
//        }
//
//
//    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        //configure Google Sign In
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        //build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient=GoogleSignIn.getClient(this,gso);



        //sign in by email and password
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString().trim();
                String pass = signInPassword.getText().toString().trim();

                //check validate of email and password
                if (email.isEmpty()) {
                    signInEmail.setError("Enter your Email");
                    signInEmail.requestFocus();
                } else if (pass.isEmpty()) {
                    signInPassword.setError("Enter your Password");
                    signInPassword.requestFocus();
                } else {
                    startActivity(
                            new Intent(getApplicationContext(), SignUp.class));
                }

            }
        });


        //sign in by google account
        signInByGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByGoogle();

            }
        });

        //sign in by facebook account
        signInByFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByFacebook();
            }
        });

        //not have an account and want to create one
        signInCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignIn.this,SignUp.class);
                startActivity(intent);

            }
        });
    }


    // [ Start..Sign in Google]
    //signIn method by google
    private void signInByGoogle(){
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount acc = task.getResult(ApiException.class);
                Toast.makeText(getApplicationContext(),"Sign In Successfully",Toast.LENGTH_SHORT).show();
                FirebaseGoogleAuth(acc.getIdToken());

            }catch(ApiException e){
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(),"Sign In Failed",Toast.LENGTH_SHORT).show();
                FirebaseGoogleAuth(null);

            }
        }
    }

    private void FirebaseGoogleAuth( String idToken){
        AuthCredential authCredential= GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(getApplicationContext(), user.getEmail()+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                    updateUI(user);

                }else{
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(SignIn.this,ChatMenu.class);
        startActivity(intent);
    }
    // [ End Sign In Google]

    //[ start Sign In with facebook]
    private void signInByFacebook() {
    }
    // [End Sign In with Facebook]


}