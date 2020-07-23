package com.potatodev.jameschristianwira_takehome.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.potatodev.jameschristianwira_takehome.APIClient;
import com.potatodev.jameschristianwira_takehome.CustomDecorator;
import com.potatodev.jameschristianwira_takehome.R;
import com.potatodev.jameschristianwira_takehome.adapter.SearchAdapter;
import com.potatodev.jameschristianwira_takehome.models.Results;
import com.potatodev.jameschristianwira_takehome.models.User;
import com.potatodev.jameschristianwira_takehome.services.GetService;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText edtSearch;
    RecyclerView rvSearchResult;
    TextView tvEmpty;
    ProgressBar progressBar;
    Drawable dwSearch, dwClear;

    int currentPage = 1;
    int maxResult = 0;
    boolean isPaused = false;
    String currentKeyword = "";
    String lastKeyword = "";

    SearchAdapter searchAdapter = new SearchAdapter();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearch = findViewById(R.id.edtSearch);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        dwSearch = ContextCompat.getDrawable(this, R.drawable.ic_baseline_search_24);
        dwClear = ContextCompat.getDrawable(this, R.drawable.ic_baseline_clear_20);

        edtSearch.setCompoundDrawablesWithIntrinsicBounds(dwSearch, null, null, null);
        edtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                Drawable right = edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT];

                if(event.getAction() == MotionEvent.ACTION_UP && right != null) {
                    if(event.getRawX() >= (edtSearch.getRight() - edtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        edtSearch.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        rvSearchResult = findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(null);
        SearchAdapter adapter = new SearchAdapter();
        rvSearchResult.addItemDecoration(new CustomDecorator(8));
        rvSearchResult.setAdapter(adapter);

        edtSearch.addTextChangedListener(new TextWatcher() {
            Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timer.cancel();

                if (charSequence.toString().length() == 0) {
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(dwSearch, null, null, null);
                    currentPage = 1;
                    searchAdapter.clearList();
                    searchAdapter.notifyDataSetChanged();

                    tvEmpty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    if(lastKeyword.equals("") || !lastKeyword.equals(currentKeyword)) {
                        currentPage = 1;
                        lastKeyword = currentKeyword;
                        maxResult = 0;
                    }
                    currentKeyword = charSequence.toString();
                    edtSearch.setCompoundDrawablesWithIntrinsicBounds(dwSearch, null, dwClear, null);

                    timer = new Timer();
                    timer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    fetchResult(false);
                                }
                            },
                            1000
                    );
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        rvSearchResult.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager layoutManager=LinearLayoutManager.class.cast(recyclerView.getLayoutManager());
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                boolean endHasBeenReached = lastVisible + 10 >= totalItemCount;
                if (totalItemCount > 0 && endHasBeenReached && totalItemCount < maxResult && !isPaused) {
                    currentPage++;
                    fetchResult(true);
                    Timer timer = new Timer();
                    isPaused = true;

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            isPaused = false;
                        }
                    }, 1000);
                }
            }
        });
    }

    public void fetchResult(boolean isContinue){
        GetService getService = APIClient.getRetrofitInstance().create(GetService.class);
        Call<Results> call = getService.getUsers(currentKeyword, currentPage);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.body() != null) {
                    tvEmpty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);

                    maxResult = response.body().getTotalCount();

                    if(!isContinue && maxResult > 0){
                        showResults(response.body().getItems());
                    } else if (response.headers().get("X-Ratelimit-Remaining").equals("0")) {
                        makeToast("You've reached the max limit. Try again in a moment.");
                    } else if (isContinue && maxResult > 0){
                        appendResults(response.body().getItems());
                    } else if (maxResult == 0){
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                makeToast("Check your internet connection!");
                progressBar.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showResults(List<User> users){
        rvSearchResult = findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        searchAdapter = new SearchAdapter(users, this);
        rvSearchResult.setAdapter(searchAdapter);
    }

    public void appendResults(List<User> users){
        searchAdapter.updateResult(users);
        searchAdapter.notifyDataSetChanged();
    }

    public void makeToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}