<div>
    <form name="loginForm" novalidate>
    <div class="modal-header">
        <h3 class="modal-title">Sign in</h3>
    </div>
    <div class="modal-body">
        <div class="row">
            <div class="col-md-12">
                <alert data-ng-show="errorMessage.hasOne()" data-close="errorMessage.clear()" data-type="danger">
                    <span data-ng-bind="errorMessage.text"></span>
                </alert>
                <p>
                     <span class="label label-danger" data-ng-show="validation.userNameNotProvided()">Bitte geben Sie Ihre Benutzerkennung ein!</span>
                     <input type="text" placeholder="Benutzerkennung" name="userName" class="form-control" data-ng-model="credentials.username" data-ng-required="true">
                </p>
                <p>
                    <span class="label label-danger" data-ng-show="validation.passwordNotProvided()">Bitte geben Sie Ihr Passwort ein!</span>
                    <input type="password" placeholder="Passwort" name="password" class="form-control" data-ng-model="credentials.password" data-ng-required="true">
                </p>
            </div>
        </div>
    </div>
    <div class="modal-footer">
        <button class="btn btn-success" data-ng-click="signIn()">Sign in</button>
        <button class="btn btn-warning" ng-click="$dismiss()">Cancel</button>
    </div>
    </form>
</div>