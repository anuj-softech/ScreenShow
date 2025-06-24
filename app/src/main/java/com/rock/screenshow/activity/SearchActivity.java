package com.rock.screenshow.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.rock.screenshow.R;
import com.rock.screenshow.adapter.VideoRowAdapter;
import com.rock.screenshow.databinding.ActivitySearchBinding;
import com.rock.screenshow.helper.Loader;
import com.rock.screenshow.helper.TVHelper;
import com.rock.screenshow.model.VideoItem;
import com.rock.screenshow.model.VideoRow;
import com.rock.screenshow.player.RockPlayer;

import org.jetbrains.annotations.NotNull;


public class SearchActivity extends Activity {
    ActivitySearchBinding lb;
    private VideoRow searchList;
    Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(lb.getRoot());
        loader = new Loader(this, R.drawable.loader);
        lb.btnSearch.setOnClickListener(v -> performSearch(lb.etSearchInput.getText().toString()));
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            lb.etSearchInput.setError("Enter a search term");
            return;
        }
        loader.startLoading();
    }


    private void updateUI() {
        loader.stopLoading();
        VideoRowAdapter.OnClickListener onClickListener = new VideoRowAdapter.OnClickListener() {
            @Override
            public void onClick(@NotNull VideoItem postId) {

            }
        };
        lb.rvSearchResults.setLayoutManager(TVHelper.getAdaptiveLM(getApplicationContext()));
        lb.rvSearchResults.setAdapter(new VideoRowAdapter (searchList, lb.rvSearchResults, onClickListener, null));
    }

    private void fetchSearchListFromApi(String query) {

    }
}
