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
