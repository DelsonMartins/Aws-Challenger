import { Component, OnInit, ViewChild } from '@angular/core';
import { ListagemService } from './../listagem.service';
import { Title } from '@angular/platform-browser';
import { Table } from 'primeng/components/table/table';
import { InfoArquivo } from './../../core/model'; 
import { ErrorHandlerService } from './../../core/error-handler.service';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { AuthService } from './../../seguranca/auth.service';


@Component({
  selector: 'app-listagem-form',
  templateUrl: './listagem-form.component.html',
  styleUrls: ['./listagem-form.component.css']
})
export class ListagemFormComponent implements OnInit {

  totalRegistros = 0;

  selectedFiles: FileList;
  currentFileUpload: File;
  progress: { percentage: number } = { percentage: 0 };
 

  @ViewChild('tabela', {static: true}) grid: Table;

  showFile = false;



  //listFiles: InfoArquivo[] = [];

  listFiles = [];


  constructor(
    private listagemService: ListagemService,
    private errorHandler: ErrorHandlerService,
    public auth: AuthService,
    private title: Title
  ) { }

  ngOnInit() {
    this.title.setTitle('Pesquisa de arquivos');
    this.pesquisar2(true);
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

  antesUploadAnexo(event) {
    event.xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('token'));
  }

  get urlUploadAnexo() {
    return this.listagemService.urlUploadAnexo();
  }

  pesquisar(enable: boolean) {
    this.listagemService.pesquisar3(this.showFile)
      .then(resultado => {
        
        this.listFiles = resultado;
        console.log("Ok 2!!!!");
        console.log(this.listFiles);
        
      })
      .catch(erro => this.errorHandler.handle(erro));
  }


  
  pesquisar2(enable: boolean) {
    this.listagemService.pesquisar4(this.showFile).subscribe((listFiles: InfoArquivo[]) => {
      this.listFiles = listFiles;
    });
  }

  upload() {
    this.progress.percentage = 0;

    this.currentFileUpload = this.selectedFiles.item(0);
    this.listagemService.pushFileToStorage(this.currentFileUpload).subscribe(event => {
      if (event instanceof HttpResponse) {
        console.log('File is completely uploaded!');
      }
    });

    this.selectedFiles = undefined;
  }

}
