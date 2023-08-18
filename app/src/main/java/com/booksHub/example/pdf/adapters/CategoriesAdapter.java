package com.booksHub.example.pdf.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.booksHub.example.pdf.R;
import com.booksHub.example.pdf.activities.WallpapersActivity;
import com.booksHub.example.pdf.models.Category;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private Context mCtx;
    private List<Category> categoryList;

    private InterstitialAd mInterstitialAd;


    public CategoriesAdapter(Context mCtx, List<Category> categoryList) {
        this.mCtx = mCtx;
        this.categoryList = categoryList;

        mInterstitialAd = new InterstitialAd(mCtx);
        //  mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");              My ID
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");      //  Test ID
        //  Load the ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());


    }

    @NonNull
    @Override
    public CategoriesAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.CategoryViewHolder holder, int position) {
        Category cat = categoryList.get(position);
        holder.tvCatName.setText(cat.name);
        Glide.with(mCtx)
                .load(cat.thumb)
                .into(holder.ivCatThumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Bitmap bitImg=ivCatThumbnail.getBitmapImage();
//                ByteArrayOutputStream baoS = new ByteArrayOutputStream();
//                bitImg.compress(Bitmap.CompressFormat.JPEG, 50, baoS);
//                intent.putExtra("bitArray", baoS.toByteArray());
//                context.getApplicationContext().startActivity(intent);
//
                byte[] byteArray = new byte[0];
                try {
                    holder.ivCatThumbnail.setDrawingCacheEnabled(true);
                    Bitmap b= holder.ivCatThumbnail.getDrawingCache();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byteArray = stream.toByteArray();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent(mCtx, WallpapersActivity.class);
                intent.putExtra("category", cat.name);
                intent.putExtra("picture", byteArray);
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    class CategoryViewHolder extends RecyclerView.ViewHolder  {

        TextView tvCatName;
        ImageView ivCatThumbnail;

        public CategoryViewHolder(View itemView) {
            super(itemView);

            tvCatName = itemView.findViewById(R.id.catItem_tvCatName);
            ivCatThumbnail = itemView.findViewById(R.id.catItem_ivCategory);

           // itemView.setOnClickListener(this);

        }

//        @Override
//        public void onClick(View view) {
//            //  When user clicks on category, the ad is loaded
//            if (mInterstitialAd.isLoaded()) {
//             //   mInterstitialAd.show();
//            } else {
//                Toast.makeText(mCtx, "Ad not loaded", Toast.LENGTH_LONG).show();
//            }
//
//
//
//        }
    }
}
