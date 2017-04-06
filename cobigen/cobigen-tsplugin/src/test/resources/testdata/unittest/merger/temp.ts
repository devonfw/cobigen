import { Observable } from 'rxjs/Rx';
import { Injectable } from '@angular/core';
import * as ts from 'typescript';

@NgModule({
  imports: [
    BrowserModule,
    CovalentCoreModule.forRoot(),
    FormsModule,
    HttpModule,
    routing,
    MaterialModule.forRoot(),
    TranslateModule.forRoot({
      provide: TranslateLoader,
      useFactory: translateFactory,
      deps: [Http]
    })
  ],
  declarations: [
    Material2AppAppComponent,
    HeaderComponent,
    LoginComponent,
    HomeComponent,
    SampledatamanagementDataGridComponent,
    SampledatamanagementAddDialogComponent
  ],
  entryComponents: [
    SampledatamanagementAddDialogComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    SampledatamanagementDataGridService,
    SecurityService,
    HttpClient,
    BusinessOperations
  ],
})
export class BusinessOperations {

  @ViewChild('dataTable') dataTable;
  public serverPath = url;
  public servicesPath = this.serverPath;

  columns: any = [
    {name: 'name', label: this.getTranslation('sampledatamanagementDataGrid.columns.name')},
    {name: 'surname', label: this.getTranslation('sampledatamanagementDataGrid.columns.surname')},
    {name: 'age', label: this.getTranslation('sampledatamanagementDataGrid.columns.age')},
    {name: 'mail', label: this.getTranslation('sampledatamanagementDataGrid.columns.mail')}
  ];  

  data: any = [];
  selectedRow: any;
  other: number = 3;
  other1: string ="potato";
  other2: boolean = true;
  other3: [number, string] = [2, "numero2"];
  other5: number[] = [2, 3, 4, 5];

  item = {
    name: '',
    surname: '',
    age: '',
    mail: ''
  };

  searchTerms: any = {
    name: null,
    surname: null,
    age: null,
    mail: null
  };

  @decorator()
  constructor() {
  }

  login(){
    return this.other1 + 'login';
  }

  saveData(data) {
    let obj = {
      id: data.id,
      name: data.name,
      surname: data.surname,
      age: data.age,
      mail: data.mail
    };
    return this.http.post(this.BO.postSampleData(),  obj )
                    .map(res => res.json());
  }

  getData(size:number, page: number, searchTerms, sort: any[]) {
      
      let pageData = {
        pagination: {
          size: size,
          page: page,
          total: 1
        },
        name: searchTerms.name,
        surname: searchTerms.surname,
        age: searchTerms.age,
        mail: searchTerms.mail,
        sort: sort
      }
      let iaia = 2 + 2;
      let iaio = http(iaia);
      return this.http.post(this.BO.postSampleDataSearch(), pageData)
                      .map(res => res.json());
    }

  logout(test: string){
    return this.other1 + 'logout';
  }

  getCsrf(){
    return this.other1 + 'security/v1/csrftoken';
  }

  ngDoCheck(): string {
    if (this.language !== this.translate.currentLang) {
      this.language = this.translate.currentLang;
      this.columns = [
            {name: 'name', label: this.getTranslation('sampledatamanagementDataGrid.columns.name')},
          
            {name: 'surname', label: this.getTranslation('sampledatamanagementDataGrid.columns.surname')},
          
            {name: 'age', label: this.getTranslation('sampledatamanagementDataGrid.columns.age')},
          
            {name: 'mail', label: this.getTranslation('sampledatamanagementDataGrid.columns.mail')}
      ];
    }
    let patata = 2 + 2;
    for(let i = 0; i< 10; i++){
      console.log(i);
    }
    return "patata";
  }

  postSampleData(){
    return this.other1 + 'sampledatamanagement/v1/sampledata/';
  }

  postSampleDataSearch(){
    return this.other1 + 'sampledatamanagement/v1/sampledata/search';
  }


   getTranslation(text: string): string {
        let value: string;
        this.translate.get(text).subscribe( (res) => {
            value = res;
        });
        return value;
    }

  deleteSampleData(){
    return this.other1 + 'sampledatamanagement/v1/sampledata/';
  }

  
}

@decoratordeprueba()
export class patata{

}
