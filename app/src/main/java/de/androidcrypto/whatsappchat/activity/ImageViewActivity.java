package de.androidcrypto.whatsappchat.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.example.chatapp.R;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView=findViewById(R.id.image_view);

        imageUrl=getIntent().getStringExtra("url");

        Picasso.get().load(imageUrl).into(imageView);
    }
}
