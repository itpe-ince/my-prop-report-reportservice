import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './report.reducer';

export const ReportDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const reportEntity = useAppSelector(state => state.reportservice.report.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="reportDetailsHeading">
          <Translate contentKey="reportserviceApp.reportserviceReport.detail.title">Report</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="reportserviceApp.reportserviceReport.id">Id</Translate>
            </span>
          </dt>
          <dd>{reportEntity.id}</dd>
          <dt>
            <span id="reportTitle">
              <Translate contentKey="reportserviceApp.reportserviceReport.reportTitle">Report Title</Translate>
            </span>
          </dt>
          <dd>{reportEntity.reportTitle}</dd>
          <dt>
            <span id="reportDate">
              <Translate contentKey="reportserviceApp.reportserviceReport.reportDate">Report Date</Translate>
            </span>
          </dt>
          <dd>{reportEntity.reportDate ? <TextFormat value={reportEntity.reportDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="authorId">
              <Translate contentKey="reportserviceApp.reportserviceReport.authorId">Author Id</Translate>
            </span>
          </dt>
          <dd>{reportEntity.authorId}</dd>
          <dt>
            <span id="summary">
              <Translate contentKey="reportserviceApp.reportserviceReport.summary">Summary</Translate>
            </span>
          </dt>
          <dd>{reportEntity.summary}</dd>
          <dt>
            <span id="exteriorState">
              <Translate contentKey="reportserviceApp.reportserviceReport.exteriorState">Exterior State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.exteriorState}</dd>
          <dt>
            <span id="constructionYear">
              <Translate contentKey="reportserviceApp.reportserviceReport.constructionYear">Construction Year</Translate>
            </span>
          </dt>
          <dd>{reportEntity.constructionYear}</dd>
          <dt>
            <span id="maintenanceState">
              <Translate contentKey="reportserviceApp.reportserviceReport.maintenanceState">Maintenance State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.maintenanceState}</dd>
          <dt>
            <span id="parkingFacility">
              <Translate contentKey="reportserviceApp.reportserviceReport.parkingFacility">Parking Facility</Translate>
            </span>
          </dt>
          <dd>{reportEntity.parkingFacility}</dd>
          <dt>
            <span id="parkingCount">
              <Translate contentKey="reportserviceApp.reportserviceReport.parkingCount">Parking Count</Translate>
            </span>
          </dt>
          <dd>{reportEntity.parkingCount}</dd>
          <dt>
            <span id="elevatorState">
              <Translate contentKey="reportserviceApp.reportserviceReport.elevatorState">Elevator State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.elevatorState}</dd>
          <dt>
            <span id="noiseState">
              <Translate contentKey="reportserviceApp.reportserviceReport.noiseState">Noise State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.noiseState}</dd>
          <dt>
            <span id="homepadState">
              <Translate contentKey="reportserviceApp.reportserviceReport.homepadState">Homepad State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.homepadState}</dd>
          <dt>
            <span id="cctvYn">
              <Translate contentKey="reportserviceApp.reportserviceReport.cctvYn">Cctv Yn</Translate>
            </span>
          </dt>
          <dd>{reportEntity.cctvYn}</dd>
          <dt>
            <span id="fireSafetyState">
              <Translate contentKey="reportserviceApp.reportserviceReport.fireSafetyState">Fire Safety State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.fireSafetyState}</dd>
          <dt>
            <span id="doorSecurityState">
              <Translate contentKey="reportserviceApp.reportserviceReport.doorSecurityState">Door Security State</Translate>
            </span>
          </dt>
          <dd>{reportEntity.doorSecurityState}</dd>
          <dt>
            <span id="maintenanceFee">
              <Translate contentKey="reportserviceApp.reportserviceReport.maintenanceFee">Maintenance Fee</Translate>
            </span>
          </dt>
          <dd>{reportEntity.maintenanceFee}</dd>
          <dt>
            <span id="redevelopmentYn">
              <Translate contentKey="reportserviceApp.reportserviceReport.redevelopmentYn">Redevelopment Yn</Translate>
            </span>
          </dt>
          <dd>{reportEntity.redevelopmentYn}</dd>
          <dt>
            <span id="rentalDemand">
              <Translate contentKey="reportserviceApp.reportserviceReport.rentalDemand">Rental Demand</Translate>
            </span>
          </dt>
          <dd>{reportEntity.rentalDemand}</dd>
          <dt>
            <span id="communityRules">
              <Translate contentKey="reportserviceApp.reportserviceReport.communityRules">Community Rules</Translate>
            </span>
          </dt>
          <dd>{reportEntity.communityRules}</dd>
          <dt>
            <span id="complexId">
              <Translate contentKey="reportserviceApp.reportserviceReport.complexId">Complex Id</Translate>
            </span>
          </dt>
          <dd>{reportEntity.complexId}</dd>
          <dt>
            <span id="complexName">
              <Translate contentKey="reportserviceApp.reportserviceReport.complexName">Complex Name</Translate>
            </span>
          </dt>
          <dd>{reportEntity.complexName}</dd>
          <dt>
            <span id="propertyId">
              <Translate contentKey="reportserviceApp.reportserviceReport.propertyId">Property Id</Translate>
            </span>
          </dt>
          <dd>{reportEntity.propertyId}</dd>
          <dt>
            <span id="propertyName">
              <Translate contentKey="reportserviceApp.reportserviceReport.propertyName">Property Name</Translate>
            </span>
          </dt>
          <dd>{reportEntity.propertyName}</dd>
          <dt>
            <span id="createdAt">
              <Translate contentKey="reportserviceApp.reportserviceReport.createdAt">Created At</Translate>
            </span>
          </dt>
          <dd>{reportEntity.createdAt ? <TextFormat value={reportEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="updatedAt">
              <Translate contentKey="reportserviceApp.reportserviceReport.updatedAt">Updated At</Translate>
            </span>
          </dt>
          <dd>{reportEntity.updatedAt ? <TextFormat value={reportEntity.updatedAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="reportserviceApp.reportserviceReport.author">Author</Translate>
          </dt>
          <dd>{reportEntity.author ? reportEntity.author.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/reportservice/report" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/reportservice/report/${reportEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ReportDetail;
