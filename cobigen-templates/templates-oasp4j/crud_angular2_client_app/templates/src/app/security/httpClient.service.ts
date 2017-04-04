import { errorSymbol } from '@angular/tsc-wrapped/src/evaluator';
import { Router } from '@angular/router';
import { BusinessOperations } from '../BusinessOperations';
import { Observable } from 'rxjs/Rx';
import { Injectable } from '@angular/core';
import { Http, Headers } from '@angular/http';

@Injectable()
export class HttpClient {
    private headers: Headers;

    constructor(private router: Router, private BO: BusinessOperations , private http: Http) {
      this.headers = new Headers();
      this.headers.append('Content-Type',  'application/json');
    }

    addDefaultHeader(name, value) {
        this.headers.delete(name);
        this.headers.append(name, value);
    }

    get(url): Observable<any> {
        return new Observable(observer => {
          this.http.get(url, {withCredentials: true, headers: this.headers})
                   .subscribe( (data) => {
                         observer.next(data);
                      }, (error) => {

                      if(error.status === 0) {
                          this.router.navigate(['/login']);                      
                      }
 
                      if(error.status >= 400 && error.status < 500) {
                          this.get(this.BO.getCsrf())
                               .map(res => res.json())
                               .subscribe( (dataToken) => {
                                   this.addDefaultHeader('x-csrf-token', dataToken.token);
                                
                                   this.http.get(url, {withCredentials: true, headers: this.headers})
                                            .subscribe( (newData) => {
                                                return observer.next(newData);
                                            }, (error) => {
                                                this.router.navigate(['/login']);
                                            })
                                        
                            }, (error) => {
                                observer.error(error);
                                this.router.navigate(['/login']);
                            });
                  } else {
                      observer.error(error);
                  }
            })
        });
    };

    post(url, data): Observable<any> {
        return new Observable(observer => {
          this.http.post(url, data, {withCredentials: true, headers: this.headers})
                   .subscribe( (data) => {
                            observer.next(data);
                       }, (error) => {

                        if(error.status === 0) {
                            this.router.navigate(['/login']);                      
                        }

                        if(error.status >= 400 && error.status < 500) {
                            this.get(this.BO.getCsrf())
                                .map(res => res.json())
                                .subscribe( (dataToken) => {
                                    this.addDefaultHeader('x-csrf-token', dataToken.token);
                                    this.http.post(url, data, {withCredentials: true, headers: this.headers})
                                                .subscribe( (newData) => {
                                                    return observer.next(newData);
                                                }, (error) => {
                                                    this.router.navigate(['/login']);
                                                })

                                }, (error) => {
                                    observer.error(error);
                                    this.router.navigate(['/login']);
                                });
                        } else {
                            observer.error(error);
                        }
                })
        });
    };

    delete(url) {
        return new Observable(observer => {
          this.http.delete(url, {withCredentials: true, headers: this.headers})
                   .subscribe( (data) => {
                            observer.next(data);
                        }, (error) => {
                      
                          if(error.status === 0) {
                            this.router.navigate(['/login']);                      
                          }
        
                          if(error.status >= 400 && error.status < 500) {
                                this.get(this.BO.getCsrf())
                                    .map(res => res.json())
                                    .subscribe( (dataToken) => {
                                        this.addDefaultHeader('x-csrf-token', dataToken.token);

                                        this.http.delete(url, {withCredentials: true, headers: this.headers})
                                                    .subscribe( (newData) => {
                                                        return observer.next(newData);
                                                    }, (error) => {
                                                        this.router.navigate(['/login']);
                                                    })

                                    }, (error) => {
                                        observer.error(error);
                                        this.router.navigate(['/login']);
                                    });
                          } else {
                              observer.error(error);
                          }
                })
        });
    }
}
