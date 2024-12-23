import { IReport } from 'app/shared/model/reportservice/report.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface IKitchen {
  id?: number;
  kitchenName?: string;
  conditionLevel?: keyof typeof QualityStateType;
  builtInCabinet?: string | null;
  sinkCondition?: keyof typeof QualityStateType;
  ventilationSystem?: string | null;
  applianceProvision?: string | null;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<IKitchen> = {};
