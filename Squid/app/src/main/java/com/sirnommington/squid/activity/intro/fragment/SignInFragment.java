package com.sirnommington.squid.activity.intro.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.RequestCode;
import com.sirnommington.squid.activity.common.GoogleSignInProvider;
import com.sirnommington.squid.activity.intro.IntroListener;
import com.sirnommington.squid.services.google.GoogleSignIn;

/**
 * Signs the user in with Google sign-in.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {

    private GoogleSignIn googleSignIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.googleSignIn = ((GoogleSignInProvider) this.getActivity()).getGoogleSignIn();

        final View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        view.findViewById(R.id.sign_in_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.sign_in_button) {
            googleSignIn.signIn(this, RequestCode.SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            this.handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (!result.isSuccess()) {
            final int errorResourceId = getErrorMessageForStatusCode(result.getStatus().getStatusCode());
            Toast.makeText(getActivity(), errorResourceId, Toast.LENGTH_LONG).show();
            return;
        }

        ((IntroListener)this.getActivity()).signInComplete();
    }

    /**
     * Gets the error message resource ID for a GoogleSignInStatusCodes status code.
     * @param statusCode The GoogleSignInStatusCodes value.
     */
    private static int getErrorMessageForStatusCode(int statusCode) {
        switch(statusCode) {
            case GoogleSignInStatusCodes.NETWORK_ERROR:
                return R.string.sign_in_fail_no_network;
            default:
                return R.string.sign_in_fail_other;
        }
    }
}
