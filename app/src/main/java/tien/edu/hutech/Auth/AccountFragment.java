package tien.edu.hutech.Auth;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import tien.edu.hutech.models.User;
import tien.edu.hutech.restaurant.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private EditText txt_email;
    private EditText txt_name;
    private EditText txt_new_password;
    private Button btn_save;

    private User user;
    private FirebaseAuth mAuth;
    private String mUid;
    private FirebaseUser userFirebase;
    private DatabaseReference mData;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        txt_email = (EditText) view.findViewById(R.id.txt_email);
        txt_name = (EditText) view.findViewById(R.id.txt_name);
        txt_new_password = (EditText) view.findViewById(R.id.txt_new_password);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();
        userFirebase = FirebaseAuth.getInstance().getCurrentUser();

        getUser();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.getUsername().equals(txt_name.getText().toString().trim())){
                    mData.runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            User s = mutableData.getValue(User.class);

                            if(s == null) {
                                return Transaction.success(mutableData);
                            }

                            s.setUsername(txt_name.getText().toString());

                            mutableData.setValue(s);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                            Toast.makeText(getActivity(), "Name is updated", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if(txt_new_password.getText().toString().trim().equals("")){

                } else {
                    if(txt_new_password.getText().toString().trim().length() < 6){
                        txt_new_password.setError("Password too short, enter minimum 6 characters");
                        return;
                    }
                    userFirebase.updatePassword(txt_new_password.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Password is updated", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Failed to update password!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }

    public void getUser() {
        mData = FirebaseDatabase.getInstance().getReference("users").child(mUid);
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mUid = dataSnapshot.getKey();
                if (mUid.equals(mUid)) {
                    user = dataSnapshot.getValue(User.class);
                    txt_email.setText(user.getEmail());
                    txt_name.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
