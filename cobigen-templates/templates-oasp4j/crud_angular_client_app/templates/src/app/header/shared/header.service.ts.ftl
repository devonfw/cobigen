import { Router } from '@angular/router';
import { Injectable }     from '@angular/core';
import { Observable } from 'rxjs/Rx';
import { AuthService } from '../../shared/security/auth.service';
import { HttpClient } from '../../shared/security/httpClient.service';
import { BusinessOperations } from '../../BusinessOperations';

@Injectable()
export class HeaderService {

    constructor(public router: Router,
                private BO: BusinessOperations,
                private http: HttpClient,
                public authService: AuthService) { }

    logout() {
        this.http.get(this.BO.logout())
                 .subscribe( () => {
                    this.authService.setLogged(false);
                    this.authService.setToken('');
                    this.router.navigate(['/login']);
                 })
    }
}
