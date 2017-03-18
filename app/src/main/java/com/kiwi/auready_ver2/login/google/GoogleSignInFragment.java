package com.kiwi.auready_ver2.login.google;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kiwi.auready_ver2.R;

public class GoogleSignInFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = "GoogleSignInFragment";

    private static final int RC_GET_TOKEN = 9002;

    private GoogleApiClient mGoogleApiClient;

    public GoogleSignInFragment() {
        // Required empty public constructor
    }

    public static GoogleSignInFragment newInstance() {
        return new GoogleSignInFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure googleSignIn options
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_google_sign_in, container, false);

        root.findViewById(R.id.bt_google_signin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test IdToken", "clicked google signin button");
                getIdToken();
            }
        });
        return root;
    }

    private void getIdToken() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_GET_TOKEN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            String idToken = result.getSignInAccount().getIdToken();
            // 1. SignIn-social; with idToken, email
            // URI; POST social-signin/:type
            requestSocialSignIn();
            // 2. Get accessToken
            // URI; POST auth/token
            // 3. Update UI
            updateUI(true);
        } else {
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if(signedIn) {
            // Display user info
            // Set signOut button VISIBLE
            // Set others GONE
        }
        else {

        }
    }

    private void requestSocialSignIn() {
        // Request to my app server
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("test idToken", "Connection is failed.");
        Toast.makeText(this.getActivity(), "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}