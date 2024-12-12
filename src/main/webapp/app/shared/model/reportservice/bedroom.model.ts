import { IReport } from 'app/shared/model/reportservice/report.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface IBedroom {
  id?: number;
  reportId?: number;
  bedroomName?: string;
  conditionLevel?: keyof typeof QualityStateType;
  roomSize?: number | null;
  closetYn?: string | null;
  acYn?: string | null;
  windowLocation?: string | null;
  windowSize?: string | null;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<IBedroom> = {};
