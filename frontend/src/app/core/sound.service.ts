import { Injectable, signal } from '@angular/core';

type SoundScene = 'board' | 'question' | 'final';

@Injectable({ providedIn: 'root' })
export class SoundService {
  readonly muted = signal(localStorage.getItem('zini-vai-mini-muted') !== 'false');
  private context?: AudioContext;
  private loop?: number;
  private scene: SoundScene = 'board';

  toggle(): void {
    this.muted.update((muted) => !muted);
    localStorage.setItem('zini-vai-mini-muted', String(this.muted()));
    if (this.muted()) {
      this.stopLoop();
      return;
    }
    void this.resumeAndPlay();
  }

  setScene(scene: SoundScene): void {
    if (this.scene === scene) return;
    this.scene = scene;
    if (!this.muted()) void this.resumeAndPlay();
  }

  result(correct: boolean): void {
    if (this.muted()) return;
    void this.playResult(correct);
  }

  private async resumeAndPlay(): Promise<void> {
    const context = this.context ??= new AudioContext();
    await context.resume();
    this.startLoop();
  }

  private startLoop(): void {
    this.stopLoop();
    if (this.scene === 'final') {
      this.chord([392, 494, 587], 0.42, 0.035);
      return;
    }
    const pulse = () => this.scene === 'board'
      ? this.chord([196, 247], 0.22, 0.018)
      : this.chord([110, 165], 0.16, 0.025);
    pulse();
    this.loop = window.setInterval(pulse, this.scene === 'board' ? 7000 : 2100);
  }

  private stopLoop(): void {
    window.clearInterval(this.loop);
    this.loop = undefined;
  }

  private async playResult(correct: boolean): Promise<void> {
    const context = this.context ??= new AudioContext();
    await context.resume();
    this.chord(correct ? [392, 494, 587] : [220, 185], correct ? 0.38 : 0.32, 0.05);
  }

  private chord(frequencies: number[], duration: number, gain: number): void {
    const context = this.context;
    if (!context || this.muted()) return;
    frequencies.forEach((frequency, index) => {
      const oscillator = context.createOscillator();
      const volume = context.createGain();
      oscillator.frequency.value = frequency;
      oscillator.type = this.scene === 'question' ? 'triangle' : 'sine';
      volume.gain.setValueAtTime(0.0001, context.currentTime);
      volume.gain.exponentialRampToValueAtTime(gain / frequencies.length, context.currentTime + 0.02);
      volume.gain.exponentialRampToValueAtTime(0.0001, context.currentTime + duration);
      oscillator.connect(volume).connect(context.destination);
      oscillator.start(context.currentTime + index * 0.02);
      oscillator.stop(context.currentTime + duration + 0.03);
    });
  }
}
