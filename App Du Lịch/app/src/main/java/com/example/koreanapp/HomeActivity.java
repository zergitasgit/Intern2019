package com.example.koreanapp;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }

    private void init() {
        // khởi tạo fragment category khi khởi động
        CategoryFragment categoryFragment = new CategoryFragment();
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, categoryFragment);
        fragmentTransaction.commit();

        // khởi tạo các fragment khi click vào mỗi item
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_category: {
                        CategoryFragment categoryFragment = new CategoryFragment();
                        FragmentTransaction fragmentTransactionCategory = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionCategory.replace(R.id.container, categoryFragment);
                        fragmentTransactionCategory.commit();
                        break;
                    }
                    case R.id.nav_place: {
                        PlaceFragment placeFragment = new PlaceFragment();
                        FragmentTransaction fragmentTransactionPlace = getSupportFragmentManager().beginTransaction();
                        fragmentTransactionPlace.replace(R.id.container, placeFragment);
                        fragmentTransactionPlace.commit();
                        break;
                    }
                    case R.id.nav_contact: {
                        ContactFragment contactFragment = new ContactFragment();
                        FragmentTransaction fragmentTransaction1Contact = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1Contact.replace(R.id.container, contactFragment);
                        fragmentTransaction1Contact.commit();
                        break;
                    }
                    case R.id.nav_promotion: {
                        PromotionFragment promotionFragment = new PromotionFragment();
                        FragmentTransaction fragmentTransaction1Promotion = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1Promotion.replace(R.id.container, promotionFragment);
                        fragmentTransaction1Promotion.commit();
                        break;
                    }
                }
                return true;
            }
        });
    }
}
