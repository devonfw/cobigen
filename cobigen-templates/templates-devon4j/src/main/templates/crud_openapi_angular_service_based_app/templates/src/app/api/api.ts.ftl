import { ${variables.component?cap_first}RestControllerService } from './${variables.component?lower_case}/${variables.component?lower_case}RestController.service';

export * from './${variables.component?lower_case}/${variables.component?lower_case}RestController.service';

export const APIS: any = [${variables.component?cap_first}RestControllerService,];
