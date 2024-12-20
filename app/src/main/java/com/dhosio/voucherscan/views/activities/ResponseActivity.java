package com.dhosio.voucherscan.views.activities;

import static com.dhosio.voucherscan.data.Response.DOESNT_EXIST;
import static com.dhosio.voucherscan.data.Response.EXPIRED;
import static com.dhosio.voucherscan.data.Response.INVALID;
import static com.dhosio.voucherscan.data.Response.REDEEMED;
import static com.dhosio.voucherscan.data.Response.UNKNOWN;
import static com.dhosio.voucherscan.data.Response.VALID;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.dhosio.voucherscan.R;
import com.dhosio.voucherscan.data.Response;
import com.dhosio.voucherscan.databinding.ActivityResponseBinding;

public class ResponseActivity extends AppCompatActivity {

    TextView responseText;
    LottieAnimationView responseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        ActivityResponseBinding responseBinding = ActivityResponseBinding.inflate(getLayoutInflater());
        setContentView(responseBinding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind Views
        bindViews(responseBinding);

        inflateAnimation(getIntent().getIntExtra("responseCode", 0));

    }

    private void inflateAnimation(int responseCode){

        Response response;

        switch (responseCode){
            case 1:
                response = VALID;
                break;
            case 2:
                response = INVALID;
                break;
            case 3:
                response = REDEEMED;
                break;
            case 4:
                response = EXPIRED;
                break;
            case 5:
                response = DOESNT_EXIST;
                break;
            default:
                response = UNKNOWN;
                break;
        }

        this.responseText.setText(response.getRedeemMessage());
        this.responseAnimation.setAnimation(response.getRedeemAnimation());
    }

    private void bindViews(ActivityResponseBinding view){
        this.responseText = view.TVApiResponse;
        this.responseAnimation = view.LAResponseAnimation;
    }
}
