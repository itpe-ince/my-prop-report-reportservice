import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Kitchen from './kitchen';
import KitchenDetail from './kitchen-detail';
import KitchenUpdate from './kitchen-update';
import KitchenDeleteDialog from './kitchen-delete-dialog';

const KitchenRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Kitchen />} />
    <Route path="new" element={<KitchenUpdate />} />
    <Route path=":id">
      <Route index element={<KitchenDetail />} />
      <Route path="edit" element={<KitchenUpdate />} />
      <Route path="delete" element={<KitchenDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default KitchenRoutes;
