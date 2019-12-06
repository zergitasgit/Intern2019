package com.weather.forecastweather.models.forecastDay

import com.google.gson.annotations.SerializedName
import com.weather.forecastweather.models.forecastDay.Weather

/*
Copyright (c) 2019 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Data (

		@SerializedName("moonrise_ts") val moonrise_ts : Double,
		@SerializedName("wind_cdir") val wind_cdir : String,
		@SerializedName("rh") val rh : Double,
		@SerializedName("pres") val pres : Double,
		@SerializedName("high_temp") val high_temp : Double,
		@SerializedName("sunset_ts") val sunset_ts : Double,
		@SerializedName("ozone") val ozone : Double,
		@SerializedName("moon_phase") val moon_phase : Double,
		@SerializedName("wind_gust_spd") val wind_gust_spd : Double,
		@SerializedName("snow_depth") val snow_depth : Double,
		@SerializedName("clouds") val clouds : Double,
		@SerializedName("ts") val ts : Double,
		@SerializedName("sunrise_ts") val sunrise_ts : Double,
		@SerializedName("app_min_temp") val app_min_temp : Double,
		@SerializedName("wind_spd") val wind_spd : Double,
		@SerializedName("pop") val pop : Double,
		@SerializedName("wind_cdir_full") val wind_cdir_full : String,
		@SerializedName("slp") val slp : Double,
		@SerializedName("valid_date") val valid_date : String,
		@SerializedName("app_max_temp") val app_max_temp : Double,
		@SerializedName("vis") val vis : Double,
		@SerializedName("dewpt") val dewpt : Double,
		@SerializedName("snow") val snow : Double,
		@SerializedName("uv") val uv : Double,
		@SerializedName("weather") val weather : Weather,
		@SerializedName("wind_dir") val wind_dir : Double,
		@SerializedName("max_dhi") val max_dhi : String,
		@SerializedName("clouds_hi") val clouds_hi : Double,
		@SerializedName("precip") val precip : Double,
		@SerializedName("low_temp") val low_temp : Double,
		@SerializedName("max_temp") val max_temp : Double,
		@SerializedName("moonset_ts") val moonset_ts : Double,
		@SerializedName("datetime") val datetime : String,
		@SerializedName("temp") val temp : Double,
		@SerializedName("min_temp") val min_temp : Double,
		@SerializedName("clouds_mid") val clouds_mid : Double,
		@SerializedName("clouds_low") val clouds_low : Double
)