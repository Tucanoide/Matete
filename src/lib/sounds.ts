'use client';

class SoundManager {
  private static instance: SoundManager;
  private sounds: Map<string, HTMLAudioElement> = new Map();
  private enabled: boolean = true;

  private constructor() {
    if (typeof window !== 'undefined') {
      this.loadSound('success', '/audio/adivino.wav');
      this.loadSound('fail', '/audio/chan.wav');
      this.loadSound('new_word', '/audio/nuevapalabra.wav');
      this.loadSound('level_up', '/audio/adivino.wav'); // Using success sound for level up for now
    }
  }

  public static getInstance(): SoundManager {
    if (!SoundManager.instance) {
      SoundManager.instance = new SoundManager();
    }
    return SoundManager.instance;
  }

  private loadSound(name: string, path: string) {
    const audio = new Audio(path);
    audio.preload = 'auto';
    this.sounds.set(name, audio);
  }

  public play(name: string) {
    if (!this.enabled) return;
    const audio = this.sounds.get(name);
    if (audio) {
      audio.currentTime = 0;
      const playPromise = audio.play();
      if (playPromise !== undefined) {
        playPromise.catch(e => console.warn('Audio play blocked:', e));
      }
    }
  }

  public setEnabled(enabled: boolean) {
    this.enabled = enabled;
  }

  public isEnabled() {
    return this.enabled;
  }
}

export const soundManager = SoundManager.getInstance();
