package com.example.carsharingapp

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.example.carsharingapp.databinding.ActivityCreateCarBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.scalebar.scalebar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException

var mapView: MapView? = null
var mapView2 : MapView? = null

class create_car : AppCompatActivity(),AdapterView.OnItemSelectedListener, recyclerViewEvent {
    //get actual location
    private  lateinit var fusedLoactionClient: FusedLocationProviderClient
    val response = ServiceBuilder.buildService(APIInterface::class.java)
    private var binding: ActivityCreateCarBinding? = null
    var brands = arrayOf("")
    var models = arrayOf("")
    var arrayAdapter:ArrayAdapter<Any>? = null
    var parentArrayAdapter:ArrayAdapter<Any>? = null
    lateinit var activityResultLauncher:ActivityResultLauncher<Intent>
    //adding pictures
    var boolean = false
    lateinit var bitmap:Bitmap
    var array:ArrayList<Uri> = mutableListOf<Uri>() as ArrayList<Uri>
    lateinit var adapter: Adapter
    var showspecs = false
    lateinit var actualPosition:Point

    @SuppressLint("NotifyDataSetChanged", "MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCarBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val fuelArrayAdapter = ArrayAdapter.createFromResource(this,
            R.array.fuel_type,
            android.R.layout.simple_spinner_item)
        fuelArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.fuelInputText?.adapter = fuelArrayAdapter

        val bodyArrayAdapter = ArrayAdapter.createFromResource(this,
            R.array.body_type,
            android.R.layout.simple_spinner_item)
        bodyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.bodyInputText?.adapter = bodyArrayAdapter

        val shifterArrayAdapter = ArrayAdapter.createFromResource(this,
            R.array.shifter_type,
            android.R.layout.simple_spinner_item)
        shifterArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding?.shifterInputText?.adapter = shifterArrayAdapter


        var mapVisible = false
        val layout = binding?.layout
        layout?.visibility = View.GONE

