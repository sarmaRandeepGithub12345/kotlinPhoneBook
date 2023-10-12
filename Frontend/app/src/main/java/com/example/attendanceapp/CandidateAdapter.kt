package com.example.attendanceapp

import android.app.Activity
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CandidateAdapter(val context:Activity,val result:MutableList<Result>):RecyclerView.Adapter<CandidateAdapter.MyViewHolder>() {

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var textfirstname = itemView.findViewById<TextView>(R.id.textFirstname)
        var textlastname = itemView.findViewById<TextView>(R.id.textLastname)
        var textphone = itemView.findViewById<TextView>(R.id.textPhoneNumber)
        var textroll = itemView.findViewById<TextView>(R.id.textRoll)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateAdapter.MyViewHolder {

        val itemview = LayoutInflater.from(parent.context).inflate(R.layout.create_item,parent,false)
        return MyViewHolder(itemview)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentItem = result[position]
        holder.textfirstname.text = currentItem.firstname
        holder.textlastname.text = currentItem.lastname
        holder.textphone.text = currentItem.phone
        holder.textroll.text = currentItem.roll
    }
}