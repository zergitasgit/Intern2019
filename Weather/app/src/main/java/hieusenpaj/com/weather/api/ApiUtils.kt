package hieusenpaj.com.weather.api

class ApiUtils {

    companion object {
        private val STAGE = "http://api.weatherbit.io/v2.0/"
         val ICON ="https://www.weatherbit.io/static/img/icons/"
        val KEY = "5aeb31f273024be7b4932f2976219884"
        fun getApiService(): ApiServices {
            return RetrofitClient.getClient(STAGE)!!.create(ApiServices::class.java)
        }
    }
}