package sambal.mydd.app.adapter.reward_club;

import static android.graphics.Paint.STRIKE_THRU_TEXT_FLAG;
import static androidx.databinding.DataBindingUtil.bind;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.reward_club.DealDetailActivity;
import sambal.mydd.app.databinding.RewardCategoryListBinding;
import sambal.mydd.app.databinding.RewardDealListBinding;
import sambal.mydd.app.models.reward_club.Deals;
import sambal.mydd.app.utils.ErrorMessage;

public class Reward_deal_list extends RecyclerView.Adapter<Reward_deal_list.MyViewHolder> {

    RewardDealListBinding rewardDealListBinding;
    public static Context context;
    public List<Deals> BasketList;


    public Reward_deal_list(Context context, List<Deals> BasketList) {
        this.context = context;
        this.BasketList = BasketList;

    }


    @Override
    public Reward_deal_list.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rewardDealListBinding = bind(LayoutInflater.from(parent.getContext()).inflate(R.layout.reward_deal_list, parent, false));
        return new Reward_deal_list.MyViewHolder(rewardDealListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(final Reward_deal_list.MyViewHolder holder, final int position) {
        Deals deals=BasketList.get(position);

        rewardDealListBinding.tvAgentCompanyName.setText(deals.getAgentCompanyName());
        rewardDealListBinding.categoryNameTv.setText(" | "+deals.getCategoryName());
        rewardDealListBinding.dealRateTv.setText("★ "+deals.getDealRating());
        rewardDealListBinding.dealNameTv.setText(deals.getDealName());
        rewardDealListBinding.clubPointsValueTv.setText(deals.getClubPointsValue());
        rewardDealListBinding.dealSavingTv.setText("Total saving: "+deals.getDealSavings());
        Glide.with(context).load(deals.getDealImage()).placeholder(R.drawable.placeholder_4_3).into(rewardDealListBinding.dealimage);
        if (deals.getDealFavourite() == 1) {
            rewardDealListBinding.dealfavImg.setImageResource(R.drawable.heartfulled);
        } else {
            rewardDealListBinding.dealfavImg.setImageResource(R.drawable.ic_heart_outlined);
        }
        rewardDealListBinding.tvFinalPrice.setText(deals.getProductFinalPrice());
        if (deals.getProductDiscountPercentageEnabled() == 0 && deals.getPriceEnabledId() == 1 && deals.getDiscountPriceEnabledId() == 0) {
            rewardDealListBinding.tvFinalPrice.setText( deals.getProductCurrency() + deals.getProductPrice());
            rewardDealListBinding.tvFinalPrice.setTextColor(Color.parseColor("#101010"));
        } else if (deals.getProductDiscountPercentageEnabled() == 1 && deals.getPriceEnabledId() == 1 && deals.getDiscountPriceEnabledId() == 1) {
            rewardDealListBinding.tvPrice.setTextColor(Color.parseColor("#AAAAAA"));
            rewardDealListBinding.tvFinalPrice.setVisibility(View.VISIBLE);
            rewardDealListBinding.tvFinalPrice.setText( deals.getProductCurrency() + deals.getProductFinalPrice());
            rewardDealListBinding.tvPrice.setPaintFlags(
                    rewardDealListBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            rewardDealListBinding.tvPrice.setText( deals.getProductCurrency() + deals.getProductPrice());
            rewardDealListBinding.tvDiscount.setText( deals.getProductDiscountPercentage() + "off");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("agentId",deals.getAgentId().toString());
                bundle.putString("productId",deals.getProductId().toString());
                ErrorMessage.INSTANCE.I(context, DealDetailActivity.class,bundle);
            }
        });

    }






    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return BasketList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {

        Button plus_btn;

        public MyViewHolder(View view) {
            super(view);

//            plus_btn = itemView.findViewById(R.id.plus_btn);


        }
    }



}
