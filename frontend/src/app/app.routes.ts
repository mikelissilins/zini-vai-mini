import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  {
    path: 'dashboard',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/dashboard.page').then((module) => module.DashboardPage),
  },
  {
    path: 'games/:id/edit',
    canActivate: [authGuard],
    loadComponent: () => import('./pages/editor.page').then((module) => module.EditorPage),
  },
];
