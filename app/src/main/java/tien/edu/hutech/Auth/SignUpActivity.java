package tien.edu.hutech.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tien.edu.hutech.models.User;
import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.MainActivity;
import tien.edu.hutech.restaurant.R;

public class SignUpActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "SignUpActivity";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private EditText txt_Email, txt_Password;
    private Button btn_SignIn, btn_SignUp, btn_Reset_Password;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDatabase   = FirebaseDatabase.getInstance().getReference();
        mAuth       = FirebaseAuth.getInstance();

        //Views
        txt_Email           = (EditText) findViewById(R.id.txt_Email);
        txt_Password        = (EditText) findViewById(R.id.txt_Password);
        progressBar         = (ProgressBar) findViewById(R.id.progressBar);
        btn_SignIn          = (Button) findViewById(R.id.btn_SignIn);
        btn_SignUp          = (Button) findViewById(R.id.btn_SignUp);
        btn_Reset_Password  = (Button) findViewById(R.id.btn_Reset_Password);

        //Events
        btn_SignIn.setOnClickListener(this);
        btn_SignUp.setOnClickListener(this);
        btn_Reset_Password.setOnClickListener(this);
    }

    private void signUp() {
        Log.d(TAG, "signUp");
        if (!validateForm()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        String mEmail = txt_Email.getText().toString();
        String mPassword = txt_Password.getText().toString();

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUser:onComplete:" + task.isSuccessful());
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(txt_Email.getText().toString())) {
            txt_Email.setError("Required");
            result = false;
        } else {
            txt_Email.setError(null);
        }

        if (TextUtils.isEmpty(txt_Password.getText().toString())) {
            txt_Password.setError("Required");
            result = false;
        } else {
            txt_Password.setError(null);
        }

        return result;
    }

    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        finish();
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_SignIn:
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                break;
            case R.id.btn_SignUp:
                signUp();
                break;
            case R.id.btn_Reset_Password:
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
                break;
        }
    }
}
