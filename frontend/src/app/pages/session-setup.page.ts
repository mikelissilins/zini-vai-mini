import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { GameApiService } from '../core/game-api.service';
import { I18nService } from '../core/i18n.service';
import { GameView, TeamInput } from '../core/models';

const TEAM_COLORS = ['#B33A3A', '#3B6FB6', '#7B4E9D', '#C46B16', '#2F7D64', '#A0475B', '#356859', '#6D5F9E', '#A65A31', '#1F7A8C', '#8C6A22', '#5D4B8A'];

@Component({
  selector: 'app-session-setup-page',
  imports: [FormsModule, RouterLink],
  templateUrl: './session-setup.page.html',
  styleUrl: './session-setup.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SessionSetupPage implements OnInit {
  private readonly api = inject(GameApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  protected readonly i18n = inject(I18nService);
  protected readonly teamColors = TEAM_COLORS;
  protected readonly game = signal<GameView | null>(null);
  protected readonly creating = signal(false);
  protected readonly error = signal('');
  protected readonly teams = signal<TeamInput[]>([
    { name: 'Viļņi', color: TEAM_COLORS[0] },
    { name: 'Bākas', color: TEAM_COLORS[1] },
  ]);

  ngOnInit(): void {
    this.api.getGame(this.route.snapshot.paramMap.get('id')!).subscribe({
      next: (game) => this.game.set(game),
      error: (error) => this.error.set(error.error?.detail || 'Spēli neizdevās ielādēt.'),
    });
  }

  protected addTeam(): void {
    if (this.teams().length >= 12) return;
    this.teams.update((teams) => [...teams, {
      name: `Komanda ${teams.length + 1}`,
      color: TEAM_COLORS[teams.length % TEAM_COLORS.length],
    }]);
  }

  protected removeTeam(index: number): void {
    if (this.teams().length <= 2) return;
    this.teams.update((teams) => teams.filter((_, teamIndex) => teamIndex !== index));
  }

  protected moveTeam(index: number, offset: number): void {
    const target = index + offset;
    if (target < 0 || target >= this.teams().length) return;
    this.teams.update((teams) => {
      const copy = [...teams];
      [copy[index], copy[target]] = [copy[target], copy[index]];
      return copy;
    });
  }

  protected updateTeam(index: number, field: keyof TeamInput, value: string): void {
    this.teams.update((teams) => teams.map((team, teamIndex) => teamIndex === index ? { ...team, [field]: value } : team));
  }

  protected launch(): void {
    const game = this.game();
    if (!game || this.teams().some((team) => !team.name.trim())) return;
    this.creating.set(true);
    this.error.set('');
    this.api.createSession(game.id, this.teams().map((team) => ({ ...team, name: team.name.trim() }))).subscribe({
      next: (session) => void this.router.navigate(['/sessions', session.id, 'host']),
      error: (error) => {
        this.creating.set(false);
        this.error.set(error.error?.detail || 'Sesiju neizdevās izveidot.');
      },
    });
  }
}
