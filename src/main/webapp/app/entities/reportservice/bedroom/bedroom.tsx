import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './bedroom.reducer';

export const Bedroom = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const bedroomList = useAppSelector(state => state.reportservice.bedroom.entities);
  const loading = useAppSelector(state => state.reportservice.bedroom.loading);
  const totalItems = useAppSelector(state => state.reportservice.bedroom.totalItems);

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
      <h2 id="bedroom-heading" data-cy="BedroomHeading">
        <Translate contentKey="reportserviceApp.reportserviceBedroom.home.title">Bedrooms</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="reportserviceApp.reportserviceBedroom.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/reportservice/bedroom/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="reportserviceApp.reportserviceBedroom.home.createLabel">Create new Bedroom</Translate>
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
                  placeholder={translate('reportserviceApp.reportserviceBedroom.home.search')}
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
        {bedroomList && bedroomList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('reportId')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.reportId">Report Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportId')} />
                </th>
                <th className="hand" onClick={sort('bedroomName')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.bedroomName">Bedroom Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bedroomName')} />
                </th>
                <th className="hand" onClick={sort('conditionLevel')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.conditionLevel">Condition Level</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('conditionLevel')} />
                </th>
                <th className="hand" onClick={sort('roomSize')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.roomSize">Room Size</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('roomSize')} />
                </th>
                <th className="hand" onClick={sort('closetYn')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.closetYn">Closet Yn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('closetYn')} />
                </th>
                <th className="hand" onClick={sort('acYn')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.acYn">Ac Yn</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('acYn')} />
                </th>
                <th className="hand" onClick={sort('windowLocation')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.windowLocation">Window Location</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('windowLocation')} />
                </th>
                <th className="hand" onClick={sort('windowSize')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.windowSize">Window Size</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('windowSize')} />
                </th>
                <th className="hand" onClick={sort('remarks')}>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.remarks">Remarks</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remarks')} />
                </th>
                <th>
                  <Translate contentKey="reportserviceApp.reportserviceBedroom.report">Report</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {bedroomList.map((bedroom, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/reportservice/bedroom/${bedroom.id}`} color="link" size="sm">
                      {bedroom.id}
                    </Button>
                  </td>
                  <td>{bedroom.reportId}</td>
                  <td>{bedroom.bedroomName}</td>
                  <td>
                    <Translate contentKey={`reportserviceApp.QualityStateType.${bedroom.conditionLevel}`} />
                  </td>
                  <td>{bedroom.roomSize}</td>
                  <td>{bedroom.closetYn}</td>
                  <td>{bedroom.acYn}</td>
                  <td>{bedroom.windowLocation}</td>
                  <td>{bedroom.windowSize}</td>
                  <td>{bedroom.remarks}</td>
                  <td>{bedroom.report ? <Link to={`/reportservice/report/${bedroom.report.id}`}>{bedroom.report.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/reportservice/bedroom/${bedroom.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/reportservice/bedroom/${bedroom.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/reportservice/bedroom/${bedroom.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="reportserviceApp.reportserviceBedroom.home.notFound">No Bedrooms found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={bedroomList && bedroomList.length > 0 ? '' : 'd-none'}>
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

export default Bedroom;