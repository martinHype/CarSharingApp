package com.example.carsharingapp

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

var mapViewRent: MapView? = null
class RentActivity : AppCompatActivity(),recyclerViewDateEvent {

    private var binding: ActivityRentBinding? = null
    private var array:ArrayList<CarSpecs> = mutableListOf<CarSpecs>() as ArrayList<CarSpecs>
    private var termsArray:ArrayList<CarTerms> = mutableListOf<CarTerms>() as ArrayList<CarTerms>

    var arrayOfDates:ArrayList<ArrayList<Dates>> = mutableListOf<ArrayList<Dates>>() as ArrayList<ArrayList<Dates>>
    var arrayOfRented:ArrayList<rangeDates> = mutableListOf<ArrayList<rangeDates>>() as ArrayList<rangeDates>


    lateinit var adapter: CarSpecsAdapter
    lateinit var adapterImages: RentImagesAdapter
    lateinit var termsAdapter:TermsOfLoanAdapter
    lateinit var viewAnnotationManager: ViewAnnotationManager
    var start_date:LocalDate? = null
    var end_date:LocalDate? = null
    var start_position = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ResourceType", "SetTextI18n")
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


        val response = ServiceBuilder.buildService(APIInterface::class.java)
        response.getRentedDays(ServiceBuilder.currentUser!!.token,CarId(carModel!!.car_id))
            .enqueue(object : Callback<List<rangeDatesResponse>>{
                override fun onResponse(
                    call: Call<List<rangeDatesResponse>>,
                    response: Response<List<rangeDatesResponse>>
                ) {
                    if(response.isSuccessful){
                        val list = response.body()
                        if(list != null){
                            for(item in list){
                                arrayOfRented.add(rangeDates(LocalDate.parse(item.start_date),
                                    LocalDate.parse(item.end_date)))
                            }
                        }
                        fillDates()
                        disableFields()
                        setUpRecyclerView()
                    }
                }

                override fun onFailure(call: Call<List<rangeDatesResponse>>, t: Throwable) {
                    Toast.makeText(applicationContext,"${t.message}",Toast.LENGTH_SHORT).show()
                }
            })



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

        binding?.datePickerButton?.setOnClickListener {

            try{
                val datesAdapter = AdapterMonth(arrayOfDates,applicationContext,this, start_date, end_date)
                binding?.dateRecyclerview?.adapter = datesAdapter
            }catch (e:Exception){
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }
            binding?.datePicker?.visibility = View.VISIBLE
        }

        binding?.closeButtonDatePicker?.setOnClickListener {
            binding?.datePicker?.visibility = View.GONE
            start_date = null
            end_date = null
        }

        binding?.saveDatesButton?.setOnClickListener {
            binding?.dateOfRental?.text = "${start_date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${end_date?.format(
                DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
            binding?.datePicker?.visibility = View.GONE
        }


