package sambal.mydd.app.adapter

import android.app.Dialog
import sambal.mydd.app.MainActivity
import sambal.mydd.app.beans.MenuList
import androidx.recyclerview.widget.RecyclerView
import sambal.mydd.app.utils.PreferenceHelper
import sambal.mydd.app.utils.SharedPreferenceVariable
import sambal.mydd.app.constant.KeyConstant
import android.content.Intent
import android.graphics.Color
import sambal.mydd.app.activity.Webview
import sambal.mydd.app.activity.NewProfile
import sambal.mydd.app.authentication.SignUpActivity
import sambal.mydd.app.activity.MyFavorites
import sambal.mydd.app.activity.PromoCode
import sambal.mydd.app.fragment.FollowingFragment
import sambal.mydd.app.activity.NewNotification
import sambal.mydd.app.activity.Refer_FriendActivity
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import sambal.mydd.app.utils.AppUtil
import android.widget.Toast
import sambal.mydd.app.utils.DialogManager
import sambal.mydd.app.utils.AppConfig
import org.json.JSONObject
import sambal.mydd.app.SplashActivity
import sambal.mydd.app.utils.ErrorMessage
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import sambal.mydd.app.R
import sambal.mydd.app.utils.PermissionUtil
import sambal.mydd.app.activity.ScanQr
import sambal.mydd.app.databinding.LeftmenuadapterBinding
import sambal.mydd.app.local_image_cache.ImageLoader
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.ArrayList

