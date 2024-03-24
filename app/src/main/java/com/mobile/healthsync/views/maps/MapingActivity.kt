package com.mobile.healthsync.views.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.mobile.healthsync.R

class MapingActivity : AppCompatActivity(),OnMapReadyCallback {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var autocompleFragment: AutocompleteSupportFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maping)

        Places.initialize(applicationContext, getString(R.string.google_map_api_key))
        autocompleFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onError(p0: Status) {
                Toast.makeText(this@MapingActivity, "Some Error in Search", Toast.LENGTH_SHORT).show()

            }

            override fun onPlaceSelected(place: Place) {
                val add = place.address
                val id = place.id
                val latLng = place.latLng!!
                val marker = addMarker(latLng)
                marker.title = "$add"
                marker.snippet = "$id"

                zoomOnMap(latLng)
            }

        })

        val mapFragment  = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


    }

    private fun zoomOnMap(latLng : LatLng){
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng,10f)
        mGoogleMap?.animateCamera(newLatLngZoom)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        //Add simple marker
        addMarker(LatLng(12.321,13.432))
        //Add draggable marker
        addDraggabeMarker(LatLng(13.234, 12.543))
        //Add custom marker
        addCustomMarker(R.drawable.flag_marker,LatLng(12.987, 14.345))

        mGoogleMap?.setOnMapClickListener { position ->
            mGoogleMap?.clear()
            addMarker(position)
        }

        mGoogleMap?.setOnMapLongClickListener { position ->
            addCustomMarker(R.drawable.flag_marker,position)
        }

        mGoogleMap?.setOnMarkerClickListener { marker ->
            marker.remove()
            false
        }

    }

    private fun addMarker(position: LatLng): Marker{

        //Add simple marker
        val marker = mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Mareker"))

        return marker!!
    }

    private fun addDraggabeMarker(position: LatLng){
        mGoogleMap?.addMarker(MarkerOptions()
            .position(position)
            .title("Draggable Marker")
            .draggable(true))
    }

    private fun addCustomMarker(icon: Int, position: LatLng){
        mGoogleMap?.addMarker(MarkerOptions().position(position)
            .title("Custom Marker")
            .icon(BitmapDescriptorFactory.fromResource(icon))
        )
    }
}