        mapView2 = binding?.createCarMap
        var mapBox2 = mapView2?.getMapboxMap()
        mapBox2?.loadStyleUri(Style.MAPBOX_STREETS)
        mapView2?.scalebar?.enabled = false
        mapView2?.compass?.enabled = false
        mapBox2?.setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapBox2?.loadStyleUri(
            Style.MAPBOX_STREETS
        )
        fusedLoactionClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLoactionClient.lastLocation.addOnSuccessListener {
            mapBox2?.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(it.longitude,it.latitude))
                        .zoom(15.0)
                        .bearing(360.0)
                        .build()
                },
                MapAnimationOptions.mapAnimationOptions {
                    duration(100)
                }
            )
            actualPosition = Point.fromLngLat(it.longitude,it.latitude)
            getAddressOfPoistion()
        }



        mapView = binding?.mapView
        var mapbox = mapView?.getMapboxMap()
        mapbox?.loadStyleUri(Style.MAPBOX_STREETS)
        mapView?.scalebar?.enabled = false
        mapView?.compass?.enabled = false
        mapbox?.setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .bearing(210.0)
                .build()
        )
        mapbox?.loadStyleUri(
            Style.MAPBOX_STREETS
        )

        mapBox2?.addOnMapClickListener(OnMapClickListener {
                mapbox?.flyTo(
                    cameraOptions {
                        center(Point.fromLngLat(actualPosition.longitude(),actualPosition.latitude()))
                            .zoom(17.0)
                            .bearing(360.0)
                            .build()
                    },
                    MapAnimationOptions.mapAnimationOptions {
                        duration(100)
                    }
                )
            layout?.visibility = View.VISIBLE
            mapVisible = true
            return@OnMapClickListener true
        })


        //onclick event when map is displayed to select location

        //receive address from api by latitude and longitude
        binding?.selectPosition?.setOnClickListener {
            val position = mapbox?.cameraState?.center
            actualPosition = position!!

            mapBox2?.flyTo(
                cameraOptions {
                    center(Point.fromLngLat(actualPosition.longitude(),actualPosition.latitude()))
                        .zoom(15.0)
                        .bearing(360.0)
                        .build()
                },
                MapAnimationOptions.mapAnimationOptions {
                    duration(100)
                }
            )

            getAddressOfPoistion()
            binding?.layout?.visibility = View.GONE


        }

        //close the pop up of the image
        binding?.closeImage?.setOnClickListener {
            binding?.popUpImage?.visibility = View.GONE
            it.visibility = View.GONE
        }



        //receive images from local gallery and store them in array<Uri>
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback {
                    if(it.resultCode == RESULT_OK){
                        val clipData: ClipData? = it.data?.clipData
                        val cout = clipData?.itemCount
                        var string = ""
                        for(i in 0 until cout!!){
                            var image = clipData.getItemAt(i).uri
                            array.add(image)
                        }
                        adapter.notifyDataSetChanged()
                    }
                })

        //recycler view
        val uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(com.example.carsharingapp.R.drawable.add_image)+ "/" + resources.getResourceTypeName(com.example.carsharingapp.R.drawable.add_image) + "/"+ resources.getResourceEntryName(com.example.carsharingapp.R.drawable.add_image))
        array.add(uri)
        val layoutManager = GridLayoutManager(this,4)
        binding?.recyclerView?.layoutManager = layoutManager
        adapter = Adapter(array,this,contentResolver,this)
        binding?.recyclerView?.adapter = adapter


        //api call to receive all brands from database

        response.getBrands()
            .enqueue(object : Callback<Brands>{
                override fun onResponse(call: Call<Brands>, response: retrofit2.Response<Brands>) {
                    if(response.isSuccessful){
                        brands = response.body()!!.brands
                        parentArrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, brands)
                        parentArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        with(binding?.brandDropdown){
                            this?.adapter = parentArrayAdapter
                            this?.onItemSelectedListener = this@create_car
                        }
                    }
                }

                override fun onFailure(call: Call<Brands>, t: Throwable) {
                    Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        parentArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, brands)
        parentArrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        //setup adapter for dropdown and itemselector
        with(binding?.brandDropdown){
            this?.adapter = parentArrayAdapter
            this?.onItemSelectedListener = this@create_car
        }

        //send post request to create new car in database and upload selected images
        binding?.registerCarButton?.setOnClickListener {
            val brand = binding?.brandDropdown?.selectedItem.toString()
            val model = binding?.modelDropdown?.selectedItem.toString()
            val registrationPlate = binding?.regPlateInputText?.text.toString()
            val price = binding?.priceInputText?.text.toString()
            //convert images from array to string and prepare for POST request
            lateinit var byteArrayOutputStream: ByteArrayOutputStream
            lateinit var bytes:ByteArray
            lateinit var base64Image:String
            var counter = 0
            for(item in array){
                if(counter != 0){
                    try {
                        byteArrayOutputStream = ByteArrayOutputStream()
                        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, item)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                        bytes = byteArrayOutputStream.toByteArray()
                        base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)


                    }catch (e:IOException){}
                }
                counter++

            }
            response.createCar(CarRegister(brand,model,"yellow","2022",ServiceBuilder.currentUser?.email.toString(),registrationPlate,price.toInt(),actualPosition.latitude(),actualPosition.longitude()))
                .enqueue(object : Callback<Response>{
                    override fun onResponse(
                        call: Call<Response>,
                        response: retrofit2.Response<Response>
                    ) {
                        if(response.isSuccessful){
                            addImages(response.body()?.response)
                            addSpecs(response.body()?.response)
                            addTerms(response.body()?.response)
                        }
                    }

                    override fun onFailure(call: Call<Response>, t: Throwable) {
                        Toast.makeText(applicationContext,t.message,Toast.LENGTH_SHORT).show()
                    }
                })

        }




    }



    private fun getAddressOfPoistion(){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.mapbox.com/geocoding/v5/mapbox.places/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val url:String = "${actualPosition.longitude()},${actualPosition.latitude()}.json?"
        val response = retrofit.create(APIInterface::class.java)
        response.getAdress(url,"pk.eyJ1IjoibWFydGluYmF0IiwiYSI6ImNsMnB6a2Z1YTBidjgzZnBjeXhleGUzZDQifQ.YuflIZcligQ5Yr1or55iUg")
            .enqueue(object : Callback<MapboxResponse>{
                override fun onResponse(
                    call: Call<MapboxResponse>,
                    response: retrofit2.Response<MapboxResponse>
                ) {
                    if(response.isSuccessful){
                        binding?.showmap?.text = "${response?.body()?.features?.get(0)?.place_name}"
                    }
                }

                override fun onFailure(call: Call<MapboxResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }


    //method that will upload images in database
    private fun addImages(car_id: String?) {
        var images:MutableList<Image> = mutableListOf()
        lateinit var byteArrayOutputStream: ByteArrayOutputStream
        lateinit var bytes:ByteArray
        lateinit var base64Image:String
        var counter = 0
        for(item in array){
            if(counter != 0){
                try {
                    byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, item)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    bytes = byteArrayOutputStream.toByteArray()
                    base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)
                    images.add(Image("${car_id}_$counter",base64Image))
                }catch (e:IOException){}
            }
            counter++
        }
        
        response.uploadImage(UploadImage(car_id!!.toInt(),images))
            .enqueue(object :Callback<Response>{
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    Toast.makeText(applicationContext, "${response.body()?.response}", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })

    }


    private fun addSpecs(car_id: String?){
        var list:ArrayList<Specs> = mutableListOf<Specs>() as ArrayList<Specs>
        list.add(Specs(1,binding?.fuelInputText?.selectedItem.toString()))
        list.add(Specs(2,binding?.bodyInputText?.selectedItem.toString()))
        list.add(Specs(3,binding?.seatsInputText?.text.toString()))
        list.add(Specs(4,binding?.shifterInputText?.selectedItem.toString()))
        list.add(Specs(5,binding?.colorInputText?.text.toString()))
        list.add(Specs(6,binding?.yearInputText?.text.toString()))
        list.add(Specs(7,binding?.milesInputText?.text.toString()))
        list.add(Specs(8,binding?.weightInputText?.text.toString()))
        list.add(Specs(9,binding?.powerInputText?.text.toString()))
        list.add(Specs(10,binding?.consumptionInputText?.text.toString()))
        list.add(Specs(11,binding?.fuelCapacityInputText?.text.toString()))
        list.add(Specs(12,binding?.engineInputText?.text.toString()))

        val response = ServiceBuilder.buildService(APIInterface ::class.java)
        response.addSpecs(ServiceBuilder.currentUser!!.token,postSpecs(car_id!!.toInt(),list))
            .enqueue(object : Callback<Response>{
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    if (response.isSuccessful){
                        Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }


    private fun addTerms(car_id: String?){
        var list:ArrayList<Terms> = mutableListOf<Terms>() as ArrayList<Terms>
        list.add(Terms(1,if(binding?.chceckBox1?.isChecked == true)1 else 0))
        list.add(Terms(2,if(binding?.chceckBox2?.isChecked == true)1 else 0))
        list.add(Terms(3,if(binding?.chceckBox3?.isChecked == true)1 else 0))
        list.add(Terms(4,if(binding?.chceckBox4?.isChecked == true)1 else 0))

        val response = ServiceBuilder.buildService(APIInterface ::class.java)
        response.addTerms(ServiceBuilder.currentUser!!.token,postTerms(car_id!!.toInt(),list))
            .enqueue(object : Callback<Response>{
                override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
                ) {
                    if (response.isSuccessful){
                        Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<Response>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }



    fun loadImages(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        intent.setAction(Intent.ACTION_GET_CONTENT)
        activityResultLauncher.launch(intent)
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            response.getModels(Brand(brands[p2]))
                .enqueue(object : Callback<Models>{
                    override fun onResponse(call: Call<Models>, response: retrofit2.Response<Models>) {
                        if(response.isSuccessful){
                            models = response.body()!!.models
                            arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_item, models)
                            arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            with(binding?.modelDropdown){
                                this?.adapter = arrayAdapter
                            }
                        }
                    }

                    override fun onFailure(call: Call<Models>, t: Throwable) {
                        Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })



    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemClick(position: Int) {
        array.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

    override fun onImageClick(position: Int) {
        if(position == 0){
            loadImages()
        }else{
            try{
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, array[position])
                binding?.popUpImage?.setImageBitmap(bitmap)
                binding?.closeImage?.visibility = View.VISIBLE
                binding?.popUpImage?.visibility = View.VISIBLE

            }catch (e: IOException){
                e.printStackTrace()
            }
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
        mapView?.onDestroy()
    }
}
