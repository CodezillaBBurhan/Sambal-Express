package sambal.mydd.app.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class Notification {
    @SerializedName("notificationId")
    @Expose
    var notificationId: Int? = null

    @SerializedName("notificationSubject")
    @Expose
    var notificationSubject: String? = null

    @SerializedName("notificationDescription")
    @Expose
    var notificationDescription: String? = null

    @SerializedName("notificationType")
    @Expose
    var notificationType: Int? = null

    @SerializedName("apPnotificationType")
    @Expose
    var apPnotificationType: String? = null

    @SerializedName("webPageURL")
    @Expose
    var webPageURL: String? = null

    @SerializedName("notificationTypeStatus")
    @Expose
    var notificationTypeStatus: String? = null

    @SerializedName("notificationMemberId")
    @Expose
    var notificationMemberId: Int? = null

    @SerializedName("notificationLocation")
    @Expose
    var notificationLocation: String? = null

    @SerializedName("notificationAgentName")
    @Expose
    var notificationAgentName: String? = null

    @SerializedName("notificationAgentId")
    @Expose
    var notificationAgentId: Int? = null

    @SerializedName("notificationCreatedDate")
    @Expose
    var notificationCreatedDate: String? = null

    @SerializedName("notificationStatus")
    @Expose
    var notificationStatus: Int? = null
}