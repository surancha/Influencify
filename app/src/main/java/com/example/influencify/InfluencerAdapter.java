package com.example.influencify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InfluencerAdapter extends RecyclerView.Adapter<InfluencerAdapter.InfluencerViewHolder> {

    private List<Influencer> influencerList;

    public InfluencerAdapter(List<Influencer> influencerList) {
        this.influencerList = influencerList;
    }

    @Override
    public InfluencerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.influencer_item, parent, false);
        return new InfluencerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfluencerViewHolder holder, int position) {
        Influencer influencer = influencerList.get(position);
        holder.tvName.setText(influencer.getName());
        holder.tvCategory.setText(influencer.getCategory());
        holder.tvRating.setText("★ " + influencer.getRating() + " Rating");
        holder.tvRpp.setText("♥ " + influencer.getRpp() + "k RPP");
        holder.tvFollowers.setText("♀ " + influencer.getFollowers() + "k Followers");
        holder.tvCpp.setText("👆 " + influencer.getCpp() + " CPP");
        holder.tvHeartCount.setText(String.valueOf(influencer.getHeartCount()));
        holder.btnPrice.setText("$" + influencer.getPrice());
    }

    @Override
    public int getItemCount() {
        return influencerList.size();
    }

    public static class InfluencerViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvCategory, tvRating, tvRpp, tvFollowers, tvCpp, tvHeartCount;
        public Button btnPrice;
        public ImageView ivProfile;

        public InfluencerViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvRpp = itemView.findViewById(R.id.tv_rpp);
            tvFollowers = itemView.findViewById(R.id.tv_followers);
            tvCpp = itemView.findViewById(R.id.tv_cpp);
            tvHeartCount = itemView.findViewById(R.id.tv_heart_count); // New field
            btnPrice = itemView.findViewById(R.id.btn_price);
            ivProfile = itemView.findViewById(R.id.iv_profile);
        }
    }
}