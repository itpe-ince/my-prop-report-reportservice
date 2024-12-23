import { IReport } from 'app/shared/model/reportservice/report.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface ILivingRoom {
  id?: number;
  livingRoomName?: string;
  conditionLevel?: keyof typeof QualityStateType;
  roomSize?: number | null;
  wallState?: keyof typeof QualityStateType;
  floorMaterial?: string | null;
  sunlight?: string | null;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<ILivingRoom> = {};
