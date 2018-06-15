<#ftl ns_prefixes={"xmi":"http://schema.omg.org/spec/XMI/2.1", "uml":"http://schema.omg.org/spec/UML/2.1"}>
import { Observable } from 'rxjs/Rx';
import { Http } from '@angular/http';
import { Injectable } from '@angular/core';
import { url } from '../assets/serverPath';

@Injectable()
export class BusinessOperations {

    public serverPath = url;
    public servicesPath = this.serverPath;

    constructor(private http: Http) { }

    login() {
        return this.serverPath + 'login';
    }
    logout() {
        return this.serverPath + 'logout';
    }
    getCsrf() {
        return this.serverPath + 'security/v1/csrftoken';
    }
    post${variables.etoName?cap_first}() {
        return this.servicesPath + '${variables.component}/v1/${variables.etoName?lower_case}/';
    }
    post${variables.etoName?cap_first}Search() {
        return this.servicesPath + '${variables.component}/v1/${variables.etoName?lower_case}/search';
    }
    delete${variables.etoName?cap_first}() {
        return this.servicesPath + '${variables.component}/v1/${variables.etoName?lower_case}/';
    }
}
