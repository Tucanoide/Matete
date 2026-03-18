import { describe, it, expect, vi, beforeEach } from 'vitest';
import { soundManager } from './sounds';

// Spy on HTMLAudioElement prototype before any sounds are played
const playSpy = vi.spyOn(HTMLAudioElement.prototype, 'play').mockImplementation(async () => {});

describe('SoundManager', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    soundManager.setEnabled(false); // Reset to default state
  });

  it('should be disabled by default (as per recent change)', () => {
    expect(soundManager.isEnabled()).toBe(false);
  });

  it('should not play sounds when disabled', () => {
    soundManager.setEnabled(false);
    soundManager.play('success');
    expect(playSpy).not.toHaveBeenCalled();
  });

  it('should play sounds when enabled', () => {
    soundManager.setEnabled(true);
    soundManager.play('success');
    expect(playSpy).toHaveBeenCalled();
  });

  it('should toggle enabled state', () => {
    soundManager.setEnabled(true);
    expect(soundManager.isEnabled()).toBe(true);
    soundManager.setEnabled(false);
    expect(soundManager.isEnabled()).toBe(false);
  });
});
