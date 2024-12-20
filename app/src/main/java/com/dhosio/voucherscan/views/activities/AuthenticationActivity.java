package com.dhosio.voucherscan.views.activities;

import static com.dhosio.voucherscan.R.id.FL_authenticationFragmentContainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.dhosio.voucherscan.R;
import com.dhosio.voucherscan.databinding.ActivityAuthenticationBinding;
import com.dhosio.voucherscan.utils.Animations;
import com.dhosio.voucherscan.utils.Constants;
import com.dhosio.voucherscan.views.fragments.LoginFragment;

public class AuthenticationActivity extends AppCompatActivity {

    Animations animations;
    FrameLayout fragmentContainer;
    LottieAnimationView lottieAnimation;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityAuthenticationBinding authenticationBinding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(authenticationBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init shared preferences
        this.sharedPreferences = getSharedPreferences(Constants.AUTH_SHARED_PREFERENCES, MODE_PRIVATE);
        this.sharedPreferencesEditor = this.sharedPreferences.edit();

        // Bind Views
        bindViews(authenticationBinding);

        // Init animations
        this.animations = new Animations(this);

        // Set Animations
        setAnimations();

        // Inflate container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(FL_authenticationFragmentContainer, new LoginFragment())
                .commit();
    }

    private void bindViews(ActivityAuthenticationBinding views) {
        this.lottieAnimation = views.LAAuthentication;
        this.fragmentContainer = views.FLAuthenticationFragmentContainer;
    }

    private void setAnimations() {
        this.lottieAnimation.setAnimation(this.animations.getFromTopAnimation());
        this.fragmentContainer.setAnimation(this.animations.getFromBottomAnimation());
    }

    private void replaceFragmentView(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction().
                replace(FL_authenticationFragmentContainer, fragment)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Validate if user is logged in
        String loggedInUserUsername = this.sharedPreferences.getString(Constants.LOGGED_IN_USER, "");

        if (!loggedInUserUsername.isEmpty()){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
