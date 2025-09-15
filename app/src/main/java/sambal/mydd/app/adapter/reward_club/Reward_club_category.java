package sambal.mydd.app.adapter.reward_club;

import static androidx.databinding.DataBindingUtil.bind;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.reward_club.RewardClubActivity;
import sambal.mydd.app.databinding.RewardCategoryListBinding;
import sambal.mydd.app.models.reward_club.Category;
import sambal.mydd.app.utils.ErrorMessage;


public class Reward_club_category extends RecyclerView.Adapter<Reward_club_category.MyViewHolder> {

    private Context context;
    private List<Category> BasketList;
    private int selectedPosition = 0; // By default, first item is selected

    public Reward_club_category(Context context, List<Category> BasketList) {
        this.context = context;
        this.BasketList = BasketList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RewardCategoryListBinding binding = RewardCategoryListBinding.inflate(inflater, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Category category = BasketList.get(position);
        holder.binding.categoryNameTv.setText(category.getCategoryName());

        // Highlight the selected item
        if (selectedPosition == position) {
            holder.binding.categoryNameTv.setBackgroundResource(R.drawable.btn_selected); // your selected background
            holder.binding.categoryNameTv.setTextColor(context.getResources().getColor(R.color.white)); // example
        } else {
            holder.binding.categoryNameTv.setBackgroundResource(R.drawable.btn_unselected); // your normal background
            holder.binding.categoryNameTv.setTextColor(context.getResources().getColor(R.color.colorPrimary)); // example
        }
        holder.binding.categoryNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ErrorMessage.E("sfnjksdhjdhsjhdjkhj");
                int previousSelected = selectedPosition;
                selectedPosition = position;

                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                ((RewardClubActivity)context).selectCategory(category.getCategoryId().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return BasketList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        RewardCategoryListBinding binding;

        public MyViewHolder(RewardCategoryListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

