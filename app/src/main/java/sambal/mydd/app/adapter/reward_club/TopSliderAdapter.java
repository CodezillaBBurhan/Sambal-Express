package sambal.mydd.app.adapter.reward_club;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sambal.mydd.app.databinding.TopSliderAdapterBinding;
import sambal.mydd.app.models.reward_club.Category;
import sambal.mydd.app.models.reward_club.Response;
import sambal.mydd.app.utils.ErrorMessage;

public class TopSliderAdapter extends RecyclerView.Adapter<TopSliderAdapter.MyViewHolder> {

    private Context context;
    private List<Category> BasketList;
    private Integer totalLength;
    private Response response;
    private double totalBarValue;
    private Integer clubTargetPoints;
    private double clubUserEarnedPoints;
    public String userEarnedPoints;


    public TopSliderAdapter(Context context, int totalLength, Response response, double totalBarValue, Integer clubTargetPoints, String clubUserEarnedPoints) {
        this.context = context;
        this.BasketList = BasketList;
        this.totalLength = totalLength;
        this.response = response;
        this.totalBarValue = totalBarValue;
        this.clubTargetPoints = clubTargetPoints;
        this.userEarnedPoints = clubUserEarnedPoints;
        this.clubUserEarnedPoints = Double.parseDouble(clubUserEarnedPoints);
    }

    @Override
    public TopSliderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TopSliderAdapterBinding binding = TopSliderAdapterBinding.inflate(inflater, parent, false);
        return new TopSliderAdapter.MyViewHolder(binding);
    }

    private int getScreenWidth() {
        return context.getResources().getDisplayMetrics().widthPixels-150;
    }

    @Override
    public void onBindViewHolder(final TopSliderAdapter.MyViewHolder holder, final int position) {

        int screenWidth = getScreenWidth();
        int itemCount = getItemCount();

        // Avoid divide by zero
        if (itemCount == 0) return;

        int itemWidth = screenWidth / itemCount;

        // Set width programmatically
        ViewGroup.LayoutParams layoutParams = holder.binding.getRoot().getLayoutParams();
        layoutParams.width = itemWidth;
        holder.binding.getRoot().setLayoutParams(layoutParams);
        if (position == 0) {
            holder.binding.titleTv.setText(response.getCurrency() + response.getClubStartPrice());
        } else {
            holder.binding.titleTv.setText(response.getCurrency() + (totalBarValue * position));
        }

        if (position + 1 == totalLength) {
            holder.binding.endTitleTv.setVisibility(View.VISIBLE);
            holder.binding.endTitleTv.setText(response.getCurrency() + (response.getClubEndPrice()));
        } else {
            holder.binding.endTitleTv.setVisibility(View.GONE);
        }

        ErrorMessage.E("clubTargetPoints>>"+clubTargetPoints+ "totalBarValue>>"+totalBarValue+"clubUserEarnedPoints>>"+clubUserEarnedPoints);

//        if((clubTargetPoints/totalBarValue* position)<=clubUserEarnedPoints){
//            holder.binding.progressBar.setProgress(100);
//        }else {
//            holder.binding.progressBar.setProgress(7);
//        }

        int stepPoints = clubTargetPoints / totalLength; // = 2500
        int currentStepStart = stepPoints * position;
        int currentStepEnd = stepPoints * (position + 1);

        if (clubUserEarnedPoints >= currentStepEnd) {
            // Full progress (earned more than this step)
            holder.binding.progressBar.setVisibility(View.VISIBLE);
            holder.binding.progressBar.setProgress(100);
        } else if (clubUserEarnedPoints > currentStepStart) {
            // Partial progress in current step
            int earnedInThisStep = (int) clubUserEarnedPoints - currentStepStart;
            int progressPercent = (int) ((earnedInThisStep / (float) stepPoints) * 100);
            holder.binding.progressBar.setVisibility(View.VISIBLE);
            holder.binding.progressBar.setProgress(progressPercent);
            holder.binding. pointsText.setText(userEarnedPoints + " points");
            holder.binding.progressBar.post(() -> {
                int barWidth = holder.binding.progressBar.getWidth();
                float thumbX = (barWidth * progressPercent) / 100f;

                // Adjust to center the text horizontally
                float textWidth = holder.binding. pointsText.getPaint().measureText(holder.binding. pointsText.getText().toString());
                float offsetX = thumbX - (textWidth / 2f);

                // Set X position of the TextView
                holder.binding. pointsText.setX(Math.max(offsetX, 0));
            });
        } else {
            // No progress yet for this step
            holder.binding.progressBar.setVisibility(View.VISIBLE); // or GONE
        }




// Wait for layout to be drawn, then set X position of TextView



    }

    @Override
    public int getItemCount() {
        return totalLength;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TopSliderAdapterBinding binding;

        public MyViewHolder(TopSliderAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
