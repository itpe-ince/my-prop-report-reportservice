import { IReport } from 'app/shared/model/reportservice/report.model';

export interface IEnvFactor {
  id?: number;
  envFactorName?: string;
  envFactorDistance?: number | null;
  remarks?: string | null;
  report?: IReport | null;
}

export const defaultValue: Readonly<IEnvFactor> = {};
