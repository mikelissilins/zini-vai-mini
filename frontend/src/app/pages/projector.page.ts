import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { GameApiService } from '../core/game-api.service';
import { SessionView } from '../core/models';
import { SoundService } from '../core/sound.service';

@Component({
  selector: 'app-projector-page',
  templateUrl: './projector.page.html',
  styleUrl: './projector.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectorPage implements OnInit, OnDestroy {
  private readonly api = inject(GameApiService);
  private readonly sound = inject(SoundService);
  private readonly token = inject(ActivatedRoute).snapshot.paramMap.get('token')!;
  private events?: EventSource;
  private feedbackTimer?: number;
  private detailTimer?: number;
  private timer?: number;
  private timedQuestionId?: string;
  protected readonly session = signal<SessionView | null>(null);
  protected readonly connected = signal(false);
  protected readonly error = signal('');
  protected readonly scoreFeedback = signal<{ awarded: number; correct: boolean; color: string } | null>(null);
  protected readonly showResultDetails = signal(false);
  protected readonly timeLeft = signal(0);
  protected readonly activeTeam = computed(() => {
    const session = this.session();
    return session?.teams.find((team) => team.id === session.activeTeamId) || null;
  });
  protected readonly rankedTeams = computed(() => [...(this.session()?.teams || [])]
    .sort((left, right) => left.rank - right.rank || right.score - left.score || left.position - right.position));
  protected readonly scene = computed(() => {
    const session = this.session();
    if (session?.status === 'FINISHED') return 'final';
    return session?.selectedQuestion ? 'question' : 'board';
  });

  ngOnInit(): void {
    this.refresh();
    this.connect();
  }

  ngOnDestroy(): void {
    this.events?.close();
    window.clearTimeout(this.feedbackTimer);
    window.clearTimeout(this.detailTimer);
    window.clearInterval(this.timer);
  }

  protected copy(lv: string, en: string): string {
    return this.session()?.locale === 'en' ? en : lv;
  }

  private refresh(): void {
    this.api.getPublicSession(this.token).subscribe({
      next: (session) => {
        this.applySnapshot(session);
        this.error.set('');
      },
      error: (error) => this.error.set(error.error?.detail || 'Spēles pārraide nav atrasta.'),
    });
  }

  private connect(): void {
    this.events = new EventSource(`/api/public/sessions/${this.token}/events`);
    this.events.addEventListener('snapshot', (event) => {
      this.applySnapshot(JSON.parse((event as MessageEvent).data) as SessionView);
      this.connected.set(true);
      this.error.set('');
    });
    this.events.onerror = () => {
      this.connected.set(false);
      this.refresh();
    };
  }

  private applySnapshot(next: SessionView): void {
    const previous = this.session();
    this.syncTimer(next);
    const nextScene = next.status === 'FINISHED' ? 'final' : next.selectedQuestion ? 'question' : 'board';
    const previousScene = previous?.status === 'FINISHED' ? 'final' : previous?.selectedQuestion ? 'question' : 'board';
    if (!previous || nextScene !== previousScene) this.sound.setScene(nextScene);
    if (next.status === 'FINISHED' && previous?.status !== 'FINISHED') {
      this.showResultDetails.set(false);
      window.clearTimeout(this.detailTimer);
      this.detailTimer = window.setTimeout(() => this.showResultDetails.set(true), 8000);
    }
    if (previous && next.usedCount > previous.usedCount) {
      const previousScore = previous.teams.find((team) => team.id === previous.activeTeamId)?.score || 0;
      const currentScore = next.teams.find((team) => team.id === previous.activeTeamId)?.score || 0;
      const result = next.categories.flatMap((category) => category.questions)
        .find((question) => question.used && !previous.categories.flatMap((category) => category.questions)
          .find((previousQuestion) => previousQuestion.id === question.id)?.used);
      this.scoreFeedback.set({
        awarded: Math.max(0, currentScore - previousScore),
        correct: result?.correct === true,
        color: previous.teams.find((team) => team.id === previous.activeTeamId)?.color || 'var(--deep-sea)',
      });
      this.sound.result(result?.correct === true);
      window.clearTimeout(this.feedbackTimer);
      this.feedbackTimer = window.setTimeout(() => this.scoreFeedback.set(null), 1300);
    }
    this.session.set(next);
  }

  private syncTimer(session: SessionView): void {
    const question = session.selectedQuestion;
    if (!question) {
      this.timedQuestionId = undefined;
      window.clearInterval(this.timer);
      this.timeLeft.set(0);
      return;
    }
    if (this.timedQuestionId === question.id) return;
    this.timedQuestionId = question.id;
    this.timeLeft.set(question.timeLimitSeconds);
    window.clearInterval(this.timer);
    this.timer = window.setInterval(() => this.timeLeft.update((seconds) => Math.max(0, seconds - 1)), 1000);
  }
}
