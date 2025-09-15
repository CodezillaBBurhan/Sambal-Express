package sambal.mydd.app.activity.reward_club;

import static sambal.mydd.app.VolleySingleton.MySingleton.getInstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//import com.stripe.android.PaymentConfiguration;
//import com.stripe.android.paymentsheet.PaymentSheet;
//import com.stripe.android.paymentsheet.PaymentSheetResult;

import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONObject;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.databinding.ActivityActiveMembershipBinding;
import sambal.mydd.app.models.notice_board.Example;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.StatusBarcolor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActiveMembershipActivity extends BaseActivity {
    PaymentSheet paymentSheet;
    PaymentSheet.CustomerConfiguration customerConfig;
    ActivityActiveMembershipBinding binding;
    String stripePublishKey="",paymentIntentId="",paymentId="";
    String customer = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_active_membership);
        setToolbarWithBackButton_colorprimary("Active Membership");
        paymentSheet = new PaymentSheet(ActiveMembershipActivity.this, ActiveMembershipActivity.this::onPaymentSheetResult);

        getRewardData();
      //  paymentSheet = new PaymentSheet(CheckoutActivity.this, CheckoutActivity.this::onPaymentSheetResult);
        binding.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!stripePublishKey.equals("") && !paymentIntentId.equals("")){
                    presentPaymentSheet();
                }
            }
        });
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_active_membership;
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(ActiveMembershipActivity.this, "");
    }

    private void presentPaymentSheet() {
        PaymentConfiguration.init(getApplicationContext(), stripePublishKey);

        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder(customer).customer(customerConfig).allowsDelayedPaymentMethods(true).build();
        paymentSheet.presentWithPaymentIntent(paymentIntentId, configuration);
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            ErrorMessage.E("Canceled");
           // check_order_complete = false;


        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            ErrorMessage.E("Got error: " + ((PaymentSheetResult.Failed) paymentSheetResult).getError());
          /* new DialogManager().showDialog(CheckoutActivity.this, "Error " + ((PaymentSheetResult.Failed) paymentSheetResult).getError());
            check_order_complete = false;*/
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "Error " + ((PaymentSheetResult.Failed) paymentSheetResult).getError(), false, null);


        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            ErrorMessage.E("paymentSheetResult<><>" + paymentSheetResult.toString());
            completeMemberShip();
         /*   if (paymentId != null && paymentIntentId != null && !paymentId.isEmpty() && !paymentIntentId.isEmpty()) {

//                CompleteOrderPayNow(agentId, SavedData.get_order_type(), SavedData.get_booking_slot_date(), SavedData.get_booking_slot_time(), SavedData.getContact_name(), "",
//                        SavedData.getUser_mobile(), SavedData.getUser_email(), doorNumber, street, city, postcode, country);

                ErrorMessage.E("SavedData<><><>" + SavedData.get_booking_mobile_number() + "<><><>" + SavedData.get_booking_mail_id() + "<><><>" + SavedData.get_booking_incharge_name() + "<><><>" +
                        SavedData.get_booking_contact_name());


                CompleteOrderPayNow(agentId, SavedData.get_order_type(), SavedData.get_booking_slot_date(), SavedData.get_booking_slot_time(), SavedData.get_booking_contact_name(), SavedData.get_booking_incharge_name(),
                        SavedData.get_booking_mobile_number(), SavedData.get_booking_mail_id(), doorNumber, street, city, postcode, country);

            }*/
        }
    }


    public void getRewardData() {
        if (AppUtil.isNetworkAvailable(ActiveMembershipActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().getSubscribeRewardClubMembership();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("rewardClub>>" + response.code());
                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            JSONObject responseObject =jsonObject.getJSONObject("response");
                            stripePublishKey=responseObject.getString("stripePublishKey");
                            paymentIntentId=responseObject.getString("paymentIntentId");
                            paymentId=responseObject.getString("paymentId");
                            customer="101";
                            String Price=responseObject.getString("currency")+responseObject.getString("membershipPrice");
                            binding.paymentHeader.setText("Pay just "+Price +" to join the Hillingdon club membership for 1 year!");

                            binding.payButton.setText("Pay "+Price);


                        } catch (Exception e) {

                            ErrorMessage.E("exceptioonnnn>>>" + e.toString());
                        }
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }


                }
            });

        } else {
            ErrorMessage.INSTANCE.T(this, getResources().getString(R.string.no_internet_connection));
        }

    }

    public void completeMemberShip() {
        if (AppUtil.isNetworkAvailable(ActiveMembershipActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().subscribePaymentProcess(paymentId,"1");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("rewardClub>>" + response.code());
                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            ErrorMessage.E("rewardClub>>" + jsonObject.toString());
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("key", "YourValueHere");
                            setResult(RESULT_OK, resultIntent);
                            finish();


                        } catch (Exception e) {

                            ErrorMessage.E("exceptioonnnn>>>" + e.toString());
                        }
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }


                }
            });

        } else {
            ErrorMessage.INSTANCE.T(this, getResources().getString(R.string.no_internet_connection));
        }

    }
}