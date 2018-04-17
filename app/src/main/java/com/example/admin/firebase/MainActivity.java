package com.example.admin.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.firebase.utils.AuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.nio.charset.MalformedInputException;

public class MainActivity extends AppCompatActivity implements AuthManager.ILoginInteraction, View.OnClickListener {

    private static final int RC_SIGN_IN = 9000;
    private static final String TAG = "MainActivity";
    private EditText etEmail;
    private EditText etPassword;
    private String email;
    private String password;
    private AuthManager authManager;
    private GoogleSignInClient mGoogleSignInClient;

    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();

        authManager = AuthManager.getDefault(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = authManager.getUser();
        //account = GoogleSignIn.getLastSignedInAccount(this);

        //Toast.makeText(this, account.getEmail(), Toast.LENGTH_SHORT).show();

        if (user != null ) {
            // already logged in, go to 2nd activity
            startSecondActivity();
        }
        else {
            // not logged in, show the google login option
            SignInButton signInButton = findViewById(R.id.btnGoogleSignIn);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
        }
    }

    private void startSecondActivity() {

        Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
        //intent.putExtra("googleEmail", account.getEmail());
        startActivity(intent);
    }

    public void onSignIn(View view) {

        getCredentials();

        authManager.signIn(email, password);
    }

    public void onRegister(View view) {

        getCredentials();

        authManager.register(email, password);
    }

    private void bindViews() {

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
    }

    private void getCredentials() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {

        Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();
        startSecondActivity();
    }

    @Override
    public void onLoginError(String error) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGoogleSignIn:
                googleSignIn();
                break;
        }
    }

    private void googleSignIn() {

        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            //handleSignInResult(task);
            try {
                firebaseAuthWithGoogle( task.getResult(ApiException.class));
            } catch (ApiException e) {
                e.printStackTrace();//can you click?
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // sign in success, show the signed in UI
            Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
            intent.putExtra("googleEmail", account.getEmail().toString());
            startActivity(intent);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult: failed code = " + e.getStatusCode());

            // handle failure
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startSecondActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
