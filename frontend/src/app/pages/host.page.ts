import { ChangeDetectionStrategy, Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';

import { GameApiService } from '../core/game-api.service';
import { SessionView } from '../core/models';

@Component({
  selector: 'app-host-page',
  imports: [RouterLink],
  templateUrl: './host.page.html',
  styleUrl: './host.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HostPage implements OnInit {
  private readonly api = inject(GameApiService);
  private readonly sessionId = inject(ActivatedRoute).snapshot.paramMap.get('id')!;
  protected readonly session = signal<SessionView | null>(null);
  protected readonly busy = signal(false);
  protected readonly error = signal('');
  protected readonly activeTeam = computed(() => {
    const session = this.session();
    return session?.teams.find((team) => team.id === session.activeTeamId) || null;
  });

  ngOnInit(): void { this.reload(); }

  protected select(questionId: string): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.selectQuestion(session.id, questionId, session.version));
  }

  protected reveal(): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.revealAnswer(session.id, session.version));
  }

  protected useHint(): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.useHint(session.id, session.version));
  }

  protected score(correct: boolean): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.scoreAnswer(session.id, correct, session.version));
  }

  protected undo(): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.undoScore(session.id, session.version));
  }

  protected finish(): void {
    const session = this.session();
    if (!session || !window.confirm('Pabeigt spēli un rādīt rezultātus?')) return;
    this.run(this.api.finishSession(session.id, session.version));
  }

  private reload(): void {
    this.api.getSession(this.sessionId).subscribe({
      next: (session) => this.session.set(session),
      error: (error) => this.error.set(error.error?.detail || 'Sesiju neizdevās ielādēt.'),
    });
  }

  private run(request: Observable<SessionView>): void {
    if (this.busy()) return;
    this.busy.set(true);
    this.error.set('');
    request.subscribe({
      next: (session) => {
        this.session.set(session);
        this.busy.set(false);
      },
      error: (error) => {
        this.busy.set(false);
        this.error.set(error.error?.detail || 'Darbību neizdevās izpildīt.');
        if (error.status === 409) this.reload();
      },
    });
  }
}
