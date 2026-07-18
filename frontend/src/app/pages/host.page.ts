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
  protected readonly canStepBack = computed(() => {
    const session = this.session();
    return !!session && (session.selectedQuestion !== null || session.canUndo);
  });
  protected readonly selectedChoiceIsCorrect = computed(() => {
    const session = this.session();
    const selectedQuestion = session?.selectedQuestion;
    return !!selectedQuestion?.options.find((option) => option.id === session?.selectedOptionId)?.correct;
  });
  protected readonly awardedPoints = computed(() => {
    const question = this.session()?.selectedQuestion;
    return question ? question.points - (question.hintUsed ? 5 : 0) : 0;
  });

  ngOnInit(): void { this.reload(); }

  protected copy(lv: string, en: string): string {
    return this.session()?.locale === 'en' ? en : lv;
  }

  protected select(questionId: string): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.selectQuestion(session.id, questionId, session.version));
  }

  protected reveal(optionId: string | null = null): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.revealAnswer(session.id, session.version, optionId));
  }

  protected useHint(): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.useHint(session.id, session.version));
  }

  protected stepBack(): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.stepBack(session.id, session.version));
  }

  protected score(correct: boolean): void {
    const session = this.session();
    if (!session) return;
    this.run(this.api.scoreAnswer(session.id, correct, session.version));
  }

  protected finish(): void {
    const session = this.session();
    if (!session || !window.confirm(this.copy('Pabeigt spēli un rādīt rezultātus?', 'Finish the game and show results?'))) return;
    this.run(this.api.finishSession(session.id, session.version));
  }

  private reload(): void {
    this.api.getSession(this.sessionId).subscribe({
      next: (session) => this.session.set(session),
      error: (error) => this.error.set(error.error?.detail || this.copy('Sesiju neizdevās ielādēt.', 'Could not load the session.')),
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
        this.error.set(error.error?.detail || this.copy('Darbību neizdevās izpildīt.', 'Could not complete the action.'));
        if (error.status === 409) this.reload();
      },
    });
  }
}
