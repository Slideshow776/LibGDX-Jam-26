package no.sandramoen.libgdxjam26.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

/**
 * Sets music1 to jump to beginning of music2 after it completes. Music2 will repeat.
 *
 * Shouldn't be any lag when it jumps between music. Useful for any music that has an intro, and needs to loop after that. (battle themes, etc).
 */
public class LoopedSound implements Music {

    public Music music1;
    public Music music2;
    public Music currMusic;

    public LoopedSound(FileHandle m1, FileHandle m2) {
        this.music1 = Gdx.audio.newMusic(m1);
        this.music1.setLooping(false);
        this.currMusic = this.music1;

        if (m2 != null) {
            this.music1.setOnCompletionListener(music -> {
                music2.play();
                currMusic = music2;
            });
            this.music2 = Gdx.audio.newMusic(m2);
            this.music2.setLooping(true);
            this.music2.play();
            this.music2.pause();
        }
    }

    @Override
    public void play() {
        this.currMusic.play();
    }

    @Override
    public void pause() {
        this.currMusic.pause();
    }

    @Override
    public void stop() {

        // Reset to be played from beginning.
        if (this.music2 != null) this.music2.stop();

        this.music1.stop();
        this.currMusic = this.music1;
    }

    @Override
    public boolean isPlaying() {
        return this.currMusic.isPlaying();
    }

    @Override
    public void setLooping(boolean isLooping) {
        this.currMusic.setLooping(isLooping);
    }

    @Override
    public boolean isLooping() {
        return this.currMusic.isLooping();
    }

    @Override
    public void setVolume(float volume) {
        if (this.music2 != null) this.music2.setVolume(volume);
        this.music1.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return this.currMusic.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        this.currMusic.setPan(pan, volume);
    }

    @Override
    public void setPosition(float position) {
        this.currMusic.setPosition(position);
    }

    @Override
    public float getPosition() {
        return this.currMusic.getPosition();
    }

    @Override
    public void dispose() {
        this.music1.dispose();
        if (this.music2 != null) this.music2.dispose();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        this.currMusic.setOnCompletionListener(listener);
    }
}
