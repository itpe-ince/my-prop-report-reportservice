import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, Translate, getPaginationState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './bathroom.reducer';

export const Bathroom = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const bathroomList = useAppSelector(state => state.reportservice.bathroom.entities);
  const loading = useAppSelector(state => state.reportservice.bathroom.loading);
  const totalItems = useAppSelector(state => state.reportservice.bathroom.totalItems);

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
      <h2 id="bathroom-heading" data-cy="BathroomHeading">
        <Translate contentKey="reportserviceApp.reportserviceBathroom.home.title">Bathrooms</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="reportserviceApp.reportserviceBathroom.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/reportservice/bathroom/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="reportserviceApp.reportserviceBathroom.home.createLabel">Create new Bathroom</Translate>
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
                  placeholder={translate('reportserviceApp.reportserviceBathroom.home.search')}
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
        {bathroomList && bathroomList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.id">Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('reportId')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.reportId">Report Id</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reportId')} />
                </th>
                <th className="hand" onClick={sort('bathroomName')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.bathroomName">Bathroom Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bathroomName')} />
                </th>
                <th className="hand" onClick={sort('condtionLevel')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.condtionLevel">Condtion Level</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('condtionLevel')} />
                </th>
                <th className="hand" onClick={sort('bathroomSize')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.bathroomSize">Bathroom Size</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bathroomSize')} />
                </th>
                <th className="hand" onClick={sort('waterPressure')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.waterPressure">Water Pressure</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('waterPressure')} />
                </th>
                <th className="hand" onClick={sort('showerBoothPresence')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.showerBoothPresence">Shower Booth Presence</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('showerBoothPresence')} />
                </th>
                <th className="hand" onClick={sort('bathtubPresence')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.bathtubPresence">Bathtub Presence</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('bathtubPresence')} />
                </th>
                <th className="hand" onClick={sort('floorAndCeiling')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.floorAndCeiling">Floor And Ceiling</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('floorAndCeiling')} />
                </th>
                <th className="hand" onClick={sort('remarks')}>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.remarks">Remarks</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('remarks')} />
                </th>
                <th>
                  <Translate contentKey="reportserviceApp.reportserviceBathroom.report">Report</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {bathroomList.map((bathroom, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/reportservice/bathroom/${bathroom.id}`} color="link" size="sm">
                      {bathroom.id}
                    </Button>
                  </td>
                  <td>{bathroom.reportId}</td>
                  <td>{bathroom.bathroomName}</td>
                  <td>
                    <Translate contentKey={`reportserviceApp.QualityStateType.${bathroom.condtionLevel}`} />
                  </td>
                  <td>{bathroom.bathroomSize}</td>
                  <td>
                    <Translate contentKey={`reportserviceApp.QualityStateType.${bathroom.waterPressure}`} />
                  </td>
                  <td>{bathroom.showerBoothPresence}</td>
                  <td>{bathroom.bathtubPresence}</td>
                  <td>
                    <Translate contentKey={`reportserviceApp.QualityStateType.${bathroom.floorAndCeiling}`} />
                  </td>
                  <td>{bathroom.remarks}</td>
                  <td>{bathroom.report ? <Link to={`/reportservice/report/${bathroom.report.id}`}>{bathroom.report.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/reportservice/bathroom/${bathroom.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/reportservice/bathroom/${bathroom.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
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
                          (window.location.href = `/reportservice/bathroom/${bathroom.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
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
              <Translate contentKey="reportserviceApp.reportserviceBathroom.home.notFound">No Bathrooms found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={bathroomList && bathroomList.length > 0 ? '' : 'd-none'}>
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

export default Bathroom;
