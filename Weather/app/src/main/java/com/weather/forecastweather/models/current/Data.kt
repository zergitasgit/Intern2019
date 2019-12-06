package hieusenpaj.com.weather.models.current

import com.google.gson.annotations.SerializedName
import com.weather.forecastweather.models.current.Weather

/*
Copyright (c) 2019 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Data (

		@SerializedName("rh") val rh : Int,
		@SerializedName("pod") val pod : String,
		@SerializedName("lon") val lon : Double,
		@SerializedName("pres") val pres : Double,
		@SerializedName("timezone") val timezone : String,
		@SerializedName("ob_time") val ob_time : String,
		@SerializedName("country_code") val country_code : String,
		@SerializedName("clouds") val clouds : Int,
		@SerializedName("ts") val ts : Int,
		@SerializedName("solar_rad") val solar_rad : Double,
		@SerializedName("state_code") val state_code : Int,
		@SerializedName("city_name") val city_name : String,
		@SerializedName("wind_spd") val wind_spd : Double,
		@SerializedName("last_ob_time") val last_ob_time : String,
		@SerializedName("wind_cdir_full") val wind_cdir_full : String,
		@SerializedName("wind_cdir") val wind_cdir : String,
		@SerializedName("slp") val slp : Double,
		@SerializedName("vis") val vis : Double,
		@SerializedName("h_angle") val h_angle : Int,
		@SerializedName("sunset") val sunset : String,
		@SerializedName("dni") val dni : Double,
		@SerializedName("dewpt") val dewpt : Double,
		@SerializedName("snow") val snow : Int,
		@SerializedName("uv") val uv : Double,
		@SerializedName("precip") val precip : Int,
		@SerializedName("wind_dir") val wind_dir : Int,
		@SerializedName("sunrise") val sunrise : String,
		@SerializedName("ghi") val ghi : Double,
		@SerializedName("dhi") val dhi : Double,
		@SerializedName("aqi") val aqi : Int,
		@SerializedName("lat") val lat : Double,
		@SerializedName("weather") val weather : Weather,
		@SerializedName("datetime") val datetime : String,
		@SerializedName("temp") val temp : Double,
		@SerializedName("station") val station : String,
		@SerializedName("elev_angle") val elev_angle : Double,
		@SerializedName("app_temp") val app_temp : Double
)