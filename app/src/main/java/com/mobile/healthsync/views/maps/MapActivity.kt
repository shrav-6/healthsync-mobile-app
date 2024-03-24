package com.mobile.healthsync.views.maps

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.location.LocationRequest
import android.location.Location
import android.media.MediaPlayer.OnCompletionListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.mancj.materialsearchbar.MaterialSearchBar
import com.mobile.healthsync.R
import java.lang.Exception

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

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
