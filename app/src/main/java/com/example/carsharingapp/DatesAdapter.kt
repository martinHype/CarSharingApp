package com.example.carsharingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class DatesAdapter(
    private val context: Context,
    private val array: ArrayList<Dates>,
    private val listener:recyclerViewDateEvent,
    private val start_date:LocalDate?,
    private val end_date:LocalDate?,
    private val monthPosition:Int
) : RecyclerView.Adapter<DatesAdapter.MyViewHolder>() {

    @SuppressLint("ResourceAsColor")
    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        lateinit var oneday:TextView
        lateinit var txtBackground:TextView
        lateinit var txtBackgroundRight:TextView
        lateinit var txtBackgroundLeft:TextView
        lateinit var layout: ConstraintLayout

        var date:LocalDate? = null
        init {
            oneday = itemView.findViewById(R.id.oneday)
            layout = itemView.findViewById(R.id.layout)
            txtBackground = itemView.findViewById(R.id.txtbackgorund)
            txtBackgroundRight = itemView.findViewById(R.id.txtbackgorundRight)
            txtBackgroundLeft = itemView.findViewById(R.id.txtbackgorundLeft)
            oneday.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                var text:TextView = v as TextView
                text.background = ContextCompat.getDrawable(context,R.drawable.background)
                if(v.id == R.id.oneday)
                    listener.onItemClick(this.date!!,monthPosition)
                else
                    listener.onItemClick(this.date!!,monthPosition)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.one_day,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(array[position].value > 0 && !array[position].disabled){
            holder.oneday.text = array[position].value.toString()
            if(start_date == array[position].date || end_date == array[position].date){
                holder.layout.background = ContextCompat.getDrawable(context,R.drawable.selected_date_background)
                holder.oneday.setTextColor(ContextCompat.getColor(context,R.color.white))
            }
            if(start_date != null && end_date != null){
                if(array[position].date > start_date && array[position].date < end_date){
                    holder.txtBackground.visibility = View.VISIBLE
                }
                if(array[position].date == start_date){
                    holder.txtBackgroundRight.visibility = View.VISIBLE
                }
                if(array[position].date == end_date){
                    holder.txtBackgroundLeft.visibility = View.VISIBLE

                }

            }
            holder.date = array[position].date
        }
        else {
            holder.oneday.text = if(array[position].value > 0){array[position].value.toString()}else " "
            holder.oneday.setTextColor(Color.GRAY)
            holder.oneday.isClickable = false
        }


    }

    override fun getItemCount(): Int {
        return array.size
    }
}

class AdapterMonth(
    private var array: ArrayList<ArrayList<Dates>>,
    private var context: Context,
    private val listener:recyclerViewDateEvent,
    private val start_date: LocalDate?,
    private val end_date: LocalDate?
):RecyclerView.Adapter<AdapterMonth.MyViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.month,
            parent,
            false
        )
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val layoutManager = GridLayoutManager(context,7)
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = DatesAdapter(context,array[position], listener,start_date,end_date,position)
        holder.textMonth.text = "${array[position].last().date.month}"
    }

    override fun getItemCount(): Int {
        return array.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var recyclerView:RecyclerView = itemView.findViewById(R.id.month_recyclerView)
        var textMonth:TextView = itemView.findViewById(R.id.month)
    }
}