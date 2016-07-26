package tk.parmclee.foodorder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mDrawerList;
    TextView mFoodCounter;
    int count;
    final int[] drawerItems = {R.string.profile, R.string.basket, R.string.settings},
            drawerPictures = {R.drawable.ic_account_circle_black_36dp,
                    R.drawable.ic_shopping_cart_black_36dp,
                    R.drawable.ic_settings_applications_black_36dp};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Fragment preferenceFragment = new SettingsFragment();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer,
                R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        setDrawerListAdapter(mDrawerList);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = getString(drawerItems[position]);
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 2:
                        getFragmentManager().beginTransaction().addToBackStack(null)
                                .replace(R.id.container, preferenceFragment).commit();
                        break;
                    default:
                }
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }

        Fragment fragment = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment);
        ft.commit();
    }

    private void setDrawerListAdapter(ListView listView) {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        for (int i = 0; i < drawerItems.length; i++) {
            map = new HashMap<>();
            map.put("text", getString(drawerItems[i]));
            map.put("picture", drawerPictures[i]);
            data.add(map);
        }
        String[] from = {"picture", "text"};
        int[] to = {R.id.picture, R.id.text};
        listView.setAdapter(new SimpleAdapter(this, data, R.layout.drawer_list_item, from, to));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem basketMenuItem = menu.add(R.string.basket);
        View basketView = View.inflate(getApplicationContext(), R.layout.basket_with_counter, null);

        MenuItemCompat.setActionView(basketMenuItem, basketView);
        MenuItemCompat.setShowAsAction(basketMenuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
        basketView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Basket", Toast.LENGTH_SHORT).show();
            }
        });
        mFoodCounter = (TextView) basketView.findViewById(R.id.basket_counter);
        if (count == 0) mFoodCounter.setVisibility(View.INVISIBLE);
        else mFoodCounter.setVisibility(View.VISIBLE);
        return true;
    }

    public void onClick(View view) {
        count = (int) Math.round(10 * Math.random());
        mFoodCounter.setText(String.valueOf(count));
        if (count == 0) mFoodCounter.setVisibility(View.INVISIBLE);
        else mFoodCounter.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    static void updateAssortment() { //this will be old school method without Retrofit

    }

    public static class MainFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }

        public MainFragment() {
            // Empty constructor required for fragment subclasses
        }

    }

    public static class SettingsFragment extends PreferenceFragment {
        Context mContext;
        final String LAST_UPDATE = "last update";

        public SettingsFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = context;
        }

        @Override
        @TargetApi(22)
        @SuppressWarnings("deprecation")
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mContext = activity.getApplicationContext();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            Preference update = findPreference(getString(R.string.update_assortment));
            update.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            boolean netAvailable = checkNetwork();
                            if (netAvailable)  MainActivity.updateAssortment();
                            else {
                                Toast.makeText(mContext, R.string.no_internet, Toast.LENGTH_SHORT)
                                        .show();
                            }
                            return true;
                        }
                    }
            );
            String lastUpdate = getPreferenceManager().getSharedPreferences().getString(LAST_UPDATE,
                    getString(R.string.unavailable));
            update.setSummary(getString(R.string.update_summary) + " " + lastUpdate);
        }

        @SuppressWarnings("deprecation")
        boolean checkNetwork(){
            ConnectivityManager connectivity = (ConnectivityManager) mContext
                    .getSystemService(CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (Network net: connectivity.getAllNetworks()){
                    NetworkInfo info = connectivity.getNetworkInfo(net);
                    if (((info.getSubtype() == ConnectivityManager.TYPE_WIFI) ||
                            (info.getSubtype() == ConnectivityManager.TYPE_WIFI)) &&
                            (info.isConnected())) return true;
                }
                return false;
            } else {
                return connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected() ||
                        connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            }
        }
    }
}
