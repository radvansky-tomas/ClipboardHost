package com.radvansky.clipboard;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private TextView clipText;
    private MyClipboardManager manager;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        clipText = (TextView)view.findViewById(R.id.clipText);
        manager = new MyClipboardManager();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        clipText.setText(manager.readFromClipboard(getActivity()));
    }
}
