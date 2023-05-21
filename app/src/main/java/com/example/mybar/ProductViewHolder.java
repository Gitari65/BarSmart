package com.example.mybar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    private TextView nameTextView, priceTextView;
    private ImageView imageView;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.product_name);
        priceTextView = itemView.findViewById(R.id.product_price);
        imageView = itemView.findViewById(R.id.product_image);
    }

    public void bindData(Product product) {
        nameTextView.setText(product.getName());
        priceTextView.setText(String.format(Locale.getDefault(), "$%.2f", product.getPrice()));
        if (product.getImageUrl() != null) {
            Picasso.get().load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .transform(new CircleTransform())
                    .into(imageView);
        } else {
            Picasso.get().load(R.drawable.placeholder_image)
                    .transform(new CircleTransform())
                    .into(imageView);
        }
    }
}
