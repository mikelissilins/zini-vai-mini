import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { GameApiService } from '../core/game-api.service';
import { SessionView } from '../core/models';

@Component({
  selector: 'app-projector-page',
  templateUrl: './projector.page.html',
  styleUrl: './projector.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProjectorPage implements OnInit, OnDestroy {
  private readonly api = inject(GameApiService);
  private readonly token = inject(ActivatedRoute).snapshot.paramMap.get('token')!;
  private events?: EventSource;
  private feedbackTimer?: number;
  protected readonly session = signal<SessionView | null>(null);
  protected readonly connected = signal(false);
  protected readonly error = signal('');
  protected readonly scoreFeedback = signal<{ awarded: number } | null>(null);
  protected readonly activeTeam = computed(() => {
    const session = this.session();
    return session?.teams.find((team) => team.id === session.activeTeamId) || null;
  });
  protected readonly rankedTeams = computed(() => [...(this.session()?.teams || [])]
    .sort((left, right) => left.rank - right.rank || right.score - left.score || left.position - right.position));
  protected readonly podiumTeams = computed(() => this.rankedTeams().filter((team) => team.rank <= 3));
  protected readonly fourthTeams = computed(() => this.rankedTeams().filter((team) => team.rank === 4));
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
    if (previous && next.usedCount > previous.usedCount) {
      const previousScore = previous.teams.find((team) => team.id === previous.activeTeamId)?.score || 0;
      const currentScore = next.teams.find((team) => team.id === previous.activeTeamId)?.score || 0;
      this.scoreFeedback.set({ awarded: Math.max(0, currentScore - previousScore) });
      window.clearTimeout(this.feedbackTimer);
      this.feedbackTimer = window.setTimeout(() => this.scoreFeedback.set(null), 1300);
    }
    this.session.set(next);
  }
}
