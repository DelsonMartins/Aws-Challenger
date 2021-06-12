import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';


import { ListagemFormComponent } from './listagem-form/listagem-form.component';

import { InputTextModule } from 'primeng/components/inputtext/inputtext';
import { ButtonModule } from 'primeng/components/button/button';
import { TableModule } from 'primeng/components/table/table';
import { DropdownModule } from 'primeng/dropdown';
import { SelectButtonModule } from 'primeng/selectbutton';

import { FileUploadModule } from 'primeng/fileupload';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { CheckboxModule } from 'primeng/checkbox';

import { SharedModule } from './../shared/shared.module';
import { ListagemRoutingModule } from './listagem-routing.module';




@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    InputTextModule,
    ButtonModule,
    TableModule,
    SelectButtonModule,
    DropdownModule,

    FileUploadModule,
    ProgressSpinnerModule,
    CheckboxModule,

    SharedModule,
    ListagemRoutingModule,
  ],
  declarations: [
    ListagemFormComponent
  ],
  exports: []
})
export class ListagemModule { }
