import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Entrance from './entrance';
import EntranceDetail from './entrance-detail';
import EntranceUpdate from './entrance-update';
import EntranceDeleteDialog from './entrance-delete-dialog';

const EntranceRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Entrance />} />
    <Route path="new" element={<EntranceUpdate />} />
    <Route path=":id">
      <Route index element={<EntranceDetail />} />
      <Route path="edit" element={<EntranceUpdate />} />
      <Route path="delete" element={<EntranceDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default EntranceRoutes;
