package com.example.carsharingapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mapbox.maps.extension.style.expressions.dsl.generated.switchCase

class CarSpecsAdapter(private val array:ArrayList<CarSpecs>,private val context: Context): RecyclerView.Adapter<CarSpecsAdapter.AdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.rent_car_items,
            parent,
            false
        )
        return AdapterViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.label.text = array[position].label
        holder.value.text = "${array[position].value}${array[position].unit}"
        holder.icon.setImageResource(when(array[position].id){
            1 -> R.drawable.fuel_icon
            2 -> R.drawable.body_icon
            3 -> R.drawable.seats_icon
            4 -> R.drawable.shifter_icon
            5 -> R.drawable.color_icon
            6 -> R.drawable.year_icon
            7 -> R.drawable.miles_icon
            8 -> R.drawable.weight_icon
            9 -> R.drawable.power_icon
            10 -> R.drawable.consumption_icon
            11 -> R.drawable.fuel_capacity_icon
            12 -> R.drawable.engine_icon
            else -> {R.drawable.fuel_icon}
        })
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class AdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val label:TextView = itemView.findViewById(R.id.rent_carItem_label)
        val value:TextView = itemView.findViewById(R.id.rent_carItem_value)
        val icon: ImageView = itemView.findViewById(R.id.rent_carItem_icon)
    }
}

class RentImagesAdapter(private val array: Array<String>?, private val context: Context):RecyclerView.Adapter<RentImagesAdapter.AdapterViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.images_item,
            parent,
            false
        )
        return AdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.image.load(ServiceBuilder.BASE_URL+"images/images/"+array!![position])
    }

    override fun getItemCount(): Int {
        return array!!.size
    }

    inner class AdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val image:ImageView = itemView.findViewById(R.id.image_display)
    }
}

class TermsOfLoanAdapter(private val array: ArrayList<CarTerms>, private val context: Context):RecyclerView.Adapter<TermsOfLoanAdapter.AdapterViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TermsOfLoanAdapter.AdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.terms_of_loan_item,
            parent,
            false
        )
        return AdapterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TermsOfLoanAdapter.AdapterViewHolder, position: Int) {
        holder.label.text = array!![position].label
        if(array[position].value == 1)
            holder.img.setImageResource(R.drawable.check_icon)
        else
            holder.img.setImageResource(R.drawable.not_icon)
    }

    override fun getItemCount(): Int {
        return array!!.size
    }
    inner class AdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val label:TextView = itemView.findViewById(R.id.terms_label)
        val img:ImageView = itemView.findViewById(R.id.terms_icon)
    }

}