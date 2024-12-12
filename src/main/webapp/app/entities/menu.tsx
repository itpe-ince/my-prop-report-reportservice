import React, { useEffect } from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { addTranslationSourcePrefix } from 'app/shared/reducers/locale';
import { useAppDispatch, useAppSelector } from 'app/config/store';

const EntitiesMenu = () => {
  const lastChange = useAppSelector(state => state.locale.lastChange);
  const dispatch = useAppDispatch();
  useEffect(() => {
    dispatch(addTranslationSourcePrefix('services/reportservice/'));
  }, [lastChange]);

  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/reportservice/report">
        <Translate contentKey="global.menu.entities.reportserviceReport" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/infrastructure">
        <Translate contentKey="global.menu.entities.reportserviceInfrastructure" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/env-factor">
        <Translate contentKey="global.menu.entities.reportserviceEnvFactor" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/living-room">
        <Translate contentKey="global.menu.entities.reportserviceLivingRoom" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/bedroom">
        <Translate contentKey="global.menu.entities.reportserviceBedroom" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/kitchen">
        <Translate contentKey="global.menu.entities.reportserviceKitchen" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/bathroom">
        <Translate contentKey="global.menu.entities.reportserviceBathroom" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/entrance">
        <Translate contentKey="global.menu.entities.reportserviceEntrance" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/reportservice/author">
        <Translate contentKey="global.menu.entities.reportserviceAuthor" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
