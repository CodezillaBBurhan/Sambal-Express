package sambal.mydd.app.activity.reward_club;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.activity.New_AgentDetails;
import sambal.mydd.app.constant.IntentConstant;
import sambal.mydd.app.databinding.ActivityRewardDealDetailBinding;
import sambal.mydd.app.models.reward_deal_detail.Example;
import sambal.mydd.app.models.reward_deal_detail.ProductDetail;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.StatusBarcolor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DealDetailActivity extends BaseActivity {
    ActivityRewardDealDetailBinding binding;
    Example example;
    String agentId="",productId="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reward_deal_detail);
        // setToolbarWithWhiteBackButton("");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
             agentId = bundle.getString("agentId");
             productId = bundle.getString("productId");
            dealDetail(agentId, productId);
        }

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.redeem0ffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (example != null && example.getResponse().getProductDetails().get(0).getRedeemButtonStatus() == 0) {
                    subscribePopup();
                } else {
                    subscribedQRCodePopup( example.getResponse().getProductDetails().get(0).getRedeemButtonOffer1(), example.getResponse().getProductDetails().get(0).getRedeemButtonOffer1QRuuid());
                }
            }
        });
        binding.redeemWithPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (example != null && example.getResponse().getProductDetails().get(0).getRedeemButtonStatus() == 0) {
                    subscribePopup();
                } else {
                    subscribedQRCodePopup( example.getResponse().getProductDetails().get(0).getRedeemButtonOffer2(), example.getResponse().getProductDetails().get(0).getRedeemButtonOffer2QRuuid());
                }
            }
        });
        binding.agentNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (example != null) {
                    Intent intent1 = new Intent(DealDetailActivity.this, New_AgentDetails.class);
                    intent1.putExtra(IntentConstant.INTENT_KEY_AGENT_ID, example.getResponse().getProductDetails().get(0).getAgentId().toString());
                    intent1.putExtra("direct", "true");
                    startActivity(intent1);
                }
            }
        });


    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_reward_deal_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(this, "");
    }

    public void dealDetail(String agentID, String productId) {
        ErrorMessage.E("rewardClub<><>" + agentID);
        if (AppUtil.isNetworkAvailable(DealDetailActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().getRewardClubDealDetails(agentID, productId);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("getRewardClubDealDetails>>" + response.code());
                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            ErrorMessage.E("rewardClub<<>>>>" + jsonObject.toString());
                            example = gson.fromJson(jsonObject.toString(), Example.class);
                            if (example != null) {
                                binding.dealNameTv.setText(example.getResponse().getProductDetails().get(0).getDealName());
                                binding.description.setText(example.getResponse().getProductDetails().get(0).getDealDescription());
                                binding.clubpointsValueTv.setText(example.getResponse().getProductDetails().get(0).getClubPointsValue());
                                binding.dealSavingTv.setText(example.getResponse().getProductDetails().get(0).getDealSavings());
                                binding.agentNameTv.setText(example.getResponse().getProductDetails().get(0).getAgentName());
                                binding.agentNameBtn.setText(example.getResponse().getProductDetails().get(0).getAgentName());
                                binding.discountInfo.setText(example.getResponse().getProductDetails().get(0).getProductDiscountPercentage() + " OFF");
                                binding.categoryNameTv.setText("| " + example.getResponse().getProductDetails().get(0).getCategoryName());
                                binding.redeem0ffBtn.setText(example.getResponse().getProductDetails().get(0).getRedeemButtonOffer1());
                                binding.redeemWithPoints.setText(example.getResponse().getProductDetails().get(0).getRedeemButtonOffer2());
                               if(example.getResponse().getProductDetails().get(0).getRedeemButtonOffer2()!=null &&
                                !example.getResponse().getProductDetails().get(0).getRedeemButtonOffer2().equals("")){
                                   binding.redeemWithPoints.setVisibility(View.VISIBLE);
                               }else {
                                   binding.redeemWithPoints.setVisibility(View.GONE);
                               }

                                Glide.with(DealDetailActivity.this).load(example.getResponse().getProductDetails().get(0).getDealImage()).placeholder(R.drawable.placeholder_4_3).into(binding.headerImage);

                                if (example.getResponse().getProductDetails().get(0).getRedeemButtonStatus() == 0) {
                                    binding.lockIcon.setText("Locked");
                                    binding.lockIcon.setTextColor(Color.parseColor("#ffcc0000"));
                                    binding.redeem0ffBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
                                    binding.redeemWithPoints.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
                                    /*int color = ContextCompat.getColor(DealDetailActivity.this, android.R.color.holo_red_dark);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        binding.lockIcon.setCompoundDrawableTintList(ColorStateList.valueOf(color));
                                    }*/
                                    binding.lockIcon.setCompoundDrawablesWithIntrinsicBounds(
                                            ContextCompat.getDrawable(DealDetailActivity.this, R.drawable.lock),
                                            null, null, null
                                    );
                                } else {
                                    binding.lockIcon.setText("Unlocked");
                                    binding.lockIcon.setTextColor(Color.parseColor("#ff669900"));
                                    binding.redeem0ffBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#78011E")));
                                    binding.redeemWithPoints.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#78011E")));
                                    int color = ContextCompat.getColor(DealDetailActivity.this, android.R.color.holo_green_dark);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        binding.lockIcon.setCompoundDrawableTintList(ColorStateList.valueOf(color));
                                    }

                                    binding.lockIcon.setCompoundDrawablesWithIntrinsicBounds(
                                            ContextCompat.getDrawable(DealDetailActivity.this, R.drawable.lock_open),
                                            null, null, null
                                    );
                                }
                            }
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

    public void subscribePopup() {
        Dialog reorder_confirmation_popup = new Dialog(this);
        reorder_confirmation_popup.setContentView(R.layout.suscribe_confirmation_popup);
        reorder_confirmation_popup.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reorder_confirmation_popup.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        reorder_confirmation_popup.getWindow().setAttributes(lp);
        reorder_confirmation_popup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton cancel_btn = reorder_confirmation_popup.findViewById(R.id.cancel_btn);
        Button subscribeNow = reorder_confirmation_popup.findViewById(R.id.subscribeNow);
        TextView title = reorder_confirmation_popup.findViewById(R.id.popup_title);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder_confirmation_popup.dismiss();
            }
        });
        subscribeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder_confirmation_popup.dismiss();
                ErrorMessage.INSTANCE.I_ActivityForResult(DealDetailActivity.this, ActiveMembershipActivity.class, null,1001);

            }
        });

        reorder_confirmation_popup.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            dealDetail(agentId, productId);
        }
    }
    public void subscribedQRCodePopup( String title,String uuid) {
        Dialog reorder_confirmation_popup = new Dialog(this);
        reorder_confirmation_popup.setContentView(R.layout.suscribed_popup);
        reorder_confirmation_popup.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reorder_confirmation_popup.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        reorder_confirmation_popup.getWindow().setAttributes(lp);
        reorder_confirmation_popup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageButton cancel_btn = reorder_confirmation_popup.findViewById(R.id.cancel_btn);
        ImageView qr_img = reorder_confirmation_popup.findViewById(R.id.qr_img);
        TextView popup_title = reorder_confirmation_popup.findViewById(R.id.popup_title);
        popup_title.setText(title);

        Bitmap qrCode = generateQRCode(uuid);
        if (qrCode != null) {
            qr_img.setImageBitmap(qrCode);
        }

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder_confirmation_popup.dismiss();
            }
        });


        reorder_confirmation_popup.show();

    }


    public Bitmap generateQRCode(String data) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 600, 600);
            Bitmap bitmap = Bitmap.createBitmap(600, 600, Bitmap.Config.RGB_565);
            for (int x = 0; x < 600; x++) {
                for (int y = 0; y < 600; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}