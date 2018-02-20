  
    <button ion-fab (click)="promptAddClicked()" >
        <ion-icon name="add-circle"></ion-icon>
    </button>
    
    <button ion-fab (click)="promptModifyClicked()" [disabled] = isDisabled  > 
        <ion-icon name="brush"></ion-icon>
    </button>
  
    <button ion-fab (click)="DeleteConfirmForm()" [disabled] = isDisabled >
        <ion-icon name="trash"></ion-icon>
    </button>
    
    <button ion-fab (click)="promptFilterClicked()" >
        <ion-icon name="search"></ion-icon>
    </button>
