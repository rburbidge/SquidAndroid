package com.sirnommington.squid.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sirnommington.squid.R;

/**
 * A fragment that will show some content or a loader.
 *
 * Override {@link #onCreateContentView(LayoutInflater, ViewGroup, Bundle)} to create the view that will be shown while not loading.
 * Override {@link #getLoadingTextId()} to override the text that is shown while loading.
 */
public abstract class ProgressFragment extends Fragment {
    private View content;
    private View progress;

    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState) {
        // Setup the layout
        final View layout = inflater.inflate(R.layout.fragment_progress, parentViewGroup, false);
        final TextView progressMessage = layout.findViewById(R.id.progress_label);
        progressMessage.setText(this.getLoadingTextId());

        this.content = layout.findViewById(R.id.content);
        this.progress = layout.findViewById(R.id.progress);

        // Create and insert the content view
        final View contentView = this.onCreateContentView(inflater, parentViewGroup, savedInstanceState);
        ((FrameLayout)this.content).addView(contentView);

        return layout;
    }

    /**
     * Creates the content view that will be shown while not loading.
     */
    public abstract View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * Gets the loading text. The default is "Loading...".
     */
    public int getLoadingTextId() {
        return R.string.loading;
    }

    /**
     * Shows or hides the loading indicator.
     * Requires that loader's ID is R.id.progress, and conent ID is R.id.content.
     * @param isLoading True to show loading, false to hide.
     */
    public void showLoading(boolean isLoading) {
        if(isLoading) {
            content.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        } else {
            content.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }
}
