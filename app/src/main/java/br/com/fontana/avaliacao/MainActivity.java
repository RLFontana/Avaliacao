package br.com.fontana.avaliacao;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import br.com.fontana.avaliacao.model.Movie;
import br.com.fontana.avaliacao.model.MovieList;

public class MainActivity extends AppCompatActivity {

    RecyclerView movieListView;
    MoviesAdapter adapter;
    private static final String API_KEY = "?api_key=4fad51595455472ef65b5dd72887ebad&language=pt-BR&region=BR";
    private final String TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated";
    private final String POPULAR = "https://api.themoviedb.org/3/movie/popular";
    boolean selectedType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedType = !selectedType;
                fab.setImageResource(selectedType ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
                changeMovies();
            }
        });

        movieListView = findViewById(R.id.rv_movie_list);
        movieListView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new MoviesAdapter(this);
        movieListView.setAdapter(adapter);
        changeMovies();

    }

    public void changeMovies(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String type = selectedType ? TOP_RATED : POPULAR;
        String url = type + API_KEY;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                MovieList list = gson.fromJson(response, MovieList.class);
                adapter.setMovieList(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView imageView;
        public MovieViewHolder(View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_cartaz);
        }
    }

    public static class MoviesAdapter extends RecyclerView.Adapter<MovieViewHolder>
    {
        private MovieList movieList;
        private LayoutInflater mInflater;
        private Context mContext;

        public MoviesAdapter(Context context)
        {
            this.mContext = context;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = mInflater.inflate(R.layout.row_movie, parent, false);
            MovieViewHolder viewHolder = new MovieViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MovieViewHolder holder, int position)
        {
            Movie movie = movieList.getResults()[position];

            RequestQueue queue = Volley.newRequestQueue(mContext);
            String url = Movie.TMDB_IMAGE_PATH + movie.getPoster_path() + API_KEY;
            ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    holder.imageView.setImageBitmap(response);
                }
            }, 0, 0, ImageView.ScaleType.FIT_CENTER, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(imageRequest);
        }

        @Override
        public int getItemCount()
        {
            return (movieList == null) ? 0 : movieList.getResults().length;
        }

        public void setMovieList(MovieList movieList)
        {
            this.movieList = movieList;
            notifyDataSetChanged();
        }
    }
}
