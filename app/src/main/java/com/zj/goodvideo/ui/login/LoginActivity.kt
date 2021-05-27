package com.zj.goodvideo.ui.login

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.tencent.connect.UserInfo
import com.tencent.connect.auth.QQToken
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.zj.goodvideo.R
import com.zj.goodvideo.databinding.ActivityLoginBinding
import com.zj.goodvideo.http.RetrofitHelper
import com.zj.goodvideo.kt.toast
import com.zj.libcommon.ui.BaseActivity
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    var tencent: Tencent? = null

    private val loginListener = object : IUiListener {
        override fun onComplete(any: Any?) {
            val response = any as? JSONObject
            response?.apply {
                val openId = getString("openid")
                val accessToken = getString("access_token")
                val expiresIn = getString("expires_in")
                val expiresTime = getLong("expires_time")

                tencent?.apply {
                    setOpenId(openId)
                    setAccessToken(accessToken, expiresIn)
                    getUserInfo(qqToken, expiresTime, openId)
                }
            }
        }

        //region 快捷登录其它情况
        override fun onError(uiError: UiError?) {
            ("登录失败:reason" + uiError?.toString()).toast()
        }

        override fun onCancel() {
            "登录取消".toast()
        }

        override fun onWarning(p0: Int) {

        }
        //endregion
    }

    override fun getLayoutId() = R.layout.activity_login

    override fun init() {
    }

    override fun initEvent() {
        binding.actionClose.setOnClickListener { finish() }
        binding.btLogin.setOnClickListener { login() }
    }

    private fun login() {
        // TODO: 2021/5/26 qq快捷登录appId
        if (tencent == null) {
            tencent = Tencent.createInstance(
                "101794421",
                applicationContext,
                "com.zj.goodvideo.fileprovider"
            )
        }
        val params = HashMap<String, Any>()
        params[Constants.KEY_SCOPE] = "all"
        params[Constants.KEY_QRCODE] = true
        tencent?.login(this, loginListener, params)
    }

    private fun getUserInfo(qqToken: QQToken?, expiresTime: Long, openId: String) {
        val userInfo = UserInfo(applicationContext, qqToken)
        userInfo.getUserInfo(object : IUiListener {
            override fun onComplete(any: Any?) {
                val response = any as? JSONObject
                response?.apply {
                    val nickname = getString("nickname")
                    val figureurl_2 = getString("figureurl_2")
                    save(nickname, figureurl_2, openId, expiresTime)
                }
            }

            override fun onError(uiError: UiError?) {
                ("登录失败:reason" + uiError?.toString()).toast()
            }

            override fun onCancel() {
                "登录取消".toast()
            }

            override fun onWarning(p0: Int) {
            }

        })
    }

    private fun save(nickname: String, avatar: String, openid: String, expires_time: Long) {
        lifecycleScope.launch {
            val response =
                RetrofitHelper.apiServer.insertUser(nickname, avatar, openid, expires_time)
            if (response.data != null) {
                UserManager.save(response.data.data)
                finish()
            } else {
                "登录失败".toast()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener)
        }
    }
}