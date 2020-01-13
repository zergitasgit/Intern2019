package com.lock.applock.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.lock.applock.service.LockService
import com.lock.applock.R
import com.lock.applock.`object`.ItemMain
import com.lock.applock.adapter.ItemMainAdapter
import com.lock.applock.adapter.TabAdapter
import com.lock.applock.fingerprint.FingerprintHandler
import com.lock.applock.helper.Helper
import com.lock.applock.helper.Helper.Companion.cipher
import com.lock.applock.helper.Helper.Companion.cipherInit
import com.lock.applock.helper.KeyboardToggleListener
import com.reader.pdfreader.fragment.AppLockedFragment
import com.reader.pdfreader.fragment.DislayAppFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var arrFragment = ArrayList<Fragment>()
    var arrIcon = ArrayList<String>()
    var tabAdapter: TabAdapter? = null
    var adapter: ItemMainAdapter? = null
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    companion object {
        var popup: ListPopupWindow? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val serviceIntent = Intent(this, LockService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(serviceIntent)
        } else {
            this.startService(serviceIntent)
        }

        popup = ListPopupWindow(this)

        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences!!.edit()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(sharedPreferences!!.getBoolean("finger",false)) {
                fingerprint()
            }
        }


        setUpViewPager()
        setTabView()
        setUpSearch()



        this.addKeyboardToggleListener {
            contener.viewTreeObserver.addOnGlobalLayoutListener {


                Handler().postDelayed({
                    val heightDiff = contener.rootView.height - contener.height
                    if (heightDiff > 300) {
                    } else {
                        if (TextUtils.isEmpty(ed_search.text.toString())) {
                            iv_search.visibility = View.VISIBLE
                            ed_search.visibility = View.GONE
                            tv_title.visibility = View.VISIBLE
                            iv_search_logic.visibility = View.GONE
                            ed_search.text.clear()
                        }

                    }
                }, 500)


            }


        }
    }


    override fun onBackPressed() {
        if (ed_search.hasFocus()) {
            iv_search.visibility = View.VISIBLE
            ed_search.visibility = View.GONE
            tv_title.visibility = View.VISIBLE
            iv_search_logic.visibility = View.GONE
            ed_search.text.clear()
        } else {
            if (popup!!.isShowing) {
                popup!!.dismiss()
            } else {
                finishAffinity()
                super.onBackPressed()
            }
        }

    }

    private fun setUpViewPager() {
        setSupportActionBar(toolbar)
        tabAdapter = TabAdapter(this, arrFragment, arrIcon, supportFragmentManager)
        tabAdapter!!.addViewFragment(
            DislayAppFragment(),
            "App"
        )
        tabAdapter!!.addViewFragment(
            AppLockedFragment(),
            "App locked"
        )

        viewpager.offscreenPageLimit = 2
        viewpager.adapter = tabAdapter

    }

    private fun setTabView() {
        sliding_tabs.setupWithViewPager(viewpager)
        for (i in 0 until sliding_tabs.tabCount) {
            sliding_tabs.getTabAt(i)!!.customView = tabAdapter?.getTabView(i)
        }


    }

    private fun setUpSearch() {
        iv_search.setOnClickListener {
            iv_search.visibility = View.GONE
            ed_search.visibility = View.VISIBLE
            tv_title.visibility = View.GONE
            ed_search.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(ed_search, InputMethodManager.SHOW_IMPLICIT)
            ed_search.addTextChangedListener(object : TextWatcher {
                @SuppressLint("DefaultLocale")
                override fun afterTextChanged(p0: Editable?) {
                    if (!TextUtils.isEmpty(p0!!.toString())) {
                        iv_search_logic.visibility = View.VISIBLE
                        iv_search_logic.setImageDrawable(resources.getDrawable(R.drawable.close))
                        iv_search_logic.setOnClickListener {
                            ed_search.text.clear()

                        }

                    } else {
                        iv_search_logic.visibility = View.GONE
                    }
//                    arrFilter = Helper.getAllDocuments(this@MainActivity)
                    val intent = Intent("SEARCH")
                    intent.putExtra("string", p0.toString())
                    sendBroadcast(intent)
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })
        }

        iv_menu.setOnClickListener {
            con.visibility = View.VISIBLE
            showListPopupWindow(it)
            val intent = Intent("POPUP")
            intent.putExtra("show",true)
            sendBroadcast(intent)

        }
    }
    fun set() {
        if(popup!!.isShowing) {
            popup!!.dismiss()

        }
    }
    private fun Activity.addKeyboardToggleListener(onKeyboardToggleAction: (shown: Boolean) -> Unit): KeyboardToggleListener? {
        val root = findViewById<View>(android.R.id.content)
        val listener = KeyboardToggleListener(
            root,
            onKeyboardToggleAction
        )
        return root?.viewTreeObserver?.run {
            addOnGlobalLayoutListener(listener)
            listener
        }
    }


    private fun showListPopupWindow(anchor: View) {
        val listPopupItems = ArrayList<ItemMain>()
        listPopupItems.add(ItemMain("Change lock", R.drawable.close))
        if (!sharedPreferences!!.getBoolean("finger", false)) {
            listPopupItems.add(ItemMain("Fingerprint", R.drawable.lock))
        } else {
            listPopupItems.add(ItemMain("Fingerprint", R.drawable.lock_click))


        }

        listPopupItems.add(ItemMain("Dark mode", R.drawable.lock_click))
//


        val listPopupWindow = createListPopupWindow(anchor, listPopupItems)
        listPopupWindow.show()

    }



    private fun createListPopupWindow(
        anchor: View,
        items: ArrayList<ItemMain>
    ): ListPopupWindow {

        adapter = ItemMainAdapter(this, items, object : ItemMainAdapter.ItemListener {
            override fun onClick(position: Int, it: View) {
                when (position) {
                    0 -> {
                        val intent = Intent(this@MainActivity, ChangeLockActivity::class.java)
                        startActivity(intent)
                        popup!!.dismiss()

                    }
                    1 -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (sharedPreferences!!.getBoolean("finger", false)) {
                            adapter!!.updateFinger(it, false)
                            edit!!.putBoolean("finger", false)
                            edit!!.apply()

                        } else {
                            adapter!!.updateFinger(it, true)
                            edit!!.putBoolean("finger", true)
                            edit!!.apply()
                        }
                        }else{
                            Toast.makeText(this@MainActivity,"thiet bi khong ho tro van tay",Toast.LENGTH_SHORT).show()
                        }

                    }
                    2 -> {

                    }

                }
//                showListPopupWindow(it)
            }

        })
        popup!!.anchorView = anchor
        popup!!.width = convertToPx(180)
        popup!!.height = convertToPx(160)
        popup!!.setBackgroundDrawable(resources.getDrawable(R.drawable.popup))
        popup!!.setAdapter(adapter)
        popup!!.setOnDismissListener {
            con.visibility = View.GONE
        }

        return popup!!
    }

    private fun convertToPx(dp: Int): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (dp * scale + 0.5f).toInt()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun fingerprint(){
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        val fingerprintManager =
            getSystemService(FINGERPRINT_SERVICE) as FingerprintManager


        // Check whether the device has a Fingerprint sensor.
        if (!fingerprintManager.isHardwareDetected) {
           Toast.makeText(this,"Your Device does not have a Fingerprint Sensor",Toast.LENGTH_SHORT).show()
        } else { // Checks whether fingerprint permission is set on manifest
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.USE_FINGERPRINT
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this,"Fingerprint authentication permission not enabled",Toast.LENGTH_SHORT).show()
            } else { // Check whether at least one fingerprint is registered
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(this,"Register at least one fingerprint in Settings",Toast.LENGTH_SHORT).show()

                } else { // Checks whether lock screen security is enabled or not
                    if (!keyguardManager.isKeyguardSecure) {
                        Toast.makeText(this,"Lock screen security not enabled in Settings",Toast.LENGTH_SHORT).show()
                    } else {
                        Helper.generateKey()
                        if (cipherInit()) {
                            val cryptoObject: FingerprintManager.CryptoObject =
                                FingerprintManager.CryptoObject(cipher!!)
                            val helper = FingerprintHandler(this)
                            helper.startAuth(fingerprintManager, cryptoObject)
                        }
                    }
                }
            }
        }
    }



}
