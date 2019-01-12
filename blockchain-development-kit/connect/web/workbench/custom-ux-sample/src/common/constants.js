const defaultUserColor = '#7c7c7c';

const oneMin = 60 * 1000;

const colorSwatch = [
  '#750B1C',
  '#A4262C',
  '#D13438',
  '#CA5010',
  '#986F0B',
  '#498205',
  '#005E50',
  '#038387',
  '#0078D7',
  '#004E8C',
  '#4F6BED',
  '#373277',
  '#8764B8',
  '#881798',
  '#C239B3',
  '#E3008C',
  '#603E30',
  '#567C73',
  '#69797E',
  '#747574',
];

const staticIdColumn = {
  id: '',
  name: 'id',
  displayName: 'Id',
  type: {
    name: 'static',
  },
};

const staticModifiedColumns = [
  {
    modifiedBy: '',
    name: 'deployedByUserId',
    displayName: 'Modified By',
    type: {
      name: 'static',
    },
  },
  {
    modifiedDate: '',
    name: 'timestamp',
    displayName: 'Modified',
    type: {
      name: 'static',
    },
  },
];

const primitiveValues = ['money', 'state', 'string', 'int', 'bool'];

export {
  staticModifiedColumns,
  staticIdColumn,
  defaultUserColor,
  colorSwatch,
  oneMin,
  primitiveValues,
};
