<li class="dropdown language-dropdown" dropdown>
    <a href class="dropdown-toggle" dropdown-toggle>
        <span class="icon-container"><span
            class="icon icon-{{getCurrentLanguage()}}-24"></span></span><span translate="">OASP.LANGUAGE</span><span
        class="caret"></span>
    </a>
    <ul class="dropdown-menu" role="menu">
        <li ng-repeat="lang in supportedLanguages" ng-show="getCurrentLanguage()!=lang.key">
            <a ng-click="changeLanguage(lang.key)">
                <span class="icon icon-{{lang.key}}-24"></span>{{lang.label}}
            </a>
        </li>
    </ul>
</li>
