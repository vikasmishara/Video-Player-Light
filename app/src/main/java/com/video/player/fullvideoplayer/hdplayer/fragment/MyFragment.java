package com.video.player.fullvideoplayer.hdplayer.fragment;

import com.video.player.fullvideoplayer.hdplayer.video.VideoItem;

import java.util.ArrayList;

public abstract class MyFragment extends BaseFragment {
    public void reloadFragment(int orientation){}
    public int getTotalSelected(){
        return 0;
    }
    public void releaseUI(){ }
    public void playItemSelected(){}
    public void shareSelected(){}
    public void deleteSelected(){}
    public void updateVideoList(ArrayList<VideoItem> videoItems){}

    public void sortAZ(){ }
    public void sortZA(){}
    public void sortSize(){}
}
