package tk.parmclee.foodorder;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ListView mDrawerList;
    TextView mFoodCounter;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer,
                R.string.close_drawer);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        setDrawerListAdapter(mDrawerList);

        ActionBar bar = getSupportActionBar();
        if (bar != null){
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setHomeButtonEnabled(true);
        }
    }

    private void setDrawerListAdapter(ListView listView){
        int[] drawerItems = {R.string.profile, R.string.basket, R.string.settings};
        int[] drawerPictures = {R.drawable.ic_account_circle_black_36dp,
                R.drawable.ic_shopping_cart_black_36dp,
                R.drawable.ic_settings_applications_black_36dp};
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        for (int i = 0; i < drawerItems.length; i++){
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
        //MenuItem logoMenuItem = menu.add(R.string.logo);
       // MenuItemCompat.setActionView(logoMenuItem, R.layout.logo);
        //MenuItemCompat.setShowAsAction(logoMenuItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        MenuItem basketMenuItem = menu.add(R.string.basket);
        View basketView = View.inflate(getApplicationContext(), R.layout.basket_with_counter, null);

        MenuItemCompat.setActionView(basketMenuItem, basketView);
        MenuItemCompat.setShowAsAction(basketMenuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);
        mFoodCounter = (TextView) basketView.findViewById(R.id.basket_counter);
        basketMenuItem.setIcon(R.drawable.ic_shopping_cart_white_32dp);
        if (count == 0) mFoodCounter.setVisibility(View.INVISIBLE);
        else mFoodCounter.setVisibility(View.VISIBLE);
        return true;
    }

    public void onClick(View view){
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
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;
        if (item.getTitle().equals(getString(R.string.basket))) {
            //todo intent to basket
        }
        return super.onOptionsItemSelected(item);
    }


}
