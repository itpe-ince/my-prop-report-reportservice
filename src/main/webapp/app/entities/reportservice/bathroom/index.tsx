import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Bathroom from './bathroom';
import BathroomDetail from './bathroom-detail';
import BathroomUpdate from './bathroom-update';
import BathroomDeleteDialog from './bathroom-delete-dialog';

const BathroomRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Bathroom />} />
    <Route path="new" element={<BathroomUpdate />} />
    <Route path=":id">
      <Route index element={<BathroomDetail />} />
      <Route path="edit" element={<BathroomUpdate />} />
      <Route path="delete" element={<BathroomDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default BathroomRoutes;
