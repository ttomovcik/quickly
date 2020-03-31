package sk.ttomovcik.quickly.views;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.Objects;

import sk.ttomovcik.quickly.activities.MainActivity;

import static java.util.Objects.*;
import static sk.ttomovcik.quickly.R.color.colorNote_amour;
import static sk.ttomovcik.quickly.R.color.colorNote_cyanite;
import static sk.ttomovcik.quickly.R.color.colorNote_lotusPink;
import static sk.ttomovcik.quickly.R.color.colorNote_mountainMeadow;
import static sk.ttomovcik.quickly.R.color.colorNote_reallyOrange;
import static sk.ttomovcik.quickly.R.id.filter_btn_archived;
import static sk.ttomovcik.quickly.R.id.filter_btn_favorites;
import static sk.ttomovcik.quickly.R.id.filter_chip_blue;
import static sk.ttomovcik.quickly.R.id.filter_chip_green;
import static sk.ttomovcik.quickly.R.id.filter_chip_orange;
import static sk.ttomovcik.quickly.R.id.filter_chip_pink;
import static sk.ttomovcik.quickly.R.id.filter_chip_red;
import static sk.ttomovcik.quickly.R.layout.modalsheet_filter;

public class BottomModalSheetFilter extends BottomSheetDialogFragment implements View.OnClickListener {

    private Chip filterChipRed, filterChipGreen, filterChipBlue, filterChipOrange, filterChipPink;
    private AppCompatButton filterBtnFavorites, filterBtnArchived;

    public static BottomModalSheetFilter newInstance() {
        return new BottomModalSheetFilter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(modalsheet_filter, container, false);


        // Find views
        filterChipRed = view.findViewById(filter_chip_red);
        filterChipGreen = view.findViewById(filter_chip_green);
        filterChipBlue = view.findViewById(filter_chip_blue);
        filterChipOrange = view.findViewById(filter_chip_orange);
        filterChipPink = view.findViewById(filter_chip_pink);
        filterBtnFavorites = view.findViewById(filter_btn_favorites);
        filterBtnArchived = view.findViewById(filter_btn_archived);

        // Set onClickListeners
        filterChipRed.setOnClickListener(this);
        filterChipGreen.setOnClickListener(this);
        filterChipBlue.setOnClickListener(this);
        filterChipOrange.setOnClickListener(this);
        filterChipPink.setOnClickListener(this);
        filterBtnFavorites.setOnClickListener(this);
        filterBtnArchived.setOnClickListener(this);

        return view;
    }

    @SuppressLint("ResourceType") @Override public void onClick(View v) {
        switch (v.getId()) {
            case filter_chip_red:
                ((MainActivity) requireNonNull(getActivity()))
                        .loadRecyclerView("withColor", getResources().getString(colorNote_amour));
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;
            case filter_chip_green:
                Log.d("filterDialog", "Green");
                ((MainActivity) requireNonNull(getActivity()))
                        .loadRecyclerView("withColor", getResources().getString(colorNote_mountainMeadow));
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;
            case filter_chip_blue:
                ((MainActivity) requireNonNull(getActivity()))
                        .loadRecyclerView("withColor", getResources().getString(colorNote_cyanite));
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;
            case filter_chip_orange:
                ((MainActivity) requireNonNull(getActivity()))
                        .loadRecyclerView("withColor", getResources().getString(colorNote_reallyOrange));
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;
            case filter_chip_pink:
                ((MainActivity) requireNonNull(getActivity()))
                        .loadRecyclerView("withColor", getResources().getString(colorNote_lotusPink));
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;
            case filter_btn_favorites:
                ((MainActivity) requireNonNull(getActivity())).loadRecyclerView("favorites", "");
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;
            case filter_btn_archived:
                ((MainActivity) requireNonNull(getActivity())).loadRecyclerView("archived", "");
                ((MainActivity) requireNonNull(getActivity())).showFilterNotification(true);
                dismiss();
                break;

        }
    }
}
