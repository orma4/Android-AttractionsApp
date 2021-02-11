package com.project.attractionsapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateAttraction extends AppCompatActivity {

    private FirebaseFirestore db;
    StorageReference storageReference;
    private Button updateAttractionBtn;
    ImageView attractionImage;
    EditText name, address, price,availableTickets;
    Uri imageUri;
    String imageURL;
    FirebaseAuth fAuth;
    String docID;
    Context context;
    private int countofimages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_attraction);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        context = this;
        name = findViewById(R.id.attractionName);
        address = findViewById(R.id.attractionAddress);
        availableTickets = findViewById(R.id.availableTickets);
        price = findViewById(R.id.price);
        updateAttractionBtn = findViewById(R.id.updateAttraction);
        attractionImage = findViewById(R.id.attractionImageView);

        final Attraction attraction = (Attraction) getIntent().getSerializableExtra("data");
        docID = attraction.getId();
        if (attraction != null) {
            name.setText(attraction.getName());
            address.setText(attraction.getAddress());
            availableTickets.setText(attraction.getAvailableTickets());
            price.setText(attraction.getPrice());
            Uri uri = Uri.parse(attraction.getUrl());
            imageUri = uri;
            Picasso.get().load(uri).into(attractionImage);
        }

        updateAttractionBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name.getText().toString().isEmpty()) {
                            name.setError("name is empty");
                            return;
                        } else if (address.getText().toString().isEmpty()) {
                            address.setError("address is empty");
                            return;
                        } else if (availableTickets.getText().toString().isEmpty()) {
                            availableTickets.setError("available tickets are empty");
                            return;
                        } else if (price.getText().toString().isEmpty()) {
                            price.setError("price is empty");
                            return;
                        }
                        DocumentReference docRef = db.collection("attraction").document(docID);
                        Map<String, Object> edited = new HashMap<>();
                        edited.put("name", name.getText().toString());
                        edited.put("address", address.getText().toString());
                        edited.put("availableTickets", availableTickets.getText().toString());
                        edited.put("price", price.getText().toString());
                        edited.put("url",attraction.getUrl());

                        docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UpdateAttraction.this, "Attraction Updated", Toast.LENGTH_LONG).show();
                                if (!((imageUri.toString()).equals(attraction.getUrl()))) {
                                    System.out.println("hi");
                                    uploadImageToFirebase(imageUri);
                                }
                                else {
                                    startActivity(new Intent(context,Booking.class));
                                }
                            }
                        });

                    }
                });

        attractionImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent,1000);
            }
        });
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

    public void OpenGallery(View view){
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1000);
    }

    public void uploadImageToFirebase(final Uri imageUri) {
        // upload image to firebase storage

        final StorageReference fileCount = storageReference.child("updated attractions/"+fAuth.getCurrentUser().getUid()+"/");
        try {
            fileCount.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item : listResult.getItems()) {
                        countofimages = listResult.getItems().size();
                    }

                    countofimages++;

                    final StorageReference fileRef = storageReference.child("updated attractions/"+fAuth.getCurrentUser().getUid()+"/"+countofimages+".jpg");
                    fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageURL= uri.toString();
                                    db.collection("attraction").document(docID).update("url", imageURL)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(context,Booking.class));
                                                }
                                            });
                                    Toast.makeText(UpdateAttraction.this, "Attraction inserted successfully", Toast.LENGTH_LONG).show();
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
            });

        }
        catch (Exception e){
            countofimages = 1;
            final StorageReference fileRef = storageReference.child("updated attractions/"+fAuth.getCurrentUser().getUid()+"/"+countofimages+".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageURL= uri.toString();
                            db.collection("attraction").document(docID).update("url", imageURL);
                            Toast.makeText(UpdateAttraction.this, "Attraction Updated successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(context,Booking.class));
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

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, UpdateAttraction.class);
    }
}
