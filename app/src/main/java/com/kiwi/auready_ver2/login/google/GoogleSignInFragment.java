package com.kiwi.auready_ver2.login.google;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kiwi.auready_ver2.R;

public class GoogleSignInFragment extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG_GOOGLE_SIGNIN_FRAG = "tag_GoogleSignInFragment";

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
            if(result.isSuccess()) {
                String idToken = result.getSignInAccount().getIdToken();
                Log.d("test idToken", "IdToken: " + idToken);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}