        binding?.rentCarButton?.setOnClickListener {
            val car_id = carModel.car_id
            val user_email = ServiceBuilder.currentUser!!.email
            val startDate:String = start_date.toString()
            val endDate:String = end_date.toString()
            response.rentCar(ServiceBuilder.currentUser!!.token,RentCar(car_id,user_email,startDate,endDate))
                .enqueue(object :Callback<com.example.carsharingapp.Response>{
                    override fun onResponse(
                        call: Call<com.example.carsharingapp.Response>,
                        response: Response<com.example.carsharingapp.Response>
                    ) {
                        if(response.isSuccessful){
                            Toast.makeText(applicationContext, "${response.body()?.response}", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onFailure(
                        call: Call<com.example.carsharingapp.Response>,
                        t: Throwable
                    ) {
                        Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

        }



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

        response.getSpecs(ServiceBuilder.currentUser!!.token,CarId(carModel.car_id))
            .enqueue(object : Callback<ResponseSpecs>{
                override fun onResponse(
                    call: Call<ResponseSpecs>,
                    response: Response<ResponseSpecs>
                ) {
                    if(response.isSuccessful){
                        val specsList = response.body()?.specs
                        if(specsList != null){
                            for(item in specsList){
                                array.add(item)
                            }
                        }
                        val layoutManager = GridLayoutManager(applicationContext,1)
                        binding?.rentCarSpecsRecyclerView?.layoutManager = layoutManager
                        adapter = CarSpecsAdapter(array,applicationContext)
                        binding?.rentCarSpecsRecyclerView?.adapter = adapter

                        val termsList = response.body()?.terms
                        if(termsList != null){
                            for(term in termsList){
                                termsArray.add(term)
                            }
                        }
                        val termsLayoutManager = GridLayoutManager(applicationContext,1)
                        binding?.rentRecyclerViewTerms?.layoutManager = termsLayoutManager
                        termsAdapter = TermsOfLoanAdapter(termsArray,applicationContext)
                        binding?.rentRecyclerViewTerms?.adapter = termsAdapter
                    }

                }

                override fun onFailure(call: Call<ResponseSpecs>, t: Throwable) {
                    Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_LONG).show()
                }
            })

    }

    private fun setUpRecyclerView() {
        try{
            val layoutManager = LinearLayoutManagerWithSmoothScroller(applicationContext,
                LinearLayoutManager.HORIZONTAL,false)
            binding?.dateRecyclerview?.layoutManager = layoutManager
            val datesAdapter = AdapterMonth(arrayOfDates,applicationContext,this, start_date, end_date)
            binding?.dateRecyclerview?.adapter = datesAdapter
            PagerSnapHelper().attachToRecyclerView(binding?.dateRecyclerview)
        }catch (e:Exception){
            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun disableFields(){
        for(item in arrayOfRented) {
            val startDateMonth = item.start_date.month.value
            var startDateDay = item.start_date.dayOfMonth
            val endDateMonth = item.end_date.month.value
            var endDateDay = item.end_date.dayOfMonth
            if(startDateMonth == endDateMonth){
                val dayOfWeek = LocalDate.of(2023,startDateMonth,1).dayOfWeek.value
                startDateDay += (dayOfWeek-2)
                endDateDay += (dayOfWeek-2)
                var monthArray = arrayOfDates[startDateMonth-1]
                for(i in startDateDay .. endDateDay){
                    var selectedItem = monthArray[i]
                    selectedItem.disabled = true
                    monthArray[i] = selectedItem
                }
                arrayOfDates[startDateMonth-1] = monthArray
            }else{
                for(i in startDateMonth .. endDateMonth){
                    val dayOfWeek = LocalDate.of(2023,i,1).dayOfWeek.value
                    var monthArray = arrayOfDates[i-1]
                    if(i == startDateMonth){
                        startDateDay += (dayOfWeek-2)

                        for(j in startDateDay until monthArray.size){
                            var selectedItem = monthArray[j]
                            selectedItem.disabled = true
                            monthArray[j] = selectedItem
                        }
                    }else if(i == endDateMonth){
                        for(j in dayOfWeek-1 .. (endDateDay+(dayOfWeek-2))){
                            var selectedItem = monthArray[j]
                            selectedItem.disabled = true
                            monthArray[j] = selectedItem
                        }
                    }else{
                        var text = ""
                        for(j in dayOfWeek-1 until monthArray.size){
                            var selectedItem = monthArray[j]
                            selectedItem.disabled = true
                            monthArray[j] = selectedItem
                        }
                    }
                    arrayOfDates[i-1] = monthArray
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public fun fillDates(){
        for (i in 1 .. 5){
            val maxDay = YearMonth.of(2023,i)
            val dayOfWeek = LocalDate.of(2023,i,1).dayOfWeek.value
            val tempArray = mutableListOf<Dates>() as ArrayList<Dates>
            for (j in 2-dayOfWeek .. maxDay.lengthOfMonth()){

                var date = if(j <= 0){
                    Dates(-1, LocalDate.MIN)
                }else{
                    Dates(j,LocalDate.of(2023,i,j))
                }
                tempArray.add(date)
            }
            arrayOfDates.add(tempArray)
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onItemClick(date: LocalDate, position: Int) {
        if(start_date == null && end_date == null){
            start_date = date
            start_position = position
        }else if(end_date == null && start_date!! < date){
            var bool = false
            if(start_date!!.month.value == date.month.value){
                var monthDays = arrayOfDates[position]
                val dayOfWeek = LocalDate.of(2023,position+1,1).dayOfWeek.value
                var startDay = start_date!!.dayOfMonth + (dayOfWeek-1)
                var endDay = date.dayOfMonth + (dayOfWeek-3)
                for(i in startDay .. endDay){
                    if(monthDays[i].disabled){
                        bool = true
                        break
                    }
                }
            }else{
                for(month in start_date!!.month.value .. date.month.value){
                    var actualMonth = arrayOfDates[month-1]
                    val dayOfWeek = LocalDate.of(2023,month,1).dayOfWeek.value
                    if(month == start_date!!.month.value){
                        var startDay = start_date!!.dayOfMonth+(dayOfWeek-2)
                        for(i in startDay until actualMonth.size){
                            if(actualMonth[i].disabled){
                                bool = true
                                break
                            }
                        }
                    }else if(month == date.month.value){
                        var endDay = date.dayOfMonth + (dayOfWeek-2)
                        for(i in (dayOfWeek-1) .. endDay){
                            if(actualMonth[i].disabled){
                                bool = true
                                break
                            }
                        }
                    }else{
                        for(i in (dayOfWeek-1) until actualMonth.size){
                            if(actualMonth[i].disabled){
                                bool = true
                                break
                            }
                        }
                    }
                    if(bool){
                        break
                    }

                }
            }

            if(!bool){
                end_date = date
                start_position = position

            }else{
                start_date = date
                start_position = position
            }

        }else{
            start_date = date
            start_position = position
            end_date = null
        }

        binding?.dates?.text = "${start_date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))} - ${if(end_date == null){"?"}else end_date?.format(
            DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        val adapter = AdapterMonth(arrayOfDates,applicationContext,this,start_date,end_date)
        binding?.dateRecyclerview?.layoutManager?.scrollToPosition(start_position)
        binding?.dateRecyclerview?.adapter = adapter
    }


}