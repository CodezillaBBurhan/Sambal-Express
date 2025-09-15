package sambal.mydd.app.activity.viewAndEarn;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.adapter.viewAndEarn.ViewAndEarnAdapter;
import sambal.mydd.app.adapter.viewAndEarn.ViewAndEarnDetailAdapter;
import sambal.mydd.app.databinding.ActivityViewAndEarnDetailBinding;
import sambal.mydd.app.models.viewAndEarnnDetail.EventDetail;
import sambal.mydd.app.models.viewAndEarnnDetail.Example;


import sambal.mydd.app.models.viewAndearn.Event;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import sambal.mydd.app.utils.StatusBarcolor;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAndEarnDetailActivity extends BaseActivity {
    ActivityViewAndEarnDetailBinding binding;
    Event category;

    private int offset = 0;
    private String Check_Data = "";
    ArrayList<EventDetail> categoryArrayList = new ArrayList<>();
    ViewAndEarnDetailAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_and_earn_detail);
       // setToolbarWithBackButton_colorprimary("Reward Club");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            category = (Event) bundle.getSerializable("data");
            viewAndEarnList();
        }

        binding.feedListRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                try {
                    if (!recyclerView.canScrollVertically(1) && dy > 0) {
                        ErrorMessage.E("Last data++++1" + Check_Data);
                        if (Check_Data.equals("") || Check_Data.equals("2")) {
                            Check_Data = "2";
                            offset = offset + 1;
                            ErrorMessage.E("count$offset" + offset);
                            viewAndEarnList();
                        }
                        //scrolled to bottom
                    } else if (!recyclerView.canScrollVertically(-1) && dy < 0) {
                        ErrorMessage.E("Last data++++2");
                        //scrolled to bottom
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_view_and_earn_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(this, "");
    }
    public void setFeedList(List<EventDetail> bannerList) {
        if (bannerList.size() > 0) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            for (int i = 0; i <bannerList.size(); i++) {
                categoryArrayList.add(bannerList.get(i));
            }
            if (Check_Data.equals("")) {
                binding.feedListRcv.setLayoutManager(layoutManager);
                PagerSnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(binding.feedListRcv);
                adapter = new ViewAndEarnDetailAdapter(this, categoryArrayList, getLifecycle());
                // binding.feedListRcv.setItemAnimator(new DefaultItemAnimator());
                binding.feedListRcv.setHasFixedSize(true);
                binding.feedListRcv.setVisibility(View.VISIBLE);
                binding.feedListRcv.setItemViewCacheSize(categoryArrayList.size());
                binding.feedListRcv.setAdapter(adapter);
                adapter.setRecyclerView(binding.feedListRcv);
            }else {
                adapter.notifyItemInserted(categoryArrayList.size());
            }
            binding.feedListRcv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstVisible != RecyclerView.NO_POSITION) {
                        adapter.playOnlyVisibleVideo(firstVisible);
                    }
                }
            });
        } else {
            binding.feedListRcv.setVisibility(View.GONE);
        }
    }
    public void viewAndEarnList() {
        if (AppUtil.isNetworkAvailable(ViewAndEarnDetailActivity.this)) {
            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(this, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().getEarnClubPointsEventDetails(String.valueOf(offset),"10",category.getEventId().toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("viewAndEarnList>>" + response.code());
                    if (dialogManager != null) {
                        dialogManager.stopProcessDialog();
                    }
                    if (response.isSuccessful()) {
                        Gson gson = new Gson();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            ErrorMessage.E("viewAndEarnList<<>>>>" + jsonObject.toString());
                            Example example = gson.fromJson(jsonObject.toString(), Example.class);
                            if(example!=null && example.getResponse()!=null && example.getResponse().getEventList().size()>0) {
                                setFeedList(example.getResponse().getEventList());
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