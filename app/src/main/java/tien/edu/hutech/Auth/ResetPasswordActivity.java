package tien.edu.hutech.Auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import tien.edu.hutech.restaurant.BaseActivity;
import tien.edu.hutech.restaurant.R;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "ResetPasswordActivity";

    private FirebaseAuth mAuth;

    private EditText txt_Email;
    private Button btn_Reset_Password, btn_Back;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        //Views
        txt_Email = (EditText) findViewById(R.id.txt_Email);
        btn_Reset_Password = (Button) findViewById(R.id.btn_Reset_Password);
        btn_Back = (Button) findViewById(R.id.btn_Back);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Events
        btn_Reset_Password.setOnClickListener(this);
        btn_Back.setOnClickListener(this);
    }

    private void resetPassword(){
        String mEmail = txt_Email.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)) {
            txt_Email.setError("Required");
            return;
        } else {
            txt_Email.setError(null);
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(mEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPasswordActivity.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_Back:
                finish();
                break;
            case R.id.btn_Reset_Password:
                resetPassword();
                break;
        }
    }
}
