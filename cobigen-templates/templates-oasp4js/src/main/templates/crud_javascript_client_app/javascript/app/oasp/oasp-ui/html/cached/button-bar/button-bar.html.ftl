<div class="btn-group btn-group-sm" role="group">
    <button data-ng-repeat="buttonDef in buttonDefs"
            data-ng-click="onButtonClick(buttonDef)"
            data-ng-disabled="isButtonDisabled(buttonDef)"
            class="btn btn-sm btn-default">
        <span data-ng-bind="buttonDef.label"></span>
    </button>
</div>
