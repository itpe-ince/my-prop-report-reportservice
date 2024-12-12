import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getAuthors } from 'app/entities/reportservice/author/author.reducer';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';
import { createEntity, getEntity, reset, updateEntity } from './report.reducer';

export const ReportUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const authors = useAppSelector(state => state.reportservice.author.entities);
  const reportEntity = useAppSelector(state => state.reportservice.report.entity);
  const loading = useAppSelector(state => state.reportservice.report.loading);
  const updating = useAppSelector(state => state.reportservice.report.updating);
  const updateSuccess = useAppSelector(state => state.reportservice.report.updateSuccess);
  const qualityStateTypeValues = Object.keys(QualityStateType);

  const handleClose = () => {
    navigate(`/reportservice/report${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getAuthors({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.reportDate = convertDateTimeToServer(values.reportDate);
    if (values.authorId !== undefined && typeof values.authorId !== 'number') {
      values.authorId = Number(values.authorId);
    }
    if (values.constructionYear !== undefined && typeof values.constructionYear !== 'number') {
      values.constructionYear = Number(values.constructionYear);
    }
    if (values.parkingCount !== undefined && typeof values.parkingCount !== 'number') {
      values.parkingCount = Number(values.parkingCount);
    }
    if (values.maintenanceFee !== undefined && typeof values.maintenanceFee !== 'number') {
      values.maintenanceFee = Number(values.maintenanceFee);
    }
    if (values.complexId !== undefined && typeof values.complexId !== 'number') {
      values.complexId = Number(values.complexId);
    }
    if (values.propertyId !== undefined && typeof values.propertyId !== 'number') {
      values.propertyId = Number(values.propertyId);
    }
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    const entity = {
      ...reportEntity,
      ...values,
      author: authors.find(it => it.id.toString() === values.author?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          reportDate: displayDefaultDateTime(),
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
        }
      : {
          exteriorState: 'HIGH',
          maintenanceState: 'HIGH',
          elevatorState: 'HIGH',
          noiseState: 'HIGH',
          homepadState: 'HIGH',
          fireSafetyState: 'HIGH',
          doorSecurityState: 'HIGH',
          ...reportEntity,
          reportDate: convertDateTimeFromServer(reportEntity.reportDate),
          createdAt: convertDateTimeFromServer(reportEntity.createdAt),
          updatedAt: convertDateTimeFromServer(reportEntity.updatedAt),
          author: reportEntity?.author?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="reportserviceApp.reportserviceReport.home.createOrEditLabel" data-cy="ReportCreateUpdateHeading">
            <Translate contentKey="reportserviceApp.reportserviceReport.home.createOrEditLabel">Create or edit a Report</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="report-id"
                  label={translate('reportserviceApp.reportserviceReport.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.reportTitle')}
                id="report-reportTitle"
                name="reportTitle"
                data-cy="reportTitle"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.reportDate')}
                id="report-reportDate"
                name="reportDate"
                data-cy="reportDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.authorId')}
                id="report-authorId"
                name="authorId"
                data-cy="authorId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.summary')}
                id="report-summary"
                name="summary"
                data-cy="summary"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.exteriorState')}
                id="report-exteriorState"
                name="exteriorState"
                data-cy="exteriorState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.constructionYear')}
                id="report-constructionYear"
                name="constructionYear"
                data-cy="constructionYear"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.maintenanceState')}
                id="report-maintenanceState"
                name="maintenanceState"
                data-cy="maintenanceState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.parkingFacility')}
                id="report-parkingFacility"
                name="parkingFacility"
                data-cy="parkingFacility"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.parkingCount')}
                id="report-parkingCount"
                name="parkingCount"
                data-cy="parkingCount"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.elevatorState')}
                id="report-elevatorState"
                name="elevatorState"
                data-cy="elevatorState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.noiseState')}
                id="report-noiseState"
                name="noiseState"
                data-cy="noiseState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.homepadState')}
                id="report-homepadState"
                name="homepadState"
                data-cy="homepadState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.cctvYn')}
                id="report-cctvYn"
                name="cctvYn"
                data-cy="cctvYn"
                type="text"
                validate={{
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 1, message: translate('entity.validation.maxlength', { max: 1 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.fireSafetyState')}
                id="report-fireSafetyState"
                name="fireSafetyState"
                data-cy="fireSafetyState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.doorSecurityState')}
                id="report-doorSecurityState"
                name="doorSecurityState"
                data-cy="doorSecurityState"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.maintenanceFee')}
                id="report-maintenanceFee"
                name="maintenanceFee"
                data-cy="maintenanceFee"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.redevelopmentYn')}
                id="report-redevelopmentYn"
                name="redevelopmentYn"
                data-cy="redevelopmentYn"
                type="text"
                validate={{
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 1, message: translate('entity.validation.maxlength', { max: 1 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.rentalDemand')}
                id="report-rentalDemand"
                name="rentalDemand"
                data-cy="rentalDemand"
                type="text"
                validate={{
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.communityRules')}
                id="report-communityRules"
                name="communityRules"
                data-cy="communityRules"
                type="text"
                validate={{
                  maxLength: { value: 2000, message: translate('entity.validation.maxlength', { max: 2000 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.complexId')}
                id="report-complexId"
                name="complexId"
                data-cy="complexId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.complexName')}
                id="report-complexName"
                name="complexName"
                data-cy="complexName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.propertyId')}
                id="report-propertyId"
                name="propertyId"
                data-cy="propertyId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.propertyName')}
                id="report-propertyName"
                name="propertyName"
                data-cy="propertyName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.createdAt')}
                id="report-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceReport.updatedAt')}
                id="report-updatedAt"
                name="updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="report-author"
                name="author"
                data-cy="author"
                label={translate('reportserviceApp.reportserviceReport.author')}
                type="select"
              >
                <option value="" key="0" />
                {authors
                  ? authors.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reportservice/report" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ReportUpdate;
