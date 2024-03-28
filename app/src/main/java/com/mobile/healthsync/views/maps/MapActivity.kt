package com.mobile.healthsync.views.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.location.LocationRequest
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter
import com.mobile.healthsync.R
import com.mobile.healthsync.model.JsonParser
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap : GoogleMap
    private lateinit var mFusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var placesClient : PlacesClient
    private lateinit var predictionlist : List<AutocompletePrediction>


    private lateinit var mLastKnownlocation : Location
    private lateinit var locationCallback : LocationCallback

    private lateinit var materialSearchBar : MaterialSearchBar
    private lateinit var mapView : View
    private lateinit var btnFind : Button

    private val DEFAULT_ZOOM : Float = 18f
    private val MARKER_ZOOM : Float = 14f

    inner class PlaceTask() : AsyncTask<String,Integer,String>() {
        override fun doInBackground(vararg strings: String?): String {
            var data : String = ""
            try {
                 data = downloadUrl(strings[0])
            }
            catch (e : IOException) {
                e.printStackTrace()
            }
            return data
        }

        @Throws(IOException::class)
        private fun downloadUrl(string: String?): String {
            val url : URL = URL(string)
            val conn : HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.connect()
            val stream :InputStream = conn.inputStream
            val reader : BufferedReader = BufferedReader(InputStreamReader(stream))
            var builder : StringBuilder = StringBuilder()
            try {
                var line: String? = reader.readLine()
                while(line != null) {
                    builder.append(line)
                    line = reader.readLine()
                }
            }
            catch (ex : Exception){
                ex.printStackTrace()
            }
            val data : String = builder.toString()
            reader.close()
            return data
        }

        override fun onPostExecute(s: String?) {
            ParserTask().execute(s)
        }
    }

    inner class ParserTask() : AsyncTask<String,Integer, List<HashMap<String,String>>>() {
        override fun doInBackground(vararg strings: String?): List<HashMap<String, String>> {
            val jsonParser : JsonParser = JsonParser()
            var mapList : List<HashMap<String,String>>? = null
            var jsonObject : JSONObject? = null
            try {
                jsonObject  = JSONObject(strings[0])
                mapList = jsonParser.parseResult(jsonObject)

            }catch (ex: JSONException) {
                ex.printStackTrace()
            }
            return mapList!!
        }

        override fun onPostExecute(hashMaps: List<HashMap<String, String>>?) {
            mMap.clear()
            for (i in 0 until (hashMaps?.size !!)) {
                val hashMaplist : HashMap<String,String> = hashMaps.get(i)
                val lat: Double = hashMaplist.get("lat")?.toDoubleOrNull()!!
                val lng: Double = hashMaplist.get("lng")?.toDoubleOrNull()!!
                val name : String = hashMaplist.get("name")!!
                val latlng : LatLng = LatLng(lat,lng)
                val marker = addMarker(latlng)
                marker.title = "$name"
            }

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(mMap.cameraPosition.target, MARKER_ZOOM)
            mMap.animateCamera(cameraUpdate)
        }

    }

    private fun addMarker(position: LatLng): Marker {
        //Add simple marker
        val marker = mMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Marker"))
        return marker!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_map)

        materialSearchBar = findViewById(R.id.searchBar)
        btnFind = findViewById(R.id.btn_find)

        var mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.view as View

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this@MapActivity)
        Places.initialize(this@MapActivity, getString(R.string.google_map_api_key))
        placesClient = Places.createClient(this@MapActivity)
        val token : AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

        materialSearchBar.setOnSearchActionListener( object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {

            }
            override fun onSearchConfirmed(text: CharSequence?) {
                startSearch(text.toString(),true, null,true)
            }
            override fun onButtonClicked(buttonCode: Int) {
                if(buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    //opening or closing a navigation drawer.
                }
                else if(buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.closeSearch()
                }
            }
        })

        materialSearchBar.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Empty implementation
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val predictionrequest : FindAutocompletePredictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry("ca")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(s.toString())
                    .build()

                placesClient.findAutocompletePredictions(predictionrequest).addOnCompleteListener( object : OnCompleteListener<FindAutocompletePredictionsResponse> {
                    override fun onComplete(task: Task<FindAutocompletePredictionsResponse>) {
                        if(task.isSuccessful){
                            val predictionResp : FindAutocompletePredictionsResponse = task.getResult()
                            if(predictionResp != null) {
                                predictionlist = predictionResp.autocompletePredictions
                                val suggestionlist =  mutableListOf<String>()
                                for(predictionItem in predictionlist) {
                                    suggestionlist.add(predictionItem.getFullText(null).toString())
                                }
                                materialSearchBar.updateLastSuggestions(suggestionlist)
                                if(!materialSearchBar.isSuggestionsVisible) {
                                    materialSearchBar.showSuggestionsList()
                                }
                            }
                        }
                        else {
                            Log.i("mytag","prediction fetching task unsuccessful")
                        }
                    }

                })
            }

            override fun afterTextChanged(s: Editable?) {
                // Empty implementation
            }
        })

        materialSearchBar.setSuggestionsClickListener(object : SuggestionsAdapter.OnItemViewClickListener{
            override fun OnItemClickListener(position: Int, v: View?) {
                if(position >= predictionlist.size) {
                    return
                }
                val selectedPrediction : AutocompletePrediction = predictionlist.get(position)
                val suggestion : String = materialSearchBar.lastSuggestions.get(position).toString()
                materialSearchBar.text = suggestion

                Handler().postDelayed(object :Runnable {
                    override fun run() {
                        materialSearchBar.clearSuggestions()
                    }
                }, 1000)

                val imm : InputMethodManager  = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                if(imm != null) {
                    imm.hideSoftInputFromWindow(materialSearchBar.windowToken,InputMethodManager.HIDE_IMPLICIT_ONLY)
                    val placeID : String = selectedPrediction.placeId
                    val placeFields : List<Place.Field>  = listOf(Place.Field.LAT_LNG)

                    val fetchplaceReq : FetchPlaceRequest = FetchPlaceRequest.builder(placeID, placeFields).build()
                    placesClient.fetchPlace(fetchplaceReq).addOnSuccessListener(object : OnSuccessListener<FetchPlaceResponse> {
                        override fun onSuccess(fetchplaceResp: FetchPlaceResponse?) {
                            val place : Place = fetchplaceResp!!.place
                            Log.i("mytag", "place found: " + place.name)
                            val latlng :LatLng   = place.latLng
                            if(latlng != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,DEFAULT_ZOOM))
                            }
                        }

                    }).addOnFailureListener( object : OnFailureListener{
                        override fun onFailure(ex: Exception) {
                            if(ex is ApiException) {
                                val apiException : ApiException = ex as ApiException
                                apiException.printStackTrace()
                                val statusCode: Int = apiException.getStatusCode()
                                Log.i("mytag", "place not found: " + ex.message)
                                Log.i("mytag", "status code: " + statusCode)
                            }
                        }

                    })
                }
            }

            override fun OnItemDeleteListener(position: Int, v: View?) {
                //Empty implementation
            }

        })


        btnFind.setOnClickListener{
            val url : String = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + //Url
            "?location=" + mLastKnownlocation.latitude + "," + mLastKnownlocation.longitude + //latitude and longitude
            "&radius=3000" + // Nearby radius
            "&types=" + "pharmacy" + //Place type
            "&sensor=true" + //Sensor
            "&key=" + resources.getString(R.string.google_map_api_key); //gMaps key

            //Execute place task method to download json data
            PlaceTask().execute(url)

        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        if((mapView != null) && (mapView.findViewById<View>(Integer.parseInt("1")) != null)){
            val locationButton : View = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById(Integer.parseInt("2"))
            val layoutparams : RelativeLayout.LayoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            layoutparams.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
            layoutparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE)
            layoutparams.setMargins(0,0,40,180)
        }

        //Check if GPS is enabled or not and request user to enable it.
        val locationRequest : LocationRequest = LocationRequest.create()
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(5000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val builder : LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient : SettingsClient = LocationServices.getSettingsClient(this@MapActivity)
        val task : Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener(this, object : OnSuccessListener<LocationSettingsResponse> {
            override fun onSuccess(locationsettingresp: LocationSettingsResponse?) {
                getDeviceLocation()
            }
        })

        task.addOnFailureListener(this, object : OnFailureListener {
            override fun onFailure(ex: Exception) {
                if(ex is ResolvableApiException){
                    val resolvable : ResolvableApiException = ex as ResolvableApiException
                    try{
                        resolvable.startResolutionForResult(this@MapActivity,51)
                    }
                    catch (siex : IntentSender.SendIntentException) {
                        siex.printStackTrace()
                    }
                }
            }
        })

        mMap.setOnMyLocationButtonClickListener( object : GoogleMap.OnMyLocationButtonClickListener {
            override fun onMyLocationButtonClick(): Boolean {
                if(materialSearchBar.isSuggestionsVisible) {
                    materialSearchBar.clearSuggestions()
                }
                if(materialSearchBar.isSearchOpened) {
                    materialSearchBar.closeSearch()
                }
                return false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode ==  51){
            if(resultCode == RESULT_OK) {
                getDeviceLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
            .addOnCompleteListener( object : OnCompleteListener<Location> {
                override fun onComplete(task: Task<Location>) {
                    if(task.isSuccessful) {
                        mLastKnownlocation = task.getResult()
                        if(mLastKnownlocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLastKnownlocation.latitude,mLastKnownlocation.longitude), DEFAULT_ZOOM))

                        }
                        else {
                            val locationrequest : LocationRequest = LocationRequest.create()
                            locationrequest.setInterval(10000)
                            locationrequest.setFastestInterval(5000)
                            locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    super.onLocationResult(locationResult)
                                    if(locationResult == null) {
                                        return;
                                    }
                                    mLastKnownlocation = locationResult.lastLocation!!
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLastKnownlocation.latitude,mLastKnownlocation.longitude),DEFAULT_ZOOM))
                                    mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
                                }
                            }
                            mFusedLocationProviderClient.requestLocationUpdates(locationrequest,locationCallback, null)
                        }
                    }
                    else {
                        Toast.makeText(this@MapActivity, "unalbe to get last location",Toast.LENGTH_SHORT).show()
                    }
                }

            })
    }

}
