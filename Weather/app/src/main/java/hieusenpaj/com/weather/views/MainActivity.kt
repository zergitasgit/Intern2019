package hieusenpaj.com.weather.views


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.widget.Toast
import hieusenpaj.com.weather.R
import hieusenpaj.com.weather.adapter.ViewPagerAdapter
import hieusenpaj.com.weather.databinding.ActivityMainBinding
import hieusenpaj.com.weather.helper.Helper
import hieusenpaj.com.weather.models.City
import hieusenpaj.com.weather.viewmodels.MainViewModel
import hieusenpaj.com.weather.viewmodels.WeatherViewModel
import hieusenpaj.com.weather.views.base.BaseActivity

class MainActivity : BaseActivity() {
    var model: MainViewModel? = null
    var arrayList = ArrayList<City>()
    var viewPagerAdapter: ViewPagerAdapter? = null
    var binding: ActivityMainBinding? = null

    override fun bindingView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        model = MainViewModel(this, binding!!)
        binding!!.viewModel = model
        binding!!.executePendingBindings()
        setSupportActionBar(binding!!.toolbar)
//

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        registerReceiver(broadcastReceiver, IntentFilter("SEARCH"))
        registerReceiver(brList, IntentFilter("SEARCH_LIST"))
        registerReceiver(brDelete, IntentFilter("DELETE"))
        registerReceiver(brTemp, IntentFilter("CHANGE_TEMP"))


    }


    override fun onRestart() {
        super.onRestart()


    }

    private var brList = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action
            val pos = p1?.extras!!.getInt("pos")
            arrayList = model!!.getCity()
            viewPagerAdapter = ViewPagerAdapter(this@MainActivity, arrayList)
            binding!!.viewPager.adapter = viewPagerAdapter
            if (action!!.equals("SEARCH_LIST", ignoreCase = true)) {
                binding!!.viewPager.currentItem = pos
            }
        }
    }
    private var brDelete = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action


            if (action!!.equals("DELETE", ignoreCase = true)) {
                arrayList = model!!.getCity()
                viewPagerAdapter = ViewPagerAdapter(this@MainActivity, arrayList)
                binding!!.viewPager.adapter = viewPagerAdapter
            }
        }
    }
    private var brTemp = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val action = p1?.action


            if (action!!.equals("CHANGE_TEMP", ignoreCase = true)) {

               model!!.brChangTemp()
            }
        }
    }


    private var broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {

            val action = p1?.action
            val lat = p1?.extras?.getDouble("lat")
            val lon = p1?.extras?.getDouble("lon")
            val pos = p1?.extras!!.getString("city")

//            if (string!!.isEmpty() || string.length == 0) {
//
//            }

            if (action!!.equals("SEARCH", ignoreCase = true)) {
                arrayList = model!!.getCity()
                viewPagerAdapter = ViewPagerAdapter(this@MainActivity, arrayList)
                binding!!.viewPager.adapter = viewPagerAdapter
                if (pos.equals(" ")) {
                    binding!!.viewPager.currentItem = arrayList.size - 1
                } else {
                    var id = model!!.getIdCity(pos)
                    binding!!.viewPager.currentItem = id - 1
                }
                model!!.setLocal(arrayList, binding!!.viewPager.currentItem)
                viewPagerAdapter!!.getModel().getWeatherSearch(lat!!, lon!!)
                setUpViewPager(binding!!)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        unregisterReceiver(brList)
        unregisterReceiver(brDelete)
        unregisterReceiver(brTemp)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    fun setUpViewPager(binding: ActivityMainBinding) {
        binding.viewPager.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageSelected(p0: Int) {
                        // no-op
                        model!!.setLocal(arrayList, binding.viewPager.currentItem)
//                        Toast.makeText(this@MainActivity,p0.toString(),Toast.LENGTH_SHORT).show()
                    }

                    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                        // no-op
                    }

                    override fun onPageScrollStateChanged(p0: Int) {
                        when (p0) {
                            ViewPager.SCROLL_STATE_SETTLING -> {

                            }
                            ViewPager.SCROLL_STATE_IDLE -> {

                            }
                            else -> {
                                // no-op
                            }
                        }
                    }
                }
        )
    }


}
