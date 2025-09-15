package sambal.mydd.app.activity.viewAndEarn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.adapter.viewAndEarn.ViewAndEarnAdapter;
import sambal.mydd.app.databinding.ActivityViewAndEarnBinding;

import sambal.mydd.app.models.viewAndearn.Event;
import sambal.mydd.app.models.viewAndearn.Example;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.StatusBarcolor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAndEarnActivity extends BaseActivity {
ActivityViewAndEarnBinding binding;
    private int offset = 0;
    private String Check_Data = "";
    ArrayList<Event> categoryArrayList = new ArrayList<>();
    ViewAndEarnAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_and_earn);
        setToolbarWithBackButton_colorprimary("View & Earn");
        viewAndEarn();


        binding.nestedscolling.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        //code to fetch more data for endless scrolling

                        if (Check_Data.equals("") || Check_Data.equals("2")) {
                            Check_Data = "2";
                            offset = offset + 1;
                            ErrorMessage.E("count$offset" + offset);
                            viewAndEarn();
                        }
                        Log.e("count", ">if is working nestedScroll>" + offset);
                    }
                }
            }
        });
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_view_and_earn;
    }
    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(this, "");
    }

    public void setFeedList(List<Event> bannerList) {
        if (bannerList.size() > 0) {
            for (int i = 0; i <bannerList.size(); i++) {
                categoryArrayList.add(bannerList.get(i));
            }

            if (Check_Data.equals("")) {
                binding.feedListRcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                 adapter = new ViewAndEarnAdapter(this, categoryArrayList);
                binding.feedListRcv.setItemAnimator(new DefaultItemAnimator());
                binding.feedListRcv.setHasFixedSize(true);
                binding.feedListRcv.setVisibility(View.VISIBLE);
                binding.feedListRcv.setItemViewCacheSize(categoryArrayList.size());
                binding.feedListRcv.setAdapter(adapter);
            } else {
                ErrorMessage.E("else is working>>" + Check_Data);
                adapter.notifyItemInserted(categoryArrayList.size());
                // cardView.scrollToPosition(Count_item+1);
            }
        } else {
            binding.feedListRcv.setVisibility(View.GONE);
        }
    }

    public void viewAndEarn() {
        if (AppUtil.isNetworkAvailable(ViewAndEarnActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);
            Call<ResponseBody> call = AppConfig.api_Interface().getEarnClubPointsEventList(String.valueOf(offset),"10");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("rewardClub>>" + response.code());
                    binding.view.setVisibility(View.GONE);
                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            ErrorMessage.E("rewardClub<<>>>>" + jsonObject.toString());
                            Example example = gson.fromJson(jsonObject.toString(), Example.class);



                           if(example!=null && example.getResponse()!=null) {
                               binding.earnClubPointTv.setText(example.getResponse().getEventHeading1());
                               binding.secondEarnClubPointTv.setText(example.getResponse().getEventHeading2());
                               binding.thirdEarnClubPointTv.setText(example.getResponse().getEventHeading3());
                               binding.fourthEarnClubPointTv.setText(example.getResponse().getEventHeading4());
                               if(example.getResponse().getEventList().size()>0) {
                                   setFeedList(example.getResponse().getEventList());
                               }else{
                                   Check_Data = "1";
                               }

                           }else {
                               Check_Data = "1";
                           }

                        } catch (Exception e) {
                            ErrorMessage.E("exceptioonnnn>>>" + e.toString());
                        }
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Check_Data = "1";
                    binding.view.setVisibility(View.GONE);
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