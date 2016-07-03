<div class="navbar navbar-inverse navbar-fixed-top" role="navigation" data-ng-controller="AppCntl">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse"
                    data-ng-init="navCollapsed = true" data-ng-click="navCollapsed = !navCollapsed">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#/">${variables.entityName} display template for OASP</a>
        </div>
        <div class="collapse navbar-collapse" data-ng-class="{'in':!navCollapsed}">
            <ul class="ng-cloak nav navbar-nav" data-ng-show="currentUser.isLoggedIn()">
                <li class="dropdown" dropdown=""><a href="#/${variables.component}" class="dropdown-toggle" dropdown-toggle=""><span translate="">${variables.component}.${variables.component}</span>
						<span class="caret"></span>
				</a>
					<ul class="dropdown-menu" role="menu">
						<li><a href="#/${variables.component}/${variables.entityName}" ng-click="navCollapsed = !navCollapsed"><span translate="">${variables.component}.${variables.entityName}</span></a></li>
					</ul></li>
            </ul>
            <div class="ng-cloak navbar-right" data-ng-show="currentUser.isLoggedIn()">
                <span class="navbar-text"><span translate="">OASP.LOGGED_IN_AS</span>:&nbsp;<span
                        data-ng-bind="currentUser.getUserName()"></span></span>
                <button data-ng-click="logOff()" class="btn navbar-btn btn-sm btn-info" translate="">OASP.LOG_OFF
                </button>
            </div>
            <ul class="nav navbar-nav navbar-right">
                <language-change></language-change>
            </ul>
        </div>
    </div>
</div>