import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import LivingRoom from './living-room';
import LivingRoomDetail from './living-room-detail';
import LivingRoomUpdate from './living-room-update';
import LivingRoomDeleteDialog from './living-room-delete-dialog';

const LivingRoomRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<LivingRoom />} />
    <Route path="new" element={<LivingRoomUpdate />} />
    <Route path=":id">
      <Route index element={<LivingRoomDetail />} />
      <Route path="edit" element={<LivingRoomUpdate />} />
      <Route path="delete" element={<LivingRoomDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LivingRoomRoutes;
