package com.niall.eazyeatsfyp.zincActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.zincEntities.RetailerCreds;

public class AmazonCredsActivity extends AppCompatActivity {

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    private DatabaseReference userRetailCreds;

    public static final String AMAZONCREDSEMAIL = "amazon_creds_email";
    public static final String AMAZONCREDSPASSWORD = "amazon_creds_password";


    private EditText emailEdit;
    private EditText pWordEdit;
    private Button loginBtn;

    private Intent i;

    private RetailerCreds retailerCreds = new RetailerCreds();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amazon_creds);

        emailEdit = findViewById(R.id.amazon_login_email_text);
        pWordEdit = findViewById(R.id.amazon_login_pword_text);
        loginBtn = findViewById(R.id.amazon_login_btn);






        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginClick();
            }
        });

    }

    public void loginClick(){

        retailerCreds.setEmail(emailEdit.getText().toString().trim());
        retailerCreds.setPassword(pWordEdit.getText().toString().trim());

        i = new Intent(this, OrderDetailsActivity.class);

        i.putExtra(AMAZONCREDSEMAIL, retailerCreds.getEmail());
        i.putExtra(AMAZONCREDSPASSWORD, retailerCreds.getPassword());

        userRetailCreds = FirebaseDatabase.getInstance().getReference("User").child(userId).child("userAmazonCreds");

        userRetailCreds.setValue(retailerCreds).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(AmazonCredsActivity.this, "Details saved!", Toast.LENGTH_SHORT).show();

                startActivity(i);
            }
        });

    }
}