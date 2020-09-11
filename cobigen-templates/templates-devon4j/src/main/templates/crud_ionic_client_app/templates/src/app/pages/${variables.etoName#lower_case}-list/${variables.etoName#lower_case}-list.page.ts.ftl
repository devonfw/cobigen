import { TranslocoService } from '@ngneat/transloco';
import { Component, Input, ViewChild } from '@angular/core';
import {
  AlertController,
  ModalController,
  NavController,
  LoadingController,
  IonList,
} from '@ionic/angular';
import { ${variables.etoName?cap_first}RestService } from '../../services/${variables.etoName?lower_case}-rest.service';
import { ${variables.etoName?cap_first}Detail } from '../${variables.etoName?lower_case}-detail/${variables.etoName?lower_case}-detail.page';
import { ${variables.etoName?cap_first} } from '../../services/interfaces/${variables.etoName?lower_case}';
import { Pageable } from '../../services/interfaces/pageable';
import { ${variables.etoName?cap_first}SearchCriteria } from '../../services/interfaces/${variables.etoName?lower_case}-search-criteria';
import { PaginatedListTo } from '../../services/interfaces/paginated-list-to';

@Component({
  selector: '${variables.etoName?lower_case}-list',
  templateUrl: '${variables.etoName?lower_case}-list.page.html',
  styleUrls: ['${variables.etoName?lower_case}-list.page.scss'],
})
export class ${variables.etoName?cap_first}List {
  /** Contains the strings for the deletion prompt */
  deleteTranslations: any = {};
  pageable: Pageable = {
    pageSize: 15,
    pageNumber: 0,
    sort: [
      {
        property: '${pojo.fields[0].name!}',
        direction: 'ASC',
      },
    ],
  };
  ${variables.etoName?lower_case}SearchCriteria: ${variables.etoName?cap_first}SearchCriteria = {
    <#list pojo.fields as field>
    ${field.name}: null,
    </#list>
    pageable: this.pageable,
  };
  ${variables.etoName?lower_case}ListItem: ${variables.etoName?cap_first} = {
    <#list pojo.fields as field>
    ${field.name}: null,
    </#list>
  };
  deleteButtonNames = ['dismiss', 'confirm'];
  deleteButtons = [
    { text: '', handler: data => {} },
    { text: '', handler: data => {} },
  ];
  @Input()
  deleteModifiedButtonsDisabled = true;
  @Input()
  infiniteScrollEnabled = true;

  ${variables.etoName?lower_case}s: ${variables.etoName?cap_first}[] = [];
  selectedItemIndex = -1;

  constructor(
    public navCtrl: NavController,
    public ${variables.etoName?lower_case}Rest: ${variables.etoName?cap_first}RestService,
    public alertCtrl: AlertController,
    public translocoService: TranslocoService,
    public modalCtrl: ModalController,
    public loadingCtrl: LoadingController,
  ) {}

  @ViewChild('slidingList', { static: true }) slidingList: IonList;
  /**
   * Runs when the page is about to enter and become the active page.
   */
  private ionViewWillEnter() {
    this.ionViewWillEnterAsync();
  }

  private async ionViewWillEnterAsync() {
    const loading = await this.loadingCtrl.create({
      message: 'Please wait...',
    });
    await loading.present();
    this.${variables.etoName?lower_case}Rest.retrieveData(this.${variables.etoName?lower_case}SearchCriteria).subscribe(
      (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
        this.${variables.etoName?lower_case}s = this.${variables.etoName?lower_case}s.concat(data.content);
        loading.dismiss();
      },
      (err: any) => {
        loading.dismiss();
        console.log(err);
      },
    );
  }

  /**
   * Get the selected item index.
   * @returns The current selected item index.
   */
  public getSelectedItemIndex(): number {
    if (this.selectedItemIndex <= -1) {
      return;
    }
    return this.selectedItemIndex;
  }

  /**
   * Set the selected item index.
   * @param  index The item index you want to set.
   */
  public setSelectedItemIndex(index: number) {
    this.selectedItemIndex = index;
    this.deleteModifiedButtonsDisabled = false;
  }

  /**
   * Executed after a pull-to-refresh event. It reloads the ${variables.etoName?lower_case} list.
   * @param  refresher Pull-to-refresh event.
   */
  public doRefresh(refresher) {
    setTimeout(() => {
      this.reload${variables.etoName?cap_first}List();
      refresher.target.complete();
    }, 300);
  }

  /**
   * Reloads the ${variables.etoName?lower_case} list, retrieving the first page.
   */
  private reload${variables.etoName?cap_first}List() {
    this.pageable.pageNumber = 0;
    this.${variables.etoName?lower_case}SearchCriteria.pageable = this.pageable;
    this.deleteModifiedButtonsDisabled = true;
    this.selectedItemIndex = -1;
    this.${variables.etoName?lower_case}Rest.retrieveData(this.${variables.etoName?lower_case}SearchCriteria).subscribe(
      (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
        this.${variables.etoName?lower_case}s = [].concat(data.content);
        this.infiniteScrollEnabled = true;
      },
      err => {
        this.${variables.etoName?lower_case}s = [];
        console.log(err);
      },
    );
  }

  /**
   * Translates a string to the current language.
   * @param  text The string to be translated.
   * @returns The translated string.
   */
  private getTranslation(text: string): string {
    let value: string;
    value = this.translocoService.translate(text);
    return value;
  }

  /**
   * Presents the create dialog to the user and creates a new ${variables.etoName?lower_case} if the data is correctly defined.
   */
  public async create${variables.etoName?cap_first}() {
    const modal = await this.modalCtrl.create({
      component: ${variables.etoName?cap_first}Detail,
      componentProps: {
        dialog: 'add',
        edit: null,
      },
    });
    await modal.present();
    modal.onDidDismiss().then(() => this.reload${variables.etoName?cap_first}List());
  }

  /**
   * Presents the search dialog to the user and sets to the list all the found ${variables.etoName?lower_case}s.
   */
  public async search${variables.etoName?cap_first}s() {

    this.deleteModifiedButtonsDisabled = true;
    this.selectedItemIndex = -1;
    const modal = await this.modalCtrl.create({
      component: ${variables.etoName?cap_first}Detail,
      componentProps: {
        dialog: 'filter',
        edit: null
      }
    });

    await modal.present();
    modal.onDidDismiss().then((data) => {
      if (data && data.data == null) {
        return;
      } else {
          this.infiniteScrollEnabled = true;
          this.${variables.etoName?lower_case}SearchCriteria = data.data[0];
          this.reload${variables.etoName?cap_first}List();
      }
    });
  }

  /**
   * Presents the modify dialog and updates the selected ${variables.etoName?lower_case}.
   */
  public async updateSelected${variables.etoName?cap_first}() {
    await this.slidingList.closeSlidingItems();

    if (!this.selectedItemIndex && this.selectedItemIndex !== 0) {
      return;
    }
    const cleanItem = this.${variables.etoName?lower_case}ListItem;
    for (const i of Object.keys(cleanItem)) {
      cleanItem[i] = this.${variables.etoName?lower_case}s[this.selectedItemIndex][i];
    }

    const modal = await this.modalCtrl.create({
      component: ${variables.etoName?cap_first}Detail,
      componentProps: {
        dialog: 'modify',
	edit: this.${variables.etoName?lower_case}s[this.selectedItemIndex],
      }
    });
    await modal.present();
    modal.onDidDismiss().then((data: any) => {
      if (data && data.data) {
        this.${variables.etoName?lower_case}s.splice(this.selectedItemIndex, 1, data.data);
      }
    });
  }

  /**
   * Presents a promt to the user to warn him about the deletion.
   */
  public async deleteSelected${variables.etoName?cap_first}() {
    await this.slidingList.closeSlidingItems();

    this.deleteTranslations = this.getTranslation(
      '${variables.component?lower_case}.${variables.etoName?lower_case}.operations.delete',
    );
    for (const i of Object.keys(this.deleteButtons)) {
      this.deleteButtons[i].text = this.deleteTranslations[
        this.deleteButtonNames[i]
      ];
    }
    const prompt = await this.alertCtrl.create({
      header: this.deleteTranslations.title,
      message: this.deleteTranslations.message,
      buttons: [
        { text: this.deleteButtons[0].text, handler: data => {} },
        {
          text: this.deleteButtons[1].text,
          handler: data => {
            this.confirmDeletion();
          },
        },
      ],
    });
    await prompt.present();
  }

  /**
   * Removes the current selected item.
   */
  private confirmDeletion() {
    if (!this.selectedItemIndex && this.selectedItemIndex !== 0) {
      return;
    }
    const search = this.${variables.etoName?lower_case}s[this.selectedItemIndex];

    this.${variables.etoName?lower_case}Rest.delete(search.id).subscribe(
      deleteresponse => {
        this.${variables.etoName?lower_case}s.splice(this.selectedItemIndex, 1);
        this.selectedItemIndex = -1;
        this.deleteModifiedButtonsDisabled = true;
      },
      err => {
        console.log(err);
      },
    );
  }

  /**
   * Executed after the user reaches the end of the last page. It tries to retrieve the next data page.
   * @param  infiniteScroll Infinite scroll event.
   */
  public doInfinite(infiniteScroll) {
    if (this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber < 0) {
      this.infiniteScrollEnabled = false;
    } else {
      this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber =
        this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber + 1;

      setTimeout(() => {
        this.${variables.etoName?lower_case}Rest
          .retrieveData(this.${variables.etoName?lower_case}SearchCriteria)
          .subscribe(
          (data: PaginatedListTo<${variables.etoName?cap_first}>) => {
              if (
                data.content.length === 0 &&
                this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber > 0
              ) {
                this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber =
                this.${variables.etoName?lower_case}SearchCriteria.pageable.pageNumber - 1;
                this.infiniteScrollEnabled = false;
              } else {
                this.${variables.etoName?lower_case}s = this.${variables.etoName?lower_case}s.concat(data.content);
              }

              infiniteScroll.target.complete();
            },
            (err) => {
              console.log(err);
            },
          );
      }, 300);
    }
  }

  /**
   * Enables the update and delete buttons for the selected ${variables.etoName?lower_case}.
   * @param  index The index of the selected ${variables.etoName?lower_case} that will be allowed to be updated or deleted.
   */
  public enableUpdateDeleteOperations(index: number) {
    if (this.selectedItemIndex !== index) {
      this.selectedItemIndex = index;
      this.deleteModifiedButtonsDisabled = false;
    } else {
      this.selectedItemIndex = -1;
      this.deleteModifiedButtonsDisabled = true;
    }
  }
}
