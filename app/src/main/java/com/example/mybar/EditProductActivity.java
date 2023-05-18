package com.example.mybar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class EditProductActivity extends AppCompatActivity {
    private EditText nameEditText, priceEditText;
    private ImageView productImageView;
    private Button saveButton;

    private String productId;
    private String productName;
    private double productPrice;
    private String productImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Retrieve the product data from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productId = extras.getString("productId");
            productName = extras.getString("productName");
            productPrice = extras.getDouble("productPrice");
            productImage = extras.getString("productImage");
        }

        // Initialize views
        nameEditText = findViewById(R.id.edit_product_name1);
        priceEditText = findViewById(R.id.edit_product_price1);
        productImageView = findViewById(R.id.edit_product_image1);
        saveButton = findViewById(R.id.save_button1);

        // Set the initial values for the views
        nameEditText.setText(productName);
        priceEditText.setText(String.valueOf(productPrice));
        Picasso.get().load(productImage).into(productImageView);

        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductDetails();
            }
        });
    }

    private void updateProductDetails() {
        // Get the updated values from the EditText fields
        String newName = nameEditText.getText().toString().trim();
        String newPriceString = priceEditText.getText().toString().trim();

        // Validate the input fields
        if (TextUtils.isEmpty(newName) || TextUtils.isEmpty(newPriceString)) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert the price to a double value
        double newPrice = Double.parseDouble(newPriceString);

        // TODO: Update the product details in the database using the productId

        // Show a success message
        Toast.makeText(this, "Product details updated", Toast.LENGTH_SHORT).show();

        // Finish the activity
        finish();
    }
}
