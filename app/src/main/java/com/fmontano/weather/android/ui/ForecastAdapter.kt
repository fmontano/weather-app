package com.fmontano.weather.android.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.fmontano.weather.android.databinding.ListItemForecastBinding
import com.fmontano.weather.android.ui.model.ForecastUIModel

class ForecastAdapter : ListAdapter<ForecastUIModel, ForecastAdapter.ForecastItemViewHolder>(DiffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastItemViewHolder =
        ForecastItemViewHolder(ListItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ForecastItemViewHolder, position: Int) =
        holder.bind(getItem(position))

    class ForecastItemViewHolder(private val binding: ListItemForecastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ForecastUIModel) {
            binding.iconImageView.load(item.iconUrl) {
                diskCachePolicy(CachePolicy.ENABLED)
            }
            binding.timeTextView.text = item.time
            binding.forecastCondition.text = item.condition
            binding.forecastWeather.text = item.weather
        }
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<ForecastUIModel>() {
        override fun areItemsTheSame(oldItem: ForecastUIModel, newItem: ForecastUIModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ForecastUIModel, newItem: ForecastUIModel): Boolean =
            oldItem == newItem
    }
}
