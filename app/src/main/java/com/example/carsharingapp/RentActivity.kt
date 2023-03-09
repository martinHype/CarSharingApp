package com.example.carsharingapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import com.example.carsharingapp.databinding.ActivityRentBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var mapViewRent: MapView? = null
class RentActivity : AppCompatActivity() {

    private var binding: ActivityRentBinding? = null
    private var array:ArrayList<CarSpecs> = mutableListOf<CarSpecs>() as ArrayList<CarSpecs>
    private var termsArray:ArrayList<Terms> = mutableListOf<Terms>() as ArrayList<Terms>
    lateinit var adapter: CarSpecsAdapter
    lateinit var adapterImages: RentImagesAdapter
    lateinit var termsAdapter:TermsOfLoanAdapter
    lateinit var viewAnnotationManager: ViewAnnotationManager

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRentBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(applicationContext,R.color.primaryBlue)

        var carModel = ServiceBuilder.actualCarModel
        binding?.txtBrandRent?.text = carModel?.car_brand
        binding?.txtModelRent?.text = carModel?.car_model
        binding?.txtPlateRent?.text = carModel?.car_licensePlate
        binding?.txtPlateRent?.text = carModel?.car_licensePlate
        binding?.txtOwnerRent?.text = carModel?.owner
        binding?.rentBrandImage?.load(ServiceBuilder.BASE_URL+"images/brands/"+carModel?.brand_url)
        binding?.rentActualLocation?.text = carModel?.adress

        mapViewRent = binding?.rentMap
        var mapbox = mapViewRent?.getMapboxMap()
        mapbox?.loadStyleUri(Style.MAPBOX_STREETS)
        mapViewRent?.scalebar?.enabled = false
        mapViewRent?.compass?.enabled = false
        viewAnnotationManager = binding?.rentMap?.viewAnnotationManager!!
        mapbox?.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(carModel!!.longitude, carModel.latitude))
                .zoom(14.0)
                .bearing(180.0)
                .build()
        )
        mapbox?.loadStyleUri(
            Style.MAPBOX_STREETS
        )
        viewAnnotationManager.addViewAnnotation(
            R.layout.car_marker,
            viewAnnotationOptions{
                geometry(Point.fromLngLat(carModel!!.longitude, carModel.latitude))
                allowOverlap(true)
            }
        )




        val response = ServiceBuilder.buildService(APIInterface::class.java)
        response.getImages(ServiceBuilder.currentUser!!.token,CarId(carModel!!.car_id))
            .enqueue(object : Callback<CarImages> {
                override fun onResponse(call: Call<CarImages>, response: Response<CarImages>) {
                    val layoutManager = GridLayoutManager(applicationContext,4)
                    binding?.rentRecyclerViewImages?.layoutManager = layoutManager
                    adapterImages = RentImagesAdapter(response?.body()?.images,applicationContext)
                    binding?.rentRecyclerViewImages?.adapter = adapterImages
                }

                override fun onFailure(call: Call<CarImages>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })

        response.getSpecs(ServiceBuilder.currentUser!!.token,CarId(carModel!!.car_id))
            .enqueue(object : Callback<List<CarSpecs>>{
                override fun onResponse(
                    call: Call<List<CarSpecs>>,
                    response: Response<List<CarSpecs>>
                ) {
                    if(response.isSuccessful){
                        val list = response.body()
                        if(list != null){
                            for(item in list){
                                array.add(item)
                            }
                        }
                        val layoutManager = GridLayoutManager(applicationContext,1)
                        binding?.rentCarSpecsRecyclerView?.layoutManager = layoutManager
                        adapter = CarSpecsAdapter(array,applicationContext)
                        binding?.rentCarSpecsRecyclerView?.adapter = adapter
                    }

                }

                override fun onFailure(call: Call<List<CarSpecs>>, t: Throwable) {
                    Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_LONG).show()
                }
            })




        fillTerms()
        val termsLayoutManager = GridLayoutManager(this,1)
        binding?.rentRecyclerViewTerms?.layoutManager = termsLayoutManager
        termsAdapter = TermsOfLoanAdapter(termsArray,applicationContext)
        binding?.rentRecyclerViewTerms?.adapter = termsAdapter




    }


    private fun fillTerms() {
        termsArray.add(Terms("Driver is required to be older than 25",false))
        termsArray.add(Terms("Toll sticker",true))
        termsArray.add(Terms("trip abroad",true))
        termsArray.add(Terms("Pets allowed",false))
        termsArray.add(Terms("Somking inside the vehicle",false))

    }


}