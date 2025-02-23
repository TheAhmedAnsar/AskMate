package com.askmate;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.webkit.WebView;

import com.askmate.databinding.ActivityNewsInfoBinding;
import com.bumptech.glide.Glide;

public class NewsInfo extends AppCompatActivity {

    ActivityNewsInfoBinding binding;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewsInfoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_news_info);
        setContentView(view);
        getWindow().setExitTransition(null);
//
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
//        View rootView = findViewById(R.id.image);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
//                @Override
//                public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
//                    // Adjust padding for transparent status bar
//                    int statusBarHeight = 0;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            statusBarHeight = insets.getInsets(WindowInsets.Type.statusBars()).top;
//                        }
//                    }
//                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
//                    layoutParams.setMargins(0, -statusBarHeight, 0, 0);
//                    v.setLayoutParams(layoutParams);
//                    return insets;
//                }
//            });
//        }
//         myIntent.putExtra("title", modal.getTitle());
//                myIntent.putExtra("timestamp", modal.getTime());
//                myIntent.putExtra("category", modal.getCategory());
//                myIntent.putExtra("image", modal.getImage());
//                myIntent.putExtra("news", modal.getContent());
//                myIntent.putExtra("newby", modal.getNewsBy());

        String title = getIntent().getStringExtra("title");
        String timestamp = getIntent().getStringExtra("timestamp");
        String image = getIntent().getStringExtra("image");
        String news = getIntent().getStringExtra("news");
        String newby = getIntent().getStringExtra("newby");
        String category = getIntent().getStringExtra("category");
         link = getIntent().getStringExtra("link");
        SpannableStringBuilder spannableStringBuilder = parseBoldMarkers(news);
//        textView.setText(spannableStringBuilder);
        binding.content.setText(news);
        binding.title.setText(title);
        binding.newsBy.setText(newby);

        Glide.with(this).load(image).into(binding.image);

binding.tapme.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        ShowDialog();
    }
});

    }
    private SpannableStringBuilder parseBoldMarkers(String text) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);

        // Find and apply bold styling to text between ** markers
        int start = 0;
        while ((start = text.indexOf("**", start)) != -1) {
            int end = text.indexOf("**", start + 2);
            if (end == -1) break;

            // Apply bold styling
            spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start + 2, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Remove markers
            spannableStringBuilder.delete(end, end + 2); // Remove end marker
            spannableStringBuilder.delete(start, start + 2); // Remove start marker
            start = end - 2;
        }

        return spannableStringBuilder;
    }
    public void ShowDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomwebview);

       WebView  webView = dialog.findViewById(R.id.webView);
       webView.loadUrl(link); // Replace with your URL

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }
}