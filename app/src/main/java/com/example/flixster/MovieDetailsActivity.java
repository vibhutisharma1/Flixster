package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.databinding.ActivityMovieDetails2Binding;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    // the movie to display
    Movie movie;

    ActivityMovieDetails2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetails2Binding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);


        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());
        //binding.imageView.setImage

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        binding.rbVoteAverage.setRating(voteAverage / 2.0f);
        int placeHolder;

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            placeHolder = R.drawable.flicks_backdrop_placeholder;
        }else{
            placeHolder = R.drawable.flicks_movie_placeholder;
        }


        Glide.with(this)
                .load(movie.getBackdropPath()).apply(RequestOptions.placeholderOf(placeHolder).
                error(R.drawable.imagenotfound).fitCenter().
                transform(new RoundedCornersTransformation(30, 10)))
                .into(binding.imageView);



        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                String NOW_MOVIE_URL = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=c83326368bec7f59a7532d49d1114399&language=en-US";
                client.get(NOW_MOVIE_URL, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Headers headers, JSON json) {
                        Log.d("MovieDetailsActivity", "onSuccess");
                        Log.d("MovieDetailsActivity", json.toString());

                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray arrayJ = jsonObject.getJSONArray("results");
                            String valKey = arrayJ.getJSONObject(0).getString("key");
                            Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                            intent.putExtra("key",valKey);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                        Log.d("MovieDetailsActivity", "onFailure");

                    }
                });


            }
        });




    }


}
