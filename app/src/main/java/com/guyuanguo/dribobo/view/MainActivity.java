package com.guyuanguo.dribobo.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.guyuanguo.dribobo.Dribbble.Dribbble;
import com.guyuanguo.dribobo.LoginActivity;
import com.guyuanguo.dribobo.R;
import com.guyuanguo.dribobo.view.bucket_list.BucketFragment;
import com.guyuanguo.dribobo.view.shot_list.ShotFragment;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer) NavigationView drawer;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.logOut) Button logOut;
    private ActionBarDrawerToggle drawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, ShotFragment.newInstance(ShotFragment.LIST_TYPE_HOME))
                    .commit();
        }
        setUpDrawer();
    }

    private void setUpDrawer() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        View headerView = drawer.getHeaderView(0);
        ((SimpleDraweeView)headerView.findViewById(R.id.user_picture)).setImageURI(Uri.parse(Dribbble.getCurrentUser().avatar_url));
        ((TextView) headerView.findViewById(R.id.name)).setText(Dribbble.getCurrentUser().name);
        ((TextView) headerView.findViewById(R.id.homePage)).setText(Dribbble.getCurrentUser().html_url);

        // Set logout_text font
        AssetManager mgr=getAssets();
        Typeface tf= Typeface.createFromAsset(mgr, "fonts/Anjelika-Custome.ttf");
        logOut.setTypeface(tf);
        logOut.setGravity(Gravity.CENTER);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                Dribbble.logout(MainActivity.this);
                clearCookies(MainActivity.this);
            }
        });

        drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) {
                    drawerLayout.closeDrawers();
                    return true;
                }
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.drawer_item_home:
                        fragment = ShotFragment.newInstance(ShotFragment.LIST_TYPE_HOME);
                        setTitle(R.string.drawer_menu_dribobo);
                        break;
                    case R.id.drawer_item_likes:
                        fragment = ShotFragment.newInstance(ShotFragment.LIST_TYPE_LIKED);
                        setTitle(R.string.drawer_menu_likes);
                        break;
                    case R.id.drawer_item_buckets:
                        fragment = BucketFragment.newInstance(null, false, null);
                        setTitle(R.string.drawer_menu_buckets);
                        break;
                }
                drawerLayout.closeDrawers();
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
