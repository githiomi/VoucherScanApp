package com.dhosio.voucherscan.views.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_LONG;
import static com.dhosio.voucherscan.utils.Constants.LOGGED_IN_USER;
import static com.dhosio.voucherscan.utils.Constants.USERS_BASE_URL;
import static com.dhosio.voucherscan.utils.Methods.createToastAlert;
import static com.dhosio.voucherscan.utils.Methods.extractText;
import static com.dhosio.voucherscan.utils.Methods.extractUser;
import static com.dhosio.voucherscan.utils.Methods.setIncorrectPassword;
import static com.dhosio.voucherscan.utils.Methods.setInputFieldError;
import static com.dhosio.voucherscan.utils.Methods.setIsRequiredField;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dhosio.voucherscan.R;
import com.dhosio.voucherscan.data.User;
import com.dhosio.voucherscan.databinding.FragmentLoginBinding;
import com.dhosio.voucherscan.utils.Animations;
import com.dhosio.voucherscan.utils.Constants;
import com.dhosio.voucherscan.views.activities.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    // Shared Preferences
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    Animations animations;
    LinearLayout toRegisterText, pageHeader;
    RelativeLayout loginButton;
    ProgressBar progressBar;
    AppCompatButton loginCompatButton;
    TextInputLayout usernameInputField, passwordInputField;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init shared preferences
        this.sharedPreferences = requireContext().getSharedPreferences(Constants.AUTH_SHARED_PREFERENCES, MODE_PRIVATE);
        this.sharedPreferencesEditor = this.sharedPreferences.edit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentLoginBinding fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);

        // Bind Views
        bindViews(fragmentLoginBinding);

        // Animations
        this.animations = new Animations(getContext());
        bindAnimations();

        this.toRegisterText.setOnClickListener(v -> Toast.makeText(requireContext(), "Please contact the Administrator to create an account", LENGTH_LONG).show());

        this.loginCompatButton.setOnClickListener(v -> {
            toggleLoadingStart();
            new Handler().postDelayed(this::performLogin, 1000);
        });

        return fragmentLoginBinding.getRoot();
    }

    private void performLogin() {

        // Remove errors
        if (this.usernameInputField.isErrorEnabled())
            this.usernameInputField.setErrorEnabled(false);
        if (this.passwordInputField.isErrorEnabled())
            this.passwordInputField.setErrorEnabled(false);

        // Extract data from text input field
        String username = extractText(this.usernameInputField).toUpperCase();
        String password = extractText(this.passwordInputField);

        System.out.println("Username: " + username);
        System.out.println("Password: " + password);

        if (username.isEmpty()) {
            toggleLoadingStop();
            setIsRequiredField(this.usernameInputField);
            return;
        }
        if (username.length() < 6) {
            toggleLoadingStop();
            setInputFieldError(this.usernameInputField, "Username must be at least 6 characters");
            return;
        }

        if (password.isEmpty()) {
            toggleLoadingStop();
            setIsRequiredField(this.passwordInputField);
            return;
        }
        if (password.length() < 6) {
            toggleLoadingStop();
            setInputFieldError(this.passwordInputField, "Password must be at least 6 characters");
            return;
        }

        validateCredentials(username, password);
    }

    private void validateCredentials(String username, String password) {

        // Get User from Database
        String url = USERS_BASE_URL + "/" + username.toLowerCase();

        JsonObjectRequest userRequest = new JsonObjectRequest(url,
                response -> {
                    try {
                        if (response.isNull("body")) {
                            toggleLoadingStop();
                            setInputFieldError(usernameInputField, "This username does not exist. Check and try again.");
                            return;
                        }

                        JSONObject jsonBody = response.getJSONObject("body");
                        User user = extractUser(jsonBody);
                        authenticate(user, password);

                    } catch (JSONException ex) {
                        createToastAlert(requireContext(), "There was an error extracting users from the database.");
                        System.out.println("Error in usersRequest: " + ex.getMessage());
                    }
                },
                error -> {
                    toggleLoadingStop();
                    createToastAlert(requireContext(), "An unexpected error occurred. Contact System Administrator.");
                    System.out.println("Fatal error in usersRequest: " + error);
                });

        Volley.newRequestQueue(requireContext()).add(userRequest);
    }

    private void authenticate(User user, String password) {

        if (!password.equals(user.getPassword())) {
            toggleLoadingStop();
            setIncorrectPassword(passwordInputField);
            return;
        }

        // Save logged in user in shared preferences
        this.sharedPreferencesEditor.putString(LOGGED_IN_USER, user.getUsername()).apply();

        // Stop loading state
        toggleLoadingStop();

        // Navigate to Home Fragment
        requireActivity().startActivity(new Intent(requireContext(), MainActivity.class));
    }

    private void bindViews(FragmentLoginBinding views) {
        this.pageHeader = views.LLPageHeader;
        this.usernameInputField = views.ILBadgeNumber;
        this.passwordInputField = views.ILLoginPassword;
        this.loginButton = views.RLLoginButton;
        this.progressBar = views.PBLoginProgressBar;
        this.toRegisterText = views.LLToRegister;
        this.loginCompatButton = views.BTNLoginButton;
    }

    private void bindAnimations() {
        this.pageHeader.setAnimation(this.animations.getFromLeftAnimation());
        this.loginButton.setAnimation(this.animations.getFromRightAnimation());
    }

    private void toggleLoadingStart() {
        this.loginButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.primary_loading_button));
        this.loginCompatButton.setVisibility(GONE);
        this.progressBar.setVisibility(VISIBLE);
    }

    private void toggleLoadingStop() {
        this.loginButton.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.primary_button));
        this.loginCompatButton.setVisibility(VISIBLE);
        this.progressBar.setVisibility(GONE);
    }
}
