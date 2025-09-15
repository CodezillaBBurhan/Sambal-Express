package sambal.mydd.app.activity.reward_club;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import sambal.mydd.app.R;
import sambal.mydd.app.activity.BaseActivity;
import sambal.mydd.app.databinding.ActivityNoticeBoardDetailBinding;
import sambal.mydd.app.models.notice_board.NoticeBoard;
import sambal.mydd.app.utils.StatusBarcolor;

public class NoticeBoardDetailActivity extends BaseActivity {
ActivityNoticeBoardDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice_board_detail);
        setToolbarWithBackButton_colorprimary("Notice Board");
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null) {
            NoticeBoard category =(NoticeBoard)bundle.getSerializable("data");
            binding.title.setText(category.getAdsTitle());
            binding.description.setText(category.getAdsDescription());
            Glide.with(this).load(category.getAdsImage()).placeholder(R.drawable.placeholder_4_3).into(binding.product1);

        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        StatusBarcolor.INSTANCE.setStatusbarColor(this, "");
    }
    @Override
    protected int getContentResId() {
        return R.layout.activity_notice_board_detail;
    }
}