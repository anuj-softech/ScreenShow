package com.rock.screenshow.player;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.C;
import androidx.media3.common.Format;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.TrackSelectionOverride;
import androidx.media3.common.Tracks;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.analytics.AnalyticsListener;
import androidx.media3.exoplayer.source.MediaSource;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.common.collect.ImmutableList;
import com.rock.screenshow.CF;
import com.rock.screenshow.R;
import com.rock.screenshow.databinding.RockPlayerBinding;
import com.rock.screenshow.databinding.SelectordialogBinding;
import com.rock.screenshow.databinding.TrackMediaBinding;
import com.rock.screenshow.player.helper.RockPlayerHelper;
import com.rock.screenshow.player.helper.ScreenShowPlayerHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class RockPlayer extends Activity {
    public static final String BOOST_S = "boostS";
    public static final String ENHANCED_C = "enhancedC";
    /**
     * RockPlayer is an {@link Activity} that handles video playback for a given episode.
     * It retrieves necessary details like episode ID, video URL, subtitles, artwork, and title
     * from the {@link Intent} that started this activity.
     * <p>
     * The expected intent extras are:
     * <ul>
     *     <li>{@code id} - The ID of the episode to be played.</li>
     *     <li>{@code url} - The URL of the video to be played.</li>
     *     <li>{@code sub} - The URL or path to the subtitle file for the video.</li>
     *     <li>{@code artwork} - The URL or path to the artwork image for the episode.</li>
     *     <li>{@code title} - The title of the episode.</li>
     * </ul>
     * <p>
     * Example usage:
     * <pre>
     * Intent intent = new Intent(context, RockPlayer.class);
     * intent.putExtra("id", "episode123");
     * intent.putExtra("url", "https://example.com/video.mp4");
     * intent.putExtra("sub", "https://example.com/subtitle.srt");
     * intent.putExtra("artwork", "https://example.com/artwork.jpg");
     * intent.putExtra("title", "Sample Episode");
     * intent.putExtra("info", dramaInfo);
     * intent.putExtra("episode", 1);
     * context.startActivity(intent);
     * </pre>
     */
    public RockPlayerBinding lb;
    String TAG = "player";
    Handler chandler;
    Runnable hideContp;
    ExoPlayer player;
    Handler timeh;
    Handler progHoverHandler;
    Runnable progHoverRunnabler;
    boolean sOpen = false;
    int initialY = 0;
    LoudnessEnhancer enhancer;
    boolean isEpisodeChanged = true;
    private Runnable updateProgressAction;
    private SharedPreferences sharedPrefferences;
    private boolean userIsInHurry = false;
    private boolean isAppStopped = false;
    private boolean speedX = false;
    private boolean progTracking = false;
    private RockPlayerHelper currentPlayerHelper;
    private String artwork = "";
    private boolean playNextEnable = true;
    private boolean languageMode = false;
    @UnstableApi
    private AnalyticsListener analyticsListener = new AnalyticsListener() {
        @Override
        public void onIsPlayingChanged(EventTime eventTime, boolean isPlaying) {
            runOnUiThread(() -> {
                if (isPlaying) {
                    lb.p.playB.setImageDrawable(getDrawable(R.drawable.pause_24dp));
                } else {
                    lb.p.playB.setImageDrawable(getDrawable(R.drawable.play));
                }
            });

            if (player.getCurrentPosition() > (player.getDuration() - 100)) {
                Log.e(TAG, "updateProgressViews: " + "completed");
            }
        }

        @Override
        public void onRenderedFirstFrame(EventTime eventTime, Object output, long renderTimeMs) {
            runOnUiThread(() -> {
                lb.p.artworkd.setVisibility(View.GONE);
                Log.e(TAG, "onRenderedFirstFrame: " + "rendered" + player.getAudioSessionId());
                boolean boostSound = sharedPrefferences.getBoolean(BOOST_S, false);
                if (sharedPrefferences.getBoolean(ENHANCED_C, false)) {
                    enhancedC(true);
                }

                Log.e(TAG, "onRenderedFirstFrame: " + "boostSound" + boostSound);
                boostSoundInPlayer(player, boostSound, lb.p.boostS);
            });
        }

        @Override
        public void onPlayerError(EventTime eventTime, PlaybackException error) {
            Log.e(TAG, "Player Error:", error);
        }

        @Override
        public void onAudioSessionIdChanged(EventTime eventTime, int audioSessionId) {
            boolean boostSound = sharedPrefferences.getBoolean(BOOST_S, false);
            if (boostSound) {
                if (enhancer != null) enhancer.release();
                enhancer = new LoudnessEnhancer(audioSessionId);
                enhancer.setTargetGain(1500);
                enhancer.setEnabled(true);
                Log.e(getClass().getSimpleName(), "Audio Session id changed and Enhancing audio : " + audioSessionId);
            }
            AnalyticsListener.super.onAudioSessionIdChanged(eventTime, audioSessionId);
        }
    };

    @NonNull
    private static Paint getEnhancedPaint() {
        ColorMatrix finalMatrix = new ColorMatrix();

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(1.5f);
        finalMatrix.postConcat(saturationMatrix);

        float contrast = 1.2f;
        float translate = (-0.5f * contrast + 0.5f) * 255f;
        ColorMatrix contrastMatrix = new ColorMatrix(new float[]{
                contrast, 0, 0, 0, translate,
                0, contrast, 0, 0, translate,
                0, 0, contrast, 0, translate,
                0, 0, 0, 1, 0
        });
        finalMatrix.postConcat(contrastMatrix);

        float brightness = 20f;
        ColorMatrix brightnessMatrix = new ColorMatrix(new float[]{
                1, 0, 0, 0, brightness,
                0, 1, 0, 0, brightness,
                0, 0, 1, 0, brightness,
                0, 0, 0, 1, 0
        });
        finalMatrix.postConcat(brightnessMatrix);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(finalMatrix));
        return paint;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = RockPlayerBinding.inflate(getLayoutInflater());
        setContentView(lb.getRoot());
        setFullScreen();
        setHeightOfWindow();
        initVariables(getIntent());
        initHandlers();
        setOnclick();
        lb.playerView.setScaleX(sharedPrefferences.getFloat("stretch", 1F));
        lb.playerView.setScaleY(sharedPrefferences.getFloat("stretchy", 1F));
    }

    private void setFullScreen() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        }
    }

    private void setHeightOfWindow() {
        lb.playerFrame.postDelayed(() -> {
            ViewGroup.LayoutParams lp = lb.playerFrame.getLayoutParams();
            lp.height = lb.playerView.getHeight();
            lb.playerFrame.setLayoutParams(lp);
        }, 300);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setHeightOfWindow();
        super.onConfigurationChanged(newConfig);
    }

    private void showMeta() {
        addToHistory();
        if (artwork != "") {
            Glide.with(getApplicationContext()).load(artwork).transition(DrawableTransitionOptions.withCrossFade(200)).into(lb.p.artworkd);
            lb.p.artworkd.animate().alpha(0.3f).setDuration(24000).setInterpolator(new CycleInterpolator(12)).start();
        }
        lb.p.videoTitle.setText(currentPlayerHelper.getTitle());
        if (!currentPlayerHelper.getEpisode().isEmpty()) {
            lb.p.titlealt.setText("Episode " + currentPlayerHelper.getEpisode());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initVariables(intent);
        isEpisodeChanged = true;
    }

    private void addToHistory() {
        currentPlayerHelper.addToHistory();
    }

    private void passiveProg() {
        lb.p.prog.setProgress(0);
        lb.p.prog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userIsInHurry = true;
                    showCont(false);
                    if (player != null) {
                        long newPosition = player.getDuration() * lb.p.prog.getProgress() / 500;
                        lb.p.time.setText(stringForTime(player.getDuration() * lb.p.prog.getProgress() / 500));
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initVariables(Intent intent) {
        if (intent.hasExtra("fileId")) {
            currentPlayerHelper = new ScreenShowPlayerHelper(intent.getStringExtra("fileId"),this);
        }
        if (currentPlayerHelper != null) {
            initPlayer();
            try {
                showMeta();
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentPlayerHelper.addSuggestions(lb, in -> {
                playAnotherVideo(in);
            });
        }
        sharedPrefferences = getSharedPreferences(CF.SHARED_PREFERENCES_ROCK_PLAYER, MODE_PRIVATE);
    }

    private void playAnotherVideo(Intent in) {
        onStop();
        startActivity(in);
    }

    private void initHandlers() {
        chandler = new Handler(getMainLooper());
        hideContp = () -> {
            hideContpF();
        };
        progHoverHandler = new Handler(getMainLooper());
        progHoverRunnabler = () -> {
            ((ViewGroup) lb.p.progcont.getParent()).removeView(lb.p.progcont);
            lb.p.controller.addView(lb.p.progcont, 0);
            lb.p.prog.setFocusable(true);
        };
        timeh = new Handler(getMainLooper());
        updateProgressAction = new Runnable() {
            @Override
            public void run() {
                updateProgressViews();
                timeh.postDelayed(this, 1000); // Update every 1 second
            }
        };
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setOnclick() {
        timeh.postDelayed(updateProgressAction, 1000);
        _focusButton(lb.p.playB);
        _focusButton(lb.p.prevB);
        _focusButton(lb.p.nextB);
        _focusButton(lb.p.subtitleD);
        _focusButton(lb.p.audioB);
        _focusButton(lb.p.qualityB);
        _focusButton(lb.p.stretch);
        _focusButton(lb.p.comment);
        _focusButton(lb.p.home);
        _focusButton(lb.p.enchanceC);
        _focusButton(lb.p.boostS);
        _focusButton(lb.p.pnext);
        _focusButton(lb.p.languageMode);
        lb.p.channel.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setScaleX(1.05F);
                v.setScaleY(1.05F);
            } else {
                v.setScaleX(1F);
                v.setScaleY(1F);
            }
        });
        lb.p.playB.setOnClickListener(v -> {
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                    lb.p.playB.setImageDrawable(getDrawable(R.drawable.play));

                } else {
                    player.play();
                    lb.p.playB.setImageDrawable(getDrawable(R.drawable.pause_24dp));
                    hideContpF();

                }
            }
        });
        if (!isTvDevice(this)) {
            setMobileGestureLogic();
        } else {

            lb.playerView.setOnClickListener(v -> {
                showCont(true);
                try {
                    if (player.isPlaying()) {
                        player.pause();
                        lb.p.playB.setImageDrawable(getDrawable(R.drawable.play));
                    } else {
                        lb.p.playB.setImageDrawable(getDrawable(R.drawable.pause_24dp));
                        player.play();
                        hideContpF();
                    }
                } catch (Exception e) {
                }
            });
        }
        //prog-logic
        lb.p.qualityB.setOnClickListener(v -> {
            queryTrack(C.TRACK_TYPE_VIDEO);
            hideContpF();
        });
        lb.p.home.setOnClickListener(v -> {
            if (player != null) {
                if (player.getPlaybackParameters().speed == 1F) {
                    player.setPlaybackParameters(new PlaybackParameters(1.5f));
                    showPlayerToast("1.5x speed", false);
                } else {
                    showPlayerToast("1x speed", true);
                    player.setPlaybackParameters(new PlaybackParameters(1f));
                }
            }
        });
        lb.p.audioB.setOnClickListener(v -> {
            queryTrack(C.TRACK_TYPE_AUDIO);
            hideContpF();
        });
        lb.p.subtitleD.setOnClickListener(v -> {
            queryTrack(C.TRACK_TYPE_TEXT);
            hideContpF();

        });
        lb.p.nextB.setOnClickListener(v -> {
            player.seekTo(player.getCurrentPosition() + 10000);
        });
        lb.p.prevB.setOnClickListener(v -> {
            player.seekTo(player.getCurrentPosition() - 10000);
        });
        lb.p.pnext.setOnClickListener(v -> {
            playNextVideo();
        });
        lb.p.boostS.setOnClickListener(v -> {
            boolean boostSound = sharedPrefferences.getBoolean(BOOST_S, false);
            boostSound = !boostSound;
            sharedPrefferences.edit().putBoolean(BOOST_S, boostSound).apply();
            if (player != null) {
                boostSoundInPlayer(player, boostSound, lb.p.boostS);
            }
        });
        lb.p.enchanceC.setOnClickListener(v -> {
            boolean enchancedCB = sharedPrefferences.getBoolean(ENHANCED_C, false);
            enchancedCB = !enchancedCB;
            sharedPrefferences.edit().putBoolean(ENHANCED_C, enchancedCB).apply();
            if (player != null) {
                enhancedC(enchancedCB);
            }
        });
        lb.p.languageMode.setOnClickListener(v -> {
            languageMode = !languageMode;
            if (player != null) changeLanguage(player);
        });
        lb.p.stretch.setOnClickListener(v -> {
            if (lb.playerView.getScaleX() == 1) {
                lb.playerView.setScaleX(1.4F);
                lb.playerView.setScaleY(1.06F);
            } else {
                lb.playerView.setScaleX(1F);
                lb.playerView.setScaleY(1F);
            }
            sharedPrefferences.edit().putFloat("stretch", lb.playerView.getScaleX()).apply();
            sharedPrefferences.edit().putFloat("stretchy", lb.playerView.getScaleY()).apply();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setMobileGestureLogic() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                showCont(true); // Show controls on single tap
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (e.getRawX() > lb.playerView.getWidth() / 2) {
                    if (player != null) {
                        player.seekTo(player.getCurrentPosition() + 10000);
                        showPlayerToast("❯❯❯ 10 seconds", true);
                    }
                } else {
                    if (player != null) {
                        player.seekTo(player.getCurrentPosition() - 10000);
                        showPlayerToast("❮❮❮ 10 seconds", true);
                    }
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (player != null) {
                    player.setPlaybackParameters(new PlaybackParameters(2f));
                    showPlayerToast("2x speed", false);
                    speedX = true;
                }
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float deltaY = e2.getY() - e1.getY();
                float deltaX = e2.getX() - e1.getX();
                if (deltaY > 100 && Math.abs(velocityY) > 1000 && Math.abs(deltaX) < 200 && e1.getY() > 100) {
                    lb.p.stretch.performClick();
                    return true;
                }
                if (deltaY < -100 && Math.abs(velocityY) > 1000 && Math.abs(deltaX) < 200) {
                    openS();
                    return true;
                }
                return false;
            }
        });


        lb.p.contpBack.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (player != null && speedX) {
                    speedX = false;
                    player.setPlaybackParameters(new PlaybackParameters(1f));
                    hidePlayerToast();
                }
            }
            return gestureDetector.onTouchEvent(event);
        });
        lb.p.contp.setOnClickListener(v -> {
            hideContpF();
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void enhancedC(boolean enhancedCB) {
        if (enhancedCB) {
            TextureView textureView = (TextureView) lb.playerView.getVideoSurfaceView();
            Paint paint = getEnhancedPaint();
            if (textureView != null) textureView.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
        } else {
            TextureView textureView = (TextureView) lb.playerView.getVideoSurfaceView();
            if (textureView != null)
                textureView.setLayerType(View.LAYER_TYPE_HARDWARE, new Paint());
        }
    }

    @SuppressLint("ResourceAsColor")
    private void changeLanguage(ExoPlayer player) {
        if (languageMode) {
            lb.p.languageMode.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setPreferredTextLanguage("en-US").setPreferredAudioLanguages("hi", "hin", "en", "en-US").build());
        } else {
            lb.p.languageMode.setBackgroundTintList(ColorStateList.valueOf(R.color.white));
            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setPreferredTextLanguage("en-US").setPreferredAudioLanguages("kor", "ko", "en", "en-US").build());
        }
    }

    private void openS() {

        ValueAnimator va = new ValueAnimator();
        va.setDuration(300);
        va.setInterpolator(new AccelerateInterpolator());
        va.setFloatValues(lb.scrollView.getScrollY(), lb.s.getRoot().getHeight());
        va.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            lb.scrollView.scrollTo(0, (int) animatedValue);
        });
        Log.e(TAG, "openS: " + "openS" + lb.s.getRoot().getHeight());
        va.start();
        lb.s.getRoot().setVisibility(View.VISIBLE);
        currentPlayerHelper.focusSuggestion(lb);
        sOpen = true;
    }

    private void hidePlayerToast() {
        lb.p.playerToast.animate().alpha(0).setDuration(200).start();
    }

    private void showPlayerToast(String s, boolean hideAfterSomeTime) {
        lb.p.playerToast.setText(s);
        lb.p.playerToast.animate().alpha(1).setDuration(200).start();
        if (hideAfterSomeTime) {
            lb.p.playerToast.postDelayed(() -> {
                hidePlayerToast();
            }, 300);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppStopped = false;
        if (!isEpisodeChanged) {
            initPlayer();
            Log.e(TAG, "Resuming : " + "Previous");
        }
    }

    @SuppressLint("ResourceAsColor")
    @OptIn(markerClass = UnstableApi.class)
    public void boostSoundInPlayer(ExoPlayer player, boolean boostSound, ImageButton imageButton) {
        Log.d("boostSound", "boostSoundInPlayer: " + boostSound);
        if (boostSound) {
            imageButton.setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
            if (enhancer != null) {
                enhancer.release();
            }
            enhancer = new LoudnessEnhancer(player.getAudioSessionId());
            enhancer.setTargetGain(1500);
            enhancer.setEnabled(true);
        } else {
            imageButton.setBackgroundTintList(ColorStateList.valueOf(R.color.white));
            if (enhancer != null) enhancer.setEnabled(false);
        }
    }


    @OptIn(markerClass = UnstableApi.class)
    private void initPlayer() {
        RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        player = new ExoPlayer.Builder(this).setRenderersFactory(renderersFactory).build();

        lb.playerView.setPlayer(player);
        player.addAnalyticsListener(analyticsListener);

        Thread playerThread = new Thread(() -> {
            Log.e(TAG, "initPlayer: " + "playerThread");
            MediaSource mediaSource = currentPlayerHelper.getMediaItem(player);
            runOnUiThread(() -> {
                long defaultValue = currentPlayerHelper.getDefaultOffset();
                long startOffset = getStartOffset(defaultValue);
                player.setMediaSource(mediaSource, startOffset);
                if (!isAppStopped) {
                    player.prepare();
                    player.play();
                    hideContpF();
                    if (userIsInHurry) {
                        player.seekTo(player.getDuration() * lb.p.prog.getProgress() / 500);
                        userIsInHurry = false;
                    }
                    progLogic();
                }
            });
        });
        playerThread.start();

    }

    private long getStartOffset(long defaultValue) {
        long startOffset = defaultValue;
        if (sharedPrefferences.getLong(currentPlayerHelper.getEpisodeID() + "%", 0) < 90) {
            startOffset = sharedPrefferences.getLong(currentPlayerHelper.getEpisodeID() + "ms", defaultValue);
        }
        return startOffset;
    }

    private void progLogic() {
        lb.p.prog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    showCont(false);
                    long newPosition = player.getDuration() * lb.p.prog.getProgress() / 500;
                    lb.p.time.setText(stringForTime(player.getDuration() * lb.p.prog.getProgress() / 500));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                progTracking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(player.getDuration() * lb.p.prog.getProgress() / 500);
                progTracking = false;
            }
        });
        AtomicBoolean progActive = new AtomicBoolean(false);
        final long[] oldpos = {0};
        lb.p.prog.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                progActive.set(true);
                oldpos[0] = lb.p.prog.getProgress();
                lb.p.prog.setThumb(getDrawable(R.drawable.thumb_pressed));
                try {
                    timeh.removeCallbacks(updateProgressAction);
                } catch (Exception e) {
                }

            } else {
                if (lb.p.prog.getProgress() != oldpos[0] && progActive.get()) {
                    Log.e(TAG, "onStopTrackingTouch: " + "progrssChanged");
                    player.seekTo(player.getDuration() * lb.p.prog.getProgress() / 500);
                    progActive.set(false);
                }
                lb.p.prog.setThumb(getDrawable(R.drawable.transparent));
                timeh.postDelayed(updateProgressAction, 1000);
            }
        });
        lb.p.prog.setOnClickListener(v -> {
            Log.e(TAG, "onStopTrackingTouch: " + "clicked");
            if (progActive.get()) {
                Log.e(TAG, "onStopTrackingTouch: " + "progrssChanged");
                player.seekTo(player.getDuration() * lb.p.prog.getProgress() / 500);
            }
        });
    }

    @Override
    protected void onStop() {
        isAppStopped = true;
        isEpisodeChanged = false;
        new Thread(() -> {
            Glide.get(this).clearDiskCache();
        }).start();

        try {
            player.stop();
            player.release();
            timeh.removeCallbacks(updateProgressAction);
            Log.e("t", "" + player.getCurrentPosition() / 1000);
            long currnt = player.getCurrentPosition();
            long total = player.getDuration();
            String episodeID = currentPlayerHelper.getEpisodeID();
            sharedPrefferences.edit().putLong(episodeID + "ms", currnt).apply();
            sharedPrefferences.edit().putLong(episodeID + "%", currnt * 100 / total).apply();
        } catch (Exception e) {
            finish();
        }
        super.onStop();
    }

    public void _focusButton(final View _view) {
        _view.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                showCont(false);
                view.setBackground(getDrawable(R.drawable.selected_circle));
                ((ImageButton) view).setColorFilter(Color.parseColor("#555555"));

            } else {
                view.setBackground(getDrawable(R.drawable.notselected));
                ((ImageButton) view).setColorFilter(Color.parseColor("#ffffff"));
            }
        });

    }

    public void showCont(boolean animate) {
        try {
            chandler.removeCallbacks(hideContp);
        } catch (Exception e) {
        }
        lb.scrollView.setScrollY(0);
        lb.p.contp.setAlpha(1F);
        lb.p.contp.setVisibility(View.VISIBLE);
        if (animate) {
            lb.p.controller.setTranslationY(lb.p.controller.getHeight());
            lb.p.controller.animate().translationY(0).setDuration(300).start();
            lb.p.titleBox.setTranslationY(-lb.p.titleBox.getHeight());
            lb.p.titleBox.animate().translationY(0).setDuration(300).start();
        }
        chandler.postDelayed(hideContp, 5500);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            if (sOpen) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    closeS();
                    return true;
                }

                return super.onKeyDown(keyCode, keyEvent);
            }
            if (lb.p.contp.getVisibility() == View.INVISIBLE) {

                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    showCont(true);
                    lb.p.playB.requestFocus();
                }
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    openS();
                    return true;
                }

                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    if (player != null) {
                        player.seekTo(player.getCurrentPosition() - 10000);
                        lb.p.time.setText(stringForTime(player.getDuration() * lb.p.prog.getProgress() / 500));
                        lb.p.prog.setProgress((int) (player.getCurrentPosition() * 500 / player.getDuration()));
                        showProgHover();
                    }
                }
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    if (player != null) {
                        player.seekTo(player.getCurrentPosition() + 10000);
                        lb.p.prog.setProgress((int) (player.getCurrentPosition() * 500 / player.getDuration()));
                        lb.p.time.setText(stringForTime(player.getDuration() * lb.p.prog.getProgress() / 500));
                        showProgHover();
                    }
                }
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                    showCont(true);
                    if (player.isPlaying()) {
                        player.pause();
                        lb.p.playB.setImageDrawable(getDrawable(R.drawable.play));
                    } else {
                        player.play();
                        lb.p.playB.setImageDrawable(getDrawable(R.drawable.pause_24dp));
                    }
                }
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    try {
                        if (player.isPlaying()) {
                            player.pause();
                            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            RockPlayer.this.finish();
                        }
                    } catch (Exception e) {
                        RockPlayer.this.finish();
                    }
                }
            } else if (lb.p.contp.getVisibility() == View.VISIBLE) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    hideContpF();
                    return true;
                }
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    if (lb.p.playB.hasFocus()) hideContpF();
                }
            }
        } else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {

        }

        return super.onKeyDown(keyCode, keyEvent);
    }

    private void closeS() {
        lb.scrollView.setScrollY(0);
        lb.s.getRoot().setVisibility(View.INVISIBLE);
        sOpen = false;
    }

    private void hideContpF() {
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0);
        animator.addUpdateListener(animation -> {
            lb.p.contp.setAlpha((Float) animation.getAnimatedValue());
            lb.p.controller.animate().translationY(lb.p.controller.getHeight()).setDuration(300).start();
            lb.p.titleBox.animate().translationY(-lb.p.titleBox.getHeight()).setDuration(300).start();
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                lb.p.contp.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(200);
        animator.start();
        lb.playerView.requestFocus();
    }

    private void showProgHover() {
        progHoverHandler.removeCallbacks(progHoverRunnabler);
        ((ViewGroup) lb.p.progcont.getParent()).removeView(lb.p.progcont);
        lb.p.hoverprogholder.addView(lb.p.progcont);
        lb.p.prog.setFocusable(false);
        progHoverHandler.postDelayed(progHoverRunnabler, 2000);
    }

    @Override
    protected void onDestroy() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
    }

    private void updateProgressViews() {
        if (player != null) {
            if (!progTracking) {
                long currentPosition = player.getCurrentPosition();
                long duration = player.getDuration();
                lb.p.time.setText(stringForTime(currentPosition));
                lb.p.time2.setText(stringForTime(duration));
                long remainingTime = duration - currentPosition;
                if (remainingTime < 20000 && remainingTime > 5000) {
                    if (playNextEnable) {
                        player.stop();
                        playNextEnable = false;
                        playNextVideo();
                        return;
                    }
                }
                long estimatedEndTimeMillis = System.currentTimeMillis() + remainingTime;
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a"); // e.g., 2:34 PM
                String endTimeString = timeFormat.format(new Date(estimatedEndTimeMillis));
                lb.p.time3e.setText("End at " + endTimeString);
                if (player.isPlaying()) {
                    lb.p.prog.setProgress((int) (currentPosition * 500 / duration));
                    lb.p.prog.setSecondaryProgress((int) (player.getBufferedPosition() * 500 / duration));
                }
            }
            if (isAppStopped) {
                player.stop();
                player.release();
            }
        }
    }

    private void playNextVideo() {
        Object ob = currentPlayerHelper.playNext();
        Log.e(TAG, "playNextVideo: " + ob);
        if (ob != null) {
            playAnotherVideo(currentPlayerHelper.startPlayNext(ob));
        }
    }

    private String stringForTime(long timeMs) {
        int seconds = (int) (timeMs / 1000);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;

        Formatter timeFormatter = new Formatter(new StringBuilder(), Locale.getDefault());

        timeFormatter.format("%02d:%02d:%02d", h, m, s);
        return timeFormatter.toString();
    }

    @OptIn(markerClass = UnstableApi.class)
    public void queryTrack(int trackTypeVideo) {

        SelectordialogBinding sb = SelectordialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder tbuilder = new AlertDialog.Builder(this, R.style.PlayerDialogStyle);
        tbuilder.setView(sb.getRoot());
        Tracks tracks = player.getCurrentTracks();
        ImmutableList<Tracks.Group> groups = tracks.getGroups();

        AlertDialog alertDialog = tbuilder.create();

        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.END; // Position the dialog to the right
            params.width = WindowManager.LayoutParams.WRAP_CONTENT; // Adjust width
            params.height = WindowManager.LayoutParams.MATCH_PARENT; // Full height
            window.setAttributes(params);
        }
        for (int j = 0; j < groups.size(); j++) {
            Tracks.Group tg = groups.get(j);

            if (tg.getType() == trackTypeVideo) {
                ArrayList<Format> formats = new ArrayList<>();
                for (int i = 0; i < tg.length; i++) {
                    formats.add(tg.getTrackFormat(i));
                }

                switch (tg.getType()) {
                    case C.TRACK_TYPE_VIDEO:
                        Collections.sort(formats, Comparator.comparingInt((Format format) -> format.height));
                        break;
                    case C.TRACK_TYPE_AUDIO:
                        Collections.sort(formats, Comparator.comparingInt((Format format) -> format.bitrate));
                        break;
                    case C.TRACK_TYPE_TEXT:
                        break;
                    default:
                        break;
                }

                addTracks(formats, sb.epglist, tg, alertDialog);
            }
        }

        alertDialog.show();
        setFullScreenPlayer(false);
        alertDialog.setOnDismissListener(dialog -> {
            setFullScreenPlayer(true);
        });

    }

    private void setFullScreenPlayer(boolean b) {
        if (b) {
            ViewGroup.LayoutParams lp = lb.playerView.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lb.playerView.setLayoutParams(lp);
        } else {
            ViewGroup.LayoutParams lp = lb.playerView.getLayoutParams();

            lp.width = lb.playerView.getWidth() - dpToPx(this, 500);
            lp.height = (int) (lp.width * (9.0 / 16.0));

            Log.e(TAG, "setFullScreenPlayer: " + lp.toString());
            lb.playerView.setLayoutParams(lp);
        }
    }

    private int dpToPx(RockPlayer rockPlayer, int i) {
        //return dp to px
        return (int) (i * rockPlayer.getResources().getDisplayMetrics().density);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void addTracks(ArrayList<Format> listFormat, LinearLayout epglist, Tracks.Group tg, AlertDialog alertDialog) {

        for (int i = 0; i < listFormat.size(); i++) {

            TrackMediaBinding tbb = TrackMediaBinding.inflate(getLayoutInflater(), epglist, true);
            tbb.titletxt.setText(tg.getTrackFormat(i).label);
            tbb.selectb.setChecked(tg.isTrackSelected(i));
            switch (tg.getType()) {
                case C.TRACK_TYPE_VIDEO:
                    tbb.titletxt.setText(tg.getTrackFormat(i).height + "p");
                    tbb.trackdesc.setText(tg.getTrackFormat(i).width + "x" + tg.getTrackFormat(i).height + " · " + tg.getTrackFormat(i).codecs + " · " + tg.getTrackFormat(i).frameRate);
                    break;
                case C.TRACK_TYPE_AUDIO:
                    tbb.trackdesc.setText(tg.getTrackFormat(i).language + " · " + tg.getTrackFormat(i).codecs + " · " + tg.getTrackFormat(i).bitrate);
                    break;
                case C.TRACK_TYPE_TEXT:
                    tbb.trackdesc.setText(tg.getTrackFormat(i).language + " · " + tg.getTrackFormat(i).frameRate);
                    break;
                default:
                    tbb.trackdesc.setText("unkonwn");
                    break;
            }
            int finalI = i;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tbb.selectb.setFocusable(View.NOT_FOCUSABLE);
            }
            tbb.linear1.setOnClickListener(v -> {
                tbb.selectb.setChecked(!tbb.selectb.isChecked());
                alertDialog.dismiss();
            });
            tbb.selectb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setTrackTypeDisabled(tg.getType(), false).build());
                    player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setOverrideForType(new TrackSelectionOverride(tg.getMediaTrackGroup(), finalI)).build());
                } else {
                    player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setTrackTypeDisabled(tg.getType(), true).build());
                }
            });
            tbb.linear1.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    tbb.titletxt.setTextColor(getColor(R.color.black));
                    tbb.trackdesc.setTextColor(getColor(R.color.black));
                    tbb.linear1.setBackgroundResource(R.drawable.selected);
                    tbb.selectb.setBackgroundDrawable(getDrawable(R.drawable.notselected));
                } else {
                    tbb.titletxt.setTextColor(getColor(R.color.white));
                    tbb.trackdesc.setTextColor(getColor(R.color.white));
                    tbb.linear1.setBackgroundResource(R.drawable.rounded_4dp);
                    tbb.selectb.setBackgroundDrawable(getDrawable(R.drawable.transparent));
                }
            });
            if (tg.isTrackSelected(i)) {
                new Handler().postDelayed(() -> {
                    tbb.linear1.requestFocus();
                }, 200);
            }

        }

    }

    public boolean isTvDevice(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null && uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
            return true;
        }
        return false;
    }

}