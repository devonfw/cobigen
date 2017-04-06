import { Observable } from 'rxjs/Rx';
import { Injectable } from '@angular/core';
import * as ts from 'typescript';
import { Patata } from 'patata/Rx';

@DecoratorTest()
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
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
    MoredatamanagementDataGridComponent,
    MoredatamanagementAddDialogComponent
  ],
  entryComponents: [
    MoredatamanagementAddDialogComponent
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    MoredatamanagementDataGridService,
    SecurityService,
    HttpClient,
    BusinessOperations
  ],
})
export class BusinessOperations extends patata{

  public serverPath = url;
  public servicesPath = this.serverPath;

  columns: patata = [
    {name: 'name', label: this.getTranslation('sampledatamanagementDataGrid.columns.name')},
    {name: 'newField', label: this.getTranslation('sampledatamanagementDataGrid.columns.newField')}
  ];  

  newProperty: number;

  data: any = [];

  selectedRow: any;

  other: number = 2;

  other1: string ="patata";

  other2: boolean = false;

  other3: [number, string] = [2, "numero2"];

  other5: number[] = [2, 3, 4, 5];

  item = {
    name: '',
    newField: ''
  };

  searchTerms: any = {
    name: null,
    newField: null
  };
  
  saveData(data) {
    let obj = {
      id: data.id,
      name: data.name,
      newField: data.newField
    };
  
    return this.http.post(this.BO.postSampleData(),  obj )
                    .map(res => res.json());
  }
  login(){
    return this.other1 + 'patata';
  }

  logout(){
    return this.other1 + 'logout';
  }

  getCsrf(){
    return this.other1 + 'security/v1/csrftoken';
  }

   getData(size:number, page: number, searchTerms, sort: any[]) {
      
      let pageData = {
        pagination: {
          size: size,
          page: page,
          total: 1
        },
        name: searchTerms.name,
        newField: searchTerms.newField,
        sort: sort
      }
      return this.http.post(this.BO.postSampleDataSearch(), pageData)
                      .map(res => res.json());
    }


  postSampleData(){
    return this.other1 + 'sampledatamanagement/v1/sampledata/';
  }

  postSampleDataSearch(){
    return this.other1 + 'sampledatamanagement/v1/sampledata/search';
  }

  deleteSampleData(int: number, text: string, is: boolean){
    return this.other1 + 'sampledatamanagement/v1/sampledata/';
  }

  newMethod(){
    let eselpatch;
    return "patata";
  }

  ngDoCheck() {
        if (this.language !== this.translate.currentLang) {
            this.language = this.translate.currentLang;
            this.columns = [
              {name: 'name', label: this.getTranslation('sampledatamanagementDataGrid.columns.name')},
              {name: 'newField', label: this.getTranslation('sampledatamanagementDataGrid.columns.newField')}    
            ];
        }
  }
}
