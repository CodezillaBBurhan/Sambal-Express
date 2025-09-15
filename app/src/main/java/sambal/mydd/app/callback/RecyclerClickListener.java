package sambal.mydd.app.callback;

import org.json.JSONObject;

public interface RecyclerClickListener {
    void setCellClicked(JSONObject jsonObject, String eventHasMultipleParts);
}
