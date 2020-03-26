package sk.ttomovcik.quickly.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import sk.ttomovcik.quickly.R;

public class BottomModalSheetNavigation extends BottomSheetDialogFragment {

    public static BottomModalSheetNavigation newInstance() {
        return new BottomModalSheetNavigation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_menu, container, false);
        return view;

    }
}
