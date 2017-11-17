package com.sirnommington.squid.activity.intro;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.sirnommington.squid.R;
import com.sirnommington.squid.services.google.GoogleSignIn;

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
            googleSignIn.signIn(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // TODO Is there a way to encapsulate this logic inside GoogleSignIn class?
        if (requestCode == GoogleSignIn.SIGN_IN_REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            this.handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (!result.isSuccess()) {
            return;
        }

        ((IntroListener)this.getActivity()).signInComplete();
    }
}
