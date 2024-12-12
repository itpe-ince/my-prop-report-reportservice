import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import EnvFactor from './env-factor';
import EnvFactorDetail from './env-factor-detail';
import EnvFactorUpdate from './env-factor-update';
import EnvFactorDeleteDialog from './env-factor-delete-dialog';

const EnvFactorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<EnvFactor />} />
    <Route path="new" element={<EnvFactorUpdate />} />
    <Route path=":id">
      <Route index element={<EnvFactorDetail />} />
      <Route path="edit" element={<EnvFactorUpdate />} />
      <Route path="delete" element={<EnvFactorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EnvFactorRoutes;
