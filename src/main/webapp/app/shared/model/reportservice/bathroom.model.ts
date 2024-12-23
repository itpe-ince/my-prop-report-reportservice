import { IReport } from 'app/shared/model/reportservice/report.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface IBathroom {
  id?: number;
  bathroomName?: string;
  condtionLevel?: keyof typeof QualityStateType;
  bathroomSize?: number | null;
  waterPressure?: keyof typeof QualityStateType;
  showerBoothPresence?: string | null;
  bathtubPresence?: string | null;
  floorAndCeiling?: keyof typeof QualityStateType;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<IBathroom> = {};
