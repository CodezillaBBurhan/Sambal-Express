package sambal.mydd.app.beans

import org.json.JSONArray
import java.io.Serializable

class DealComments : Serializable {
    var commentsId: String? = null
    var userId: String? = null
    var userOwnComments: String? = null
    var userName: String? = null
    var userImage: String? = null
    var userRating: String? = null
    var userComments: String? = null
    var userCommentsDate: String? = null
    var mReplyList: JSONArray? = null

    constructor() {}
    constructor(
        commentsId: String?,
        userId: String?,
        userOwnComments: String?,
        userName: String?,
        userImage: String?,
        userRating: String?,
        userComments: String?,
        userCommentsDate: String?,
        mReplyList: JSONArray?
    ) {
        this.commentsId = commentsId
        this.userId = userId
        this.userOwnComments = userOwnComments
        this.userName = userName
        this.userImage = userImage
        this.userRating = userRating
        this.userComments = userComments
        this.userCommentsDate = userCommentsDate
        this.mReplyList = mReplyList
    }

    fun getmReplyList(): JSONArray? {
        return mReplyList
    }

    fun setmReplyList(mReplyList: JSONArray?) {
        this.mReplyList = mReplyList
    }
}