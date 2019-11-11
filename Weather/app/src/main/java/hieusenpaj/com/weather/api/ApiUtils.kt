package hieusenpaj.com.weather.api

class ApiUtils {

    companion object {
        private val STAGE = "http://api.weatherbit.io/v2.0/"
         val ICON ="https://www.weatherbit.io/static/img/icons/"
        val KEY = "9ea0445067254c1a9594a5c8b1f08f75"
        fun getApiService(): ApiServices {
            return RetrofitClient.getClient(STAGE)!!.create(ApiServices::class.java)
        }
    }
}