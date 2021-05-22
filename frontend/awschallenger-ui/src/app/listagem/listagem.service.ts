import { URLSearchParams } from '@angular/http';
import { Injectable } from '@angular/core';

import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';

import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { AuthHttp } from 'angular2-jwt';
import * as moment from 'moment';


import { environment } from './../../environments/environment';
import { InfoArquivo } from './../core/model'; 

import { ErrorHandlerService } from './../core/error-handler.service';

@Injectable()
export class ListagemService {

    listagemUrl: string;

    showFile = false;
    fileUploads: Observable<string[]>;
  

  constructor(private http: HttpClient, private errorHandler: ErrorHandlerService) { 
    this.listagemUrl = `${environment.apiUrl}/aws/file`;
  }

  urlUploadAnexo(): string {
    return `${this.listagemUrl}/anexo`;
  }

  /*pushFileToStorage(file: File): Observable<HttpEvent<{}>> {
    const formdata: FormData = new FormData();

    formdata.append('file', file);

    const req = new HttpRequest('POST', 'http://localhost:8080/aws/file/upload', formdata, {
      reportProgress: true,
      responseType: 'text'
    });

    return this.http.request(req);
  }*/

  pushFileToStorage(hero: File): Observable<File> {
    const headers = new HttpHeaders()
    .append('Authorization', 'Basic YWRtaW5AYWxnYW1vbmV5LmNvbTphZG1pbg==');

    return this.http.post<File>(`${this.listagemUrl}/anexo`, { headers })
    .pipe(
      retry(2),
      catchError(this.handleError))
  }

  /*
  pesquisar3(enable: boolean): Promise<any> {
    const headers = new HttpHeaders()
      .append('Authorization', 'Basic YWRtaW5AYWxnYW1vbmV5LmNvbTphZG1pbg==');

    return this.http.get(`${this.lancamentosUrl}`, { headers })
      .toPromise()
      .then(response => {
        const responseJson = response.json();
        const arquivos = responseJson.content;
        return arquivos;
      });
  } 
*/
  pesquisar4(enable: boolean): Observable<InfoArquivo[]> {
    //const headers = new HttpHeaders()
      //.append('Authorization', 'Basic YWRtaW5AYWxnYW1vbmV5LmNvbTphZG1pbg==');
    //  .append('Authorization', 'Basic YW5ndWxhcjpAbmd1bEByMA=='); 

    return this.http.get<InfoArquivo[]>(`${this.listagemUrl}/all`)
      .pipe(
        retry(2),
        catchError(this.handleError))
  } 

//catchError(this.handleError))
  //     .catch(erro => this.errorHandler.handle(erro)); `${this.pessoasUrl}/${codigo}/ativo`
   
  pesquisar3(enable: boolean): Promise<any> {
    const headers = new HttpHeaders()
      .append('Authorization', 'Basic YWRtaW5AYWxnYW1vbmV5LmNvbTphZG1pbg==');

    return this.http.get<any>(`${this.listagemUrl}/all`, { headers })
      .toPromise()
      .then(response => {
        const listagem = response.content;
        console.log ("Passei");
        console.log (listagem);
        return listagem;
        //response['content']
      });
      //.then(response => response['content']);
      //.then(response =>  {
       // console.log ("Passei");
       // console.log (response);
      //});
  } 

  pesquisar2(enable: boolean) {
    this.showFile = enable;

    if (enable) {
      //this.fileUploads = this.uploadService.getFiles();
      //return this.http.get(`${this.lancamentosUrl}`)
      return this.http.get('http://localhost:8080/aws/file/all');
    }
  }

  // Manipulação de erros
  handleError(error: HttpErrorResponse) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // Erro ocorreu no lado do client
      errorMessage = error.error.message;
    } else {
      // Erro ocorreu no lado do servidor
      errorMessage = `Código do erro: ${error.status}, ` + `menssagem: ${error.message}`;
    }
    console.log(errorMessage);
    return throwError(errorMessage);
  };


  excluir(codigo: number): Promise<void> {
    return this.http.delete(`${this.listagemUrl}/${codigo}`)
      .toPromise()
      .then(() => null);
  }

  
  /*
  private converterStringsParaDatas(infoArquivos: InfoArquivo[]) {
    for (const infoArquivo of infoArquivos) {
      infoArquivos.data1 = moment(infoArquivos.dataVencimento,
        'YYYY-MM-DD').toDate();

      if (infoArquivos.dataPagamento) {
        infoArquivos.dataPagamento = moment(infoArquivos.dataPagamento,
          'YYYY-MM-DD').toDate();
      }
    }
  }

  */

}