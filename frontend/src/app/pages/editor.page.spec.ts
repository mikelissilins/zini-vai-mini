import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { GameApiService } from '../core/game-api.service';
import { GameView } from '../core/models';
import { EditorPage } from './editor.page';

describe('EditorPage', () => {
  beforeEach(async () => {
    const game: GameView = {
      id: 'game-1',
      title: 'Testa spēle',
      description: null,
      locale: 'lv',
      template: false,
      templateKey: null,
      version: 0,
      playable: false,
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
      categories: [],
    };
    await TestBed.configureTestingModule({
      imports: [EditorPage],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => 'game-1' } } } },
        { provide: GameApiService, useValue: { getGame: () => of(game) } },
      ],
    }).compileComponents();
  });

  it('requires a game title before saving', async () => {
    const fixture = TestBed.createComponent(EditorPage);
    fixture.detectChanges();
    await fixture.whenStable();
    const form = (fixture.componentInstance as unknown as { form: { controls: { title: { setValue(value: string): void } }; invalid: boolean } }).form;

    form.controls.title.setValue('');

    expect(form.invalid).toBe(true);
  });
});
