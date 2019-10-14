package hieusenpaj.com.weather.api

import hieusenpaj.com.weather.models.current.CurrentWeather
import hieusenpaj.com.weather.models.forecastDay.ForecastDay
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {
    @GET("current/daily")
    fun getCurrentWeather(@Query("lat") lat:Double,@Query("lon") lon :Double,
                          @Query("key") key:String): Call<CurrentWeather>
    @GET("forecast/daily")
    fun getForecastWeather(@Query("lat") lat:Double,@Query("lon") lon :Double,
                           @Query("key") key:String,
                           @Query("days") day:String) : Call<ForecastDay>


}