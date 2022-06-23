package com.meltingb.medicare.view.fragment

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.meltingb.medicare.R
import com.meltingb.medicare.core.BaseFragment
import com.meltingb.medicare.data.Document
import com.meltingb.medicare.databinding.FragmentMapBinding
import com.meltingb.medicare.utils.*
import com.meltingb.medicare.view.adapter.CustomCalloutBalloonAdapter
import com.meltingb.medicare.view.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.fragment_map.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.koin.androidx.viewmodel.ext.android.viewModel


class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), LocationListener {

    lateinit var mMapView: MapView
    lateinit var mLocationManager: LocationManager
    lateinit var mAdView: AdView

    private val viewModel: MapViewModel by viewModel()
    private var mapList = mutableListOf<Document>()

    companion object {
        const val MIN_DISTANCE_CHANGE_UPDATES = 10f
        const val MIN_TIME_UPDATE: Long = 60000
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mAdView = view.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        viewModel.navigationLiveData.observe(viewLifecycleOwner, ::navigate)
        // init Map
        mMapView = MapView(requireActivity())
        binding.mapView.addView(mMapView)
        getLocation()

        viewModel.hospitalListLiveData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                it.forEach {
                    mapList.add(it)
                }
                viewModel.allListLiveData.value = mapList
                setMarkers(it, CATEGORY_CODE_HP)
            }
        })

        viewModel.pharmacyListLiveData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                it.forEach {
                    mapList.add(it)
                }
                viewModel.allListLiveData.value = mapList
                setMarkers(it, CATEGORY_CODE_PM)
            }
        })

        viewModel.allListLiveData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                mMapView.setCalloutBalloonAdapter(CustomCalloutBalloonAdapter(layoutInflater, it))
            }
        })
    }

    private fun initMapView(lat: Double, long: Double) {
        mMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(lat, long), 4, true)
        val marker = MapPOIItem()
        marker.itemName = "Default Marker"
        marker.tag = 0
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(lat, long)
        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.isShowCalloutBalloonOnTouch = false
        marker.isShowDisclosureButtonOnCalloutBalloon = false
        mMapView.addPOIItem(marker)

        viewModel.getHospital(long.toString(), lat.toString())
        viewModel.getPharmacy(long.toString(), lat.toString())
    }

    private fun setMarkers(data: List<Document>, code: String) {
        val markers = mutableListOf<MapPOIItem>()
        for (place in data) {
            val marker = MapPOIItem().apply {
                itemName = place.place_name ?: ""
                tag = place.id.toInt()
                mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                markerType = MapPOIItem.MarkerType.CustomImage
                when (code) {
                    CATEGORY_CODE_HP -> {
                        customImageResourceId = R.drawable.ic_marker_hp
                    }
                    CATEGORY_CODE_PM -> {
                        customImageResourceId = R.drawable.ic_marker_pm
                    }
                }
                setCustomImageAnchor(0.5f, 1.0f)
            }
            markers.add(marker)
        }
        mMapView.addPOIItems(markers.toTypedArray())
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Common.showRequestPermission(requireContext())
            return
        } else {
            mLocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) return

            if (isNetworkEnabled) {
                mLocationManager.requestLocationUpdates(NETWORK_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_CHANGE_UPDATES, this)
                val location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//                if(location != null) initMapView(location!!.latitude, location!!.longitude)
            }
            if (isGPSEnabled) {
                mLocationManager.requestLocationUpdates(GPS_PROVIDER, MIN_TIME_UPDATE, MIN_DISTANCE_CHANGE_UPDATES, this)
                val location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) initMapView(location!!.latitude, location!!.longitude)
            }
        }
    }

    override fun onLocationChanged(location: Location) {

    }

    private fun navigate(event: NavigationEvent) {
        when (event) {
            NavigationEvent.HomeView -> findNavController().popBackStack()
            NavigationEvent.HelpView -> findNavController().navigate(R.id.action_mapFragment_to_takeDetailFragment)
            NavigationEvent.SearchView -> findNavController().navigate(R.id.action_mapFragment_to_searchFragment)
            NavigationEvent.AddView -> findNavController().navigate(R.id.action_mapFragment_to_addFragment)
        }
    }
}