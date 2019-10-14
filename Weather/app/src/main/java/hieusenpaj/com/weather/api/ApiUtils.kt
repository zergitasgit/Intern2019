package hieusenpaj.com.weather.api

class ApiUtils {

    companion object {
        private val STAGE = "http://api.weatherbit.io/v2.0/"
        val KEY = "c49e19a452d24c37bc44fc11515ea454"
        fun getApiService(): ApiServices {
            return RetrofitClient.getClient(STAGE)!!.create(ApiServices::class.java)
        }
    }
}