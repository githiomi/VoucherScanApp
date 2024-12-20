package com.dhosio.voucherscan.data;

import com.dhosio.voucherscan.R;

public enum Response {

    /*
        1. Valid
        2. Invalid
        3. Redeemed
        4. Expired
        5. Doesn't Exist
     */
    VALID("The scanned voucher is valid and redeemable", R.raw.success),
    INVALID("The scanned voucher is invalid", R.raw.error_frown),
    REDEEMED("The scanned voucher has already been redeemed", R.raw.warning_circle),
    EXPIRED("The scanned voucher has expired", R.raw.warning_triangle),
    DOESNT_EXIST("The scanned voucher does not exist", R.raw.not_found),
    UNKNOWN("The scanned voucher is unknown", R.raw.voucher_scan);

    private String redeemMessage;
    private int redeemAnimation;

    Response(String redeemMessage, int redeemAnimation) {
        this.redeemMessage = redeemMessage;
        this.redeemAnimation = redeemAnimation;
    }

    public String getRedeemMessage() {
        return redeemMessage;
    }

    public int getRedeemAnimation() {
        return redeemAnimation;
    }
}
