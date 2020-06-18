package com.lukaszgalinski.gamefuture

import android.content.Context
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL

private const val TAG: String = "HttpHandler:"
private const val GET_METHOD = "GET"
class HttpHandler(private val context: Context){

    fun makeServiceCall(reqUrl: String): String{
        var response = ""
        try {
            val url = URL(reqUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.requestMethod = GET_METHOD
            val inputStream: InputStream = BufferedInputStream(connection.inputStream)
            response = streamToString(inputStream)
        } catch (e: MalformedURLException) {
            Log.e(TAG,  context.resources.getString(R.string.loading_malformedException) + e.message);
        } catch (e: ProtocolException) {
            Log.e(TAG, context.resources.getString(R.string.loading_protocolException)  + e.message);
        } catch (e: IOException) {
            Log.e(TAG, context.resources.getString(R.string.loading_ioException)  + e.message);
        } catch (e: Exception) {
            Log.e(TAG, context.resources.getString(R.string.loading_exception) + e.message);
        }
        return response
    }

    private fun streamToString(input: InputStream): String {
        val reader = BufferedReader(InputStreamReader(input))
        val sb = StringBuilder()
        var line: String?
        try {
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                input.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return sb.toString()
    }
}