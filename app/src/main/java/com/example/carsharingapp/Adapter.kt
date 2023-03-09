package com.example.carsharingapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.security.PrivilegedAction

class Adapter (
    private val array:ArrayList<Uri>,
    private val context:Context,
    private val contentResolver: ContentResolver,
    private val listener:recyclerViewEvent
    ): RecyclerView.Adapter<Adapter.AdapterViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup,viewType:Int):Adapter.AdapterViewHolder{
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.item,
                parent,
                false
            )
        return AdapterViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        try{
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, array[position])
            if(position == 0)
                holder.removeItem.visibility = View.GONE
            holder.image.setImageBitmap(bitmap)

        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return array.size
    }

    private fun removeItem(position: Int){
        Toast.makeText(context,"$position",Toast.LENGTH_SHORT).show()
        array.removeAt(position)
        notifyItemRemoved(position)
    }


    inner class AdapterViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        val image: ImageView = itemView.findViewById(R.id.image_view)
        val removeItem:ImageView = itemView.findViewById(R.id.remove_item)

        init {
            removeItem.setOnClickListener(this)
            image.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                if(p0?.id!! == R.id.remove_item)
                    listener.onItemClick(position)
                else
                    listener.onImageClick(position)
            }
        }
    }




}
