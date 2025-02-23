package com.askmate.Notes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.askmate.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdapterUpload extends RecyclerView.Adapter<AdapterUpload.ViewHolder> {
ArrayList<NotesModal> modal = new ArrayList<>();
    public AdapterUpload(ArrayList<NotesModal> modal) {
        this.modal = modal;
    }
String cityLocality;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_upload_pdf, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(modal.get(position).getText());
        String fileName = modal.get(position).getText();
        String fileUrl = modal.get(position).getLink();


        SharedPreferences preferences = holder.itemView.getContext().getSharedPreferences("coaching_profiles", Context.MODE_PRIVATE);
        float latitude = preferences.getFloat("latitude", 0f); // 0f is the default value if the key is not found
        float longitude = preferences.getFloat("longitude", 0f); // 0f is the default value if the key is not found

// Check if latitude and longitude are not 0 (indicating default values)
        if (latitude != 0f && longitude != 0f) {
            // Use Geocoder to get the locality from latitude and longitude
            Geocoder geocoder = new Geocoder(holder.itemView.getContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    cityLocality = address.getLocality();
                    // Now you have the locality from latitude and longitude
                    Log.d("Locality", "Locality: " +     cityLocality);
                } else {
                    Log.d("Locality", "No address found for the given coordinates.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Locality", "Error getting address from Geocoder: " + e.getMessage());
            }
        } else {
            Log.d("Locality", "Latitude and longitude not found in SharedPreferences.");
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ((UploadActivity) holder.itemView.getContext()).handlePdfFileOpen(fileName, fileUrl);
                File localFile = new File(holder.itemView.getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/AskMate Notes", fileName);

                if (localFile.exists()) {
                    // File exists, open it
                    openPdfFromLocalStorage(holder.itemView.getContext(), localFile);
                } else {
                    // File does not exist, download it
                    downloadAndSavePdf(fileUrl, fileName, holder.itemView.getContext());
                }

//                Toast.makeText(view.getContext(),modal.get(position).getPushKey() , Toast.LENGTH_SHORT).show();

            }
        });


    }



    private void openPdfFromLocalStorage(Context context, File file) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }

    private void downloadAndSavePdf(String fileUrl, String fileName, Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Downloading file...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // Create a specific directory in external files
        File notesDirectory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "AskMate Notes");

        // Create the directory if it doesn't exist
        if (!notesDirectory.exists()) {
            notesDirectory.mkdirs(); // Creates the directory
        }

        // Define the file path in the "AskMate Notes" folder
        File localFile = new File(notesDirectory, fileName);

        // Download the file from Firebase Storage
        FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl).getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(context, "File saved locally in AskMate Notes", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        openPdfFromLocalStorage(context, localFile); // Open the file after download
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to download file", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return modal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
TextView textView;
AppCompatImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.menuItem);

        }



    }


    private int getItemPosition(String pushKey) {
        for (int i = 0; i < modal.size(); i++) {
            if (modal.get(i).getPushKey().equals(pushKey)) {
                return i;
            }
        }
        return -1; // Item not found
    }

}
