package com.dhosio.voucherscan.views.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.dhosio.voucherscan.R.drawable.ic_female;
import static com.dhosio.voucherscan.R.drawable.ic_male;
import static com.dhosio.voucherscan.utils.Constants.AUTH_SHARED_PREFERENCES;
import static com.dhosio.voucherscan.utils.Constants.LOGGED_IN_USER;
import static com.dhosio.voucherscan.utils.Constants.USERS_BASE_URL;
import static com.dhosio.voucherscan.utils.Methods.createToastAlert;
import static com.dhosio.voucherscan.utils.Methods.extractUser;
import static com.dhosio.voucherscan.utils.Methods.makeVoucherRequest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dhosio.voucherscan.R;
import com.dhosio.voucherscan.data.User;
import com.dhosio.voucherscan.databinding.ActivityMainBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    // Views
    CircleImageView profilePicture;
    TextView usernameTextView;
    LottieAnimationView scanLottieAnimation;
    AppCompatButton logoutButton, scanVoucherButton;
    RelativeLayout loadingLayout, responseLayout;

    // Variables
    String loggedInUsername;
    User user;

    // Shared Preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    // Activity Launcher
    ActivityResultLauncher<ScanOptions> scannerActivityResultLauncher;
    ScanOptions scanOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // View Binding
        ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews(activityMainBinding);

        setupQRScanner();

        // Listeners
        this.logoutButton.setOnClickListener(v -> logout());
        this.scanVoucherButton.setOnClickListener(this::launchQRScanner);

        this.sharedPreferences = getSharedPreferences(AUTH_SHARED_PREFERENCES, MODE_PRIVATE);
        this.sharedPreferencesEditor = this.sharedPreferences.edit();

        loggedInUsername = this.sharedPreferences.getString(LOGGED_IN_USER, "");
        if (loggedInUsername.isEmpty()) openAuthenticationActivity();

        Volley.newRequestQueue(this).add(getUserFromDatabase());
    }

    private JsonObjectRequest getUserFromDatabase() {

        String url = USERS_BASE_URL + "/" + loggedInUsername.toLowerCase();

        return new JsonObjectRequest(url,
                response -> {
                    try {
                        showResponseLayout();

                        JSONObject jsonBody = response.getJSONObject("body");
                        user = extractUser(jsonBody);

                        populateUserData(user);

                    } catch (JSONException ex) {
                        Toast.makeText(this, "There was an error extracting users from the database.", Toast.LENGTH_LONG).show();
                        System.out.println("Error in usersRequest: " + ex.getMessage());
                    }

                },
                error -> {
                    createToastAlert(this, "An unexpected error occurred. Contact System Administrator.");
                    logout();
                    System.out.println("There was an error accessing the API: " + error);
                });
    }

    private void showResponseLayout(){
        this.loadingLayout.setVisibility(GONE);
        this.responseLayout.setVisibility(VISIBLE);
    }

    private void populateUserData(User user) {
        this.usernameTextView.setText(user.getUsername());

        // Set profile picture
        int profilePicture = user.getGender().equals("Male") ? ic_male : ic_female;
        this.profilePicture.setImageDrawable(AppCompatResources.getDrawable(this, profilePicture));
    }

    private void initViews(ActivityMainBinding view) {
        this.loadingLayout = view.RLLoading;
        this.responseLayout = view.RLActiveResponse;
        this.profilePicture = view.IVProfilePicture;
        this.usernameTextView = view.TVUsername;
        this.logoutButton = view.BTLLogout;
        this.scanVoucherButton = view.BTNScanVoucher;
        this.scanLottieAnimation = view.LAScan;
    }

    private void setupQRScanner() {
        scanOptions = new ScanOptions()
                .setPrompt("Please center the QR Code for scanning")
                .setBeepEnabled(true)
                .setOrientationLocked(true)
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE);

        this.scannerActivityResultLauncher = registerForActivityResult(new ScanContract(),
                result -> {
//                    if (result.getContents() == null) {
//                        createToastAlert(this, "QR Code Scan was unsuccessful. Please try again.");
//                    }
//
//                    String uniqueVoucherCode = result.getContents().toString();
//                    makeVoucherRequest(this, uniqueVoucherCode);
                    makeVoucherRequest(this);
                });
    }

    private void launchQRScanner(View v) {
        scannerActivityResultLauncher.launch(this.scanOptions);
    }

    private void openAuthenticationActivity() {
        startActivity(new Intent(this, AuthenticationActivity.class));
        finish();
    }

    private void logout() {
        this.sharedPreferencesEditor.remove(LOGGED_IN_USER).apply();
        openAuthenticationActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (loggedInUsername.isEmpty()) {
            openAuthenticationActivity();
        }
    }
}
