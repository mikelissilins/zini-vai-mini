import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { GameApiService } from '../core/game-api.service';
import { I18nService } from '../core/i18n.service';
import { GameSummary, SessionSummary } from '../core/models';

@Component({
  selector: 'app-dashboard-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './dashboard.page.html',
  styleUrl: './dashboard.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardPage implements OnInit {
  private readonly api = inject(GameApiService);
  private readonly router = inject(Router);
  protected readonly i18n = inject(I18nService);

  protected readonly templates = signal<GameSummary[]>([]);
  protected readonly games = signal<GameSummary[]>([]);
  protected readonly sessions = signal<SessionSummary[]>([]);
  protected readonly loading = signal(true);
  protected readonly creating = signal(false);
  protected readonly error = signal('');
  protected readonly pendingDelete = signal<{ type: 'game' | 'session'; id: string; title: string } | null>(null);
  protected selectedTemplateId = '';
  protected newGameTitle = '';

  ngOnInit(): void {
    this.reload();
  }

  protected createGame(): void {
    if (!this.selectedTemplateId || !this.newGameTitle.trim()) return;
    this.creating.set(true);
    this.error.set('');
    this.api.createFromTemplate(this.selectedTemplateId, this.newGameTitle.trim(), this.i18n.locale()).subscribe({
      next: (game) => void this.router.navigate(['/games', game.id, 'edit']),
      error: (error) => {
        this.creating.set(false);
        this.error.set(error.error?.detail || this.i18n.t('createGameError'));
      },
    });
  }

  protected deleteGame(game: GameSummary): void {
    this.pendingDelete.set({ type: 'game', id: game.id, title: game.title });
  }

  protected deleteSession(session: SessionSummary): void {
    this.pendingDelete.set({ type: 'session', id: session.id, title: session.title });
  }

  protected cancelDelete(): void {
    this.pendingDelete.set(null);
  }

  protected confirmDelete(): void {
    const pending = this.pendingDelete();
    if (!pending) return;
    this.pendingDelete.set(null);
    if (pending.type === 'game') {
      this.api.deleteGame(pending.id).subscribe({
        next: () => this.games.update((items) => items.filter((item) => item.id !== pending.id)),
        error: (error) => this.error.set(error.error?.detail || this.i18n.t('deleteGameError')),
      });
      return;
    }
    this.api.deleteSession(pending.id).subscribe({
      next: () => this.sessions.update((items) => items.filter((item) => item.id !== pending.id)),
      error: (error) => this.error.set(error.error?.detail || this.i18n.t('deleteSessionError')),
    });
  }

  protected selectTemplate(template: GameSummary): void {
    this.selectedTemplateId = template.id;
    if (!this.newGameTitle) {
      this.newGameTitle = template.templateKey === 'blank' ? this.i18n.t('defaultGameName') : this.i18n.t('campGameName');
    }
  }

  private reload(): void {
    this.loading.set(true);
    forkJoin({ templates: this.api.listTemplates(), games: this.api.listGames(), sessions: this.api.listSessions() }).subscribe({
      next: ({ templates, games, sessions }) => {
        this.templates.set(templates);
        this.games.set(games);
        this.sessions.set(sessions);
        if (templates.length) this.selectTemplate(templates.find((item) => item.templateKey === 'camp') || templates[0]);
        this.loading.set(false);
      },
      error: (error) => {
        this.error.set(error.status === 403 ? this.i18n.t('ownerError') : this.i18n.t('loadGameError'));
        this.loading.set(false);
      },
    });
  }
}