class AdapterLeftMenu(private val context: MainActivity, private val mList: ArrayList<MenuList>) :
    RecyclerView.Adapter<AdapterLeftMenu.MyViewHolder>() {
    val dialog1 = Dialog(context)


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)

        Log.e("MenuList", ">>$position")
        val menu = mList[position]
        holder.binding.name.text = menu.menuName
        Log.e("MenuList", ">>" + menu.menuName + "<>" + menu.menuId)
        val imgLoader = ImageLoader(
            context
        )
        imgLoader.DisplayImage(menu.leftmenuIcon, holder.binding.ivLeftMenu)
        // Picasso.with(context).load(menu.getLeftmenuIcon()).into(holder.binding.ivLeftMenu);
        holder.binding.tvCode.visibility = View.INVISIBLE
        holder.binding.ivLeft.visibility = View.VISIBLE
        holder.binding.views.visibility = View.VISIBLE

        if (menu.menuId == "1" && PreferenceHelper.getInstance(context)?.isLogin == true) {
            if (!SharedPreferenceVariable.loadSavedPreferences(context, KeyConstant.KEY_CODE)
                    ?.isEmpty()!!
            ) {
                holder.binding.tvCode.text =
                    "(" + (SharedPreferenceVariable.loadSavedPreferences(
                        context, KeyConstant.KEY_CODE
                    ) + ")")
                holder.binding.tvCode.setTextColor(Color.parseColor("#FF5092"))
                holder.binding.tvCode.visibility = View.VISIBLE
            } else {
                holder.binding.tvCode.visibility = View.GONE
                holder.binding.tvCode.text = ""
                holder.binding.tvCode.setTextColor(Color.parseColor("#FF5092"))
            }
        }

        if (menu.menuId == "12") {
            holder.binding.tvLanguage.visibility = View.VISIBLE
        } else {
            holder.binding.tvLanguage.visibility = View.GONE
        }
        if (menu.menuId == "11") {
            holder.binding.ivLeft.visibility = View.GONE
        } else {
            holder.binding.ivLeft.visibility = View.VISIBLE
        }




        holder.binding.ll.setOnClickListener {


            if (AppUtil.isNetworkAvailable(context)) {


                if (menu.menuId == "3") {
                    if (menu.webURLStatus == "1") {
                        dialog1.dismiss()
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        dialog1.dismiss()

                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            context.startActivity(Intent(context, NewProfile::class.java))
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "5") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            /* ConstBollea.isDirect = true;
                            MainActivity.llFav.performClick();*/
                            context.startActivity(Intent(context, MyFavorites::class.java))
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "4") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            context.startActivity(Intent(context, PromoCode::class.java))
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "6") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        dialog1.dismiss()

                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {

                            /*                        ConstBollea.isDirect = false;
                                                    MainActivity.llFav.performClick();*/
                            context.startActivity(Intent(context, FollowingFragment::class.java))
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "9") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            context.startActivity(
                                Intent(context, Webview::class.java)
                                    .putExtra("url", menu.webURL)
                                    .putExtra("title", "FAQ")
                                    .putExtra("type", "non_direct")
                            )
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "10") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            context.startActivity(
                                Intent(context, Webview::class.java)
                                    .putExtra("url", menu.webURL)
                                    .putExtra("title", menu.menuName)
                                    .putExtra("type", "non_direct")
                            )
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "8") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        if (PreferenceHelper.getInstance(context)?.isLogin == true) {
                            context.startActivity(
                                Intent(context, NewNotification::class.java)
                                    .putExtra("agentId", "")
                                    .putExtra("title", "Notifications")
                            )
                        } else {
                            context.startActivity(Intent(context, SignUpActivity::class.java))
                        }
                    }
                } else if (menu.menuId == "1" && PreferenceHelper.getInstance(context)?.isLogin == true) {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        checkPermissions()
                        //context.startActivity(new Intent(context, ScanQr.class) .putExtra("check", "side_drawer"));
                    }
                } else if (menu.menuId == "1" && PreferenceHelper.getInstance(context)?.isLogin == false) {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        context.startActivity(Intent(context, SignUpActivity::class.java))
                    }
                } else if (menu.menuId == "11" && PreferenceHelper.getInstance(context)?.isLogin == true) {
                    exitPopup("Are you sure want to Log out?")


                } else if (menu.menuId == "7") {
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/plain"
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

                    // share.putExtra(Intent.EXTRA_SUBJECT, menu.getWebURL());
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        menu.webURL /*+"\n"+"https://play.google.com/store/apps/details?id=sambal.mydd.app&hl=en"*/
                    )
                    context.startActivity(
                        Intent.createChooser(
                            share,
                            "MyDD Points Google Playstore URL"
                        )
                    )
                } else if (menu.menuId == "2") {
                    dialog1.dismiss()

                    if (menu.webURLStatus == "1") {
                        context.startActivity(
                            Intent(context, Webview::class.java)
                                .putExtra("url", menu.webURL)
                                .putExtra("title", menu.menuName)
                                .putExtra("type", "non_direct")
                        )
                    } else {
                        context.startActivity(Intent(context, Refer_FriendActivity::class.java))
                        //closeDrawer();
                    }
                }
                // closeDrawer();
            } else {
                ErrorMessage.T(context, "No Internet Found!")
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LeftmenuadapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }


    fun exitPopup(msg: String?) {
        dialog1.setContentView(R.layout.popup_common)
        val contentText = dialog1.findViewById<TextView>(R.id.popup_content)
        contentText.text = msg
        val btnNo = dialog1.findViewById<TextView>(R.id.popup_no_btn)
        btnNo.text = "No"
        val btnOk = dialog1.findViewById<TextView>(R.id.popup_yes_btn)
        btnOk.text = "Yes"
        dialog1.setCancelable(false)
        dialog1.show()
        try {
            btnOk.setOnClickListener {
                if (AppUtil.isNetworkAvailable(context)) {
                    dialog1.dismiss()
                    doLogout()
                } else {
                    Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show()
                }
            }
            btnNo.setOnClickListener { dialog1.dismiss() }
        } catch (e: Exception) {
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun closeDrawer() {
        if (MainActivity.drawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            MainActivity.drawerLayout!!.closeDrawer(GravityCompat.START)
        }
    }

    fun loadFragment(fragment: Fragment?) {

        // load fragment
        val transaction = context.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    inner class MyViewHolder(var binding: LeftmenuadapterBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    private fun doLogout() {
        if (AppUtil.isNetworkAvailable(context)) {
            val dialogManager = DialogManager()
            dialogManager.showProcessDialog(context, "", false, null)
            val call = AppConfig.api_Interface().logout()
            call!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        try {
                            val obj = JSONObject(response.body()!!.string())
                            dialogManager.stopProcessDialog()
                            Toast.makeText(context, obj.optString("message"), Toast.LENGTH_SHORT)
                                .show()
                            SharedPreferenceVariable.ClearSharePref(context)
                            PreferenceHelper.getInstance(context)?.logout
                            PreferenceHelper.getInstance(context)?.isLogin = false
                            dialog1.dismiss()

                            context.startActivity(
                                Intent(context, SplashActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            )
                        } catch (e: Exception) {
                        }
                    } else {
                        dialogManager.stopProcessDialog()
                        Log.e("sendToken", "else is working" + response.code().toString())
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    ErrorMessage.E("ON FAILURE > " + t.message)
                    dialogManager.stopProcessDialog()
                    Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            ErrorMessage.T(context, "No Internet Found!")
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.isCameraPermissionGranted(context)) {
                //context.startActivity(new Intent(context, ScanQr.class) .putExtra("check", "side_drawer"));
                dialog1.dismiss()

                context.startActivityForResult(Intent(context, ScanQr::class.java), 80)
            } else {
                context.alertPopup()
            }
        } else {
            //context.startActivity(new Intent(context, ScanQr.class) .putExtra("check", "side_drawer"));
            dialog1.dismiss()

            context.startActivityForResult(Intent(context, ScanQr::class.java), 80)
        }
    }
}