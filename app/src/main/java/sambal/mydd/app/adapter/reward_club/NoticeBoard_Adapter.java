package sambal.mydd.app.adapter.reward_club;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.reward_club.NoticeBoardDetailActivity;
import sambal.mydd.app.activity.reward_club.NoticeBoardListActivity;
import sambal.mydd.app.activity.reward_club.RewardClubActivity;
import sambal.mydd.app.databinding.NoticeBoardListAdapterBinding;
import sambal.mydd.app.databinding.RewardCategoryListBinding;
import sambal.mydd.app.models.notice_board.NoticeBoard;
import sambal.mydd.app.models.reward_club.Category;
import sambal.mydd.app.utils.ErrorMessage;

public class NoticeBoard_Adapter extends RecyclerView.Adapter<NoticeBoard_Adapter.MyViewHolder> {

    private Context context;
    private List<NoticeBoard> noticeBoardList;
    private int selectedPosition = 0; // By default, first item is selected

    public NoticeBoard_Adapter(Context context, List<NoticeBoard> noticeBoardList) {
        this.context = context;
        this.noticeBoardList = noticeBoardList;
    }

    @Override
    public NoticeBoard_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        NoticeBoardListAdapterBinding binding = NoticeBoardListAdapterBinding.inflate(inflater, parent, false);
        return new NoticeBoard_Adapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final NoticeBoard_Adapter.MyViewHolder holder, final int position) {
        NoticeBoard category = noticeBoardList.get(position);
        holder.binding.title.setText(category.getAdsTitle());
        holder.binding.description.setText(category.getAdsDescription());
        Glide.with(context).load(category.getAdsImage()).placeholder(R.drawable.placeholder_4_3).into(holder.binding.product1);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  ((NoticeBoardListActivity)context).noticeBoardPopup(category);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",category);
                ErrorMessage.INSTANCE.I(context, NoticeBoardDetailActivity.class,bundle);
            }
        });

    }

    @Override
    public int getItemCount() {
        return noticeBoardList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        NoticeBoardListAdapterBinding binding;

        public MyViewHolder(NoticeBoardListAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
