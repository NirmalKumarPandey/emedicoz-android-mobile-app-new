package com.emedicoz.app.modelo;

import com.google.android.exoplayer2.Player;

public class PlayerState {
    private Player player;
    private float playbackSpeed;
    private int qualitySelection;
    private String playbackSpeedText;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public float getPlaybackSpeed() {
        return playbackSpeed;
    }

    public void setPlaybackSpeed(float playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }

    public int getQualitySelection() {
        return qualitySelection;
    }

    public void setQualitySelection(int qualitySelection) {
        this.qualitySelection = qualitySelection;
    }

    public String getPlaybackSpeedText() {
        return playbackSpeedText;
    }

    public void setPlaybackSpeedText(String playbackSpeedText) {
        this.playbackSpeedText = playbackSpeedText;
    }
}

