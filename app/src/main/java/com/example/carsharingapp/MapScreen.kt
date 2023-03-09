package com.example.carsharingapp



import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.drawerlayout.widget.DrawerLayout
import coil.load
import com.example.carsharingapp.databinding.ActivityMapScreenBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.expressions.dsl.generated.switchCase
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import java.util.*

class MapScreen : AppCompatActivity(),PermissionsListener,NavigationView.OnNavigationItemSelectedListener{
    private lateinit var permissionsManager:PermissionsManager
    private lateinit var mapView: MapView
    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var drawerLayout:DrawerLayout
    private lateinit var carModel:Car_model

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

        drawerLayout = binding?.root!!

        val navigationView:NavigationView = findViewById(R.id.nav_view)
        val headerView:View = navigationView.getHeaderView(0)
        val navUserName:TextView = headerView.findViewById(R.id.user_name)
        val navEmail:TextView = headerView.findViewById(R.id.email)

        navUserName.text = ServiceBuilder.currentUser?.getFullName()
        navEmail.text = ServiceBuilder.currentUser?.email

        navigationView.setNavigationItemSelectedListener(this)
        navigationView.bringToFront()

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
        binding?.floatingMenuButton?.setOnClickListener {
            drawerLayout.open()
        }


        binding?.floatingButton?.setOnClickListener {
            getLocation()
        }

        binding?.imagebackground?.setOnClickListener {
            if(drawerOpen){
                binding?.button?.callOnClick()
                drawerOpen = false
            }
            drawerLayout.close()
        }

        binding?.rentCar?.setOnClickListener {

            val intent = Intent(applicationContext,RentActivity::class.java)
            ServiceBuilder.actualCarModel = carModel
            startActivity(intent)
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
        }
        fillCars()
    }


    fun fillCars(){
        val responde = ServiceBuilder.buildService(APIInterface :: class.java)
        responde.recieveCars(ServiceBuilder.currentUser!!.token).enqueue(object : Callback<List<Cars>>{
            override fun onResponse(
                call: Call<List<Cars>>,
                response: Response<List<Cars>>
            ) {
                if(response.code() == 200){
                    val listCars = response.body()
                    if (listCars != null) {
                        for(car in listCars){
                            carArray[car.car_id] = Car_model(
                                car.car_id,
                                car.brand,
                                car.model,
                                car.registration_plate,
                                car.image,
                                car.first_name + " " + car.last_name,
                                car.price,
                                false,
                                car.latitude,
                                car.longitude,
                                car.image_url
                            )
                            addAnnotationView(
                                Point.fromLngLat(car.longitude,car.latitude),
                                "${car.price}€/day",
                                car.image_url,
                                car.car_id
                            )
                        }
                    }
                    Toast.makeText(applicationContext, "User authorized", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(applicationContext, "User unauthorized", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<List<Cars>>, t: Throwable) {
                Toast.makeText(applicationContext,t.message,Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun addAnnotationView(point:Point,text:String,resource:String,id:Int){
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
        imageView.load(ServiceBuilder.BASE_URL+"images/brands/"+resource)
    }


    @SuppressLint("SetTextI18n")
    private fun setUpCar(key:Int){
        carModel = carArray[key]!!
        if(carModel != null){
            binding?.txtCarBrand?.text = carModel.car_brand
            binding?.txtCarModel?.text = carModel.car_model
            binding?.txtLicensePlate?.text = carModel.car_licensePlate
            binding?.imageViewCar?.load(ServiceBuilder.BASE_URL+"images/images/"+carModel.image)
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
        }else{
            super.onBackPressed()
        }
        drawerLayout.close()

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

    }

    override fun onPermissionResult(granted: Boolean) {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_logout -> logout()
            R.id.create_car -> {
                startActivity(Intent(this,create_car::class.java))
            }

        }
        return false
    }

    fun logout(){
        var value = UsersDb.logoutUser(applicationContext)
        finish()

    }


}
