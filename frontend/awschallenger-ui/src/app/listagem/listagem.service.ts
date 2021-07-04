import { Injectable } from '@angular/core';

import { Observable, throwError } from 'rxjs';
import { retry, catchError } from 'rxjs/operators';

import { HttpClient, HttpErrorResponse, HttpHeaders,  HttpParams } from '@angular/common/http';

import { environment } from './../../environments/environment';
import { InfoArquivo } from './../core/model'; 

import { ErrorHandlerService } from './../core/error-handler.service';

export interface  ListagemFiltro {
  historical: boolean;
}


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

  pushFileToStorage(hero: File): Observable<File> {
    const headers = new HttpHeaders()
    .append('Authorization', 'Basic YWRtaW5AYWxnYW1vbmV5LmNvbTphZG1pbg==');

    return this.http.post<File>(`${this.listagemUrl}/anexo`, { headers })
    .pipe(
      retry(2),
      catchError(this.handleError))
  }

  download(arquivo: InfoArquivo): Observable<Blob> {
    return this.http.get(`${this.listagemUrl}/download/${arquivo.name}`, {
      responseType: 'blob'
    });
  }

  excluir(arquivo: InfoArquivo):Observable<InfoArquivo> {
    return this.http.delete<InfoArquivo>(`${this.listagemUrl}/${arquivo.name}`)
    .pipe(
      retry(2),
      catchError(this.handleError))
  }

  pesquisar(filtro: ListagemFiltro): Observable<InfoArquivo[]> {

    let params = new HttpParams();

    params = params.set('historical', filtro.historical.toString());
    
    console.log('Pesquisa historico ['+ filtro.historical.toString() + ']') ;


    return this.http.get<InfoArquivo[]>(`${this.listagemUrl}/all`, { params })
      .pipe(
        retry(2),
        catchError(this.handleError))
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
  }


  auditar(): Observable<void> {
    return this.http.get<void>(`${this.listagemUrl}/audit`)
    .pipe(
      retry(2),
      catchError(this.handleError))
  }

  excluir2(arquivo: InfoArquivo): Promise<void> {
    return this.http.delete(`${this.listagemUrl}/${arquivo}`)
      .toPromise()
      .then(() => null);
  }
  
}