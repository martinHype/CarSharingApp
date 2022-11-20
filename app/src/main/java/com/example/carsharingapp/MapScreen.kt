package com.example.carsharingapp


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.example.carsharingapp.databinding.ActivityMapScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.compat.LocationEngineProvider
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.util.*

class MapScreen : AppCompatActivity(),PermissionsListener{
    private lateinit var permissionsManager:PermissionsManager
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    private var binding:ActivityMapScreenBinding? = null
    private val carArray:MutableMap<Int,Car_model> = mutableMapOf()
    private var drawerOpen:Boolean = false
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener{
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener{
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener{
        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
            TODO("Not yet implemented")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        fillCars();

        mapView = findViewById(R.id.mapView)
        mapboxMap = mapView.getMapboxMap()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        viewAnnotationManager = binding?.mapView?.viewAnnotationManager!!
        if(PermissionsManager.areLocationPermissionsGranted(this)){
            onMapReady()
        }else{
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }


        binding?.floatingButton?.setOnClickListener {
            getLocation()
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
        mFusedLocationClient.lastLocation.addOnCompleteListener(this) {
            val location: Location? = it.result
            if(location != null){
                mapboxMap.flyTo(
                    cameraOptions {
                        center(Point.fromLngLat(location.longitude,location.latitude))
                            .zoom(17.0)
                            .bearing(180.0)
                    },
                    MapAnimationOptions.mapAnimationOptions {
                        duration(7000)
                    }
                )
            }
        }

    }

    private fun fillCars() {
        carArray.put(1,Car_model(Point.fromLngLat(18.155048825458078,47.98636542288173),"Mercedes Benz","C-class sedan","NZ-111AB",R.drawable.car,R.drawable.mercedes,"Janko Hrasko","Michalská bašta 87, 940 01 \n Nové Zámky",20,false))
        carArray.put(2,Car_model(Point.fromLngLat(18.15618348106757,47.984885746757826),"Škoda","SuperB","NZ-123CB",R.drawable.skodacar,R.drawable.skoda,"Anton Jaky","Andovska 80, 940 01 \n Nové Zámky",15,false))
    }

    private fun onMapReady() {
        mapView.scalebar.enabled = false
        mapView.compass.enabled = false
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
            try{
                for(key in carArray.keys){
                    var carModel = carArray[key]
                    if (carModel != null) {
                        addAnnotationView(carModel.location,"${carModel.price_perDay.toString()}€/h",carModel.markerImage,key)
                    }
                }
                //addAnnotationView(Point.fromLngLat(18.155048825458078,47.98636542288173),"20€/h",R.drawable.mercedes,"1")
                //addAnnotationView(Point.fromLngLat( 18.15618348106757,47.984885746757826),"15€/h",R.drawable.skoda,"2")
            }catch (exception:Exception){
                Toast.makeText(this,exception.message.toString(),Toast.LENGTH_LONG).show()
            }


        }
    }


    private fun addAnnotationView(point:Point,text:String,resource:Int,id:Int){
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.marker,
            options = viewAnnotationOptions {
                geometry(point)
                allowOverlap(true)
            }
        )
        viewAnnotation.tag = id

        val textView = viewAnnotation.findViewById<TextView>(R.id.annotation)
        textView.text = text
        viewAnnotation.setOnClickListener{
            setUpCar(it.tag as Int)

            if(!drawerOpen){
                binding?.button?.callOnClick()
                drawerOpen = true
            }
        }
        val imageView = viewAnnotation.findViewById<ImageView>(R.id.brandImage)
        imageView.setImageResource(resource)
    }


    @SuppressLint("SetTextI18n")
    private fun setUpCar(key:Int){
        var carModel = carArray[key]
        if(carModel != null){
            binding?.txtCarBrand?.text = carModel.car_brand
            binding?.txtCarModel?.text = carModel.car_model
            binding?.txtLicensePlate?.text = carModel.car_licensePlate
            binding?.imageViewCar?.setImageResource(carModel.image)
            binding?.txtCarOwner?.text = carModel.owner
            binding?.txtActualLocation?.text = carModel.adress
            binding?.txtPricePerDay?.text = "${carModel.price_perDay}€"
        }

    }


    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(this@MapScreen,R.drawable.user_puck),
                shadowImage = AppCompatResources.getDrawable(this@MapScreen,R.drawable.user_puck_shadow),
                scaleExpression = interpolate { linear()
                                                zoom()
                                                stop {
                                                    literal(0.0)
                                                    literal(0.6)
                                                }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)

    }


    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onBackPressed() {
        if(drawerOpen){
            binding?.button?.callOnClick()
            drawerOpen = false
        }
    }
    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        TODO("Not yet implemented")
    }

    override fun onPermissionResult(granted: Boolean) {
        TODO("Not yet implemented")
    }


}
