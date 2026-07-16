import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { GameInput, GameSummary, GameView, Locale, MediaView } from './models';

@Injectable({ providedIn: 'root' })
export class GameApiService {
  private readonly http = inject(HttpClient);

  listTemplates() {
    return this.http.get<GameSummary[]>('/api/templates');
  }

  listGames() {
    return this.http.get<GameSummary[]>('/api/games');
  }

  getGame(id: string) {
    return this.http.get<GameView>(`/api/games/${id}`);
  }

  createFromTemplate(templateId: string, title: string, locale: Locale) {
    return this.http.post<GameView>(`/api/games/from-template/${templateId}`, { title, locale });
  }

  saveGame(id: string, game: GameInput) {
    return this.http.put<GameView>(`/api/games/${id}`, game);
  }

  deleteGame(id: string) {
    return this.http.delete<void>(`/api/games/${id}`);
  }

  uploadImage(file: File) {
    const form = new FormData();
    form.append('file', file);
    return this.http.post<MediaView>('/api/media', form);
  }
}
