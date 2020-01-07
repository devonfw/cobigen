<!DOCTYPE html>
<html>
<body>
<div class="row">
  <div>
  </div>
  <div class="floatDiv">
    <div class="col-md-4">
      <div style="width:300px;">
	      <h2>Welcome to the generated OASP entity displayer for ${variables.entityName}</h2>
	    <p>
		  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet
		  lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient
		   montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien
		   nunc eget odio.
	    </p>
  	  </div>
	</div>
  </div>
  <div class="floatDiv">
    <div class="col-md-4">
      <div class="loginForm">
        <alert data-ng-show="errorMessage.hasOne()" data-close="errorMessage.clear()" data-type="danger"><span data-ng-bind="errorMessage.text"></span></alert>
        <form name="loginForm" novalidate>
            <p>
                <span class="label label-danger" data-ng-show="validation.userNameNotProvided()">Bitte geben Sie Ihre Benutzerkennung ein!</span>
                <input type="text" placeholder="Benutzerkennung" name="userName" class="form-control" data-ng-model="credentials.username" data-ng-required="true">
            </p>
            <p>
                <span class="label label-danger" data-ng-show="validation.passwordNotProvided()">Bitte geben Sie Ihr Passwort ein!</span>
                <input type="password" placeholder="Passwort" name="password" class="form-control" data-ng-model="credentials.password" data-ng-required="true">
            </p>
            <p>
            <button type="submit" class="btn btn-success" data-ng-click="signIn()">Sign in</button>
            </p>
        </form>
      </div>
    </div>
  </div>
  <div class="col-md-8" style="clear:both;"></div>
</div>
</body>
</html>