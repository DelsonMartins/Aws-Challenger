import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { JwtModule, JwtHelperService } from '@auth0/angular-jwt';
import { ButtonModule } from 'primeng/components/button/button';
import { InputTextModule } from 'primeng/components/inputtext/inputtext';



import { AuthHttp } from 'angular2-jwt';
import { AuthGuard } from './auth.guard';
import { AuthService } from './auth.service';
import { LogoutService } from './logout.service';

import { SegurancaRoutingModule } from './seguranca-routing.module';
import { LoginFormComponent } from './login-form/login-form.component';



import { environment } from '../../environments/environment';
import { MoneyHttpInterceptor } from './money-http-interceptor';



/*
export function authHttpServiceFactory(auth: AuthService, http: Http, options: RequestOptions) {
  const config = new AuthConfig({
    globalHeaders: [
      { 'Content-Type': 'application/json' }
    ]
  });

  return new MoneyHttp(auth, config, http, options);
}*/

export function tokenGetter(): string {
  return localStorage.getItem('token');
}

@NgModule({
  imports: [
   CommonModule,
   FormsModule,

 
   JwtModule.forRoot({
    config: {
      tokenGetter,
      allowedDomains: environment.tokenAllowedDomains,
      disallowedRoutes: environment.tokenDisallowedRoutes
    }
  }),

  InputTextModule,
  ButtonModule,
 
    SegurancaRoutingModule
  ],
  declarations: [LoginFormComponent],
  providers: [
    JwtHelperService,

    {
      provide: HTTP_INTERCEPTORS,
      useClass: MoneyHttpInterceptor,
      multi: true
    },
    AuthGuard,
    LogoutService,
    {
      provide: AuthHttp
     // useFactory: authHttpServiceFactory
      //deps: [AuthService, Http, RequestOptions]
   }  
    ]
})
export class SegurancaModule { }