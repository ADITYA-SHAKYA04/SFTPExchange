package com.example.mediastreamapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.FrameLayout;
import org.videolan.libvlc.*;
import org.videolan.libvlc.util.VLCVideoLayout;

public class VlcPopupPlayer extends Dialog {
    private final String streamUrl;
    private LibVLC libVLC;
    private MediaPlayer mediaPlayer;
    private VLCVideoLayout videoLayout;

    public VlcPopupPlayer(Context context, String streamUrl) {
        super(context);
        this.streamUrl = streamUrl;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        videoLayout = new VLCVideoLayout(getContext());
        FrameLayout root = new FrameLayout(getContext());
        root.addView(videoLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(root);
        getWindow().setLayout(900, 600);
        getWindow().setGravity(Gravity.CENTER);
        setCancelable(true);
        // VLC setup
        libVLC = new LibVLC(getContext());
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.attachViews(videoLayout, null, false, false);
        Media media = new Media(libVLC, streamUrl);
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.detachViews();
            mediaPlayer.release();
        }
        if (libVLC != null) {
            libVLC.release();
        }
    }
}
