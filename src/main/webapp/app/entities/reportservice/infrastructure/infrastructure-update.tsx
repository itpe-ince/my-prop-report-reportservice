import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getReports } from 'app/entities/reportservice/report/report.reducer';
import { InfraType } from 'app/shared/model/enumerations/infra-type.model';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';
import { createEntity, getEntity, reset, updateEntity } from './infrastructure.reducer';

export const InfrastructureUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const reports = useAppSelector(state => state.reportservice.report.entities);
  const infrastructureEntity = useAppSelector(state => state.reportservice.infrastructure.entity);
  const loading = useAppSelector(state => state.reportservice.infrastructure.loading);
  const updating = useAppSelector(state => state.reportservice.infrastructure.updating);
  const updateSuccess = useAppSelector(state => state.reportservice.infrastructure.updateSuccess);
  const infraTypeValues = Object.keys(InfraType);
  const qualityStateTypeValues = Object.keys(QualityStateType);

  const handleClose = () => {
    navigate(`/reportservice/infrastructure${location.search}`);
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
    if (values.infraDistance !== undefined && typeof values.infraDistance !== 'number') {
      values.infraDistance = Number(values.infraDistance);
    }

    const entity = {
      ...infrastructureEntity,
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
          infraType: 'SCHOOL',
          conditionLevel: 'HIGH',
          infraDistanceUnit: 'HIGH',
          ...infrastructureEntity,
          report: infrastructureEntity?.report?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="reportserviceApp.reportserviceInfrastructure.home.createOrEditLabel" data-cy="InfrastructureCreateUpdateHeading">
            <Translate contentKey="reportserviceApp.reportserviceInfrastructure.home.createOrEditLabel">
              Create or edit a Infrastructure
            </Translate>
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
                  id="infrastructure-id"
                  label={translate('reportserviceApp.reportserviceInfrastructure.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.reportId')}
                id="infrastructure-reportId"
                name="reportId"
                data-cy="reportId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.infraType')}
                id="infrastructure-infraType"
                name="infraType"
                data-cy="infraType"
                type="select"
              >
                {infraTypeValues.map(infraType => (
                  <option value={infraType} key={infraType}>
                    {translate(`reportserviceApp.InfraType.${infraType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.infraName')}
                id="infrastructure-infraName"
                name="infraName"
                data-cy="infraName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 200, message: translate('entity.validation.maxlength', { max: 200 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.conditionLevel')}
                id="infrastructure-conditionLevel"
                name="conditionLevel"
                data-cy="conditionLevel"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.infraDistance')}
                id="infrastructure-infraDistance"
                name="infraDistance"
                data-cy="infraDistance"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.infraDistanceUnit')}
                id="infrastructure-infraDistanceUnit"
                name="infraDistanceUnit"
                data-cy="infraDistanceUnit"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceInfrastructure.remarks')}
                id="infrastructure-remarks"
                name="remarks"
                data-cy="remarks"
                type="text"
              />
              <ValidatedField
                id="infrastructure-report"
                name="report"
                data-cy="report"
                label={translate('reportserviceApp.reportserviceInfrastructure.report')}
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
              <Button
                tag={Link}
                id="cancel-save"
                data-cy="entityCreateCancelButton"
                to="/reportservice/infrastructure"
                replace
                color="info"
              >
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

export default InfrastructureUpdate;
