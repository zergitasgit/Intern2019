package hieusenpaj.com.weather.api

class ApiUtils {

    companion object {
        private val STAGE = "http://api.weatherbit.io/v2.0/"
         val ICON ="https://www.weatherbit.io/static/img/icons/"
        val KEY2 ="c8e78cb55c7f4415bd27354dbf00a13e"
        val KEY ="bfcb165c26c14417a9fdd58a3e9c2186"
        val KEY1="e9833fb72e504923bdde8004b882edf9"
        val KEY3="f123ee6d83af4b15a8e539483a256a60"
        fun getApiService(): ApiServices {
            return RetrofitClient.getClient(STAGE)!!.create(ApiServices::class.java)
        }
    }
}