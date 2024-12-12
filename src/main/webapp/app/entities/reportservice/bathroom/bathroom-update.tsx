import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getReports } from 'app/entities/reportservice/report/report.reducer';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';
import { createEntity, getEntity, reset, updateEntity } from './bathroom.reducer';

export const BathroomUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const reports = useAppSelector(state => state.reportservice.report.entities);
  const bathroomEntity = useAppSelector(state => state.reportservice.bathroom.entity);
  const loading = useAppSelector(state => state.reportservice.bathroom.loading);
  const updating = useAppSelector(state => state.reportservice.bathroom.updating);
  const updateSuccess = useAppSelector(state => state.reportservice.bathroom.updateSuccess);
  const qualityStateTypeValues = Object.keys(QualityStateType);

  const handleClose = () => {
    navigate(`/reportservice/bathroom${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getReports({}));
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
    if (values.reportId !== undefined && typeof values.reportId !== 'number') {
      values.reportId = Number(values.reportId);
    }
    if (values.bathroomSize !== undefined && typeof values.bathroomSize !== 'number') {
      values.bathroomSize = Number(values.bathroomSize);
    }

    const entity = {
      ...bathroomEntity,
      ...values,
      report: reports.find(it => it.id.toString() === values.report?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          condtionLevel: 'HIGH',
          waterPressure: 'HIGH',
          floorAndCeiling: 'HIGH',
          ...bathroomEntity,
          report: bathroomEntity?.report?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="reportserviceApp.reportserviceBathroom.home.createOrEditLabel" data-cy="BathroomCreateUpdateHeading">
            <Translate contentKey="reportserviceApp.reportserviceBathroom.home.createOrEditLabel">Create or edit a Bathroom</Translate>
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
                  id="bathroom-id"
                  label={translate('reportserviceApp.reportserviceBathroom.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.reportId')}
                id="bathroom-reportId"
                name="reportId"
                data-cy="reportId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.bathroomName')}
                id="bathroom-bathroomName"
                name="bathroomName"
                data-cy="bathroomName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.condtionLevel')}
                id="bathroom-condtionLevel"
                name="condtionLevel"
                data-cy="condtionLevel"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.bathroomSize')}
                id="bathroom-bathroomSize"
                name="bathroomSize"
                data-cy="bathroomSize"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.waterPressure')}
                id="bathroom-waterPressure"
                name="waterPressure"
                data-cy="waterPressure"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.showerBoothPresence')}
                id="bathroom-showerBoothPresence"
                name="showerBoothPresence"
                data-cy="showerBoothPresence"
                type="text"
                validate={{
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 1, message: translate('entity.validation.maxlength', { max: 1 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.bathtubPresence')}
                id="bathroom-bathtubPresence"
                name="bathtubPresence"
                data-cy="bathtubPresence"
                type="text"
                validate={{
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 1, message: translate('entity.validation.maxlength', { max: 1 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.floorAndCeiling')}
                id="bathroom-floorAndCeiling"
                name="floorAndCeiling"
                data-cy="floorAndCeiling"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceBathroom.remarks')}
                id="bathroom-remarks"
                name="remarks"
                data-cy="remarks"
                type="text"
              />
              <ValidatedField
                id="bathroom-report"
                name="report"
                data-cy="report"
                label={translate('reportserviceApp.reportserviceBathroom.report')}
                type="select"
              >
                <option value="" key="0" />
                {reports
                  ? reports.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reportservice/bathroom" replace color="info">
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

export default BathroomUpdate;
