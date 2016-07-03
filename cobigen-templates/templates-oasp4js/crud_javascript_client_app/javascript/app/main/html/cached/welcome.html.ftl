<!DOCTYPE html>
<html>
<body>
  <div data-ng-controller="AppCntl">
  	<h3>Welcome to the generated OASP4JS entity display for ${variables.entityName}, <span data-ng-bind="currentUser.getUserName()">!</span></h3>
	<span class="b">
		Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet
		lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient
		montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien
		nunc eget odio.<br>
		Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean euismod bibendum laoreet. Proin gravida dolor sit amet
		lacus accumsan et viverra justo commodo. Proin sodales pulvinar tempor. Cum sociis natoque penatibus et magnis dis parturient
		montes, nascetur ridiculus mus. Nam fermentum, nulla luctus pharetra vulputate, felis tellus mollis orci, sed rhoncus sapien
		nunc eget odio.
  </span>
  <span class="b">
  	<p>Go to <a href="#/${variables.component}/${variables.entityName}">Display dialog </a> of the ${variables.component} component.</p>
  </span>
  </div>
</body>
</html>