package com.example.admin.firebase.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class AuthManager {

    private FirebaseAuth mAuth;
    Activity activity;
    ILoginInteraction loginListener;
    ISignOutInteraction signOutListener;
    FirebaseUser user;
    private static AuthManager instance = null;


    private AuthManager() {
        mAuth = FirebaseAuth.getInstance();
        //this.activity = activity;
    }


    public static AuthManager getDefault(Activity activity) {

        if (instance == null) {
            instance = new AuthManager();
        }
        instance.attach(activity);
        return instance;
    }

    private void attach(Object object) {
        if (object instanceof ILoginInteraction) {
            this.loginListener = (ILoginInteraction) object;
        }
        if (object instanceof ISignOutInteraction) {
            this.signOutListener = (ISignOutInteraction) object;
        }
        if (object instanceof Activity) {
            this.activity = (Activity) object;
        }
    }

    public void register(String email, String password) {

       // user = null;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user = mAuth.getCurrentUser();
                            loginListener.onLoginSuccess(user);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            loginListener.onLoginError(task.getException().getMessage());
                        }

                        // ...
                    }
                });
    }

    public void signIn(String email, String password) {
        //user = null;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            user = mAuth.getCurrentUser();
                            loginListener.onLoginSuccess(user);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            loginListener.onLoginError(task.getException().getMessage());
                        }

                        // ...
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        signOutListener.onSignOut(user == null);
    }

    public FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    public interface ILoginInteraction {

        void onLoginSuccess(FirebaseUser user);

        void onLoginError(String error);

    }
    public interface  ISignOutInteraction {

        void onSignOut(boolean isSignedOut);
    }
}
