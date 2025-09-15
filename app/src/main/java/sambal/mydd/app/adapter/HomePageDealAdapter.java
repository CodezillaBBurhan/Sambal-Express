package sambal.mydd.app.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.List;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.LatestProductDetails;
import sambal.mydd.app.constant.IntentConstant;
import sambal.mydd.app.constant.KeyConstant;
import sambal.mydd.app.constant.MessageConstant;
import sambal.mydd.app.databinding.HomePageDealItemBinding;
import sambal.mydd.app.models.HomePageDeal.Example;
import sambal.mydd.app.models.HomePageDeal.LatestDeals;
import sambal.mydd.app.utils.AppConfig;
import sambal.mydd.app.utils.AppUtil;
import sambal.mydd.app.utils.DialogManager;
import sambal.mydd.app.utils.ErrorMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.lang.Exception;


public class HomePageDealAdapter extends RecyclerView.Adapter<HomePageDealAdapter.MyViewHolder> {

    private Context context;
    private List<LatestDeals> noticeBoardList;

    public HomePageDealAdapter(Context context, List<LatestDeals> noticeBoardList) {
        this.context = context;
        this.noticeBoardList = noticeBoardList;
    }

    @Override
    public HomePageDealAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        HomePageDealItemBinding binding = HomePageDealItemBinding.inflate(inflater, parent, false);
        return new HomePageDealAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final HomePageDealAdapter.MyViewHolder holder, final int position) {

        LatestDeals deal = noticeBoardList.get(position);
        holder.binding.textTitle.setText(deal.getProductName());
        holder.binding.textExpiry.setText("Expire on  -" + deal.getDealExpiredDate());
        holder.binding.textPrice.setText("Only " + deal.getProductCurrency() + deal.getProductFinalPrice());
        Glide.with(context).load(deal.getProductImage()).placeholder(context.getResources().getDrawable(R.drawable.placeholder_4_3)).error(context.getResources().getDrawable(R.drawable.placeholder_4_3)).into(holder.binding.imgProduct);
       if(deal.getProductFavourite()==0) {
           holder.binding.btnFavorite.setImageDrawable(context.getDrawable(R.drawable.ic_favourite_black_new));
       }else if(deal.getProductFavourite()==1) {
           holder.binding.btnFavorite.setImageDrawable(context.getDrawable(R.drawable.ic_heart_red));
       }
      holder.itemView.setOnClickListener(v -> {
          Intent intent =new Intent(context, LatestProductDetails.class);
          intent.putExtra(IntentConstant.INTENT_KEY_PRODUCT_ID, deal.getProductId().toString());
          intent.putExtra(IntentConstant.INTENT_KEY_AGENT_ID,
                  deal.getProductAgentId().toString());
          intent.putExtra("type", "non_direct");
          intent.putExtra("pos", "");
          context.startActivity(intent);
      });
       holder.binding.btnFavorite.setOnClickListener(v -> {
           if (deal.getProductFavourite() == 1) {
               commonPopup(deal.getProductId().toString(),
                       holder,
                       position,
                       deal.getProductFavourite()
                       );
           } else {
               updateFavoriteProduct(deal.getProductId().toString(),
                       holder,
                       position,
                       deal.getProductFavourite());
           }
       });

    }

    @Override
    public int getItemCount() {
        return noticeBoardList.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder {
        HomePageDealItemBinding binding;

        public MyViewHolder(HomePageDealItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void commonPopup(
            String productId,
            MyViewHolder holder,
            int pos,
            int productFavourite

    ) {
        // Show Dialog...
        Dialog dialog1 = new Dialog(context);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.popup_common);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog1.getWindow().setAttributes(lp);

        TextView contentText = dialog1.findViewById(R.id.popup_content);
        contentText.setText("Are you sure you want to remove from favourites?");

        TextView btnNo = dialog1.findViewById(R.id.popup_no_btn);
        btnNo.setText("No");

        TextView btnOk = dialog1.findViewById(R.id.popup_yes_btn);
        btnOk.setText("Yes");

        dialog1.setCancelable(false);
        dialog1.show();

        try {
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateFavoriteProduct(productId, holder, pos, productFavourite);
                    dialog1.dismiss();
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog1.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace(); // optional: log the exception
        }
    }
    private void updateFavoriteProduct(
            String productId,
            MyViewHolder holder,
            int posi,
            int productFavourite
    ) {
        if (AppUtil.isNetworkAvailable(context)) {

            DialogManager dialogManager = new DialogManager();
            dialogManager.showProcessDialog(context, "", false, null);

            Call<ResponseBody> call = AppConfig.api_Interface().updateFavouriteDeal(productId);
            if (call != null) {
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String responseStr = response.body().string();
                                JSONObject resp = new JSONObject(responseStr);
                                Log.e("updateFavoriteProduct", resp.toString());

                                if (resp.getString("error_type").equals("200")) {
                                    ((Activity) context).runOnUiThread(() -> {
                                        dialogManager.stopProcessDialog();


                                            if (productFavourite == 0) {
                                                noticeBoardList.get(posi).setProductFavourite(1);
                                                holder.binding.btnFavorite.setImageDrawable(
                                                        context.getResources().getDrawable(R.drawable.ic_heart_red));
                                            } else if (productFavourite == 1) {
                                                noticeBoardList.get(posi).setProductFavourite(0);
                                                holder.binding.btnFavorite.setImageDrawable(
                                                        context.getResources().getDrawable(R.drawable.ic_favourite_black_new));
                                            }

                                    });
                                } else {
                                    if (KeyConstant.KEY_MESSAGE_FALSE.equalsIgnoreCase(resp.optString(KeyConstant.KEY_STATUS))) {
                                        dialogManager.stopProcessDialog();
                                       /* AppUtil.showMsgAlert(holder.binding.textExpiry,
                                                resp.optString(KeyConstant.KEY_MESSAGE));*/
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                dialogManager.stopProcessDialog();
                                Log.e("Exception", ">> " + e);
                                /*AppUtil.showMsgAlert(holder.binding.btnFavorite,
                                        MessageConstant.MESSAGE_SOMETHING_WRONG);*/
                            }
                        } else {
                            dialogManager.stopProcessDialog();
                           /* AppUtil.showMsgAlert(holder.binding.btnFavorite,
                                    MessageConstant.MESSAGE_SOMETHING_WRONG);*/
                            Log.e("sendToken", "Response code: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        ErrorMessage.E("ON FAILURE > " + t.getMessage());
                        dialogManager.stopProcessDialog();
                       /* AppUtil.showMsgAlert(holder.binding.btnFavorite, t.getMessage());*/
                    }
                });
            }

        } else {
            /*AppUtil.showMsgAlert(holder.binding.btnFavorite,
                    MessageConstant.MESSAGE_INTERNET_CONNECTION);*/
        }
    }

}
