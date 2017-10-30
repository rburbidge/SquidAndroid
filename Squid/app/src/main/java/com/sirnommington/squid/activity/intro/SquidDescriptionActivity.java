package com.sirnommington.squid.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sirnommington.squid.R;

/**
 * Shows a description of the application.
 */
public class SquidDescriptionActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squid_description);

        findViewById(R.id.next_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.next_button) {
            final Intent signIn = new Intent(this, SignInActivity.class);
            signIn.addFlags(ActivityHelper.ACTIVITY_START_FLAGS);
            this.startActivity(signIn);
        }
    }
}
