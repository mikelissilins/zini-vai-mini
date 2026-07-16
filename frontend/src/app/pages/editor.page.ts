import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { GameApiService } from '../core/game-api.service';
import { I18nService } from '../core/i18n.service';
import { GameInput, GameView, QuestionInput, QuestionType } from '../core/models';

const POINTS = [10, 20, 30, 40, 50];
const CATEGORY_COLORS = ['#0E758C', '#F77F5B', '#55B8CC', '#5CA67A', '#7A6FF0', '#F2A65A'];

@Component({
  selector: 'app-editor-page',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './editor.page.html',
  styleUrl: './editor.page.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditorPage implements OnInit {
  private readonly api = inject(GameApiService);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  protected readonly i18n = inject(I18nService);

  private readonly gameId = this.route.snapshot.paramMap.get('id')!;
  protected readonly loading = signal(true);
  protected readonly saving = signal(false);
  protected readonly saved = signal(false);
  protected readonly playable = signal(false);
  protected readonly error = signal('');
  protected readonly uploadQuestion = signal<FormGroup | null>(null);

  protected readonly form = this.fb.group({
    title: ['', [Validators.required, Validators.maxLength(160)]],
    description: ['', Validators.maxLength(600)],
    locale: ['lv', Validators.required],
    version: [0, Validators.required],
    categories: this.fb.array<FormGroup>([]),
  });

  ngOnInit(): void {
    this.api.getGame(this.gameId).subscribe({
      next: (game) => {
        this.populate(game);
        this.loading.set(false);
      },
      error: (error) => {
        this.error.set(error.error?.detail || 'Spēli neizdevās ielādēt.');
        this.loading.set(false);
      },
    });
  }

  protected get categories(): FormArray<FormGroup> {
    return this.form.controls.categories;
  }

  protected questions(category: FormGroup): FormArray<FormGroup> {
    return category.controls['questions'] as FormArray<FormGroup>;
  }

  protected options(question: FormGroup): FormArray<FormGroup> {
    return question.controls['options'] as FormArray<FormGroup>;
  }

  protected addCategory(): void {
    this.categories.push(this.createCategoryGroup({
      name: `Sadaļa ${this.categories.length + 1}`,
      color: CATEGORY_COLORS[this.categories.length % CATEGORY_COLORS.length],
      questions: POINTS.map((points) => this.emptyQuestion(points)),
    }));
    this.markChanged();
  }

  protected removeCategory(index: number): void {
    this.categories.removeAt(index);
    this.markChanged();
  }

  protected setQuestionType(question: FormGroup, type: QuestionType): void {
    question.controls['type'].setValue(type);
    const options = this.options(question);
    if (type === 'MULTIPLE_CHOICE' && options.length < 2) {
      options.push(this.createOptionGroup('', true));
      options.push(this.createOptionGroup('', false));
    }
    this.markChanged();
  }

  protected addOption(question: FormGroup): void {
    if (this.options(question).length < 4) this.options(question).push(this.createOptionGroup('', false));
    this.markChanged();
  }

  protected removeOption(question: FormGroup, index: number): void {
    if (this.options(question).length > 2) this.options(question).removeAt(index);
    this.markChanged();
  }

  protected setCorrect(question: FormGroup, selectedIndex: number): void {
    this.options(question).controls.forEach((option, index) => option.controls['correct'].setValue(index === selectedIndex));
    this.markChanged();
  }

  protected uploadImage(question: FormGroup, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) return;
    this.uploadQuestion.set(question);
    this.error.set('');
    this.api.uploadImage(file).subscribe({
      next: (media) => {
        question.patchValue({ mediaAssetId: media.id, mediaUrl: media.url });
        this.uploadQuestion.set(null);
        this.markChanged();
      },
      error: (error) => {
        this.uploadQuestion.set(null);
        this.error.set(error.error?.detail || 'Attēlu neizdevās augšupielādēt.');
      },
    });
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.error.set('');
    this.api.saveGame(this.gameId, this.toInput()).subscribe({
      next: (game) => {
        this.form.controls.version.setValue(game.version);
        this.playable.set(game.playable);
        this.saving.set(false);
        this.saved.set(true);
        setTimeout(() => this.saved.set(false), 1800);
      },
      error: (error) => {
        this.saving.set(false);
        this.error.set(error.status === 409
          ? 'Spēle ir mainīta citā logā. Pārlādē lapu, lai nezaudētu jaunāko versiju.'
          : error.error?.detail || 'Spēli neizdevās saglabāt.');
      },
    });
  }

  protected markChanged(): void {
    this.saved.set(false);
    this.form.markAsDirty();
  }

  private populate(game: GameView): void {
    this.form.patchValue({
      title: game.title,
      description: game.description || '',
      locale: game.locale,
      version: game.version,
    });
    this.categories.clear();
    game.categories.forEach((category) => this.categories.push(this.createCategoryGroup(category)));
    this.playable.set(game.playable);
    this.form.markAsPristine();
  }

  private createCategoryGroup(category: { name: string; color: string; questions: QuestionInput[] }): FormGroup {
    return this.fb.group({
      name: [category.name, [Validators.required, Validators.maxLength(80)]],
      color: [category.color, [Validators.required, Validators.pattern(/^#[0-9A-Fa-f]{6}$/)]],
      questions: this.fb.array(category.questions
        .sort((left, right) => left.points - right.points)
        .map((question) => this.createQuestionGroup(question))),
    });
  }

  private createQuestionGroup(question: QuestionInput): FormGroup {
    return this.fb.group({
      points: [question.points, Validators.required],
      type: [question.type || 'FREE_TEXT', Validators.required],
      prompt: [question.prompt || '', Validators.maxLength(1200)],
      answer: [question.answer || '', Validators.maxLength(1200)],
      explanation: [question.explanation || '', Validators.maxLength(1600)],
      mediaAssetId: [question.mediaAssetId || null],
      mediaUrl: [question.mediaUrl || null],
      options: this.fb.array((question.options || []).map((option) => this.createOptionGroup(option.text, option.correct))),
    });
  }

  private createOptionGroup(text: string, correct: boolean): FormGroup {
    return this.fb.group({
      text: [text, [Validators.required, Validators.maxLength(600)]],
      correct: [correct],
    });
  }

  private emptyQuestion(points: number): QuestionInput {
    return {
      points,
      type: 'FREE_TEXT',
      prompt: '',
      answer: '',
      explanation: null,
      mediaAssetId: null,
      options: [],
    };
  }

  private toInput(): GameInput {
    const value = this.form.getRawValue();
    return {
      title: value.title!,
      description: value.description || null,
      locale: value.locale as 'lv' | 'en',
      version: value.version,
      categories: (value.categories || []).map((category) => ({
        name: category['name'],
        color: category['color'],
        questions: (category['questions'] || []).map((question: Record<string, unknown>) => ({
          points: question['points'],
          type: question['type'],
          prompt: question['prompt'],
          answer: question['answer'],
          explanation: question['explanation'] || null,
          mediaAssetId: question['mediaAssetId'] || null,
          options: question['type'] === 'MULTIPLE_CHOICE' ? question['options'] : [],
        })),
      })),
    } as GameInput;
  }
}
