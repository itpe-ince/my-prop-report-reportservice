import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './env-factor.reducer';

export const EnvFactorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const envFactorEntity = useAppSelector(state => state.reportservice.envFactor.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="envFactorDetailsHeading">
          <Translate contentKey="reportserviceApp.reportserviceEnvFactor.detail.title">EnvFactor</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="reportserviceApp.reportserviceEnvFactor.id">Id</Translate>
            </span>
          </dt>
          <dd>{envFactorEntity.id}</dd>
          <dt>
            <span id="reportId">
              <Translate contentKey="reportserviceApp.reportserviceEnvFactor.reportId">Report Id</Translate>
            </span>
          </dt>
          <dd>{envFactorEntity.reportId}</dd>
          <dt>
            <span id="envFactorName">
              <Translate contentKey="reportserviceApp.reportserviceEnvFactor.envFactorName">Env Factor Name</Translate>
            </span>
          </dt>
          <dd>{envFactorEntity.envFactorName}</dd>
          <dt>
            <span id="envFactorDistance">
              <Translate contentKey="reportserviceApp.reportserviceEnvFactor.envFactorDistance">Env Factor Distance</Translate>
            </span>
          </dt>
          <dd>{envFactorEntity.envFactorDistance}</dd>
          <dt>
            <span id="remarks">
              <Translate contentKey="reportserviceApp.reportserviceEnvFactor.remarks">Remarks</Translate>
            </span>
          </dt>
          <dd>{envFactorEntity.remarks}</dd>
          <dt>
            <Translate contentKey="reportserviceApp.reportserviceEnvFactor.report">Report</Translate>
          </dt>
          <dd>{envFactorEntity.report ? envFactorEntity.report.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/reportservice/env-factor" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/reportservice/env-factor/${envFactorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default EnvFactorDetail;
