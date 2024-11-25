package com.example.CQUPT;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.example.CQUPT.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView everyDay = headerView.findViewById(R.id.header_everyday);
        everyDay.setText("这是每日一言");

        // 配置导航
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, 
                R.id.nav_network, R.id.nav_news, R.id.nav_course_selection, R.id.settingsFragment, R.id.aboutFragment)
                .setOpenableLayout(drawer)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // 根据设置选择启动页面
        if (savedInstanceState == null) { // 只在首次创建时设置
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String startPage = sharedPreferences.getString("start_page", "课程表");
            
            int startDestination = R.id.nav_home; // 默认为课程表
            switch (startPage) {
                case "校园公告":
                    startDestination = R.id.nav_news;
                    break;
                case "校园网登录":
                    startDestination = R.id.nav_network;
                    break;
            }
            
            // 更新导航图的起始目的地
            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.mobile_navigation);
            navGraph.setStartDestination(startDestination);
            navController.setGraph(navGraph);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}