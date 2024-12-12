import report from 'app/entities/reportservice/report/report.reducer';
import infrastructure from 'app/entities/reportservice/infrastructure/infrastructure.reducer';
import envFactor from 'app/entities/reportservice/env-factor/env-factor.reducer';
import livingRoom from 'app/entities/reportservice/living-room/living-room.reducer';
import bedroom from 'app/entities/reportservice/bedroom/bedroom.reducer';
import kitchen from 'app/entities/reportservice/kitchen/kitchen.reducer';
import bathroom from 'app/entities/reportservice/bathroom/bathroom.reducer';
import entrance from 'app/entities/reportservice/entrance/entrance.reducer';
import author from 'app/entities/reportservice/author/author.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  report,
  infrastructure,
  envFactor,
  livingRoom,
  bedroom,
  kitchen,
  bathroom,
  entrance,
  author,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
