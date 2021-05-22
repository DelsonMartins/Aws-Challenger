import { Routes, RouterModule } from '@angular/router';
import { NgModule } from '@angular/core';
import { AuthGuard } from './../seguranca/auth.guard';
import { ListagemFormComponent } from './listagem-form/listagem-form.component';

const routes: Routes = [
  {
    path: 'listagem',
    component: ListagemFormComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_PESQUISAR_ARQUIVO'] }
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [RouterModule]
})
export class ListagemRoutingModule { }