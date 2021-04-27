package com.niall.eazyeatsfyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.niall.eazyeatsfyp.entities.User;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.niall.eazyeatsfyp.util.Constants.RECIPE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;

public class RegisterActivity extends AppCompatActivity {



    private FirebaseAuth mainAuth;
    private EditText emailEdit;
    private EditText usernameEdit;
    private EditText passwordEdit;


    private View root;

    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);

        mainAuth = FirebaseAuth.getInstance();

        if (mainAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, BNavigationActivity.class));
            return;
        }



        root = findViewById(R.id.activity_register_new_layout);

        emailEdit = findViewById(R.id.emailEditText);
        usernameEdit = findViewById(R.id.uNameEditText);
        passwordEdit = findViewById(R.id.passwordEditText);

    }

    @NotNull
    private String getPasswordInput() {
        return passwordEdit.getText().toString();
    }

    @NotNull
    private String getEmailInput() {
        return emailEdit.getText().toString();
    }



    public void onLoginClick(View view){
        final Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);


    }

    public void onRegisterClick(View view) {

        Log.d(TAG, "onRegisterClick: value of emailEdit: " + emailEdit.getText().toString());;

        if(usernameEdit.getText().toString().equals("") || usernameEdit.getText().toString().isEmpty())
            Snackbar.make(root, "You must provide a username!", Snackbar.LENGTH_LONG).show();
        else if (getEmailInput() == null || getPasswordInput() == null || emailEdit.getText().toString() == null) {
            Snackbar.make(root, "You must enter all fields!", Snackbar.LENGTH_LONG);
        } else {

        final Intent i = new Intent(this, BNavigationActivity.class);

        mainAuth.createUserWithEmailAndPassword(getEmailInput(), getPasswordInput())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Registration successful.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mainAuth.getCurrentUser();
                            String userId = user.getUid();

                            User aUser = new User(usernameEdit.getText().toString(), emailEdit.getText().toString(), null, null, null, null);

                            String name = user.getDisplayName();

                            System.out.println(name);

                            DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                            db.child("User").child(userId).setValue(aUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Write successful", Toast.LENGTH_LONG).show();

                                    startActivity(i);
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(root, "Register failed: " + task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        //...
                    }
                });

    }


    }

}