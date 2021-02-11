package com.project.attractionsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import java.util.UUID;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class InsertAttraction extends AppCompatActivity {

    private EditText attractionName;
    private EditText address;
    private EditText price;
    private EditText ticketsAmount;
    ImageView attractionImage;
    StorageReference storageReference;
    private FirebaseFirestore db;
    FirebaseAuth fAuth;
    Uri imageUri;
    String imageURL;
    final Map<String, Object> attraction = new HashMap<>();
    String docID;
    private int countOfImages;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_attraction);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        attractionName = findViewById(R.id.attraction_name);
        address = findViewById(R.id.address);
        price = findViewById(R.id.price);
        ticketsAmount = findViewById(R.id.available_tickets);
        attractionImage = findViewById(R.id.attractionImageView);
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();

        attractionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });
        }

    public void OpenGallery(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1000);
    }

    public String getDocID(){
        return this.docID;
    }

    public void SaveAttraction (View view) {
       String priceAmount = price.getText().toString();
       String ticketsAmountString = ticketsAmount.getText().toString();
       int convertedPrice, convertedTicketsAmount;
       try {
            convertedPrice = Integer.parseInt(priceAmount);
            convertedTicketsAmount = Integer.parseInt(ticketsAmountString);
       }
       catch (Exception e){
           Toast.makeText(getApplicationContext(), "Make sure all the fields are filled correctly!", Toast.LENGTH_LONG).show();
           return;
       }
       try {
           imageUri.toString();
       }catch (Exception e) {
           Toast.makeText(getApplicationContext(), "Make sure all the fields are filled correctly!", Toast.LENGTH_LONG).show();
           return;
       }

        if (TextUtils.isEmpty(attractionName.getText())){
            attractionName.setError("Attraction Name field is Empty!");
            return;
        }
        else if (TextUtils.isEmpty(address.getText())){
            address.setError("Address field is Empty!");
            return;
        }
        else if (TextUtils.isEmpty(price.getText())){
            price.setError("Price field is Empty!");
            return;
        }
        else if (TextUtils.isEmpty(ticketsAmount.getText())){
            ticketsAmount.setError("Tickets amount field is Empty!");
            return;
        }

        else{
            this.docID = db.collection("attraction").document().getId();

            attraction.put("name",attractionName.getText().toString());
            attraction.put("address",address.getText().toString());
            attraction.put("price",price.getText().toString());
            attraction.put("availableTickets",ticketsAmount.getText().toString());
            attraction.put("url","");
            attraction.put("id",docID);


            db.collection("attraction").document(docID)
                    .set(attraction)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            uploadImageToFirebase(imageUri);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(InsertAttraction.this, "Insert to database failed", Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                Picasso.get().load(imageUri).into(attractionImage);
            }
        }
    }
    public void uploadImageToFirebase(final Uri imageUri) {
        // upload image to firebase storage

        try {
            String uniqueID = UUID.randomUUID().toString();
            final StorageReference fileRef = storageReference.child("inserted attractions/"+userId+"/"+ uniqueID +".jpg");
                fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageURL= uri.toString();
                                db.collection("attraction").document(getDocID()).update("url", imageURL);
                                Toast.makeText(InsertAttraction.this, "Attraction inserted successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                    }
                });

            }
        catch (Exception e){
            String uniqueID = UUID.randomUUID().toString();
            final StorageReference fileRef = storageReference.child("inserted attractions/"+userId+"/"+ uniqueID +".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL= uri.toString();
                            db.collection("attraction").document(getDocID()).update("url", imageURL);
                            Toast.makeText(InsertAttraction.this, "Attraction inserted successfully", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}
