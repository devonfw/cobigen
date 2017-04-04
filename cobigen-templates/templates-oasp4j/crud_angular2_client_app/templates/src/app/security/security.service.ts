import { Observable } from 'rxjs/Rx';
import { Http } from '@angular/http';
import { HttpClient } from './httpClient.service';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BusinessOperations } from '../BusinessOperations';

@Injectable()
export class SecurityService {

    constructor(private BO: BusinessOperations, private router: Router, private http: HttpClient) {
    }

    login(username, password) {
        return new Observable ( (observer) => {
           this.http.post(this.BO.login(), JSON.stringify({j_username: username, j_password: password}))
                    .map(res => JSON.stringify(res))
                    .subscribe(() => {
                       this.getCsrfToken();
                       observer.next();
                    }, (error) => {
                        observer.error(error);
                    });
        });
    }

    getCsrfToken() {
        this.http.get(this.BO.getCsrf())
                        .map(res => res.json())
                        .subscribe( (data) => {
                          this.http.addDefaultHeader('x-csrf-token', data.token);
                        });
    }

    checkCsrfToken() {
        this.http.get(this.BO.getCsrf())
                        .map(res => res.json())
                        .subscribe( (data) => {
                          this.router.navigate([('/home')])
                        }, (error) => {
                          this.router.navigate([('/login')])                            
                        });
    }

    logout() {
        this.http.get(this.BO.logout())
                 .subscribe( () => {
                    this.router.navigate(['/login']);
                    this.http.addDefaultHeader('x-csrf-token', "");
                 })
    }
}
