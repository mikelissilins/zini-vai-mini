import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-dashboard-page',
  template: `
    <section class="section">
      <div class="container">
        <p class="eyebrow">Vadītāja telpa</p>
        <h1 class="title is-2">Spēļu panelis top</h1>
        <p class="subtitle">Nākamajā posmā šeit būs template galerija un spēļu redaktors.</p>
      </div>
    </section>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardPage {}
