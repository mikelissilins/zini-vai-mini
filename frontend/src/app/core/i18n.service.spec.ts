import { TestBed } from '@angular/core/testing';

import { I18nService } from './i18n.service';

describe('I18nService', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
  });

  it('switches language and persists the choice', () => {
    const service = TestBed.inject(I18nService);

    service.setLocale('en');

    expect(service.t('startGame')).toBe('Start game');
    expect(localStorage.getItem('locale')).toBe('en');
    expect(document.documentElement.lang).toBe('en');
  });
});
