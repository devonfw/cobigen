import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable()
export class BusinessOperationsService {
  public serverPath: string = environment.restPathRoot;

  login(): string {
    return this.serverPath + 'auth/login';
  }
  logout(): string {
    return this.serverPath + 'logout';
  }
  getCsrf(): string {
    return this.serverPath + 'security/v1/csrftoken';
  }
}
