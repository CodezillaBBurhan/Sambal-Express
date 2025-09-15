package sambal.mydd.app.callback;

import com.google.gson.JsonObject;
import com.pubnub.api.models.consumer.history.PNHistoryItemResult;

import java.util.List;

/**
 * Created by codezilla-11 on 6/9/17.
 */

public interface ChatHistoryCallback {

    void onRefreshHistoryList(List<PNHistoryItemResult> list);

    void clearData();

    void onRefreshChatList(JsonObject jsonObject);
}
