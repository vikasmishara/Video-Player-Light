package com.video.player.fullvideoplayer.hdplayer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;
import com.video.player.fullvideoplayer.hdplayer.R;
import com.video.player.fullvideoplayer.hdplayer.fragment.FragmentFolderList;
import com.video.player.fullvideoplayer.hdplayer.kxUtil.kxUtils;
import com.video.player.fullvideoplayer.hdplayer.video.VideoItem;


import java.util.ArrayList;

public class FirstActivity extends BaseActivity {
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        currentFragment = new FragmentFolderList();
        setSupportActionBar(toolbar);
        //setupCast();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        if (kxUtils.isMarshmallow()) checkPermissionAndThenLoad();
        else loadEverything();

        String action = getIntent().getAction();
        if (Intent.ACTION_VIEW.equals(action)) {

            Intent receivedIntent = getIntent();
            Uri receivedUri = receivedIntent.getData();

            assert receivedUri != null;
            String _file = receivedUri.toString();
            mGlobalVar.playingVideo = new VideoItem();
            mGlobalVar.playingVideo.setPath(_file);
            mGlobalVar.playingVideo.setVideoTitle(kxUtils.getFileNameFromPath(_file));
            mGlobalVar.videoItemsPlaylist = new ArrayList<>();
            mGlobalVar.videoItemsPlaylist.add(mGlobalVar.playingVideo);
            if (mGlobalVar.videoService == null) {
                mGlobalVar.isOpenFromIntent = true;
            } else {
                mGlobalVar.videoService.playVideo(0, false);
                showFloatingView(FirstActivity.this, true);
                finish();
            }
        }
        showRateDialog();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void loadEverything() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    public void reloadData() {
        currentFragment = new FragmentFolderList();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGlobalVar.isNeedRefreshFolder && currentFragment != null) {
            mGlobalVar.isNeedRefreshFolder = false;
            reloadData();
        }
//        if(castContext == null){
//             return;
//        }
//        if(mGlobalVar.videoService == null || mGlobalVar.videoService.getPlayerManager() == null){
//            return;
//        }
//        mGlobalVar.videoService.getPlayerManager().updateCast(castContext);
    }

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            return;

        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        },2000);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.secound, menu);
//        if(currentFragment instanceof FragmentVideoList){
//            if(PreferencesUtility.getInstance(FirstActivity.this).isAlbumsInGrid())
//                getMenuInflater().inflate(R.menu.first, menu);
//            else
//                getMenuInflater().inflate(R.menu.first1, menu);
//        }
//        else if (currentFragment instanceof FragmentFolderList)
//        {
//            getMenuInflater().inflate(R.menu.secound, menu);
//        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.secound, menu);
//        if(currentFragment instanceof FragmentVideoList){
//            if(PreferencesUtility.getInstance(FirstActivity.this).isAlbumsInGrid())
//                getMenuInflater().inflate(R.menu.first, menu);
//            else
//                getMenuInflater().inflate(R.menu.first1, menu);
//        }
//        else if (currentFragment instanceof FragmentFolderList)
//        {
//            getMenuInflater().inflate(R.menu.secound, menu);
//        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Handler handler = new Handler();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            if (kxUtils.isMarshmallow()) {
                if (kxUtils.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    navigateToSearch(this);
                }
            } else navigateToSearch(this);
        }
        if (id == R.id.action_setting) {
            startActivity(new Intent(FirstActivity.this, SettingsActivity.class));
        } else if (id == R.id.menu_sort_by_az) {
            currentFragment.sortAZ();
        } else if (id == R.id.menu_sort_by_za) {
            currentFragment.sortZA();
        } else if (id == R.id.menu_sort_by_total_videos) {
            currentFragment.sortSize();
        } else if (id == R.id.action_go_to_playing) {
            if (mGlobalVar.videoService == null || mGlobalVar.playingVideo == null) {
                Toast.makeText(this, getString(R.string.no_video_playing), Toast.LENGTH_SHORT).show();
                return false;
            }
            final Intent intent = new Intent(FirstActivity.this, PlayVideoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_stay_x);

        } else if (id == R.id.action_about) {
            startActivity(new Intent(FirstActivity.this, AboutActivity.class));
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

        } else if (id == R.id.action_musicPlayer) {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }


        return false;
    }

    public static void navigateToSearch(Activity context) {
        final Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(intent);
    }

    AlertDialog dialog;
    Button btnDownload;
    TextView textView1;


    public void showRateDialog() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .threshold(3)
                .session(8)
                .positiveButtonText("Maybe later")
                .negativeButtonText("Never")
                .negativeButtonTextColor(R.color.grey_500)
                .ratingBarColor(R.color.nice_yellow)
                .playstoreUrl("https://play.google.com/store/apps/details?id=com.musicplayer.musicapps.mp3player.audioplayer")
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        sendEmail();
                    }
                }).build();

        ratingDialog.show();
    }

    private void sendEmail() {
        String[] TO = {"labcompact@gmail.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Video Player Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please type your message here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FirstActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
