package com.sirnommington.squid.activity.intro.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sirnommington.squid.R;
import com.sirnommington.squid.activity.intro.IntroListener;

/**
 * Describes the app.
 */
public class DescriptionFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_description, container, false);
        view.findViewById(R.id.next_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.next_button) {
            ((IntroListener)getActivity()).descriptionComplete();
        }
    }
}
