package sambal.mydd.app.adapter.viewAndEarn;

import static androidx.databinding.DataBindingUtil.bind;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.viewAndEarn.ViewAndEarnDetailActivity;
import sambal.mydd.app.databinding.ViewEarnFeedLayoutBinding;
import sambal.mydd.app.models.viewAndearn.Event;
import sambal.mydd.app.models.viewAndearn.Example;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAndEarnAdapter extends RecyclerView.Adapter<ViewAndEarnAdapter.MyViewHolder> {

    private Context context;
    private List<Event> noticeBoardList;

    public ViewAndEarnAdapter(Context context, List<Event> noticeBoardList) {
        this.context = context;
        this.noticeBoardList = noticeBoardList;
    }

    @Override
    public ViewAndEarnAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewEarnFeedLayoutBinding binding = ViewEarnFeedLayoutBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final ViewAndEarnAdapter.MyViewHolder holder, final int position) {
        Event category = noticeBoardList.get(position);

        holder.binding.tvAgentName.setText(category.getAgentName());
        holder.binding.likeCountTv.setText(String.valueOf(category.getEventLikesCount()));
        holder.binding.mainContentTv.setText(category.getEventDesc());

       Glide.with(context).load(category.getAgentImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.ivAgentImage);

        if (category.getEventType() == 1) {
            holder.binding.playIcon.setVisibility(View.GONE);
            Glide.with(context).load(category.getEventImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.imgFeed);
        }else if (category.getEventType() == 2) {
            try {
            holder.binding.playIcon.setVisibility(View.VISIBLE);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(category.getEventVideo(), new HashMap<>());

            Bitmap bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST); // 1 second
            holder.binding.imgFeed.setImageBitmap(bitmap);
            retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if (category.getEventType() == 3) {
            String thumbnailUrl = "https://img.youtube.com/vi/" + category.getEventYoutubeVideoId() + "/hqdefault.jpg";
            holder.binding.playIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(thumbnailUrl).placeholder(R.drawable.placeholder_4_3).into(holder.binding.imgFeed);
        }else if (category.getEventType() == 4) {
            holder.binding.playIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(category.getEventImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.imgFeed);
        }

        // Set like icon
        if (category.getEventLikesStatus() == 1) {
            holder.binding.likeImg.setImageResource(R.drawable.ic_heart_red);
        } else {
            holder.binding.likeImg.setImageResource(R.drawable.heart_black);
        }

        // On click detail
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", category);
            ErrorMessage.INSTANCE.I(context, ViewAndEarnDetailActivity.class, bundle);
        });

        // On click share
        holder.binding.shareBtn.setOnClickListener(v -> {
            if (category != null && category.getEventShareText() != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, category.getEventShareText());
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        // On click like
        holder.binding.likeImg.setOnClickListener(v -> {
            if (category != null && category.getEventLikesStatus() != null) {
                changeLikeStatus(category.getEventId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noticeBoardList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        ViewEarnFeedLayoutBinding binding;

        public MyViewHolder(ViewEarnFeedLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void changeLikeStatus(Integer eventId, int position) {
        if (AppUtil.isNetworkAvailable(context)) {
            Call<ResponseBody> call = AppConfig.api_Interface().likeEvent(eventId.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            ErrorMessage.E("rewardClub<<>>>>" + jsonObject.toString());

                            Event category = noticeBoardList.get(position);

                            // Toggle status
                            int currentStatus = category.getEventLikesStatus() != null ? category.getEventLikesStatus() : 0;
                            int newStatus = (currentStatus == 1) ? 0 : 1;
                            category.setEventLikesStatus(newStatus);

                            // Optionally update like count
                            int count = category.getEventLikesCount() != null ? category.getEventLikesCount() : 0;
                            category.setEventLikesCount(newStatus == 1 ? count + 1 : count - 1);

                            notifyItemChanged(position);

                        } catch (Exception e) {
                            ErrorMessage.E("exception>>>" + e.toString());
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ErrorMessage.E("API error: " + t.toString());
                }
            });

        } else {
            ErrorMessage.INSTANCE.T(context, context.getString(R.string.no_internet_connection));
        }
    }
}
