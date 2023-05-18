package com.example.mybar;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextPrice, editTextQuantity;
    private ImageView imageViewProduct;
    private Button buttonSelectImage, buttonAddProduct;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        storageReference = FirebaseStorage.getInstance().getReference("products");
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        editTextName = findViewById(R.id.editTextName);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduct();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewProduct.setImageURI(imageUri);
        }
    }

    private void addProduct() {
        final String name = editTextName.getText().toString().trim();
        final String priceString = editTextPrice.getText().toString().trim();
        final String quantityString = editTextQuantity.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Please enter a product name");
            editTextName.requestFocus();
            return;
        }

        if (priceString.isEmpty()) {
            editTextPrice.setError("Please enter a price");
            editTextPrice.requestFocus();
            return;
        }

        if (quantityString.isEmpty()) {
            editTextQuantity.setError("Please enter a quantity");
            editTextQuantity.requestFocus();
            return;
        }

        double price = Double.parseDouble(priceString);
        int quantity = Integer.parseInt(quantityString);

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String id = databaseReference.push().getKey();

                        Product product = new Product(id, name, price, quantity, downloadUri.toString());
                        databaseReference.child(id).setValue(product);

                        Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            String id = databaseReference.push().getKey();

            Product product = new Product(id, name, price, quantity, "");
            databaseReference.child(id).setValue(product);

            Toast.makeText(AddProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}
