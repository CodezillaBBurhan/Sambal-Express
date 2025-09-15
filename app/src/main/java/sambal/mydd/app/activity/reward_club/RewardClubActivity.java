package sambal.mydd.app.activity.reward_club;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.adapter.reward_club.Reward_club_category;
import sambal.mydd.app.adapter.reward_club.Reward_deal_list;
import sambal.mydd.app.adapter.reward_club.TopSliderAdapter;
import sambal.mydd.app.databinding.ActivityRewardClubBinding;
import sambal.mydd.app.models.reward_club.Category;
import sambal.mydd.app.models.reward_club.Deals;
import sambal.mydd.app.models.reward_club.Example;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.StatusBarcolor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RewardClubActivity extends BaseActivity {
    ActivityRewardClubBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reward_club);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reward_club);
        setToolbarWithBackButton_colorprimary("Reward Club");


       // setupDynamicStepper(0.0, 10.0, 2.5, 3.2, 3200);
        rewardClub("0");

        binding.activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorMessage.INSTANCE.I_ActivityForResult(RewardClubActivity.this,ActiveMembershipActivity.class,null,1001);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            rewardClub("0");
        }
    }
    @Override
    protected int getContentResId() {
        return R.layout.activity_reward_club;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(this, "");
    }

    public void setCategoryList(List<Category> bannerList) {

        if (bannerList.size() > 0) {
            binding.categoryRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            Reward_club_category adapter = new Reward_club_category(this, bannerList);
            binding.categoryRcv.setItemAnimator(new DefaultItemAnimator());
            binding.categoryRcv.setHasFixedSize(true);
            binding.categoryRcv.setVisibility(View.VISIBLE);
            binding.categoryRcv.setItemViewCacheSize(bannerList.size());
            binding.categoryRcv.setAdapter(adapter);

        } else {
            binding.categoryRcv.setVisibility(View.GONE);
        }
    }
    public void setTopBanner(sambal.mydd.app.models.reward_club.Response response) {

        if (response !=null) {
            double totalSpliteValue=Double.parseDouble(response.getClubEndPrice())/response.getClubPriceSplit();
            TopSliderAdapter adapter1 = new TopSliderAdapter(this, response.getClubPriceSplit(),response,totalSpliteValue,response.getClubTargetPoints(),response.getClubUserEarnedPoints());
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
                @Override
                public boolean canScrollHorizontally() {
                    return false; // disable horizontal scrolling
                }

                @Override
                public boolean canScrollVertically() {
                    return false; // disable vertical scrolling
                }
            };

            binding.progressPointRcv.setLayoutManager(layoutManager);
            binding.progressPointRcv.setItemAnimator(new DefaultItemAnimator());
            binding.progressPointRcv.setHasFixedSize(true);
            binding.progressPointRcv.setVisibility(View.VISIBLE);
            binding.progressPointRcv.setItemViewCacheSize(response.getClubPriceSplit());
            binding.progressPointRcv.setAdapter(adapter1);

        } else {
            binding.progressPointRcv.setVisibility(View.GONE);
        }
    }

    public void setDealList(List<Deals> bannerList) {

        if (bannerList.size() > 0) {
            binding.tvNOData.setVisibility(View.GONE);
            binding.dealRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            Reward_deal_list adapter = new Reward_deal_list(this, bannerList);
            binding.dealRcv.setItemAnimator(new DefaultItemAnimator());
            binding.dealRcv.setHasFixedSize(true);
            binding.dealRcv.setVisibility(View.VISIBLE);
            binding.dealRcv.setItemViewCacheSize(bannerList.size());
            binding.dealRcv.setAdapter(adapter);

        } else {
            binding.dealRcv.setVisibility(View.GONE);
            binding.tvNOData.setVisibility(View.VISIBLE);
        }
    }



    public void rewardClub(String id) {
        ErrorMessage.E("rewardClub<><>" + id);
        if (AppUtil.isNetworkAvailable(RewardClubActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().getRewardClub(id);
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
                            ErrorMessage.E("rewardClub<<>>>>" + jsonObject.toString());
                            Example example = gson.fromJson(jsonObject.toString(), Example.class);
                            if (example != null) {
                                binding.clubPointsEarnedRemarks.setText(example.getResponse().getClubPointsEarnedRemarks());
                                binding.clubPointsPaymentRemarks.setText(example.getResponse().getClubPointsPaymentRemarks());

                                if(example.getResponse().getClubMembershipStatus()==0){
                                    binding.activateButton.setVisibility(View.VISIBLE);
                                    binding.alreadyActivatedLayout.setVisibility(View.GONE);
                                    binding.clubPointsEarnedRemarks.setVisibility(View.VISIBLE);
                                    binding.clubPointsPaymentRemarks.setVisibility(View.VISIBLE);
                                }else {
                                    binding.activateButton.setVisibility(View.GONE);
                                    binding.alreadyActivatedLayout.setVisibility(View.VISIBLE);
                                    binding.clubPointsEarnedRemarks.setVisibility(View.GONE);
                                    binding.clubPointsPaymentRemarks.setVisibility(View.GONE);
                                    binding.totalRemainingDaysCountTv.setText(example.getResponse().getClubMembershipRemainingDays()+" days remaining");
                                }
                                applyColoredSplitText(binding.clubPointsRemarks,example.getResponse().getClubPointsRemarks(),"#FFFFFF","#ED8D0B");
                               if(id.equals("0")){
                                setCategoryList(example.getResponse().getCategoryList());}
                                setDealList(example.getResponse().getDealsList());
                                setTopBanner(example.getResponse());
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

    public static void applyColoredSplitText(TextView textView, String input, String leftColor, String rightColor) {
        if (input != null && input.contains("=")) {
            String[] parts = input.split("=");
            if (parts.length == 2) {
                String leftText = parts[0].trim();
                String rightText = parts[1].trim();

                String combinedText = leftText + " = " + rightText;
                SpannableString spannable = new SpannableString(combinedText);

                // Apply color to left part
                spannable.setSpan(
                        new ForegroundColorSpan(Color.parseColor(leftColor)),
                        0,
                        leftText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                // Apply color to right part
                spannable.setSpan(
                        new ForegroundColorSpan(Color.parseColor(rightColor)),
                        leftText.length() + 3, // account for " = "
                        combinedText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                textView.setText(spannable);
            } else {
                textView.setText(input); // fallback if format is unexpected
            }
        } else {
            textView.setText(input); // fallback if '=' not present
        }


    }


    public void selectCategory(String id){
        rewardClub(id);
    }


}