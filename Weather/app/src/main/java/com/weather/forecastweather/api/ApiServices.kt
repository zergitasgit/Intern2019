package com.weather.forecastweather.api

import com.weather.forecastweather.models.current.CurrentWeather
import com.weather.forecastweather.models.forecastDay.ForecastDay
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("current")
    fun getCurrentWeather(@Query("lat") lat:Double,@Query("lon") lon :Double,
                          @Query("key") key:String): Call<CurrentWeather>
    @GET("forecast/daily")
    fun getForecastWeather(@Query("lat") lat:Double,@Query("lon") lon :Double,
                           @Query("key") key:String,
                           @Query("days") day:String) : Call<ForecastDay>
  


}