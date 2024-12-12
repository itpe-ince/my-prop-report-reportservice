import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import { ReducersMapObject, combineReducers } from '@reduxjs/toolkit';

import getStore from 'app/config/store';

import entitiesReducers from './reducers';

import Report from './reportservice/report';
import Infrastructure from './reportservice/infrastructure';
import EnvFactor from './reportservice/env-factor';
import LivingRoom from './reportservice/living-room';
import Bedroom from './reportservice/bedroom';
import Kitchen from './reportservice/kitchen';
import Bathroom from './reportservice/bathroom';
import Entrance from './reportservice/entrance';
import Author from './reportservice/author';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  const store = getStore();
  store.injectReducer('reportservice', combineReducers(entitiesReducers as ReducersMapObject));
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="/report/*" element={<Report />} />
        <Route path="/infrastructure/*" element={<Infrastructure />} />
        <Route path="/env-factor/*" element={<EnvFactor />} />
        <Route path="/living-room/*" element={<LivingRoom />} />
        <Route path="/bedroom/*" element={<Bedroom />} />
        <Route path="/kitchen/*" element={<Kitchen />} />
        <Route path="/bathroom/*" element={<Bathroom />} />
        <Route path="/entrance/*" element={<Entrance />} />
        <Route path="/author/*" element={<Author />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
