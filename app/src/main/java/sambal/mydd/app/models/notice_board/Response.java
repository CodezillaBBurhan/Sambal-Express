package sambal.mydd.app.models.notice_board;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

    @SerializedName("noticeBoardList")
    @Expose
    private List<NoticeBoard> noticeBoardList;

    public List<NoticeBoard> getNoticeBoardList() {
        return noticeBoardList;
    }

    public void setNoticeBoardList(List<NoticeBoard> noticeBoardList) {
        this.noticeBoardList = noticeBoardList;
    }
}
