import dayjs from 'dayjs';
import { IAuthor } from 'app/shared/model/reportservice/author.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';

export interface IReport {
  id?: number;
  reportTitle?: string;
  reportDate?: dayjs.Dayjs | null;
  summary?: string | null;
  exteriorState?: keyof typeof QualityStateType;
  constructionYear?: number | null;
  maintenanceState?: keyof typeof QualityStateType;
  parkingFacility?: string | null;
  parkingCount?: number | null;
  elevatorState?: keyof typeof QualityStateType;
  noiseState?: keyof typeof QualityStateType;
  homepadState?: keyof typeof QualityStateType;
  cctvYn?: string | null;
  fireSafetyState?: keyof typeof QualityStateType;
  doorSecurityState?: keyof typeof QualityStateType;
  maintenanceFee?: number | null;
  redevelopmentYn?: string | null;
  rentalDemand?: string | null;
  communityRules?: string | null;
  complexId?: number;
  complexName?: string;
  propertyId?: number;
  propertyName?: string;
  createdAt?: dayjs.Dayjs;
  updatedAt?: dayjs.Dayjs | null;
  author?: IAuthor | null;
}

export const defaultValue: Readonly<IReport> = {};
