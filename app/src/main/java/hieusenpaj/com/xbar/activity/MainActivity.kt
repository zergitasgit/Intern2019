package hieusenpaj.com.xbar.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.os.Build
import android.view.MenuItem

import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import android.widget.CompoundButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.content_main.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener
import hieusenpaj.com.xbar.R
import hieusenpaj.com.xbar.Utility
import hieusenpaj.com.xbar.`object`.Action
import hieusenpaj.com.xbar.adapter.ActionAdapter
import hieusenpaj.com.xbar.db.DBAction
import hieusenpaj.com.xbar.dialog.MainDialog
import hieusenpaj.com.xbar.service.WindownService
import kotlinx.android.synthetic.main.dialog_action.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ColorPickerDialogListener {
    override fun onDialogDismissed(dialogId: Int) {

    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        if (dialogId == 0) {

            val string = "#" + Integer.toHexString(color)
            val intent = Intent("COLOR")
            intent.putExtra("code", string)
            sendBroadcast(intent)
            edit!!.putString("color", string)
            edit!!.apply()
            Toast.makeText(
                this@MainActivity,
                "Selected Color: #" + Integer.toHexString(color),
                Toast.LENGTH_SHORT
            ).show()


        }
    }

    private var accessibilityDialog: AlertDialog? = null
    private var db = DBAction(this)
    private var arr = ArrayList<Action>()
    private var adapter: ActionAdapter? = null
    private var positon = 0
    var sharedPreferences: SharedPreferences? = null
    var edit: SharedPreferences.Editor? = null
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        val id = p0.itemId

        if (id == R.id.nav_home) {

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("hieu", Context.MODE_PRIVATE)
        edit = sharedPreferences?.edit()
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {   //Android M Or Over
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName)
            )
            startActivityForResult(intent, 3)
            return
        }

        setUp()
        setUpAccSe()
        swicht()
        setUpOnclick()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun perSuss() {
        if (Settings.canDrawOverlays(this)) {
            val serviceIntent = Intent(this, WindownService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(serviceIntent)
            } else {
                this.startService(serviceIntent)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 3) {
//            perSuss()
        }
    }

    fun setUp() {

        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun swicht() {
        if (sharedPreferences!!.getBoolean("switch", false)) {
            if(sharedPreferences!!.getBoolean("destroy",false)) {
                perSuss()


            }
            switch_id.isChecked = true
            tv_switch.text = "On"

        }
        switch_id.setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (switch_id.isChecked) {
                    perSuss()
                    tv_switch.text = "On"
                    edit!!.putBoolean("switch", true)
                    edit!!.apply()
                } else {
                    val intent = Intent("STOP")
                    sendBroadcast(intent)
                    tv_switch.text = "Off"
                    edit!!.putBoolean("switch", false)
                    edit!!.apply()
                }
            })
    }

    fun setUpAccSe() {
        if (!Utility.isAccessibilityEnabled(
                applicationContext,
                WindownService.ACCESSIBILITY_ID
            )
        ) {
            if (accessibilityDialog == null) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage(
                    "It is good practice to explain to the user why you need " +
                            "the Accessibility permission and how it is used to automatically " +
                            "disable the view to avoid the \"Screen Overlay Detected\" popup"
                )
                    .setTitle("Auto-Disable")
                builder.setPositiveButton("enable") { dialog, id ->
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivityForResult(
                        intent,
                        WindownService.ACCESSIBILITY_REQUEST_CODE
                    )
                }
                builder.setNegativeButton(
                    "no thanks"
                ) { dialog, id ->
                    dialog.dismiss()
                    Toast.makeText(
                        this@MainActivity,
                        "XBar is not enabled :(",
                        Toast.LENGTH_LONG
                    ).show()
                }
                accessibilityDialog = builder.create()
            }
            accessibilityDialog!!.show()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Accessibility is already enabled! :)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setUpOnclick() {
        ll_up.setOnClickListener {
            setOnclick("up")
        }
        ll_left.setOnClickListener {
            setOnclick("left")
        }
        ll_right.setOnClickListener {
            setOnclick("right")
        }
        ll_on_click.setOnClickListener {
            setOnclick("on")
        }
        ll_double_click.setOnClickListener {
            setOnclick("double")
        }
        tv_up.text = sharedPreferences!!.getString("tvup", "")
        tv_left.text = sharedPreferences!!.getString("tvleft", "")
        tv_right.text = sharedPreferences!!.getString("tvright", "")
        tv_on_click.text = sharedPreferences!!.getString("tvon", "")
        tv_double_click.text = sharedPreferences!!.getString("tvdouble", "")
        cb_shadow.isChecked = sharedPreferences!!.getBoolean("cbShadow", false)


        ll_color.setOnClickListener {
            ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                .setAllowPresets(false)
                .setDialogId(0)
                .setColor(Color.parseColor(sharedPreferences!!.getString("color", "#000000")))
                .setShowAlphaSlider(true)
                .show(this)
        }

        cb_shadow.setOnCheckedChangeListener { p0, p1 ->
            val intent = Intent("SHADOW")
            if (p1) {
                edit!!.putBoolean("cbShadow", true)
                edit!!.apply()
                intent.putExtra("cb", false)
                sendBroadcast(intent)
            } else {
                edit!!.putBoolean("cbShadow", false)
                edit!!.apply()
                intent.putExtra("cb", true)
                sendBroadcast(intent)
            }
        }
        ll_shadow.setOnClickListener {
            cbShadow()

        }
        ll_width.setOnClickListener {
            val dialog = MainDialog(this,"width",object :MainDialog.OnClickDialog{
                override fun onClick(value: Int) {
                    tv_with.text = "$value%"
                }

            })
            dialog.show()
        }
        tv_with.text =sharedPreferences!!.getInt("sbWidth", 100).toString() + "%"
        ll_height.setOnClickListener {
            val dialog = MainDialog(this,"height",object :MainDialog.OnClickDialog{
                override fun onClick(value: Int) {
                    tv_height.text  = "$value%"
                }

            })
            dialog.show()
        }
        tv_height.text = sharedPreferences!!.getInt("sbHeight", 50).toString() + "%"
        ll_margin.setOnClickListener {
            val dialog = MainDialog(this,"margin",object :MainDialog.OnClickDialog{
                override fun onClick(value: Int) {
                    tv_margin.text  = "$value%"
                }

            })
            dialog.show()
        }
        tv_margin.text = sharedPreferences!!.getInt("sbMargin",0).toString() + "%"
    }

    fun cbShadow() {
        val intent = Intent("SHADOW")
        if (!sharedPreferences!!.getBoolean("cbShadow", false)) {
            cb_shadow.isChecked = true
            edit!!.putBoolean("cbShadow", true)
            edit!!.apply()
            intent.putExtra("cb", false)
            sendBroadcast(intent)
        } else {
            cb_shadow.isChecked = false
            edit!!.putBoolean("cbShadow", false)
            edit!!.apply()
            intent.putExtra("cb", true)
            sendBroadcast(intent)
        }
    }

    fun setOnclick(status: String) {
        val dialog = Dialog(this)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        dialog.setContentView(R.layout.dialog_action)
        dialog.show()
        val window = dialog.window
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        arr = db.getAction()
        dialog.rv_dialog.layoutManager = LinearLayoutManager(this)
        adapter = ActionAdapter(this, arr, object : ActionAdapter.Listener {
            override fun onClick(pos: Int, code: String) {
                adapter!!.setAllFalse()
                arr[pos].isCheck = !arr[pos].isCheck
                adapter!!.notifyDataSetChanged()
                edit!!.putInt("pos" + status, pos)
                edit!!.apply()

            }


        })
        dialog.rv_dialog.adapter = adapter
        dialog.rl_cancle.setOnClickListener {
            dialog.dismiss()
            adapter!!.setAllFalse()
        }
        dialog.rl_done.setOnClickListener {
            edit!!.putInt(status, arr[sharedPreferences!!.getInt("pos" + status, 0)].code.toInt())
            edit!!.putString(
                "tv" + status,
                arr[sharedPreferences!!.getInt("pos" + status, 0)].action
            )
            edit!!.apply()
            when (status) {
                "up" -> tv_up.text = arr[sharedPreferences!!.getInt("posup", 0)].action
                "left" -> tv_left.text = arr[sharedPreferences!!.getInt("posleft", 0)].action
                "right" -> tv_right.text = arr[sharedPreferences!!.getInt("posright", 0)].action
                "on" -> tv_on_click.text = arr[sharedPreferences!!.getInt("poson", 0)].action
                "double" -> tv_double_click.text =
                    arr[sharedPreferences!!.getInt("posdouble", 0)].action
            }

            dialog.dismiss()
        }

    }

//
}
