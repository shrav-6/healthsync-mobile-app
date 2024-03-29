package com.mobile.healthsync.model

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class JsonParser {
    private fun parseJsonObject( jsonObject : JSONObject) : HashMap<String,String> {
        val dataList : HashMap<String, String> = hashMapOf()
        try {
            val name : String = jsonObject.getString("name")
            val latitude : String = jsonObject.getJSONObject("geometry")
                .getJSONObject("location").getString("lat");
            val longitude : String = jsonObject.getJSONObject("geometry")
                .getJSONObject("location").getString("lng")

            dataList.put("name",name)
            dataList.put("lat",latitude)
            dataList.put("lng",longitude)
        }
        catch (ex : JSONException) {
            ex.printStackTrace()
        }
        return dataList
    }

    private fun parseJsonArray(jsonArray: JSONArray): List<HashMap<String, String>> {
        val dataList : MutableList<HashMap<String,String>> = mutableListOf();
        for (i in 0 until jsonArray.length()) {
            try {
                val data : HashMap<String,String> = parseJsonObject(jsonArray.getJSONObject(i) as JSONObject)
                dataList.add(data)
            }
            catch (ex: JSONException) {
                ex.printStackTrace()
            }
        }
        return dataList
    }

    public fun parseResult (jsonObject: JSONObject): List<HashMap<String,String>> {
        var jsonArray : JSONArray? = null
        try
        {
            jsonArray = jsonObject.getJSONArray("results")
        }catch (ex: JSONException) {
            ex.printStackTrace()
        }
        return parseJsonArray(jsonArray!!);
    }
}