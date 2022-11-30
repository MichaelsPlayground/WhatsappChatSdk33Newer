package de.androidcrypto.whatsappchat.activity;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import de.androidcrypto.whatsappchat.MainActivity;
import com.example.chatapp.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private Button LoginButton,PhoneLoginButton;
    private EditText UserEmail,UserPassword;
    private TextView Signup,ForgotPassword;
    private ProgressDialog progressDialog;
    private DatabaseReference userRef;

    // variables for Google sign-in
    private SignInClient signInClient;

    private static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog=new ProgressDialog(this);

        LoginButton=findViewById(R.id.login_button);
        PhoneLoginButton=findViewById(R.id.phone_login_button);
        UserEmail=findViewById(R.id.login_email);
        UserPassword=findViewById(R.id.login_password);
        Signup=findViewById(R.id.need_new_account_link);
        ForgotPassword=findViewById(R.id.forget_password_link);

        // Configure Google Sign In
        signInClient = Identity.getSignInClient(getApplicationContext());
        // Display One-Tap Sign In if user isn't logged in
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            oneTapSignIn();
        }

        com.google.android.gms.common.SignInButton signInBtn = findViewById(R.id.btnSignInGoogleSignIn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "SignIn with an Google account");
                signIn();
            }
        });


        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToRegisterActivity();
            }
        });

        PhoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneintent =new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phoneintent);
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=UserEmail.getText().toString();
                String password=UserPassword.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(LoginActivity.this,"Please enter email...",Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this,"Please enter password...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setTitle("Log In");
                    progressDialog.setMessage("please wait...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                String currentUserId=firebaseAuth.getCurrentUser().getUid();

                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(new OnCompleteListener<String>() {
                                            @Override
                                            public void onComplete(@NonNull Task<String> task) {
                                                if (!task.isSuccessful()) {
                                                    Log.i(TAG, "Fetching FCM registration token failed", task.getException());
                                                    return;
                                                }

                                                // Get new FCM registration token
                                                String deviceToken = task.getResult();
                                                userRef.child(currentUserId).child("device_token").setValue(deviceToken)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful())
                                                                {
                                                                    sendUserToMainActivity();
                                                                    Toast.makeText(LoginActivity.this,"Logged in Successfully...",Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });

                                                // Log and toast
                                                String msg = "token: " + deviceToken;
                                                Log.i(TAG, msg);
                                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                /* deprecated
                                String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                userRef.child(currentUserId).child("device_token").setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    sendUserToMainActivity();
                                                    Toast.makeText(LoginActivity.this,"Logged in Successfully...",Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                 */
                            }
                            else
                            {
                                String errormessage=task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error :"+errormessage,Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    /**
     * section for Google sign-in
     */

    private void oneTapSignIn() {
        // Configure One Tap UI
        BeginSignInRequest oneTapRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(true)
                                .build()
                )
                .build();

        // Display the One Tap UI
        signInClient.beginSignIn(oneTapRequest)
                .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        launchSignIn(beginSignInResult.getPendingIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                    }
                });
    }

    private void signIn() {
        GetSignInIntentRequest signInRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        signInClient.getSignInIntent(signInRequest)
                .addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        launchSignIn(pendingIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Google Sign-in failed", e);
                    }
                });
    }

    private void launchSignIn(PendingIntent pendingIntent) {
        try {
            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent)
                    .build();
            signInLauncher.launch(intentSenderRequest);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't start Sign In: " + e.getLocalizedMessage());
        }
    }

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    handleSignInResult(result.getData());
                }
            }
    );

    private void handleSignInResult(Intent data) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            Log.d(TAG, "firebaseAuthWithGoogle:" + credential.getId());
            Log.d(TAG, "signIn token:" + idToken);
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e);
            //updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        //showProgressBar();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            sendUserToMainActivity();
                            Toast.makeText(LoginActivity.this,"Logged in Successfully...",Toast.LENGTH_SHORT).show();
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //View view = findViewById(R.id.signin_google_layout);
                            //Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        //hideProgressBar();
                    }
                });
    }

    /**
     * section for other buttons
     */

    private void sendUserToMainActivity() {
        Intent mainIntent=new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {
        Intent newuserIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(newuserIntent);
    }
}
