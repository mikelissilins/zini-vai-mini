export type Locale = 'lv' | 'en';
export type QuestionType = 'FREE_TEXT' | 'MULTIPLE_CHOICE';

export interface OptionInput {
  text: string;
  correct: boolean;
}

export interface QuestionInput {
  id?: string;
  points: number;
  type: QuestionType;
  prompt: string;
  answer: string;
  explanation: string | null;
  mediaAssetId: string | null;
  mediaUrl?: string | null;
  options: OptionInput[];
}

export interface CategoryInput {
  id?: string;
  name: string;
  color: string;
  questions: QuestionInput[];
}

export interface GameInput {
  title: string;
  description: string | null;
  locale: Locale;
  version: number | null;
  categories: CategoryInput[];
}

export interface GameView extends GameInput {
  id: string;
  template: boolean;
  templateKey: string | null;
  version: number;
  playable: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface GameSummary {
  id: string;
  title: string;
  description: string | null;
  locale: Locale;
  template: boolean;
  templateKey: string | null;
  version: number;
  playable: boolean;
  categoryCount: number;
  updatedAt: string;
}

export interface MediaView {
  id: string;
  fileName: string;
  contentType: string;
  byteSize: number;
  url: string;
}

export type SessionStatus = 'ACTIVE' | 'FINISHED';

export interface TeamInput {
  name: string;
  color: string;
}

export interface SessionTeam extends TeamInput {
  id: string;
  position: number;
  score: number;
  rank: number;
  correctAnswers: number;
  wrongAnswers: number;
}

export interface SessionSummary {
  id: string;
  gameId: string;
  title: string;
  status: SessionStatus;
  teamCount: number;
  usedCount: number;
  totalQuestions: number;
  updatedAt: string;
}

export interface BoardQuestion {
  id: string;
  points: number;
  used: boolean;
  correct: boolean | null;
  teamColor: string | null;
}

export interface BoardCategory {
  name: string;
  color: string;
  position: number;
  questions: BoardQuestion[];
}

export interface SessionOption {
  id: string;
  text: string;
  correct: boolean;
}

export interface SelectedQuestion {
  id: string;
  categoryName: string;
  categoryColor: string;
  points: number;
  type: QuestionType;
  prompt: string;
  answer: string | null;
  explanation: string | null;
  mediaUrl: string | null;
  hintUsed: boolean;
  options: SessionOption[];
}

export interface SessionView {
  id: string;
  gameId: string;
  publicToken: string;
  title: string;
  locale: Locale;
  status: SessionStatus;
  version: number;
  activeTeamIndex: number;
  activeTeamId: string;
  selectedOptionId: string | null;
  usedCount: number;
  totalQuestions: number;
  answerRevealed: boolean;
  canUndo: boolean;
  createdAt: string;
  updatedAt: string;
  teams: SessionTeam[];
  categories: BoardCategory[];
  selectedQuestion: SelectedQuestion | null;
}
