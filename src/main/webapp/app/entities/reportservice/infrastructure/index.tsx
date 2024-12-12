import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Infrastructure from './infrastructure';
import InfrastructureDetail from './infrastructure-detail';
import InfrastructureUpdate from './infrastructure-update';
import InfrastructureDeleteDialog from './infrastructure-delete-dialog';

const InfrastructureRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Infrastructure />} />
    <Route path="new" element={<InfrastructureUpdate />} />
    <Route path=":id">
      <Route index element={<InfrastructureDetail />} />
      <Route path="edit" element={<InfrastructureUpdate />} />
      <Route path="delete" element={<InfrastructureDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default InfrastructureRoutes;
