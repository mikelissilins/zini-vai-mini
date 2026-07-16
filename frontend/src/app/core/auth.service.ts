import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Clerk } from '@clerk/clerk-js';
import { ClerkUI } from '@clerk/ui/entry';
import { firstValueFrom } from 'rxjs';

interface RuntimeConfig {
  clerkPublishableKey: string;
  locales: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private clerk?: Clerk;
  private initialization?: Promise<void>;

  readonly ready = signal(false);
  readonly user = signal<Clerk['user']>(null);
  readonly signedIn = computed(() => this.user() !== null);

  initialize(): Promise<void> {
    if (this.ready()) return Promise.resolve();
    this.initialization ??= this.initializeWhenBackendIsReady();
    return this.initialization;
  }

  openSignIn(): void {
    this.clerk?.openSignIn({ fallbackRedirectUrl: '/dashboard' });
  }

  openSignUp(): void {
    this.clerk?.openSignUp({ fallbackRedirectUrl: '/dashboard' });
  }

  async signOut(): Promise<void> {
    await this.clerk?.signOut();
  }

  async getToken(): Promise<string | null> {
    return (await this.clerk?.session?.getToken()) ?? null;
  }

  private async initializeWhenBackendIsReady(): Promise<void> {
    while (!this.ready()) {
      try {
        const config = await firstValueFrom(this.http.get<RuntimeConfig>('/api/public/config'));
        this.clerk = new Clerk(config.clerkPublishableKey);
        await this.clerk.load({ ui: { ClerkUI } });
        this.user.set(this.clerk.user);
        this.clerk.addListener(({ user }) => this.user.set(user));
        this.ready.set(true);
      } catch {
        await new Promise((resolve) => setTimeout(resolve, 1500));
      }
    }
  }
}
