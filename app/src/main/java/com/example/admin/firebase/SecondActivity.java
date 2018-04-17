package com.example.admin.firebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.admin.firebase.utils.AuthManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondActivity extends AppCompatActivity implements AuthManager.ISignOutInteraction {

    private TextView tvEmail;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        tvEmail = findViewById(R.id.tvEmail);
        authManager = AuthManager.getDefault(this);
        FirebaseUser user = authManager.getUser();


        if (user != null)
            tvEmail.setText(authManager.getUser().getEmail());
        else
            tvEmail.setText(getIntent().getStringExtra("googleEmail"));
    }

    public void onSignOut(View view) {


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mGoogleSignInClient.signOut();
//try noww?kcrashed
        authManager.signOut();
    }

    @Override
    public void onSignOut(boolean isSignedOut) {
        finish();
    }
}
