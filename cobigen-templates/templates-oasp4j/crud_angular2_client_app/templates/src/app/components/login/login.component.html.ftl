<md-card class="login-card-form">
    <h2 class = "md-display-2" style="text-align:center">{{'login.title' | translate}}</h2>
    <md-card-content class="center-align">
        <div class="input-field login-card-form-input">
            <form #loginForm = "ngForm">

                <md-input-container style="width:100%;">
                    <input md-input type="text" [placeholder]="'login.username' | translate" ngModel name="username" required>
                </md-input-container>

                <md-input-container style="width:100%;">
                    <input md-input type="password" [placeholder]="'login.password' | translate" ngModel name="password" required>
                </md-input-container>
                
                
                <button md-raised-button color="primary" [disabled]="!loginForm.form.valid" (click)="login(loginForm.form)">{{'buttons.submit'| translate}}</button>
           
            </form>
        </div>
    </md-card-content>
</md-card>