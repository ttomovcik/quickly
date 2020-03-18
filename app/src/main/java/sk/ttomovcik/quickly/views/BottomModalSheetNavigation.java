package sk.ttomovcik.quickly.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.Objects;

import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;
import sk.ttomovcik.quickly.R;
import sk.ttomovcik.quickly.Login;

public class BottomModalSheetNavigation extends BottomSheetDialogFragment
        implements NavigationView.OnNavigationItemSelectedListener {

    public static BottomModalSheetNavigation newInstance() {
        return new BottomModalSheetNavigation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_menu, container, false);

        NavigationView navigationView = view.findViewById(R.id.menu_bottomsheet_main);
        navigationView.setNavigationItemSelectedListener(this);
        view.findViewById(R.id.rl_bottomsheet_profileInfo).setOnClickListener(v -> {
            startActivity(new Intent(getContext(), Login.class));
            dismiss();
        });
        return view;

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                dismiss();
                return true;
            case R.id.menu_helpAndSupport:
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .addDefaultShareMenuItem()
                        .setToolbarColor(this.getResources().getColor(R.color.colorPrimaryDark))
                        .setShowTitle(true)
                        .setColorScheme(CustomTabsIntent.COLOR_SCHEME_SYSTEM)
                        .build();
                CustomTabsHelper.addKeepAliveExtra(Objects.requireNonNull(getContext()), customTabsIntent.intent);
                CustomTabsHelper.openCustomTab(getContext(), customTabsIntent,
                        Uri.parse(getString(R.string.app_wiki)),
                        new WebViewFallback());
                dismiss();
                return true;
            case R.id.menu_aboutApp:
                new LibsBuilder()
                        .withAboutIconShown(true)
                        .withVersionShown(true)
                        .withAboutAppName(getString(R.string.app_name))
                        .withActivityTheme(R.style.Theme_MaterialComponents_Light_NoActionBar)
                        .withAboutDescription(getString(R.string.app_description))
                        .start(Objects.requireNonNull(getActivity()).getApplicationContext());
                dismiss();
                return true;
        }
        return false;
    }
}
