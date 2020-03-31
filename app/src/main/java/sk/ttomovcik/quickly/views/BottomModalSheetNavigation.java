package sk.ttomovcik.quickly.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.activities.Settings;

import static java.util.Objects.requireNonNull;
import static sk.ttomovcik.quickly.R.layout.bottomsheet_menu;
import static sk.ttomovcik.quickly.R.string.app_bug_tracker;
import static sk.ttomovcik.quickly.R.string.app_source_code_link;

public class BottomModalSheetNavigation extends BottomSheetDialogFragment {

    @OnClick(R.id.btn_settings) void openSettings() {
        startActivity(new Intent(requireNonNull(getActivity()).getApplicationContext(), Settings.class));
        new Thread(() -> requireNonNull(getActivity()).finish());
    }

    @OnClick(R.id.btn_help) void openHomepage() {
        openWebsite(0);
    }

    @OnClick(R.id.btn_issueTracker) void openBugtracker() {
        openWebsite(1);
    }

    public static BottomModalSheetNavigation newInstance() {
        return new BottomModalSheetNavigation();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(bottomsheet_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void openWebsite(int pageId) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        switch (pageId) {
            case 0:
                i.setData(Uri.parse(getString(app_source_code_link)));
                dismiss();
                break;
            case 1:
                i.setData(Uri.parse(getString(app_bug_tracker)));
                dismiss();
                break;
        }
        startActivity(i);
    }
}
