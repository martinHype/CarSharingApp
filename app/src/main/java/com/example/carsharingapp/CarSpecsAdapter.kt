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
        holder.icon.setImageResource(array[position].icon)
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

class TermsOfLoanAdapter(private val array: ArrayList<Terms>, private val context: Context):RecyclerView.Adapter<TermsOfLoanAdapter.AdapterViewHolder>(){


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
        if(array[position].value)
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