package com.example.projectandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        testFireStore();

    }

    private void testFireStore(){
        final String TAG = "testFireStore";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("recommended")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void signInClick(View view){
        final String TAG = "signInClick";
        String email = ((TextView)findViewById(R.id.usernameText)).getText().toString();
        String password = ((TextView)findViewById(R.id.passwordText)).getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Toast.makeText(MainActivity.this, "Sign in.", Toast.LENGTH_SHORT).show();
                            openActivityList();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Invalid Username or Password.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    public void openActivityList(){
        Intent intent = new Intent(this, activity_list.class);
        startActivity(intent);
    }

    public void sigoutClick(View view){
        final String TAG = "sigoutClick";
        Log.w(TAG, "sign out");
        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        Toast.makeText(MainActivity.this, "Sign out.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI(currentUser);
        }
    }

    public void updateUI(FirebaseUser currentUser){
        final String TAG = "updateUI";
        TextView loginName = findViewById(R.id.textView);
        if(currentUser != null){
            Log.d(TAG, currentUser.getEmail());

            loginName.setText(currentUser.getEmail());
        }
        else{
            loginName.setText("Not have User");
        }
    }
}
