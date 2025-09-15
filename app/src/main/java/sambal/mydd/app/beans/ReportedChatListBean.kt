package sambal.mydd.app.beans

import android.os.Parcel
import android.os.Parcelable

class ReportedChatListBean(var reportId: String, var agentId: String, var userId: String, var userName: String, var reportedUserId: String, var reportedUserName: String, var message: String, var imageURL: String, var timeToken: String, var timeTokenEnd: String, var comments: String, var reportDate: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString(),
            parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reportId)
        parcel.writeString(agentId)
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(reportedUserId)
        parcel.writeString(reportedUserName)
        parcel.writeString(message)
        parcel.writeString(imageURL)
        parcel.writeString(timeToken)
        parcel.writeString(timeTokenEnd)
        parcel.writeString(comments)
        parcel.writeString(reportDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReportedChatListBean> {
        override fun createFromParcel(parcel: Parcel): ReportedChatListBean {
            return ReportedChatListBean(parcel)
        }

        override fun newArray(size: Int): Array<ReportedChatListBean?> {
            return arrayOfNulls(size)
        }
    }
}