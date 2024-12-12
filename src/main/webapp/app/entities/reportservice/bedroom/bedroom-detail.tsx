import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './bedroom.reducer';

export const BedroomDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bedroomEntity = useAppSelector(state => state.reportservice.bedroom.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bedroomDetailsHeading">
          <Translate contentKey="reportserviceApp.reportserviceBedroom.detail.title">Bedroom</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.id">Id</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.id}</dd>
          <dt>
            <span id="reportId">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.reportId">Report Id</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.reportId}</dd>
          <dt>
            <span id="bedroomName">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.bedroomName">Bedroom Name</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.bedroomName}</dd>
          <dt>
            <span id="conditionLevel">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.conditionLevel">Condition Level</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.conditionLevel}</dd>
          <dt>
            <span id="roomSize">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.roomSize">Room Size</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.roomSize}</dd>
          <dt>
            <span id="closetYn">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.closetYn">Closet Yn</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.closetYn}</dd>
          <dt>
            <span id="acYn">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.acYn">Ac Yn</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.acYn}</dd>
          <dt>
            <span id="windowLocation">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.windowLocation">Window Location</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.windowLocation}</dd>
          <dt>
            <span id="windowSize">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.windowSize">Window Size</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.windowSize}</dd>
          <dt>
            <span id="remarks">
              <Translate contentKey="reportserviceApp.reportserviceBedroom.remarks">Remarks</Translate>
            </span>
          </dt>
          <dd>{bedroomEntity.remarks}</dd>
          <dt>
            <Translate contentKey="reportserviceApp.reportserviceBedroom.report">Report</Translate>
          </dt>
          <dd>{bedroomEntity.report ? bedroomEntity.report.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/reportservice/bedroom" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/reportservice/bedroom/${bedroomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BedroomDetail;
