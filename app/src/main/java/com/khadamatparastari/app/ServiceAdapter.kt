package com.khadamatparastari.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(
    private val services: List<Service>,
    private val onItemClick: (Service) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvServiceTitle)
        val description: TextView = itemView.findViewById(R.id.tvServiceDescription)
        val price: TextView = itemView.findViewById(R.id.tvServicePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.title.text = service.title
        holder.description.text = service.description
        holder.price.text = service.price
        holder.itemView.setOnClickListener { onItemClick(service) }
    }

    override fun getItemCount(): Int = services.size
}
