import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Bedroom from './bedroom';
import BedroomDetail from './bedroom-detail';
import BedroomUpdate from './bedroom-update';
import BedroomDeleteDialog from './bedroom-delete-dialog';

const BedroomRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Bedroom />} />
    <Route path="new" element={<BedroomUpdate />} />
    <Route path=":id">
      <Route index element={<BedroomDetail />} />
      <Route path="edit" element={<BedroomUpdate />} />
      <Route path="delete" element={<BedroomDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BedroomRoutes;
