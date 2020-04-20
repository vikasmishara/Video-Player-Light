package com.video.player.fullvideoplayer.hdplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.video.player.fullvideoplayer.hdplayer.R;
import com.video.player.fullvideoplayer.hdplayer.fragment.FragmentVideoList;
import com.video.player.fullvideoplayer.hdplayer.kxUtil.PreferencesUtility;
import com.video.player.fullvideoplayer.hdplayer.video.VideoItem;

import java.util.ArrayList;

public class FolderDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentFragment = new FragmentVideoList();
        setContentView(R.layout.activity_folder_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (mGlobalVar.folderItem != null && mGlobalVar.folderItem.getFolderName() != null)
            setActionBar(mGlobalVar.folderItem.getFolderName());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!mGlobalVar.isMutilSelectEnalble)
            finish();
        else releaseUI();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGlobalVar.isNeedRefreshFolder) {
            currentFragment = new FragmentVideoList();
        }
    }

    @Override
    public void onBackPressed() {
        if (!mGlobalVar.isMutilSelectEnalble)
            super.onBackPressed();
        else releaseUI();

    }

    @Override
    public void updateMultiSelectedState() {
        if (currentFragment == null) return;
        int totalVideoSelected = currentFragment.getTotalSelected();
        if (totalVideoSelected == 0) {
            releaseUI();
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(String.valueOf(totalVideoSelected) + " " + getString(R.string.video));
            }
        }
    }

    @Override
    public void updateListAfterDelete(ArrayList<VideoItem> videoItems) {
        if (currentFragment == null) return;
        currentFragment.updateVideoList(videoItems);
    }

    private void releaseUI() {
        mGlobalVar.isMutilSelectEnalble = false;
        setActionBar(mGlobalVar.folderItem.getFolderName());
        if (currentFragment != null) currentFragment.releaseUI();
    }

    public void setActionBar(String title) {
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (!mGlobalVar.isMutilSelectEnalble) {
            if (PreferencesUtility.getInstance(FolderDetailActivity.this).isAlbumsInGrid())
                getMenuInflater().inflate(R.menu.first, menu);
            else
                getMenuInflater().inflate(R.menu.first1, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_multiselected_option, menu);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (!mGlobalVar.isMutilSelectEnalble) {
            if (PreferencesUtility.getInstance(FolderDetailActivity.this).isAlbumsInGrid())
                getMenuInflater().inflate(R.menu.first, menu);
            else
                getMenuInflater().inflate(R.menu.first1, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_multiselected_option, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Handler handler = new Handler();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_view_list) {
            PreferencesUtility.getInstance(FolderDetailActivity.this).setAlbumsInGrid(false);
            currentFragment = new FragmentVideoList();
            handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit(), 500);

        }
        if (id == R.id.action_view_grid) {
            PreferencesUtility.getInstance(FolderDetailActivity.this).setAlbumsInGrid(true);
            currentFragment = new FragmentVideoList();
            handler.postDelayed(() -> getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit(), 500);

        }
        if (id == R.id.action_play) {
            currentFragment.playItemSelected();
            releaseUI();

        } else if (id == R.id.action_rate_app) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            }

        } else if (id == R.id.action_share_app) {
            String shareBody = getString(R.string.share_desc) + " \n " + " \n " + "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Select App to Share Video :)"));

        } else if (id == R.id.action_share) {
            currentFragment.shareSelected();
        } else if (id == R.id.action_delete) {
            currentFragment.deleteSelected();
        } else if (id == R.id.menu_sort_by_az) {
            currentFragment.sortAZ();
        } else if (id == R.id.menu_sort_by_za) {
            currentFragment.sortZA();
        } else if (id == R.id.menu_sort_by_size) {
            currentFragment.sortSize();
        } else if (id == R.id.action_go_to_playing) {
            if (mGlobalVar.videoService == null || mGlobalVar.playingVideo == null) {
                Toast.makeText(this, getString(R.string.no_video_playing), Toast.LENGTH_SHORT).show();
                return false;
            }
            final Intent intent = new Intent(FolderDetailActivity.this, PlayVideoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_stay_x);

        }
        return false;
    }
}
