package sambal.mydd.app.adapter.viewAndEarn;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.viewAndEarn.ViewAndEarnDetailActivity;
import sambal.mydd.app.databinding.RewardClubDetailAdapterBinding;

import sambal.mydd.app.models.viewAndEarnnDetail.EventDetail;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.ErrorMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAndEarnDetailAdapter extends RecyclerView.Adapter<ViewAndEarnDetailAdapter.MyViewHolder> {

    private Context context;
    private List<EventDetail> noticeBoardList;
    private Lifecycle lifecycle;
    private RecyclerView recyclerView;
    private int currentlyPlayingPosition = -1;

    public ViewAndEarnDetailAdapter(Context context, List<EventDetail> noticeBoardList, Lifecycle lifecycle) {
        this.context = context;
        this.noticeBoardList = noticeBoardList;
        this.lifecycle = lifecycle;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RewardClubDetailAdapterBinding binding = RewardClubDetailAdapterBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override

    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        EventDetail category = noticeBoardList.get(position);
        int totalSeconds = category.getEventSeconds();
        holder.cancelTasks();

        if (category.getEventStatus() == 2) {
            holder.binding.progressBar.setVisibility(View.GONE);
            holder.binding.pointsInfo.setText("Viewed — Earned " + category.getEventClubPoints() + " Club Points");
            holder.binding.pointsInfo.setTextColor(context.getResources().getColor(R.color.yellow_text_color));

        } else {
            holder.binding.progressBar.setVisibility(View.VISIBLE);


            holder.binding.progressBar.setMax(totalSeconds);
            holder.binding.progressBar.setProgress(0);

            holder.binding.pointsInfo.setText("View for " + totalSeconds + " Seconds — Earn " + category.getEventClubPoints() + " Club Points!");
            holder.binding.pointsInfo.setTextColor(context.getResources().getColor(R.color.white));

            /*if (category.getEventType() == 1) {*/
                holder.countDownTimer = new CountDownTimer(totalSeconds * 1000L, 1000L) {
                    int progress = 0;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        progress++;
                        int secondsRemaining = (int) (millisUntilFinished / 1000);

                        // Update progress bar
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            holder.binding.progressBar.setProgress(progress, true);
                        } else {
                            holder.binding.progressBar.setProgress(progress);
                        }

                        // Update the text to show remaining seconds
                        holder.binding.pointsInfo.setText("View for " + secondsRemaining + " Seconds — Earn " + category.getEventClubPoints() + " Club Points!");
                    }

                    @Override
                    public void onFinish() {
                        holder.isTimerRunning = false;
                        ErrorMessage.E("onFinish>>>>");
                        eventViewed(category.getEventId(), position);
                    }
                }.start();

                holder.isTimerRunning = true;
           /* }*/

        }

        holder.binding.brandName.setText(category.getAgentName());
        holder.binding.description.setText(category.getEventDesc());
        Glide.with(context).load(category.getAgentImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.agentImg);

        holder.binding.likeIcon.setImageResource(category.getEventLikesStatus() == 1 ? R.drawable.ic_heart_red : R.drawable.heart_black);
        if (category.getEventLikesStatus() != 1) {
            holder.binding.likeIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
        holder.binding.likeCount.setText(String.valueOf(category.getEventLikesCount()));

        switch (category.getEventType()) {
            case 1:
                holder.binding.playIcon.setVisibility(View.GONE);
                Glide.with(context).load(category.getEventImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.mainImage);
                break;
            case 2:
                holder.binding.playIcon.setVisibility(View.VISIBLE);
                try {
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(category.getEventVideo(), new HashMap<>());
                    Bitmap bitmap = retriever.getFrameAtTime(1000000, MediaMetadataRetriever.OPTION_CLOSEST);
                    holder.binding.mainImage.setImageBitmap(bitmap);
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                holder.binding.playIcon.setVisibility(View.VISIBLE);
                String thumbnailUrl = "https://img.youtube.com/vi/" + category.getEventYoutubeVideoId() + "/hqdefault.jpg";
                Glide.with(context).load(thumbnailUrl).placeholder(R.drawable.placeholder_4_3).into(holder.binding.mainImage);
                break;
            case 4:
                holder.binding.playIcon.setVisibility(View.VISIBLE);
                Glide.with(context).load(category.getEventImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.mainImage);
                break;
        }

        holder.binding.shareIcon.setOnClickListener(v -> {
            if (category.getEventShareText() != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
                shareIntent.putExtra(Intent.EXTRA_TEXT, category.getEventShareText());
                context.startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        holder.binding.playIcon.setOnClickListener(v -> {
            holder.binding.mainImage.setVisibility(View.GONE);
            holder.binding.playIcon.setVisibility(View.GONE);

            if (category.getEventType() == 2) {
                holder.binding.videoPlayer.setVisibility(View.VISIBLE);
                playVideo(category.getEventVideo(), holder.binding.videoPlayer);
            } else if (category.getEventType() == 3) {
                holder.binding.youtubePlayerView.setVisibility(View.VISIBLE);
              /*  lifecycle.addObserver(holder.binding.youtubePlayerView);
                holder.binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(YouTubePlayer youTubePlayer) {
                        youTubePlayer.loadVideo(category.getEventYoutubeVideoId(), 0);
                    }
                });*/
                playYoutubeVideo(holder, category.getEventYoutubeVideoId());
            }
            if(category.getEventStatus()!=2){
                holder.countDownTimer = new CountDownTimer(totalSeconds * 1000L, 1000L) {
                    int progress = 0;

                    @Override
                    public void onTick(long millisUntilFinished) {
                        progress++;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            holder.binding.progressBar.setProgress(progress, true);
                        } else {
                            holder.binding.progressBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onFinish() {
                        holder.isTimerRunning = false;
                        ErrorMessage.E("onFinish>>>>");
                        eventViewed(category.getEventId(), position);
                        // TODO: Handle completion callback if needed
                    }
                }.start();

                holder.isTimerRunning = true;
            }
        });

        holder.binding.likeIcon.setOnClickListener(v -> changeLikeStatus(category.getEventId(), position));
        holder.binding.backArrow.setOnClickListener(v -> ((ViewAndEarnDetailActivity) context).finish());
    }

    public void playYoutubeVideo(MyViewHolder holder, String videoId) {
        // Enable JavaScript & media playback
        holder.binding.youtubePlayerView.getSettings().setJavaScriptEnabled(true);
        holder.binding.youtubePlayerView.getSettings().setMediaPlaybackRequiresUserGesture(false); // ✅ Important for autoplay on Android

// Allow YouTube iframe to work
        holder.binding.youtubePlayerView.getSettings().setDomStorageEnabled(true);
        holder.binding.youtubePlayerView.getSettings().setAllowFileAccess(true);
        holder.binding.youtubePlayerView.getSettings().setLoadWithOverviewMode(true);
        holder.binding.youtubePlayerView.getSettings().setUseWideViewPort(true);

// Load YouTube video with autoplay=1

        String html = "<html><body style='margin:0;padding:0;'>" +
                "<iframe width='100%' height='100%' " +
                "src='https://www.youtube.com/embed/" + videoId + "?autoplay=1&mute=1&controls=1' " +
                "frameborder='0' allow='autoplay; encrypted-media' allowfullscreen>" +
                "</iframe></body></html>";

        holder.binding.youtubePlayerView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

    }

    private void playMedia(MyViewHolder holder, EventDetail item) {
        holder.binding.mainImage.setVisibility(View.VISIBLE);

        if (item.getEventType() == 1) {
            holder.binding.playIcon.setVisibility(View.GONE);
        } else {
            holder.cancelTasks();
            holder.binding.playIcon.setVisibility(View.VISIBLE);
        }


        if (item.getEventType() == 2) {
            holder.binding.mainImage.setVisibility(View.GONE);
            holder.binding.playIcon.setVisibility(View.GONE);
            holder.binding.videoPlayer.setVisibility(View.VISIBLE);
            playVideo(item.getEventVideo(), holder.binding.videoPlayer);
        }
        else if (item.getEventType() == 3) {
           /* holder.binding.mainImage.setVisibility(View.GONE);
            holder.binding.playIcon.setVisibility(View.GONE);
            holder.binding.youtubePlayerView.setVisibility(View.VISIBLE);
            holder.binding.youtubePlayerView.post(() -> {
                lifecycle.addObserver(holder.binding.youtubePlayerView);
                holder.binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(YouTubePlayer youTubePlayer) {
                        ErrorMessage.E("YouTubePlayer onReady called: " + item.getEventYoutubeVideoId());
                        youTubePlayer.loadVideo(item.getEventYoutubeVideoId(), 0);
                    }
                });
            });*/
            holder.binding.youtubePlayerView.setVisibility(View.VISIBLE);
            holder.binding.playIcon.setVisibility(View.GONE);
            holder.binding.mainImage.setVisibility(View.GONE);
            playYoutubeVideo(holder, item.getEventYoutubeVideoId());


        }
        else if (item.getEventType() == 1) {
            holder.binding.playIcon.setVisibility(View.GONE);
            Glide.with(context).load(item.getEventImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.mainImage);

        }else if (item.getEventType() == 4) {
            holder.binding.playIcon.setVisibility(View.VISIBLE);
            Glide.with(context).load(item.getEventImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.mainImage);

        }
    }

    public void playOnlyVisibleVideo(int position) {
        if (recyclerView == null) return; // 🔐 prevent crash
        if (position == currentlyPlayingPosition) return;

        if (currentlyPlayingPosition != -1) {
            RecyclerView.ViewHolder oldHolder = recyclerView.findViewHolderForAdapterPosition(currentlyPlayingPosition);
            if (oldHolder instanceof MyViewHolder) {
                ((MyViewHolder) oldHolder).cancelTasks();
            }
        }

        RecyclerView.ViewHolder newHolder = recyclerView.findViewHolderForAdapterPosition(position);
        if (newHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) newHolder;
            EventDetail item = noticeBoardList.get(position);
            playMedia(holder, item);
            currentlyPlayingPosition = position;
        }
    }


    private void playVideo(String url, PlayerView playerView) {
        ExoPlayer player = new ExoPlayer.Builder(context).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(url));
        player.setMediaItem(mediaItem);
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare();
        player.play();
    }

    @Override
    public int getItemCount() {
        return noticeBoardList.size();
    }

    @Override
    public void onViewRecycled(@NonNull MyViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cancelTasks();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RewardClubDetailAdapterBinding binding;
        public CountDownTimer countDownTimer;
        public boolean isTimerRunning = false;

        // lifecycle.addObserver(binding.youtubePlayerView);
        public MyViewHolder(RewardClubDetailAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void cancelTasks() {
            if (countDownTimer != null) {
                countDownTimer.cancel(); // 🛑 Stop previous timer
                countDownTimer = null;
            }
            isTimerRunning = false;
            if (binding.videoPlayer.getPlayer() != null) {
                binding.videoPlayer.getPlayer().stop();
                binding.videoPlayer.getPlayer().release();
                binding.videoPlayer.setPlayer(null);
            }
            //  binding.youtubePlayerView.release();
            binding.youtubePlayerView.setVisibility(View.GONE);
            binding.videoPlayer.setVisibility(View.GONE);
            binding.mainImage.setVisibility(View.VISIBLE);
            binding.playIcon.setVisibility(View.VISIBLE);
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
                            EventDetail category = noticeBoardList.get(position);
                            int currentStatus = category.getEventLikesStatus() != null ? category.getEventLikesStatus() : 0;
                            int newStatus = (currentStatus == 1) ? 0 : 1;
                            category.setEventLikesStatus(newStatus);
                            int count = category.getEventLikesCount() != null ? category.getEventLikesCount() : 0;
                            category.setEventLikesCount(newStatus == 1 ? count + 1 : count - 1);
                            notifyItemChanged(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            ErrorMessage.INSTANCE.T(context, context.getString(R.string.no_internet_connection));
        }
    }

    private void eventViewed(Integer eventId, int position) {
        if (AppUtil.isNetworkAvailable(context)) {
            Call<ResponseBody> call = AppConfig.api_Interface().getEarnClubPointsEventViewed(eventId.toString());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    ErrorMessage.E("eventViewed>>>" + response.code());
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            ErrorMessage.E("eventViewed>>>" + jsonObject.toString());
                            EventDetail category = noticeBoardList.get(position);
                            int currentStatus = category.getEventStatus() != null ? category.getEventStatus() : 0;
                            int newStatus = (currentStatus == 1) ? 2 : 1;
                            category.setEventStatus(newStatus);
                            notifyItemChanged(position);
                            giftClubPointsPopup(category, context);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } else {
            ErrorMessage.INSTANCE.T(context, context.getString(R.string.no_internet_connection));
        }
    }

    public void giftClubPointsPopup(EventDetail category, Context context) {
        Dialog reorder_confirmation_popup = new Dialog(context);
        reorder_confirmation_popup.setContentView(R.layout.gift_club_points_popup);
        reorder_confirmation_popup.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(reorder_confirmation_popup.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        reorder_confirmation_popup.getWindow().setAttributes(lp);
        reorder_confirmation_popup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        ImageButton cancel_btn = reorder_confirmation_popup.findViewById(R.id.cancel_rejection_button);
        TextView title = reorder_confirmation_popup.findViewById(R.id.accepted_heading_tv);
        title.setText("You've just earned " + category.getEventClubPoints() + " Club Points!");


        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reorder_confirmation_popup.dismiss();
            }
        });


        reorder_confirmation_popup.show();

    }

}


