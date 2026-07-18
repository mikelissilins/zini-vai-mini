import { Injectable, signal } from '@angular/core';

import { Locale } from './models';

const translations: Record<Locale, Record<string, string>> = {
  lv: {
    signIn: 'Ieiet', signUp: 'Izveidot kontu', signOut: 'Iziet', dashboard: 'Spēļu panelis',
    heroEyebrow: 'Nometnes viktorīna', heroTitle: 'Izveido spēli. Spēlējiet kopā.',
    heroLead: 'Izvēlies sadaļas, pievieno jautājumus un skaiti punktus.',
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
    draftHint: 'Lai sāktu spēli, katrā sadaļā aizpildi 10 līdz 70 punktu jautājumus un atbildes.',
    addExtendedPoints: 'Pievienot 60 un 70', removeExtendedPoints: 'Noņemt 60 un 70',
    gameCount: 'spēles', chooseTemplateTitle: 'Izvēlies template vai izveido spēli',
    emptyGamesHint: 'Izvēlies template augstāk un izveido pirmo spēli.', noDescription: 'Bez apraksta',
    defaultGameName: 'Mana spēle', campGameName: 'Zini vai mini', deleteConfirm: 'Dzēst “{title}”?',
    loadGameError: 'Datus neizdevās ielādēt.', createGameError: 'Spēli neizdevās izveidot.', deleteGameError: 'Spēli neizdevās izdzēst.',
    ownerError: 'Šis Clerk konts nav aplikācijas īpašnieks.', starterBible: 'Bībele', starterJesus: 'Jēzus', starterCamp: 'Nometne',
    startGame: 'Sākt spēli', teamSetup: 'Komandu iestatījumi', teams: 'Komandas', addTeam: 'Pievienot komandu',
    teamName: 'Komandas nosaukums', launchGame: 'Atvērt vadītāja paneli', activeSessions: 'Iesāktās spēles',
    resume: 'Turpināt', finished: 'Pabeigta', progress: 'Progress', noSessions: 'Vēl nav sāktu spēļu.',
  },
  en: {
    signIn: 'Sign in', signUp: 'Create account', signOut: 'Sign out', dashboard: 'Game dashboard',
    heroEyebrow: 'Camp team quiz', heroTitle: 'Build a game. Play together.',
    heroLead: 'Choose categories, add questions, and keep score.',
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
    draftHint: 'To start, fill every 10 to 70 point question and answer in each category.',
    addExtendedPoints: 'Add 60 and 70', removeExtendedPoints: 'Remove 60 and 70',
    gameCount: 'games', chooseTemplateTitle: 'Choose a template or create a game',
    emptyGamesHint: 'Choose a template above and create your first game.', noDescription: 'No description',
    defaultGameName: 'My game', campGameName: 'Zini vai mini', deleteConfirm: 'Delete “{title}”?',
    loadGameError: 'Could not load data.', createGameError: 'Could not create the game.', deleteGameError: 'Could not delete the game.',
    ownerError: 'This Clerk account is not the application owner.', starterBible: 'Bible', starterJesus: 'Jesus', starterCamp: 'Camp',
    startGame: 'Start game', teamSetup: 'Team setup', teams: 'Teams', addTeam: 'Add team',
    teamName: 'Team name', launchGame: 'Open host panel', activeSessions: 'Started games',
    resume: 'Resume', finished: 'Finished', progress: 'Progress', noSessions: 'No started games yet.',
  },
};

@Injectable({ providedIn: 'root' })
export class I18nService {
  readonly locale = signal<Locale>(localStorage.getItem('zini-vai-mini-locale') === 'en' ? 'en' : 'lv');

  constructor() {
    document.documentElement.lang = this.locale();
  }

  t(key: string): string {
    return translations[this.locale()][key] || translations.lv[key] || key;
  }

  setLocale(locale: Locale): void {
    this.locale.set(locale);
    document.documentElement.lang = locale;
    localStorage.setItem('zini-vai-mini-locale', locale);
  }
}
