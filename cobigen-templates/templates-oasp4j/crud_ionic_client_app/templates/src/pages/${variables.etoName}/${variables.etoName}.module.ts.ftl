import { NgModule } from '@angular/core';
import { IonicPageModule } from 'ionic-angular';
import { ${variables.etoName}Page } from './${variables.etoName}';

@NgModule({
	declarations: [
		${variables.etoName}Page,
	],
	imports: [
		IonicPageModule.forChild(${variables.etoName}Page),
	],
})
export class TablePageModule {}
