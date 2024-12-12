import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getReports } from 'app/entities/reportservice/report/report.reducer';
import { QualityStateType } from 'app/shared/model/enumerations/quality-state-type.model';
import { createEntity, getEntity, reset, updateEntity } from './entrance.reducer';

export const EntranceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const reports = useAppSelector(state => state.reportservice.report.entities);
  const entranceEntity = useAppSelector(state => state.reportservice.entrance.entity);
  const loading = useAppSelector(state => state.reportservice.entrance.loading);
  const updating = useAppSelector(state => state.reportservice.entrance.updating);
  const updateSuccess = useAppSelector(state => state.reportservice.entrance.updateSuccess);
  const qualityStateTypeValues = Object.keys(QualityStateType);

  const handleClose = () => {
    navigate(`/reportservice/entrance${location.search}`);
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
    if (values.entranceSize !== undefined && typeof values.entranceSize !== 'number') {
      values.entranceSize = Number(values.entranceSize);
    }
    if (values.shoeRackSize !== undefined && typeof values.shoeRackSize !== 'number') {
      values.shoeRackSize = Number(values.shoeRackSize);
    }

    const entity = {
      ...entranceEntity,
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
          ...entranceEntity,
          report: entranceEntity?.report?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="reportserviceApp.reportserviceEntrance.home.createOrEditLabel" data-cy="EntranceCreateUpdateHeading">
            <Translate contentKey="reportserviceApp.reportserviceEntrance.home.createOrEditLabel">Create or edit a Entrance</Translate>
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
                  id="entrance-id"
                  label={translate('reportserviceApp.reportserviceEntrance.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('reportserviceApp.reportserviceEntrance.reportId')}
                id="entrance-reportId"
                name="reportId"
                data-cy="reportId"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceEntrance.entranceName')}
                id="entrance-entranceName"
                name="entranceName"
                data-cy="entranceName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceEntrance.condtionLevel')}
                id="entrance-condtionLevel"
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
                label={translate('reportserviceApp.reportserviceEntrance.entranceSize')}
                id="entrance-entranceSize"
                name="entranceSize"
                data-cy="entranceSize"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceEntrance.shoeRackSize')}
                id="entrance-shoeRackSize"
                name="shoeRackSize"
                data-cy="shoeRackSize"
                type="text"
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceEntrance.pantryPresence')}
                id="entrance-pantryPresence"
                name="pantryPresence"
                data-cy="pantryPresence"
                type="text"
                validate={{
                  minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                  maxLength: { value: 1, message: translate('entity.validation.maxlength', { max: 1 }) },
                }}
              />
              <ValidatedField
                label={translate('reportserviceApp.reportserviceEntrance.remarks')}
                id="entrance-remarks"
                name="remarks"
                data-cy="remarks"
                type="text"
              />
              <ValidatedField
                id="entrance-report"
                name="report"
                data-cy="report"
                label={translate('reportserviceApp.reportserviceEntrance.report')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/reportservice/entrance" replace color="info">
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

export default EntranceUpdate;
