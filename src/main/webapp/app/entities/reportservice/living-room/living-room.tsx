import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './living-room.reducer';

export const LivingRoom = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const livingRoomList = useAppSelector(state => state.reportservice.livingRoom.entities);
  const loading = useAppSelector(state => state.reportservice.livingRoom.loading);
  const totalItems = useAppSelector(state => state.reportservice.livingRoom.totalItems);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
  };

  const startSearching = e => {
    if (search) {
      setPaginationState({
        ...paginationState,
        activePage: 1,
      });
      dispatch(
        searchEntities({
          query: search,
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort, search]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="living-room-heading" data-cy="LivingRoomHeading">
        <Translate contentKey="reportserviceApp.reportserviceLivingRoom.home.title">Living Rooms</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="reportserviceApp.reportserviceLivingRoom.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/reportservice/living-room/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="reportserviceApp.reportserviceLivingRoom.home.createLabel">Create new Living Room</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('reportserviceApp.reportserviceLivingRoom.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {livingRoomList && livingRoomList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('reportId')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.reportId">Report Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportId')} />
                </th>
                <th className="hand" onClick={sort('livingRoomName')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.livingRoomName">Living Room Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('livingRoomName')} />
                </th>
                <th className="hand" onClick={sort('conditionLevel')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.conditionLevel">Condition Level</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('conditionLevel')} />
                </th>
                <th className="hand" onClick={sort('roomSize')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.roomSize">Room Size</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('roomSize')} />
                </th>
                <th className="hand" onClick={sort('wallState')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.wallState">Wall State</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('wallState')} />
                </th>
                <th className="hand" onClick={sort('floorMaterial')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.floorMaterial">Floor Material</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('floorMaterial')} />
                </th>
                <th className="hand" onClick={sort('sunlight')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.sunlight">Sunlight</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('sunlight')} />
                </th>
                <th className="hand" onClick={sort('remarks')}>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.remarks">Remarks</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remarks')} />
                </th>
                <th>
                  <Translate contentKey="reportserviceApp.reportserviceLivingRoom.report">Report</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {livingRoomList.map((livingRoom, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/reportservice/living-room/${livingRoom.id}`} color="link" size="sm">
                      {livingRoom.id}
                    </Button>
                  </td>
                  <td>{livingRoom.reportId}</td>
                  <td>{livingRoom.livingRoomName}</td>
                  <td>
                    <Translate contentKey={`reportserviceApp.QualityStateType.${livingRoom.conditionLevel}`} />
                  </td>
                  <td>{livingRoom.roomSize}</td>
                  <td>
                    <Translate contentKey={`reportserviceApp.QualityStateType.${livingRoom.wallState}`} />
                  </td>
                  <td>{livingRoom.floorMaterial}</td>
                  <td>{livingRoom.sunlight}</td>
                  <td>{livingRoom.remarks}</td>
                  <td>
                    {livingRoom.report ? <Link to={`/reportservice/report/${livingRoom.report.id}`}>{livingRoom.report.id}</Link> : ''}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/reportservice/living-room/${livingRoom.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/reportservice/living-room/${livingRoom.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/reportservice/living-room/${livingRoom.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="reportserviceApp.reportserviceLivingRoom.home.notFound">No Living Rooms found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={livingRoomList && livingRoomList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default LivingRoom;
