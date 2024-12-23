import { IReport } from 'app/shared/model/reportservice/report.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface IEntrance {
  id?: number;
  entranceName?: string;
  condtionLevel?: keyof typeof QualityStateType;
  entranceSize?: number | null;
  shoeRackSize?: number | null;
  pantryPresence?: string | null;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<IEntrance> = {};
