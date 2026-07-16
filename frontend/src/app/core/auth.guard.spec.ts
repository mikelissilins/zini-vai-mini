import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';

import { AuthService } from './auth.service';
import { authGuard } from './auth.guard';

describe('authGuard', () => {
  const signedIn = signal(false);

  beforeEach(() => {
    signedIn.set(false);
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: { signedIn, initialize: () => Promise.resolve() },
        },
      ],
    });
  });

  it('redirects signed-out visitors to the landing page', async () => {
    const result = await TestBed.runInInjectionContext(() => authGuard(
      {} as ActivatedRouteSnapshot,
      {} as RouterStateSnapshot,
    ));

    expect(result).toBeInstanceOf(UrlTree);
    expect(TestBed.inject(Router).serializeUrl(result as UrlTree)).toBe('/');
  });

  it('allows signed-in visitors', async () => {
    signedIn.set(true);
    const result = await TestBed.runInInjectionContext(() => authGuard(
      {} as ActivatedRouteSnapshot,
      {} as RouterStateSnapshot,
    ));

    expect(result).toBe(true);
  });
});
