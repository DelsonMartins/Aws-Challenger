import { Component, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService } from 'primeng/components/common/api';
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
  historical: boolean = false;;
  progress: { percentage: number } = { percentage: 0 };
 

  @ViewChild('tabela', {static: true}) grid: Table;

  showFile = false;

  listFiles = [];


  constructor(
    private listagemService: ListagemService,
    private errorHandler: ErrorHandlerService,
    public auth: AuthService,
    private title: Title,
    private confirmation: ConfirmationService
  ) { }

  ngOnInit() {
    this.title.setTitle('AMcom - AWS');
    this.pesquisar();
  }

  selectFile(event) {
    this.selectedFiles = event.target.files;
  }

 
  get urlUploadAnexo() {
    return this.listagemService.urlUploadAnexo();
  }

    
  pesquisar() {
    this.listagemService.pesquisar({historical: this.historical}).subscribe((listFiles: InfoArquivo[]) => {
      this.listFiles = listFiles;
    });
  }

  excluir(arquivo: any) {

    arquivo.name = this.clearName(arquivo.name);

    console.log('Exclusao Arquivo ' + arquivo.name);

    this.listagemService.excluir(arquivo).subscribe();

    this.pesquisar();
    
  
  }

  clearBarra (arquivo: any) {
    const nome = arquivo.name;

    if (nome) {
      return nome.substring(nome.indexOf('/') + 1, nome.length);
    } else {
      return '';
    } 
  }

  clearName (nomeArquivo: string) {
   
    const nome = nomeArquivo;

    if (nome) {
      return nome.substring(nome.indexOf('/') + 1, nome.length);
    } else {
      return '';
    } 
  }

  viewFile(arquivo: any){
    console.log('Visualizando arquivo ' + arquivo.url);
    window.open(arquivo.url);
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

  confirmarExclusao(arquivo: any) {
    this.confirmation.confirm({
      message: 'Tem certeza que deseja excluir o arquivo ?',
      accept: () => {
        this.excluir(arquivo);
      }
    });
  }

  excluir2(arquivo: any) {
    this.listagemService.excluir2(arquivo)
      .then(() => {
        if (this.grid.first === 0) {
          this.pesquisar();
        } else {
          this.grid.first = 0;
        }

        console.log('Arquivo excluído com sucesso!!');
      })
      .catch(erro => this.errorHandler.handle(erro));
  }

}
