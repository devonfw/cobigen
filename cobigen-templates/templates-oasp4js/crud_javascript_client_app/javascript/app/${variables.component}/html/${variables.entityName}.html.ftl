<!-- <!DOCTYPE html>
<html>
<body>
<div class="row">
    <div class="col-md-12">

    <div>
    <div id="u183" class="ax_paragraph b" style="visibility: visible;">

      <div id="u184" class="text b" style="top: 9px; transform-origin: 480px 10px 0px;">
            <p id="cache131" class="b">
                <span id="cache132" class="b" style="font-family: Arial Bold,Arial; color: rgb(51, 51, 51); opacity: 1; font-weight: 700;">${variables.entityName}-Anzeige</span>
            </p>
        </div>
    </div>

      <div style="visibility: visible;" id="u185" class="ax_horizontal_line b">
        </div>

      <div style="visibility: visible;">
        </div>

    <div style="visibility: visible;" id="u162" class="ax_shape b">



      <div id="u168"  class="panel_state b">
            <table class="raw-table" tr-ng-grid=""
                   selection-mode="SingleRow"
                   enable-filtering="true"
                   enable-sorting="true"
                   selected-items="selectedItems"
                   items="gridOptions.data"
                   style="margin-left: 1px; position: relative; overflow: hidden;">
                <thead>
                <tr>
                		<th ng-repeat="(key,value) in gridOptions.data[0]" display-name="{{key}}"></th>
                    <th field-name="id" display-name="Id" cell-width="7em"></th> //ng-repeat for entity-attributes
                    <th field-name="label" display-name="Fehlerart"></th>
                    <th field-name="logtime" display-name="Zeitpunkt"></th>
                </tr>
                </thead>
            </table>
          </div>

      	  <div style="visibility: visible;" id="u205" class="ax_horizontal_line b">
          </div>

          <div style="visibility: visible;" id="u166" class="ax_paragraph b" data-label="${variables.entityName}description">
              <div ng-show="selectedItems && selectedItems.length" id="${variables.entityName}Details" class="text b">
                <p class="b">
                  <span class="b">{{ selectedItems[0].id + " - " + selectedItems[0].label }}</span>
                </p>
 	            <div id="u185" class="ax_horizontal_line b">
	            </div>
                <span id="${variables.entityName}Description" class="b" ng-repeat="(key, value) in selectedItems[0]"><b>{{key}}</b>: {{value}}&nbsp;&nbsp;&nbsp;</span>
                </p>
          	  </div>

              <div ng-show="!(selectedItems && selectedItems.length)" id="HintShowDetails" class="text b">
                <p class="b">
                  <span class="b">Select row to show more details here</span>
                </p>
          	  </div>
          </div>
    </div>
</div>
</body>
</html>-->



<!DOCTYPE html>
<html>
<body>
<div class="row">
    <div class="col-md-12">
        <h2>${variables.entityName} Display</h2>
				<div id="u168"  class="panel_state b">
				<table class="raw-table"
					tr-ng-grid=""
					items="gridOptions.data"
					selected-items="selectedItems"
					selection-mode="SingleRow"
					enable-filtering="true"
					enable-sorting="true"
					style="margin-left: 1px; position: relative; overflow: auto;">
					<thead>
                <!--<tr>
                	<div ng-repeat="(key,value) in gridOptions.data[0]" ng-if="key != 'modificationCounter' && key != 'revision'">
                		<th field-name="key" display-name="key"></th>
               		</div>
                </tr>-->
        	</thead>
        </table>
      </div>

              <div style="visibility: visible;" id="u166" class="ax_paragraph b" data-label="${variables.entityName}description">
              <div ng-show="selectedItems && selectedItems.length" id="${variables.entityName}Details" class="text b">
                <h3>Details</h3>
 	            <div id="u185" class="ax_horizontal_line b">
	            </div>
                <span id="${variables.entityName}Description" class="b" ng-repeat="(key, value) in selectedItems[0]"><b>{{key}}</b>: {{value}}&nbsp;&nbsp;&nbsp;</span>
                </p>
          	  </div>

              <div ng-show="!(selectedItems && selectedItems.length)" id="HintShowDetails" class="text b">
                <p class="b">
                  <span class="b">Select row to show more details here</span>
                </p>
          	  </div>

       <br>
       <br>
       <br>
       <pagination total-items="totalItems" items-per-page="numPerPage" ng-model="currentPage" num-pages="numPages" class="pagination-sm" boundary-links="true" rotate="false" max-size="maxSize"></pagination>
    </div>
</div>
</body>
</html>