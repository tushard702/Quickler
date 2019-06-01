package app.example.com.quickler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout nameInput, emailInput, passwordInput, confirmPasswordInput;
    private Button signupBtn;
    private String name, email, password, confirmPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameInput = findViewById(R.id.signup_name);
        emailInput = findViewById(R.id.signup_email);
        passwordInput = findViewById(R.id.signup_password);
        confirmPasswordInput = findViewById(R.id.signup_confirm_password);
        signupBtn = findViewById(R.id.signup_btn);

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {

        name = nameInput.getEditText().getText().toString();
        email = emailInput.getEditText().getText().toString();
        password = passwordInput.getEditText().getText().toString();
        confirmPassword = confirmPasswordInput.getEditText().getText().toString();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)){
            if(confirmPassword.equals(password)){
                if(password.length() >= 8){

                    final ProgressDialog signupProgress = new ProgressDialog(SignUpActivity.this);
                    signupProgress.setTitle("Registering User");
                    signupProgress.setMessage("Please wait while we regiter user details....");
                    signupProgress.setCanceledOnTouchOutside(false);
                    signupProgress.show();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                userDatabase.child(currentUser.getUid()).child("Name").setValue(name);
                                userDatabase.child(currentUser.getUid()).child("Email").setValue(email);
                                userDatabase.child(currentUser.getUid()).child("Image").setValue("default");

                                signupProgress.dismiss();

                                Toast.makeText(SignUpActivity.this, "Registered Sucessfully", Toast.LENGTH_SHORT).show();
                                Intent feedsIntent = new Intent(SignUpActivity.this, FeedsActivity.class);
                                feedsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(feedsIntent);
                                finish();

                            }else {
                                signupProgress.hide();
                                Toast.makeText(SignUpActivity.this, "" + task.getException() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else {
                    Toast.makeText(SignUpActivity.this, "Password too short, length must be greater than 8", Toast.LENGTH_SHORT).show();
                }

            }else {
                Toast.makeText(SignUpActivity.this, "Password and Confirm Password must be equal", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(SignUpActivity.this, "Please fill all the details", Toast.LENGTH_SHORT).show();
        }
    }
}
