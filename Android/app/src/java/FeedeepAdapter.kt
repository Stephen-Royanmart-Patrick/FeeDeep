package com.dicoding.picodiploma.feedeepaplikasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.feedeepaplikasi.Data.FeedeepData
import com.dicoding.picodiploma.feedeepaplikasi.databinding.ActivityDetailBinding

class FeedeepAdapter (private var listdetect: ArrayList<FeedeepData>): RecyclerView.Adapter<FeedeepAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedeepAdapter.ListViewHolder {
        val binding = ActivityDetailBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedeepAdapter.ListViewHolder, position: Int) {
        holder.bind(listdetect[position])
    }

    override fun getItemCount(): Int=listdetect.size

    class ListViewHolder(private val binding: ActivityDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: FeedeepData){
            with(binding){
                Glide.with(itemView.context)
                    .load(data.photodetect)
                    .apply(RequestOptions().override(60,60))
                    .into(imageView)
                binding.txtDetectionObject.text=data.Nutrient
                binding.txtDescriptionManfaat.text=data.afficacy
                binding.txtDescriptionPenyajian.text=data.serving

            }
        }
    }

}