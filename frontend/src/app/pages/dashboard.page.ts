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
  protected selectedTemplateId = '';
  protected newGameTitle = '';

  ngOnInit(): void {
    this.reload();
  }

  protected createGame(): void {
    if (!this.selectedTemplateId || !this.newGameTitle.trim()) return;
    this.creating.set(true);
    this.error.set('');
    this.api.createFromTemplate(this.selectedTemplateId, this.newGameTitle.trim(), 'en').subscribe({
      next: (game) => void this.router.navigate(['/games', game.id, 'edit']),
      error: (error) => {
        this.creating.set(false);
        this.error.set(error.error?.detail || 'Spēli neizdevās izveidot.');
      },
    });
  }

  protected deleteGame(game: GameSummary): void {
    if (!window.confirm(`Dzēst “${game.title}”?`)) return;
    this.api.deleteGame(game.id).subscribe({
      next: () => this.games.update((items) => items.filter((item) => item.id !== game.id)),
      error: (error) => this.error.set(error.error?.detail || 'Spēli neizdevās izdzēst.'),
    });
  }

  protected selectTemplate(template: GameSummary): void {
    this.selectedTemplateId = template.id;
    if (!this.newGameTitle) {
      this.newGameTitle = template.templateKey === 'blank' ? 'Mana spēle' : 'Zini vai mini';
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
        this.error.set(error.status === 403 ? 'Šis Clerk konts nav aplikācijas īpašnieks.' : 'Datus neizdevās ielādēt.');
        this.loading.set(false);
      },
    });
  }
}
