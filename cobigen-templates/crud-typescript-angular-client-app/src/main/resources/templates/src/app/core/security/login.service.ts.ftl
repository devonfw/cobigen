import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { BusinessOperationsService } from '../../core/shared/business-operations.service';
import { environment } from '../../../environments/environment';

@Injectable()
export class LoginService {
  constructor(
    public router: Router,
    private BO: BusinessOperationsService,
    private http: HttpClient,
  ) {}

  login(username: string, password: string): Observable<any> {
    let options: any;

    // CSRF
    if (environment.security === 'csrf') {
      options = {
        withCredentials: true,
        responseType: 'text',
      };
    }

    // JWT
    if (environment.security === 'jwt') {
      options = { responseType: 'text', observe: 'response' };
    }

    return this.http.post(
      this.BO.login(),
      {
        username: username,
        password: password,
      },
      options,
    );
  }

  logout(): Observable<string> {
    return this.http.get(this.BO.logout(), { responseType: 'text' });
  }

  getCsrf(): Observable<any> {
    return this.http.get(this.BO.getCsrf(), { withCredentials: true });
  }
}
