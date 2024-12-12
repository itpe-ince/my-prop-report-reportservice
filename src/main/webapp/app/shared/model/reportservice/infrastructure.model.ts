import { IReport } from 'app/shared/model/reportservice/report.model';
import { InfraType } from 'app/shared/model/enumerations/infra-type.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface IInfrastructure {
  id?: number;
  reportId?: number;
  infraType?: keyof typeof InfraType;
  infraName?: string;
  conditionLevel?: keyof typeof QualityStateType;
  infraDistance?: number | null;
  infraDistanceUnit?: keyof typeof QualityStateType | null;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<IInfrastructure> = {};
