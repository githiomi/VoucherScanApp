package com.dhosio.voucherscan.utils;

import static androidx.core.content.ContextCompat.startActivity;
import static com.dhosio.voucherscan.utils.Constants.USERS_BASE_URL;
import static com.dhosio.voucherscan.utils.Constants.VOUCHER_BASE_URL;
import static com.dhosio.voucherscan.utils.Constants.VOUCHER_REDEEM_URL;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dhosio.voucherscan.data.User;
import com.dhosio.voucherscan.views.activities.ResponseActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Methods {

    // To extract the text from a TextInput
    public static String extractText(TextInputLayout textInputLayout) {
        return Objects.requireNonNull(textInputLayout.getEditText()).getText().toString().trim();
    }

    // Set error that a field input is required
    public static void setIsRequiredField(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("This is a required field!");
    }

    // Set error that a field input is not valid
    public static void setInputFieldError(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("Please enter a valid value!");
    }

    public static void setInputFieldError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(error);
    }

    public static void setIncorrectPassword(TextInputLayout textInputLayout) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError("Incorrect password. Please try again.");
    }

    public static void createToastAlert(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void makeVoucherRequest(Context context) {
        JsonObjectRequest voucherRequest = new JsonObjectRequest(VOUCHER_BASE_URL,
                response -> {
                    System.out.println("Getting voucher from database");

                    try {
                        String UUID = response.getString("body");

                        System.out.println("UUID Found: " + UUID);

                        // Call method to redeem the voucher
                        redeemVoucher(context, UUID);

                    } catch (JSONException ex) {
                        createToastAlert(context, "There was an error making voucher request the voucher.");
                        System.out.println("Error in making voucher request: " + ex.getMessage());
                    }
                },
                error -> {
                    createToastAlert(context, "An unexpected error occurred making voucher request.");
                    System.out.println("There was an error making voucher request: " + error);
                });

        Volley.newRequestQueue(context).add(voucherRequest);
    }

    private static void redeemVoucher(Context context, String voucherId) {

        String url = VOUCHER_REDEEM_URL + voucherId;

        JsonObjectRequest redeemRequest = new JsonObjectRequest(url,
                response -> {
                    System.out.println("Redeeming voucher with ID: " + voucherId);

                    try {
                        int responseCode = response.getInt("body");

                        System.out.println("Response Code: " + responseCode);
                        startActivity(context, new Intent(context, ResponseActivity.class).putExtra("responseCode", responseCode), null);

                    } catch (JSONException ex) {
                        createToastAlert(context, "There was an error extracting users from the database.");
                        System.out.println("Error in usersRequest: " + ex.getMessage());
                    }
                },
                error -> {
                    createToastAlert(context, "An unexpected error occurred. Could not redeem voucher");
                    System.out.println("There was an error accessing the API to redeem voucher: " + error);
                });

        Volley.newRequestQueue(context).add(redeemRequest);
    }

    // Method to get user object from the database
    public static User retrieveUser(Context context, String username) {

        AtomicReference<User> user = new AtomicReference<>();

        String url = USERS_BASE_URL + "/" + username;

        JsonObjectRequest userRequest = new JsonObjectRequest(url,
                response -> {
                    System.out.format("Getting user (%s) from the database", username);

                    try {
                        JSONObject jsonBody = response.getJSONObject("body");
                        user.set(extractUser(jsonBody));

                    } catch (JSONException ex) {
                        Toast.makeText(context, "There was an error extracting users from the database.", Toast.LENGTH_LONG).show();
                        System.out.println("Error in usersRequest: " + ex.getMessage());
                    }
                },
                error -> {
                    Toast.makeText(context, "Fatal error in usersRequest.", Toast.LENGTH_LONG).show();
                    System.out.println("Fatal error in usersRequest: " + error);
                });

        Volley.newRequestQueue(context).add(userRequest);

        return user.get();
    }

    public static User extractUser(JSONObject jsonObject) throws JSONException {

        String empId = jsonObject.getString("empId");
        String firstName = jsonObject.getString("firstName");
        String lastName = jsonObject.getString("empId");
        String username = jsonObject.getString("username");
        String gender = jsonObject.getString("gender");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String dateOfBirth = jsonObject.getString("dateOfBirth");
        String branch = jsonObject.getString("branch");

        return new User(empId, firstName, lastName, username, gender, email, password, generateDOB(dateOfBirth), branch);
    }

    private static LocalDate generateDOB(String dateOfBirth) {
        String[] dob = dateOfBirth.split("-");
        int year = Integer.parseInt(dob[0]);
        int month = Integer.parseInt(dob[1]);
        int day = Integer.parseInt(dob[2]);

        return LocalDate.of(year, month, day);
    }

}
