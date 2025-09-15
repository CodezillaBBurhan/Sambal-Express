package sambal.mydd.app.activity.reward_club;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.adapter.reward_club.NoticeBoard_Adapter;
import sambal.mydd.app.adapter.reward_club.Reward_deal_list;
import sambal.mydd.app.databinding.ActivityNoticeBoardListBinding;
import sambal.mydd.app.models.notice_board.Example;
import sambal.mydd.app.models.notice_board.NoticeBoard;
import sambal.mydd.app.models.reward_club.Deals;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.StatusBarcolor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticeBoardListActivity extends BaseActivity {
    ActivityNoticeBoardListBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice_board_list);
        setToolbarWithBackButton_colorprimary("Notice Board");
        noticeBoardList();
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_notice_board_list;
    }
    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(this, "");
    }
    public void noticeBoardList() {
        if (AppUtil.isNetworkAvailable(NoticeBoardListActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().getNoticeBoardList();
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
                            ErrorMessage.E("noticeBoardList<<>>>>" + jsonObject.toString());
                            Example example = gson.fromJson(jsonObject.toString(), Example.class);
                            if (example != null) {
                                setDealList(example.getResponse().getNoticeBoardList());
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
    public void setDealList(List<NoticeBoard> noticeBoardList) {

        if (noticeBoardList.size() > 0) {
            binding.tvNOData.setVisibility(View.GONE);
            binding.noticeBoardRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            NoticeBoard_Adapter adapter = new NoticeBoard_Adapter(this, noticeBoardList);
            binding.noticeBoardRcv.setItemAnimator(new DefaultItemAnimator());
            binding.noticeBoardRcv.setHasFixedSize(true);
            binding.noticeBoardRcv.setVisibility(View.VISIBLE);
            binding.noticeBoardRcv.setItemViewCacheSize(noticeBoardList.size());
            binding.noticeBoardRcv.setAdapter(adapter);

        } else {
            binding.noticeBoardRcv.setVisibility(View.GONE);
            binding.tvNOData.setVisibility(View.VISIBLE);
        }
    }


    public void noticeBoardPopup( NoticeBoard category) {
        Dialog reorder_confirmation_popup = new Dialog(this);
        reorder_confirmation_popup.setContentView(R.layout.notice_board_popup);
        reorder_confirmation_popup.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reorder_confirmation_popup.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        reorder_confirmation_popup.getWindow().setAttributes(lp);
        reorder_confirmation_popup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        ImageButton cancel_btn = reorder_confirmation_popup.findViewById(R.id.cancel_btn);
        TextView title = reorder_confirmation_popup.findViewById(R.id.title);
        TextView description = reorder_confirmation_popup.findViewById(R.id.description);
        ImageView product1 = reorder_confirmation_popup.findViewById(R.id.product1);

        title.setText(category.getAdsTitle());
        description.setText(category.getAdsDescription());
        Glide.with(this).load(category.getAdsImage()).placeholder(R.drawable.placeholder_4_3).into(product1);


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder_confirmation_popup.dismiss();
            }
        });


        reorder_confirmation_popup.show();

    }


}