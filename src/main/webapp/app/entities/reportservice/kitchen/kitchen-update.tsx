import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getReports } from 'app/entities/reportservice/report/report.reducer';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';
import { createEntity, getEntity, reset, updateEntity } from './kitchen.reducer';

export const KitchenUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const reports = useAppSelector(state => state.reportservice.report.entities);
  const kitchenEntity = useAppSelector(state => state.reportservice.kitchen.entity);
  const loading = useAppSelector(state => state.reportservice.kitchen.loading);
  const updating = useAppSelector(state => state.reportservice.kitchen.updating);
  const updateSuccess = useAppSelector(state => state.reportservice.kitchen.updateSuccess);
  const qualityStateTypeValues = Object.keys(QualityStateType);

  const handleClose = () => {
    navigate(`/reportservice/kitchen${location.search}`);
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

    const entity = {
      ...kitchenEntity,
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
          conditionLevel: 'HIGH',
          sinkCondition: 'HIGH',
          ...kitchenEntity,
          report: kitchenEntity?.report?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="reportserviceApp.reportserviceKitchen.home.createOrEditLabel" data-cy="KitchenCreateUpdateHeading">
            <Translate contentKey="reportserviceApp.reportserviceKitchen.home.createOrEditLabel">Create or edit a Kitchen</Translate>
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
                  id="kitchen-id"
                  label={translate('reportserviceApp.reportserviceKitchen.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.reportId')}
                id="kitchen-reportId"
                name="reportId"
                data-cy="reportId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.kitchenName')}
                id="kitchen-kitchenName"
                name="kitchenName"
                data-cy="kitchenName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.conditionLevel')}
                id="kitchen-conditionLevel"
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
                label={translate('reportserviceApp.reportserviceKitchen.builtInCabinet')}
                id="kitchen-builtInCabinet"
                name="builtInCabinet"
                data-cy="builtInCabinet"
                type="text"
                validate={{
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 1, message: translate('entity.validation.maxlength', { max: 1 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.sinkCondition')}
                id="kitchen-sinkCondition"
                name="sinkCondition"
                data-cy="sinkCondition"
                type="select"
              >
                {qualityStateTypeValues.map(qualityStateType => (
                  <option value={qualityStateType} key={qualityStateType}>
                    {translate(`reportserviceApp.QualityStateType.${qualityStateType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.ventilationSystem')}
                id="kitchen-ventilationSystem"
                name="ventilationSystem"
                data-cy="ventilationSystem"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.applianceProvision')}
                id="kitchen-applianceProvision"
                name="applianceProvision"
                data-cy="applianceProvision"
                type="text"
                validate={{
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceKitchen.remarks')}
                id="kitchen-remarks"
                name="remarks"
                data-cy="remarks"
                type="text"
              />
              <ValidatedField
                id="kitchen-report"
                name="report"
                data-cy="report"
                label={translate('reportserviceApp.reportserviceKitchen.report')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reportservice/kitchen" replace color="info">
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

export default KitchenUpdate;
