package sambal.mydd.app.adapter

import sambal.mydd.app.beans.AgentMainBean
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.app.Dialog
import android.text.TextUtils
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.os.Build
import com.squareup.picasso.Picasso
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import android.content.Intent
import sambal.mydd.app.models.RefreshCard
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import org.json.JSONObject
import sambal.mydd.app.constant.KeyConstant
import org.json.JSONException
import sambal.mydd.app.constant.MessageConstant
import sambal.mydd.app.beans.CharityListBean
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import sambal.mydd.app.R
import sambal.mydd.app.activity.*
import sambal.mydd.app.apiResponse.ApiResponse
import sambal.mydd.app.databinding.AdapstorepointsBinding
import sambal.mydd.app.utils.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.Exception
import java.util.ArrayList

class AdapterStorePoints(
    private val context: ActivityStorePoints,
    private val list: ArrayList<AgentMainBean>,
    recyclerView: RecyclerView?,
    var fragment: ActivityStorePoints
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var loading = false
    private var dialog1: Dialog? = null
    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    var dialogManager: DialogManager? = null
    private var adap: AdapterStprePointDealList? = null
    private var checkClick = true
    fun setLoaded() {
        loading = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val binding =
                AdapstorepointsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            vh = ViewHolder(binding)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.progressbar_item, parent, false)
            vh = ProgressViewHolder(v)
        }
        return vh
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            (holder as ProgressViewHolder).progressBar.visibility = View.GONE
        } catch (e: Exception) {
        }
        if (holder is ViewHolder) {
            try {
                val model = list[position]
                if(model.getmList()!=null && model.getmList().size>0){
                    holder.binding.voucherListRcv.visibility=View.VISIBLE

                    val adap = VoucherListAdapter(context,
                        model.getmList(),model.agentWalletType,model.agentId,fragment,"store")
                    holder.binding.voucherListRcv.layoutManager = LinearLayoutManager(
                        context,
                        LinearLayoutManager.HORIZONTAL, // Change this to HORIZONTAL
                        false
                    )
                    holder.binding.voucherListRcv.adapter = adap
                    holder.binding.voucherListRcv.isNestedScrollingEnabled = false
                    adap.notifyDataSetChanged()
                }else {
                    holder.binding.voucherListRcv.visibility=View.GONE
                }

                try {
                    Log.e("Sizess", model!!.getmList().size.toString() + "")
                    if (model.getmList().size > 0) {
                        if (model.agentNotificationCount != "" && model.agentNotificationCount != "0") {
                            holder.binding.llNoti.visibility = View.VISIBLE
                            holder.binding.tvNotiCounts.text = "" + model.agentNotificationCount
                        } else {
                            holder.binding.llNoti.visibility = View.GONE
                        }
                        if(model.getmList()!=null && model.getmList().size==1){
                            holder.binding.llVoucher.visibility = View.VISIBLE
                            holder.binding.voucherListRcv.visibility = View.GONE

                        }
                        else {
                            holder.binding.llVoucher.visibility = View.GONE
                            holder.binding.voucherListRcv.visibility = View.VISIBLE
                        }

                        holder.binding.viewDonate.visibility = View.VISIBLE
                        holder.binding.tvBalance.text =
                            model.getmList()[0].currency + model.getmList()[0].voucherPrice
                        holder.binding.tvAvailable.text = model.getmList()[0].voucherText
                        if (!TextUtils.isEmpty(model.getmList()[0].voucherRedeemedPrice) || model.getmList()[0].voucherRedeemedPrice != "") {
                            Log.e("voucherId >>", "" + model.getmList()[0].voucherId)
                            Log.e("voucherId >>", "" + model.getmList()[0].voucherRedeemedText)
                            holder.binding.tvRedeemedPrice.visibility = View.VISIBLE
                            holder.binding.tvRedeemedPrice.text =
                                model.getmList()[0].voucherRedeemedText + " " + model.getmList()[0].currency + "" + model.getmList()[0].voucherRedeemedPrice
                        } else {
                            holder.binding.tvRedeemedPrice.visibility = View.GONE
                        }
                        try {
                            if (model.getmList()[0].voucherRedeemEnabled.equals(
                                    "1",
                                    ignoreCase = true
                                )
                            ) {
                                holder.binding.llRedeem.visibility = View.VISIBLE
                                holder.binding.llRedeem.isEnabled = true
                                holder.binding.llRedeem.background =
                                    context.resources.getDrawable(R.drawable.llyellow)
                            } else {
                                holder.binding.llRedeem.visibility = View.VISIBLE
                                holder.binding.llRedeem.isEnabled = false
                                holder.binding.llRedeem.background =
                                    context.resources.getDrawable(R.drawable.llredeemgrey)
                            }
                        } catch (e: Exception) {
                            Log.e("Ex1", e.toString())
                        }
                        holder.binding.progressCircular.backgroundProgressBarColor = ContextCompat.getColor(
                            context, R.color.lightGrayColor
                        )
                        holder.binding.progressCircular.progressBarColor = Color.parseColor("#000000")
                    } else {
                        holder.binding.llVoucher.visibility = View.GONE
                        holder.binding.viewDonate.visibility = View.GONE
                        holder.binding.progressCircular.backgroundProgressBarColor = ContextCompat.getColor(
                            context, R.color.lightGrayColor
                        )
                        holder.binding.progressCircular.progressBarColor = Color.parseColor("#000000")
                    }
                } catch (e: Exception) {
                }
                if (model!!.agentWalletType == "1") {
                    holder.binding.progressCircular.visibility = View.VISIBLE
                    holder.binding.piechart1.visibility = View.GONE
                    holder.binding.tvPointRemarks.visibility = View.VISIBLE
                    holder.binding.frpie.visibility = View.GONE
                    holder.binding.lllAgentPoints.visibility = View.VISIBLE
                    holder.binding.llDoublePoint.visibility = View.VISIBLE
                    holder.binding.tvToday.visibility = View.VISIBLE
                    holder.binding.tvDealName.visibility = View.GONE
                    holder.binding.llVisits.visibility = View.GONE
                    holder.binding.lldon.visibility = View.GONE
                    holder.binding.tvDonated.visibility = View.GONE
                    holder.binding.llFree.visibility = View.GONE
                    holder.binding.tvPointRemarks.visibility = View.VISIBLE
                    holder.binding.tvPointRemarks.text = model.pointRemarks
                    if (model.charityEnabled == "1") {
                        if (model.donateStatus == "1") {
                            holder.binding.llDonate.visibility = View.VISIBLE
                            val param = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1.0f
                            )
                            holder.binding.tvDonated.visibility = View.VISIBLE
                            holder.binding.lldon.visibility = View.VISIBLE
                            holder.binding.lllAgentPoints.visibility = View.VISIBLE
                            holder.binding.tvDonate.visibility = View.VISIBLE
                            holder.binding.tvDonated.visibility = View.VISIBLE
                            holder.binding.tvDonated.text = model.charityDonatedText
                            holder.binding.tvDonate.isEnabled = true
                            holder.binding.tvDonate.text = "Donating"
                            holder.binding.cherityNameTv.visibility = View.VISIBLE
                            holder.binding.tvDonate.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.binding.donateBackgroungLayout.background =
                                context.resources.getDrawable(
                                    R.drawable.background_donate_pink
                                )
                            holder.binding.ivDonate.setImageDrawable(
                                context.resources.getDrawable(R.drawable.ic_donating)
                            )
                            if (model.redeemRemarks != "" && model.redeemRemarks != null) {
                                val redemRemark =
                                    model.redeemRemarks.split("=".toRegex()).toTypedArray()
                                holder.binding.tvAgentPointsStart.text = redemRemark[0] + "="
                                holder.binding.tvAgentPointsEnd.text = redemRemark[1]
                                holder.binding.tvAgentPointsEnd.setTextColor(Color.parseColor("#ff4a0b"))
                            }
                            if (model.userEarnedPoints.equals(
                                    "",
                                    ignoreCase = true
                                ) || TextUtils.isEmpty(
                                    model.userEarnedPoints
                                )
                            ) {
                                val text =
                                    "<font color=#ff4a0b>" + "0" + "</font><font color=#101010>" + "/" + model.targetPoints + "</font>"
                                holder.binding.tvUserEarned.text = Html.fromHtml(text)
                            } else {
                                val text =
                                    "<font color=#ff4a0b>" + model.userEarnedPoints + "</font><font color=#101010>" + "/" + model.targetPoints + "</font>"
                                holder.binding.tvUserEarned.text = Html.fromHtml(text)
                            }
                            holder.binding.progressCircular.backgroundProgressBarColor =
                                ContextCompat.getColor(
                                    context, R.color.lightGrayColor
                                )
                            holder.binding.progressCircular.progressBarColor = Color.parseColor("#ff4a0b")
                            val animationDuration = 2500 // 2500ms = 2,5s
                            try {
                                holder.binding.progressCircular.progressMax =
                                    model.targetPoints.toInt().toFloat()
                                holder.binding.progressCircular.setProgressWithAnimation(
                                    model.userEarnedPoints.toInt().toFloat(), animationDuration.toLong()
                                )
                            } catch (e: Exception) {
                            }
                            holder.binding.tvMainPoints.text = "Points"
                            holder.binding.tvMainPoints.setTextColor(Color.parseColor("#8d8d8d"))
                            try {
                                holder.binding.cherityNameTv.visibility = View.VISIBLE
                                val builder = SpannableStringBuilder()
                                val str3 = SpannableString(model.charityName + " ")
                                str3.setSpan(
                                    ForegroundColorSpan(context.resources.getColor(R.color.black)),
                                    0,
                                    str3.length,
                                    0
                                )
                                builder.append(str3)
                                val str6 = SpannableString(model.charityMemberCount)
                                str6.setSpan(
                                    ForegroundColorSpan(Color.parseColor("#007cfa")),
                                    0,
                                    str6.length,
                                    0
                                )
                                builder.append(str6)
                                holder.binding.cherityNameTv.setText(
                                    builder,
                                    TextView.BufferType.SPANNABLE
                                )
                            } catch (e: Exception) {
                            }
                            try {
                                if (model.agentRecommendText != null && model.agentRecommendText != "") {
                                    holder.binding.recommandedFriendTextTv.visibility = View.VISIBLE
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        holder.binding.recommandedFriendTextTv.text = Html.fromHtml(
                                            model.agentRecommendText, Html.FROM_HTML_MODE_COMPACT
                                        )
                                    } else {
                                        holder.binding.recommandedFriendTextTv.text = Html.fromHtml(
                                            model.agentRecommendText
                                        )
                                    }
                                } else {
                                    holder.binding.recommandedFriendTextTv.visibility = View.GONE
                                }
                            } catch (e: Exception) {
                            }
                        } else {
                            holder.binding.lllAgentPoints.visibility = View.VISIBLE
                            holder.binding.lldon.visibility = View.VISIBLE
                            holder.binding.llDonate.visibility = View.VISIBLE
                            holder.binding.tvDonated.visibility = View.VISIBLE
                            holder.binding.tvDonated.text = model.charityDonatedText
                            holder.binding.tvDonate.isEnabled = true
                            if (model.redeemRemarks != "" && model.redeemRemarks != null) {
                                val redemRemark =
                                    model.redeemRemarks.split("=".toRegex()).toTypedArray()
                                Log.e("redeeme", redemRemark.toString() + "")
                                holder.binding.tvAgentPointsStart.text = redemRemark[0] + "="
                                holder.binding.tvAgentPointsEnd.text = redemRemark[1]
                                holder.binding.tvAgentPointsEnd.setTextColor(Color.parseColor("#000000"))
                            }
                            holder.binding.tvDonate.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.binding.cherityNameTv.visibility = View.GONE
                            holder.binding.tvDonate.text = "Donate"
                            holder.binding.donateBackgroungLayout.background =
                                context.resources.getDrawable(
                                    R.drawable.background_donate_green
                                )
                            holder.binding.ivDonate.setImageDrawable(
                                context.resources.getDrawable(R.drawable.heartfulled)
                            )
                            if (model.userEarnedPoints.equals(
                                    "",
                                    ignoreCase = true
                                ) || TextUtils.isEmpty(
                                    model.userEarnedPoints
                                )
                            ) {
                                val text =
                                    "<font color=#000000>" + "0" + "</font><font color=#101010>" + "/" + model.targetPoints + "</font>"
                                holder.binding.tvUserEarned.text = Html.fromHtml(text)
                            } else {
                                val text =
                                    "<font color=#000000>" + model.userEarnedPoints + "</font><font color=#101010>" + "/" + model.targetPoints + "</font>"
                                holder.binding.tvUserEarned.text = Html.fromHtml(text)
                            }
                            holder.binding.progressCircular.backgroundProgressBarColor =
                                ContextCompat.getColor(
                                    context, R.color.lightGrayColor
                                )
                            holder.binding.progressCircular.progressBarColor = Color.parseColor("#000000")
                            val animationDuration = 2500 // 2500ms = 2,5s
                            try {
                                holder.binding.progressCircular.progressMax =
                                    model.targetPoints.toInt().toFloat()
                                holder.binding.progressCircular.setProgressWithAnimation(
                                    model.userEarnedPoints.toInt().toFloat(), animationDuration.toLong()
                                )
                            } catch (e: Exception) {
                            }
                        }
                    } else {
                        holder.binding.cherityNameTv.visibility = View.GONE
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            2.0f
                        )
                        holder.binding.llReco.gravity = Gravity.CENTER
                        holder.binding.recommandedFriendTextTv.gravity = Gravity.CENTER
                        holder.binding.recommendedLayout.gravity = Gravity.CENTER
                        holder.binding.lldon.visibility = View.INVISIBLE
                        holder.binding.tvDonated.visibility = View.GONE
                        holder.binding.llDonate.visibility = View.GONE
                        holder.binding.viewDonate.visibility = View.VISIBLE
                        holder.binding.tvDonate.isEnabled = false
                        holder.binding.viewDonate.visibility = View.GONE
                        holder.binding.tvPointRemarks.visibility = View.VISIBLE
                        if (model.redeemRemarks != "" && model.redeemRemarks != null) {
                            val redemRemark =
                                model.redeemRemarks.split("=".toRegex()).toTypedArray()
                            holder.binding.tvAgentPointsStart.text = redemRemark[0] + "="
                            holder.binding.tvAgentPointsEnd.text = redemRemark[1]
                            holder.binding.tvAgentPointsEnd.setTextColor(Color.parseColor("#000000"))
                        }
                        if (model.userEarnedPoints.equals(
                                "",
                                ignoreCase = true
                            ) || TextUtils.isEmpty(
                                model.userEarnedPoints
                            )
                        ) {
                            val text =
                                "<font color=#000000>" + "0" + "</font><font color=#101010>" + "/" + model.targetPoints + "</font>"
                            holder.binding.tvUserEarned.text = Html.fromHtml(text)
                        } else {
                            val text =
                                "<font color=#000000>" + model.userEarnedPoints + "</font><font color=#101010>" + "/" + model.targetPoints + "</font>"
                            holder.binding.tvUserEarned.text = Html.fromHtml(text)
                        }
                        holder.binding.tvMainPoints.text = "Points"
                        holder.binding.tvMainPoints.setTextColor(Color.parseColor("#8d8d8d"))
                        holder.binding.progressCircular.backgroundProgressBarColor = ContextCompat.getColor(
                            context, R.color.lightGrayColor
                        )
                        val animationDuration = 2500 // 2500ms = 2,5s
                        try {
                            holder.binding.progressCircular.progressMax =
                                model.targetPoints.toInt().toFloat()
                            holder.binding.progressCircular.setProgressWithAnimation(
                                model.userEarnedPoints.toInt().toFloat(), animationDuration.toLong()
                            )
                        } catch (e: Exception) {
                        }
                    }
                    if (model.agentStandardPointStatus.equals("1", ignoreCase = true)) {
                        holder.binding.llDoublePoint.visibility = View.VISIBLE
                        holder.binding.tvDoublePoints.text = " Standard Points "
                        holder.binding.tvMininimum.visibility = View.GONE
                    } else if (model.agentDoublePointStatus.equals("1", ignoreCase = true)) {
                        holder.binding.llDoublePoint.visibility = View.VISIBLE
                        holder.binding.tvDoublePoints.text = " Double Points "
                        holder.binding.tvMininimum.visibility = View.VISIBLE
                    } else if (model.agentBonusPointStatus.equals("1", ignoreCase = true)) {
                        holder.binding.llDoublePoint.visibility = View.VISIBLE
                        holder.binding.tvDoublePoints.text =
                            " Bonus Points " + model.agentBonusPoint + " "
                        holder.binding.tvMininimum.visibility = View.VISIBLE
                    } else {
                        holder.binding.llDoublePoint.visibility = View.GONE
                        holder.binding.tvMininimum.visibility = View.GONE
                    }
                } else {
                    if (model.charityEnabled == "1") {
                        if (model.donateStatus == "1") {
                            holder.binding.viewDonate.visibility = View.VISIBLE
                            holder.binding.llDonate.visibility = View.VISIBLE
                            val param = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1.0f
                            )
                            holder.binding.lldon.visibility = View.VISIBLE
                            holder.binding.lllAgentPoints.visibility = View.VISIBLE
                            holder.binding.tvDonate.isEnabled = true
                            holder.binding.tvDonate.text = "Donating"
                            holder.binding.tvDonate.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.binding.donateBackgroungLayout.background =
                                context.resources.getDrawable(
                                    R.drawable.background_donate_pink
                                )
                            holder.binding.ivDonate.setImageDrawable(
                                context.resources.getDrawable(R.drawable.ic_donating)
                            )
                            try {
                                holder.binding.cherityNameTv.visibility = View.VISIBLE
                                val builder = SpannableStringBuilder()
                                val str3 = SpannableString(model.charityName + " ")
                                str3.setSpan(
                                    ForegroundColorSpan(context.resources.getColor(R.color.black)),
                                    0,
                                    str3.length,
                                    0
                                )
                                builder.append(str3)
                                val str6 = SpannableString(model.charityMemberCount)
                                str6.setSpan(
                                    ForegroundColorSpan(Color.parseColor("#007cfa")),
                                    0,
                                    str6.length,
                                    0
                                )
                                builder.append(str6)
                                holder.binding.cherityNameTv.setText(
                                    builder,
                                    TextView.BufferType.SPANNABLE
                                )
                            } catch (e: Exception) {
                            }
                        } else {
                            holder.binding.lllAgentPoints.visibility = View.VISIBLE
                            holder.binding.viewDonate.visibility = View.VISIBLE
                            holder.binding.llDonate.visibility = View.VISIBLE
                            val param = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1.0f
                            )
                            holder.binding.lldon.visibility = View.VISIBLE
                            holder.binding.tvDonate.isEnabled = true
                            if (model.redeemRemarks != "" && model.redeemRemarks != null) {
                                val redemRemark =
                                    model.redeemRemarks.split("=".toRegex()).toTypedArray()
                                Log.e("redeeme", redemRemark.toString() + "")
                                holder.binding.tvAgentPointsStart.text = redemRemark[0] + "="
                                holder.binding.tvAgentPointsEnd.text = redemRemark[1]
                                holder.binding.tvAgentPointsEnd.setTextColor(Color.parseColor("#000000"))
                            }
                            holder.binding.tvDonate.setTextColor(Color.parseColor("#FFFFFF"))
                            holder.binding.tvDonate.text = "Donate"
                            holder.binding.cherityNameTv.visibility = View.GONE
                            holder.binding.donateBackgroungLayout.background =
                                context.resources.getDrawable(
                                    R.drawable.background_donate_green
                                )
                            holder.binding.ivDonate.setImageDrawable(
                                context.resources.getDrawable(R.drawable.heartfulled)
                            )
                        }
                    } else {
                        holder.binding.cherityNameTv.visibility = View.GONE
                        holder.binding.recommandedFriendTextTv.gravity = Gravity.CENTER
                        holder.binding.recommendedLayout.gravity = Gravity.CENTER
                        holder.binding.progressCircular.backgroundProgressBarColor = ContextCompat.getColor(
                            context, R.color.lightGrayColor
                        )
                        holder.binding.progressCircular.progressBarColor = Color.parseColor("#000000")
                    }
                    holder.binding.progressCircular.visibility = View.GONE
                    holder.binding.piechart1.visibility = View.VISIBLE
                    holder.binding.llVisits.visibility = View.VISIBLE
                    holder.binding.frpie.visibility = View.VISIBLE
                    holder.binding.circleView.visibility = View.GONE
                    holder.binding.llDoublePoint.visibility = View.GONE
                    holder.binding.viewDonate.visibility = View.GONE
                    holder.binding.tvToday.visibility = View.GONE
                    holder.binding.tvToday.visibility = View.GONE
                    holder.binding.tvPointRemarks.visibility = View.VISIBLE
                    holder.binding.tvPointRemarks.text = model.minspend.toString()
                    if (model.agentUserFreeDealStatus.equals("0", ignoreCase = true)) {
                        holder.binding.llFree.visibility = View.GONE
                    } else {
                        holder.binding.llFree.visibility = View.VISIBLE
                        holder.binding.tvFree.text = model.agentUserFreeDealText
                    }
                    try {
                        Picasso.with(context)
                            .load(model.agentUserFreeDealImage)
                            .placeholder(R.drawable.mainimageplaceholder)
                            .error(R.drawable.mainimageplaceholder)
                            .into(holder.binding.cardBgImg)
                    } catch (e: Exception) {
                    }
                    val yValues = ArrayList<PieEntry>()
                    val colors = ArrayList<Int>()
                    yValues.clear()
                    colors.clear()
                    for (i in model.getmVisitcolorList().indices) {
                        yValues.add(PieEntry(1f, ""))
                        colors.add(Color.parseColor(model.getmVisitcolorList()[i].visitColor))
                    }
                    holder.binding.piechart1.setDrawSliceText(false)
                    holder.binding.piechart1.dragDecelerationFrictionCoef = 0.9f
                    holder.binding.piechart1.transparentCircleRadius = 0f
                    holder.binding.piechart1.centerText = ""
                    holder.binding.piechart1.isRotationEnabled = false
                    holder.binding.piechart1.setDrawCenterText(false)
                    holder.binding.piechart1.setDrawEntryLabels(false)
                    holder.binding.piechart1.setDrawMarkers(false)
                    holder.binding.piechart1.description.isEnabled = false
                    holder.binding.piechart1.legend.isEnabled = false
                    val dataSet = PieDataSet(yValues, "1")
                    dataSet.sliceSpace = 10f
                    dataSet.selectionShift = 10f
                    dataSet.colors = colors
                    dataSet.formLineWidth = 10f
                    val pieData = PieData(dataSet)
                    dataSet.setDrawValues(false)
                    holder.binding.piechart1.data = pieData
                    try {
                        Log.e("Models", model.redeemRemarks)
                        if (model.redeemRemarks != "" && model.redeemRemarks != null) {
                            val redemRemark =
                                model.redeemRemarks.split("=".toRegex()).toTypedArray()
                            Log.e("redeeme", redemRemark.toString() + "")
                            holder.binding.tvAgentPointsStart.text = redemRemark[0] + "="
                            holder.binding.tvAgentPointsEnd.text = redemRemark[1]
                            holder.binding.tvAgentPointsEnd.setTextColor(Color.parseColor("#000000"))
                        }
                    } catch (e: Exception) {
                    }
                    holder.binding.tvAgentPointsEnd.setTextColor(Color.parseColor("#000000"))
                    val target = model.agentUserTargetVisitCount.toInt()
                    val userDeals = model.agentUserVisitCount.toInt()
                    val total = target - userDeals
                    holder.binding.tvUserEarned.text =
                        total.toString() + "".replace("-".toRegex(), "")
                    holder.binding.tvUserEarned.textSize = 18f
                    holder.binding.tvMainPoints.text = "Visits to go"
                    holder.binding.tvMainPoints.setTextColor(Color.parseColor("#101010"))
                    holder.binding.tvDoublePoints.text = model.agentUserTodayVisit
                    holder.binding.tvDealName.visibility = View.VISIBLE
                    holder.binding.tvDealName.text = model.agentUserFreeDealName
                    if (model.getmVisitcolorList().size > 0) {
                        for (i in model.getmVisitcolorList().indices) {
                            holder.binding.circleView.setBarColor(
                                Color.parseColor(
                                    model.getmVisitcolorList()[i].visitColor
                                )
                            )
                        }
                    }
                    holder.binding.llVisits.setOnClickListener {
                        context.startActivity(
                            Intent(context, LatestProductDetails::class.java)
                                .putExtra("agentId", model.agentId)
                                .putExtra("direct", "false")
                                .putExtra("product_id", model.agentUserFreeDealID)
                                .putExtra("pos", position)
                        )
                    }
                    holder.binding.btnLocked.setOnClickListener { holder.binding.llVisits.performClick() }
                    holder.binding.llFree.setOnClickListener {
                        context.startActivity(
                            Intent(
                                context,
                                ExclusiveDeals::class.java
                            ).putExtra("agentId", model.agentId)
                        )
                    }
                    if (model.agentUserDealStatus.equals("1", ignoreCase = true)) {
                        holder.binding.llVisits.visibility = View.VISIBLE
                    } else {
                        holder.binding.llVisits.visibility = View.GONE
                    }
                }
                holder.binding.llRedeem.setOnClickListener {
                    if (model.agentWalletType.equals("1", ignoreCase = true)) {
                        holder.binding.llRedeem.isEnabled = false
                        redeemVoucher(
                            model.getmList()[0].voucherId,
                            model.agentId,
                            model.getmList()[0].voucherPrice,
                            true,
                            holder.binding.tvAgentCompanyName
                        ) { holder.binding.llRedeem.isEnabled = true }
                    } else {
                        context.startActivity(
                            Intent(context, ScanQrVisitBusiness::class.java)
                                .putExtra("UUID", model.getmList()[0].voucherUUID)
                                .putExtra("amount", model.getmList()[0].voucherPrice)
                                .putExtra("currency", model.getmList()[0].currency)
                                .putExtra("storevouchers", true)
                        )
                    }
                }
                if (model.agentNotificationEnabled.equals("1", ignoreCase = true)) {
                    holder.binding.ivNoti.visibility = View.VISIBLE
                } else {
                    holder.binding.ivNoti.visibility = View.GONE
                }
                Log.e("MembershipStatus", ">>" + model.membershipStatus)
                if (model.membershipStatus == "1") {
                    holder.binding.membershipImg.visibility = View.VISIBLE
                    Picasso.with(context)
                        .load(model.membershipImage)
                        .placeholder(R.drawable.mainimageplaceholder)
                        .error(R.drawable.mainimageplaceholder)
                        .into(holder.binding.membershipImg)
                } else {
                    holder.binding.membershipImg.visibility = View.GONE
                }
                holder.binding.tvAgentCompanyName.text = model.agentCompanyName
                val textDistance =
                    "<font color=#101010>" + model.agentAddress + "</font> <font color=#007cfa>" + " (" + model.distance + ")" + "</font>"
                holder.binding.tvagentAddress.text = Html.fromHtml(textDistance)
                holder.binding.tvDonated.text = model.charityDonatedText
                holder.binding.tvMininimum.text = model.minspend.toString()
                if (model.getmDealList() != null && model.getmDealList().size > 0) {
                    Log.e("dealsize>", "" + position + "<>" + model.getmDealList().size)
                    holder.binding.lloffer.visibility = View.VISIBLE
                    holder.binding.viewDonate.visibility = View.VISIBLE
                    adap = AdapterStprePointDealList(context, model.getmDealList(), position)
                    holder.binding.rvOffer.layoutManager = LinearLayoutManager(
                        context, LinearLayoutManager.HORIZONTAL, false
                    )
                    holder.binding.rvOffer.setHasFixedSize(true)
                    holder.binding.rvOffer.setItemViewCacheSize(
                        model.getmDealList().size
                    )
                    holder.binding.rvOffer.adapter = adap
                } else {
                    holder.binding.viewDonate.visibility = View.GONE
                    holder.binding.lloffer.visibility = View.GONE
                }
                holder.binding.llDoublePoint.setOnClickListener { fragment.showFAQDialog(model.agentPointsFAQ) }
                holder.binding.tvDonate.setOnClickListener {
                    holder.binding.tvDonate.isEnabled = false
                    holder.binding.tvDonate.isClickable = false
                    if (checkClick) {
                        checkClick = false
                        getCharityList(
                            model.agentId,
                            holder.binding.tvDonate
                        )
                    }
                }
                holder.binding.llTc.setOnClickListener {
                    Log.e("TC", "TC")
                    val dialog1 = Dialog(context, R.style.NewDialog)
                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog1.setContentView(R.layout.ddtermsconditions)
                    val lp = WindowManager.LayoutParams()
                    lp.copyFrom(dialog1.window!!.attributes)
                    lp.width = WindowManager.LayoutParams.FILL_PARENT
                    lp.height = WindowManager.LayoutParams.FILL_PARENT
                    dialog1.window!!.attributes = lp
                    val ivClose = dialog1.findViewById<ImageView>(R.id.ivClose)
                    ivClose.setOnClickListener { dialog1.dismiss() }
                    val tvTerms = dialog1.findViewById<TextView>(R.id.tvTerms)
                    tvTerms.text = model.termsAndConditions
                    dialog1.show()
                }
                holder.binding.tvReco.setOnClickListener {


                    if (AppUtil.isNetworkAvailable(context)) {

                        context.startActivity(
                            Intent(context, Refer_FriendActivity::class.java)
                                .putExtra("id", model.agentId).putExtra("check", "my_wallet")
                        )
                    }

                    else{
                        AppUtil.showMsgAlert(
                            holder.itemView,
                            MessageConstant.MESSAGE_INTERNET_CONNECTION
                        )
                    }



                }
                holder.binding.tvAgentCompanyName.setOnClickListener {
                    context.goToAgentDetailPage(
                        model.agentId, position
                    )
                }
                holder.binding.ivNoti.setOnClickListener {
                    val i = Intent(context, NewNotification::class.java)
                    i.putExtra("agentId", model.agentId)
                    i.putExtra("title", model.agentCompanyName)
                    i.putExtra("check", "MyWallete")
                    context.startActivityForResult(i, 100)
                }
                if (model.agentRecommendEnabled == "0") {
                    holder.binding.tvReco.visibility = View.GONE
                    holder.binding.ivReco.visibility = View.GONE
                    holder.binding.llReco.visibility = View.GONE
                    if (holder.binding.llDonate.visibility == View.VISIBLE) {
                        holder.binding.lldon.visibility = View.VISIBLE
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                        )
                        holder.binding.llReco.layoutParams = param
                        holder.binding.recommandedFriendTextTv.gravity = Gravity.START
                        holder.binding.recommendedLayout.gravity = Gravity.START
                        holder.binding.llReco.gravity = Gravity.START
                    } else {
                        holder.binding.lldon.visibility = View.GONE
                    }
                } else if (model.charityEnabled == "1" && model.agentRecommendEnabled == "1") {
                    holder.binding.tvReco.visibility = View.VISIBLE
                    holder.binding.ivReco.visibility = View.GONE
                    holder.binding.llReco.visibility = View.VISIBLE
                    holder.binding.llDonate.visibility = View.VISIBLE
                    holder.binding.lldon.visibility = View.VISIBLE
                    val param = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                    )
                    holder.binding.llReco.layoutParams = param
                    holder.binding.llDonate.layoutParams = param
                    holder.binding.recommandedFriendTextTv.gravity = Gravity.START
                    holder.binding.recommendedLayout.gravity = Gravity.START
                    holder.binding.llReco.gravity = Gravity.START
                } else if (model.charityEnabled == "0" && model.agentRecommendEnabled == "1") {
                    holder.binding.tvReco.visibility = View.VISIBLE
                    holder.binding.ivReco.visibility = View.GONE
                    holder.binding.llReco.visibility = View.VISIBLE
                    holder.binding.llDonate.visibility = View.GONE
                    holder.binding.lldon.visibility = View.GONE
                    if (holder.binding.llDonate.visibility == View.GONE) {
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            2.0f
                        )
                        holder.binding.llReco.layoutParams = param
                        holder.binding.llReco.gravity = Gravity.CENTER
                        holder.binding.recommandedFriendTextTv.gravity = Gravity.CENTER
                        holder.binding.recommendedLayout.gravity = Gravity.CENTER
                    } else {
                        val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f
                        )
                    }
                    holder.binding.lldon.visibility = View.VISIBLE
                    try {
                        if (model.agentRecommendText != null && model.agentRecommendText != "") {
                            holder.binding.recommandedFriendTextTv.visibility = View.VISIBLE
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                holder.binding.recommandedFriendTextTv.text = Html.fromHtml(
                                    model.agentRecommendText, Html.FROM_HTML_MODE_COMPACT
                                )
                            } else {
                                holder.binding.recommandedFriendTextTv.text = Html.fromHtml(
                                    model.agentRecommendText
                                )
                            }
                        } else {
                            holder.binding.recommandedFriendTextTv.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                    }
                }
                if (TextUtils.isEmpty(model.charityDonatedText)) {
                    holder.binding.tvDonated.visibility = View.GONE
                } else {
                    holder.binding.tvDonated.visibility = View.VISIBLE
                    holder.binding.tvDonated.text = model.charityDonatedText
                }
                try {
                    if (model.agentRecommendText != null && model.agentRecommendText != "") {
                        holder.binding.recommandedFriendTextTv.visibility = View.VISIBLE
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            holder.binding.recommandedFriendTextTv.text = Html.fromHtml(
                                model.agentRecommendText, Html.FROM_HTML_MODE_COMPACT
                            )
                        } else {
                            holder.binding.recommandedFriendTextTv.text = Html.fromHtml(
                                model.agentRecommendText
                            )
                        }
                    } else {
                        holder.binding.recommandedFriendTextTv.visibility = View.GONE
                    }
                } catch (e: Exception) {
                }
                try {
                    if (model.giftCardslist.size > 0) {
                        holder.binding.giftcardLayout.visibility = View.VISIBLE
                        val listview_adater = WalletGiftCardAdapter(
                            context, model.giftCardslist
                        )
                        holder.binding.giftCardRcv.layoutManager = LinearLayoutManager(
                            context, RecyclerView.HORIZONTAL, false
                        )
                        holder.binding.giftCardRcv.itemAnimator = DefaultItemAnimator()
                        holder.binding.giftCardRcv.scheduleLayoutAnimation()
                        holder.binding.giftCardRcv.isNestedScrollingEnabled = false
                        holder.binding.giftCardRcv.setItemViewCacheSize(
                            model.giftCardslist.size
                        )
                        holder.binding.giftCardRcv.adapter = listview_adater
                        listview_adater.notifyDataSetChanged()
                    } else {
                        holder.binding.giftcardLayout.visibility = View.GONE
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
                Log.e("Ex", e.toString())
            }
        } else if (holder is ProgressViewHolder) {
            holder.progressBar.visibility = View.GONE
            holder.progressBar.isIndeterminate = true
        }
        try {
            Glide.with(context)
                .load(context.resources.getDrawable(R.drawable.r_frnd))
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder) // show error drawable if the image is not a gif
                .into((holder as ViewHolder).binding.ivReco)
        } catch (e: Exception) {
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun redeemVoucher(
        voucherId: String,
        agentId: String,
        voucherPrice: String,
        b: Boolean,
        tvAgentName: TextView,
        refreshCard: RefreshCard
    ) {
        dialogManager = DialogManager()
        if (AppUtil.isNetworkAvailable(context)) {
            if (b) {
                dialogManager!!.showProcessDialog(context, "", false, null)
            }
            val call = AppConfig.api_Interface().getVoucherRedeem(voucherId, agentId, voucherPrice)
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val resp = JSONObject(response.body()!!.string())
                            Log.e("getVoucherRedeem ", resp.toString() + "")
                            val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                            if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                                val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                                dialogManager!!.stopProcessDialog()
                                fragment.showQRCODE(responseObj, refreshCard)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                tvAgentName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                            dialogManager!!.stopProcessDialog()
                            AppUtil.showMsgAlert(
                                tvAgentName,
                                MessageConstant.MESSAGE_SOMETHING_WRONG
                            )
                        }
                    } else {
                        dialogManager!!.stopProcessDialog()
                        AppUtil.showMsgAlert(tvAgentName, MessageConstant.MESSAGE_SOMETHING_WRONG)
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager!!.stopProcessDialog()
                    AppUtil.showMsgAlert(tvAgentName, t.message)
                }
            })
        } else {
            AppUtil.showMsgAlert(tvAgentName, MessageConstant.MESSAGE_INTERNET_CONNECTION)
        }
    }


    private fun getCharityList(agentId: String?,tvDonate: TextView) {
        val apiResponse: ApiResponse = object : ApiResponse() {}
        apiResponse.getCharityList(context, agentId,tvDonate,object : ResponseListener {

            override fun onSuccess(response: ResponseBody?) {
                try {
                    val resp = JSONObject(response!!.string())
                    Log.e("getCharityList", resp.toString() + "")
                    val errorType = resp.optString(KeyConstant.KEY_ERROR_TYPE)
                    if (errorType == KeyConstant.KEY_RESPONSE_CODE_200) {
                        val responseObj = resp.optJSONObject(KeyConstant.KEY_RESPONSE)
                        val mList = ArrayList<CharityListBean>()
                        mList.clear()
                        val ar = responseObj.optJSONArray("charityList")
                        for (i in 0 until ar.length()) {
                            val obj = ar.optJSONObject(i)
                            val charityId = obj.optString("charityId")
                            val charityName = obj.optString("charityName")
                            val charitySubName = obj.optString("charitySubName")
                            val charityStatus = obj.optString("charityStatus")
                            val charityJoinedText = obj.optString("charityJoinedText")
                            val charityWebURL = obj.optString("charityWebURL")
                            val charityImage = obj.optString("charityImage")
                            val charityDescription = obj.optString("charityDescription")
                            val cn = CharityListBean(
                                charityId,
                                charityName,
                                charitySubName,
                                charityStatus,
                                charityJoinedText,
                                charityWebURL,
                                charityImage,
                                charityDescription,
                                false
                            )
                            mList.add(cn)
                        }
                        checkClick = true
                        tvDonate.isEnabled = true
                        tvDonate.isClickable = true
                        context.runOnUiThread {
                            isChange = false
                            if (dialog1 == null) {
                                dialog1 = Dialog(context, R.style.NewDialog)
                                dialog1!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                                dialog1!!.setContentView(R.layout.dialogcharity)
                                val lp = WindowManager.LayoutParams()
                                lp.copyFrom(dialog1!!.window!!.attributes)
                                lp.width = WindowManager.LayoutParams.FILL_PARENT
                                lp.height = WindowManager.LayoutParams.FILL_PARENT
                                dialog1!!.window!!.attributes = lp
                                val ivClose =
                                    dialog1!!.findViewById<ImageView>(R.id.ivClose)
                                ivClose.setOnClickListener {
                                    Log.e("isCha", isChange.toString() + "")
                                    charity_id = ""
                                    fragment.getCharityList("")
                                    dialog1!!.dismiss()
                                    dialog1 = null
                                }
                                val rv =
                                    dialog1!!.findViewById<RecyclerView>(R.id.rvCharitylist)
                                val tvPoints =
                                    dialog1!!.findViewById<TextView>(R.id.tvPoints)
                                val tvP = dialog1!!.findViewById<TextView>(R.id.tvP)
                                val tvAgree = dialog1!!.findViewById<TextView>(R.id.tvAgree)
                                val tvCharity =
                                    dialog1!!.findViewById<TextView>(R.id.tvCharity)
                                val cbAgr = dialog1!!.findViewById<CheckBox>(R.id.cbAgr)
                                val btnDonate =
                                    dialog1!!.findViewById<Button>(R.id.btnDonate)
                                tvAgree.setOnClickListener {
                                    context.startActivity(
                                        Intent(context, Webview::class.java)
                                            .putExtra(
                                                "url",
                                                responseObj.optString("termsAndConditionsURL")
                                            )
                                            .putExtra("type", "non_direct")
                                            .putExtra("title", "Terms & Conditions")
                                    )
                                }
                                tvPoints.text =
                                    "(" + responseObj.optString("redeemRemarks") + ")"
                                val cbcollectmy =
                                    dialog1!!.findViewById<RadioButton>(R.id.cbcollectmy)
                                if (responseObj.optString("charityStatus")
                                        .equals("1", ignoreCase = true)
                                ) {
                                    cbcollectmy.isChecked = true
                                    fragment.getCharityList(responseObj.optString("charityId"))
                                    charity_id = responseObj.optString("charityId")
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            cbcollectmy.buttonTintList =
                                                ColorStateList.valueOf(
                                                    ContextCompat.getColor(
                                                        context, R.color.colorPrimary
                                                    )
                                                )
                                        }
                                    } catch (e: Exception) {
                                    }
                                } else {
                                    cbcollectmy.isChecked = false
                                    try {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            cbcollectmy.buttonTintList =
                                                ColorStateList.valueOf(
                                                    ContextCompat.getColor(
                                                        context, R.color.defult_background
                                                    )
                                                )
                                        }
                                    } catch (e: Exception) {
                                    }
                                }
                                tvP.text = responseObj.optString("myswlfText")
                                tvCharity.text = responseObj.optString("charityTitleText")
                                btnDonate.setOnClickListener(View.OnClickListener {
                                    if (charity_id.equals("", ignoreCase = true)) {
                                        AppUtil.showMsgAlert(
                                            tvP,
                                            "Select One of the charity"
                                        )
                                        return@OnClickListener
                                    } else if (!cbAgr.isChecked) {
                                        AppUtil.showMsgAlert(
                                            tvP,
                                            "Agree terms and conditions"
                                        )
                                        return@OnClickListener
                                    } else if (!AppUtil.isNetworkAvailable(context)) {
                                        AppUtil.showMsgAlert(
                                            tvP,
                                            MessageConstant.MESSAGE_INTERNET_CONNECTION
                                        )
                                    } else {
                                        fragment.doCharity(agentId!!, charity_id)
                                        if (dialog1 != null) {
                                            dialog1!!.dismiss()
                                            dialog1 = null
                                        }
                                    }
                                })
                                val adap = AdapterCharityListStorePoints(
                                    context, mList, fragment, cbcollectmy
                                )
                                rv.layoutManager = LinearLayoutManager(
                                    context,
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                                rv.adapter = adap
                                cbcollectmy.setOnCheckedChangeListener { compoundButton, b ->
                                    if (b) {
                                        for (i in mList.indices) {
                                            mList[i].isChecked = false
                                            mList[i].charityStatus = "0"
                                            fragment.getCharityList(responseObj.optString("charityId"))
                                            charity_id = responseObj.optString("charityId")
                                            val adap = AdapterCharityListStorePoints(
                                                context, mList, fragment, cbcollectmy
                                            )
                                            rv.layoutManager = LinearLayoutManager(
                                                context, LinearLayoutManager.VERTICAL, false
                                            )
                                            rv.adapter = adap
                                        }
                                    } else {
                                    }
                                }
                                dialog1!!.show()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.e("Ex", e.toString())
                    AppUtil.showMsgAlert(tvDonate, MessageConstant.MESSAGE_SOMETHING_WRONG)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e("Ex", e.toString())
                    AppUtil.showMsgAlert(tvDonate, MessageConstant.MESSAGE_SOMETHING_WRONG)
                }
            }

            override fun onFailure(text: String?) {
                ErrorMessage.E("ON FAILURE > " + text)
                AppUtil.showMsgAlert(tvDonate, text)
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] != null) VIEW_ITEM else VIEW_PROG
    }

    private inner class ProgressViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar

        init {
            progressBar = v.findViewById(R.id.progressBar1)
            progressBar.visibility = View.GONE
        }
    }

    private inner class ViewHolder internal constructor(var binding: AdapstorepointsBinding) :
        RecyclerView.ViewHolder(binding.getRoot())

    companion object {
        @JvmField
        var isChange = false
        @JvmField
        var charity_id = ""
    }
}