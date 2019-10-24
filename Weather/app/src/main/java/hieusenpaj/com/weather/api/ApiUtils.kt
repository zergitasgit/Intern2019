package hieusenpaj.com.weather.api

class ApiUtils {

    companion object {
        private val STAGE = "http://api.weatherbit.io/v2.0/"
         val ICON ="https://www.weatherbit.io/static/img/icons/"
        val KEY = "71e37f1685cd4d018885fcf0ad66e423"
        fun getApiService(): ApiServices {
            return RetrofitClient.getClient(STAGE)!!.create(ApiServices::class.java)
        }
    }
}