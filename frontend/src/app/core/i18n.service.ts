import { Injectable, computed, signal } from '@angular/core';

import { Locale } from './models';

const translations: Record<Locale, Record<string, string>> = {
  lv: {
    signIn: 'Ieiet', signUp: 'Izveidot kontu', signOut: 'Iziet', dashboard: 'Spēļu panelis',
    heroEyebrow: 'Nometnes spēle komandām', heroTitle: 'Jautājumi, punkti un īsts fināla sprādziens.',
    heroLead: 'Izveido tēmas, saliec jautājumus, vadi komandas un parādi rezultātus uz atsevišķa projektora ekrāna.',
    startBuilding: 'Sākt veidot spēli', openDashboard: 'Atvērt spēļu paneli', wakingUp: 'Spēle mostas…',
    controlRoom: 'Vadītāja telpa', yourGames: 'Tavas spēles', chooseTemplate: 'Izvēlies sākuma template',
    newGameName: 'Jaunās spēles nosaukums', createGame: 'Izveidot spēli', noGames: 'Vēl nav nevienas spēles.',
    edit: 'Rediģēt', delete: 'Dzēst', ready: 'Gatava spēlei', draft: 'Melnraksts', categories: 'sadaļas',
    editor: 'Spēles redaktors', save: 'Saglabāt', saving: 'Saglabā...', saved: 'Saglabāts',
    gameTitle: 'Spēles nosaukums', description: 'Apraksts', language: 'Valoda', addCategory: 'Pievienot sadaļu',
    categoryName: 'Sadaļas nosaukums', removeCategory: 'Dzēst sadaļu', question: 'Jautājums', answer: 'Pareizā atbilde',
    explanation: 'Pavediens / interesants fakts', freeText: 'Brīvā atbilde', multipleChoice: 'Atbilžu varianti',
    addOption: 'Pievienot variantu', correct: 'Pareizā', image: 'Jautājuma attēls', uploadImage: 'Augšupielādēt attēlu',
    playableHint: 'Visas punktu vietas ir aizpildītas. Spēli var sākt.',
    draftHint: 'Lai sāktu spēli, katrā sadaļā aizpildi 10, 20, 30, 40 un 50 punktu jautājumu un atbildi.',
    startGame: 'Sākt spēli', teamSetup: 'Komandu iestatījumi', teams: 'Komandas', addTeam: 'Pievienot komandu',
    teamName: 'Komandas nosaukums', launchGame: 'Atvērt vadītāja paneli', activeSessions: 'Iesāktās spēles',
    resume: 'Turpināt', finished: 'Pabeigta', progress: 'Progress', noSessions: 'Vēl nav sāktu spēļu.',
  },
  en: {
    signIn: 'Sign in', signUp: 'Create account', signOut: 'Sign out', dashboard: 'Game dashboard',
    heroEyebrow: 'A team game for camp', heroTitle: 'Questions, points, and a proper final reveal.',
    heroLead: 'Create categories, write questions, lead teams, and show results on a separate projector screen.',
    startBuilding: 'Build a game', openDashboard: 'Open dashboard', wakingUp: 'Waking up the game…',
    controlRoom: 'Host room', yourGames: 'Your games', chooseTemplate: 'Choose a starting template',
    newGameName: 'New game title', createGame: 'Create game', noGames: 'No games yet.',
    edit: 'Edit', delete: 'Delete', ready: 'Ready to play', draft: 'Draft', categories: 'categories',
    editor: 'Game editor', save: 'Save', saving: 'Saving...', saved: 'Saved',
    gameTitle: 'Game title', description: 'Description', language: 'Language', addCategory: 'Add category',
    categoryName: 'Category name', removeCategory: 'Remove category', question: 'Question', answer: 'Correct answer',
    explanation: 'Hint / interesting fact', freeText: 'Free answer', multipleChoice: 'Multiple choice',
    addOption: 'Add option', correct: 'Correct', image: 'Question image', uploadImage: 'Upload image',
    playableHint: 'Every point slot is complete. This game can be started.',
    draftHint: 'To start, fill the 10, 20, 30, 40 and 50 point question and answer in every category.',
    startGame: 'Start game', teamSetup: 'Team setup', teams: 'Teams', addTeam: 'Add team',
    teamName: 'Team name', launchGame: 'Open host panel', activeSessions: 'Started games',
    resume: 'Resume', finished: 'Finished', progress: 'Progress', noSessions: 'No started games yet.',
  },
};

@Injectable({ providedIn: 'root' })
export class I18nService {
  readonly locale = signal<Locale>((localStorage.getItem('locale') as Locale) || 'lv');
  readonly alternateLocale = computed<Locale>(() => this.locale() === 'lv' ? 'en' : 'lv');

  t(key: string): string {
    return translations[this.locale()][key] || translations.lv[key] || key;
  }

  setLocale(locale: Locale): void {
    this.locale.set(locale);
    localStorage.setItem('locale', locale);
    document.documentElement.lang = locale;
  }

  toggle(): void {
    this.setLocale(this.alternateLocale());
  }
}
