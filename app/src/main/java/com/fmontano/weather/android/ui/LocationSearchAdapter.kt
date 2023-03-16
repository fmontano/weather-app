package com.fmontano.weather.android.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fmontano.weather.android.databinding.ListItemLocationSearchBinding
import com.fmontano.weather.android.ui.model.GeoLocationUIModel

class LocationSearchAdapter(
    private val onLocationClickListener: (location: GeoLocationUIModel) -> Unit
) : ListAdapter<GeoLocationUIModel, LocationSearchAdapter.LocationViewHolder>(DiffUtilCallback) {

    inner class LocationViewHolder(private val binding: ListItemLocationSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(location: GeoLocationUIModel) {
            binding.searchResultTextView.text = location.displayName
            binding.searchResultTextView.setOnClickListener {
                onLocationClickListener(location)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder =
        LocationViewHolder(ListItemLocationSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object DiffUtilCallback : DiffUtil.ItemCallback<GeoLocationUIModel>() {
        override fun areItemsTheSame(oldItem: GeoLocationUIModel, newItem: GeoLocationUIModel): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: GeoLocationUIModel, newItem: GeoLocationUIModel): Boolean =
            oldItem == newItem
    }
}
