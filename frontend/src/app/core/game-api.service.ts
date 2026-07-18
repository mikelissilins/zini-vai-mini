import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { GameInput, GameSummary, GameView, Locale, MediaView, SessionSummary, SessionView, TeamInput } from './models';

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

  listSessions() {
    return this.http.get<SessionSummary[]>('/api/sessions');
  }

  createSession(gameId: string, teams: TeamInput[]) {
    return this.http.post<SessionView>('/api/sessions', { gameId, teams });
  }

  getSession(id: string) {
    return this.http.get<SessionView>(`/api/sessions/${id}`);
  }

  selectQuestion(id: string, questionId: string, version: number) {
    return this.http.post<SessionView>(`/api/sessions/${id}/select`, { questionId, version });
  }

  revealAnswer(id: string, version: number, optionId: string | null = null) {
    return this.http.post<SessionView>(`/api/sessions/${id}/reveal`, { optionId, version });
  }

  useHint(id: string, version: number) {
    return this.http.post<SessionView>(`/api/sessions/${id}/hint`, { version });
  }

  scoreAnswer(id: string, correct: boolean, version: number) {
    return this.http.post<SessionView>(`/api/sessions/${id}/score`, { correct, version });
  }

  undoScore(id: string, version: number) {
    return this.http.post<SessionView>(`/api/sessions/${id}/undo`, { version });
  }

  finishSession(id: string, version: number) {
    return this.http.post<SessionView>(`/api/sessions/${id}/finish`, { version });
  }

  getPublicSession(token: string) {
    return this.http.get<SessionView>(`/api/public/sessions/${token}`);
  }
}
