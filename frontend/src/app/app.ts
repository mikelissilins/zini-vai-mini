import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

import { AuthService } from './core/auth.service';
import { I18nService } from './core/i18n.service';
import { SoundService } from './core/sound.service';

@Component({
  selector: 'app-root',
  imports: [RouterLink, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App implements OnInit {
  protected readonly auth = inject(AuthService);
  protected readonly i18n = inject(I18nService);
  protected readonly sound = inject(SoundService);
  protected readonly location = window.location;
  protected readonly projectorMode = window.location.pathname.startsWith('/live/');

  ngOnInit(): void {
    if (!this.projectorMode) void this.auth.initialize();
  }
}
