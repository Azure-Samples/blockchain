import { colorSwatch, oneMin } from './constants';

export const randomColor = (hashedNum) => {
  const bgColor = colorSwatch[hashedNum % colorSwatch.length];
  return bgColor;
};

export const hashAlphaNum = (alphaNum) => {
  let hash = 0;
  if (alphaNum.length === 0) return hash;
  for (let i = 0; i < alphaNum.length; i += 1) {
    const char = alphaNum.charCodeAt(i);
    hash = ((hash << 5) - hash) + char; // eslint-disable-line no-bitwise
    hash &= hash; // eslint-disable-line no-bitwise
  }
  return Math.abs(hash);
};

export const makeDictionary = data => data.reduce((acc, current) => {
  const Id = current.id;
  return {
    ...acc,
    [Id]: current,
  };
}, {});

export const isTimeDiffGreater = (timestamp, threshold) => {
  const dateCreated = new Date(`${timestamp}Z`);
  const currentTime = new Date();
  const timeDiff = currentTime - dateCreated;
  return timeDiff > threshold;
};

export const buildProvisioningStatusObj = (currentItem, currentItemKey) => {
  let result = {};
  let status = 2;
  if (currentItem[currentItemKey] !== 2) {
    const moreThanOneMin =
      isTimeDiffGreater(currentItem.timestamp, oneMin);
    if (moreThanOneMin) {
      status = 3;
    } else {
      status = currentItem[currentItemKey];
    }
  }

  result = {
    ...result,
    State: {
      value: status,
      type: 'state',
    },
  };
  return result;
};

export const buildWorkflowStateObj = (propertyObj, columnHeaders, contractsStates) =>
  contractsStates.reduce((result, stateProperty) => {
    const header = columnHeaders[propertyObj.workflowPropertyId][propertyObj.workflowPropertyId];

    if (stateProperty.value === Number(propertyObj.value)) {
      return {
        ...result,
        [header]: {
          value: stateProperty.displayName,
          type: columnHeaders[propertyObj.workflowPropertyId].type.name,
          percentComplete: stateProperty.percentComplete,
          style: stateProperty.style,
        },
      };
    }

    return result;
  }, {});

export const buildWorkflowPropertiesObj = (currentItem, columnHeaders, contractsStates) =>
  currentItem.contractProperties.reduce((result, propertyObj) => {
    const hasWorkflowPropertyId = Object.prototype
      .hasOwnProperty.call(columnHeaders, propertyObj.workflowPropertyId);
    if (hasWorkflowPropertyId) {
      if (columnHeaders[propertyObj.workflowPropertyId][propertyObj.workflowPropertyId] === 'State') {
        return buildWorkflowStateObj(
          propertyObj,
          columnHeaders, contractsStates,
        );
      }
      const header = columnHeaders[propertyObj.workflowPropertyId][propertyObj.workflowPropertyId];
      let { value } = propertyObj;
      if (propertyObj.value === '0x0000000000000000000000000000000000000000') {
        value = '-';
      }
      return {
        ...result,
        [header]: {
          value,
          type: columnHeaders[propertyObj.workflowPropertyId].type.name,
        },
      };
    }
    return result;
  }, {});

export const formatData = (data, columnHeaders, contractsStates) =>
  data.map(currentItem =>
    Object.keys(currentItem).reduce(
      (result, currentItemKey) => {
        if (currentItemKey === 'provisioningStatus') {
          const provisioningObj =
          buildProvisioningStatusObj(currentItem, currentItemKey);
          return {
            ...result,
            ...provisioningObj,
          };
        }
        if (currentItemKey === 'contractProperties') {
          const workflowPropertiesformattedObj = buildWorkflowPropertiesObj(
            currentItem,
            columnHeaders,
            contractsStates,
            currentItemKey,
          );
          return {
            ...result,
            ...workflowPropertiesformattedObj,
          };
        }
        // if (currentItemKey === 'ledgerIdentifier' && currentItem[currentItemKey] !== null) {
        //   return {
        //     ...result,
        //     contractAddress: currentItem.ledgerIdentifier.toString(),
        //   };
        // }
        return result;
      },
      {
        deployedByUserId: currentItem.deployedByUserId.toString(),
        timestamp: currentItem.timestamp.toString(),
        id: currentItem.id.toString(),
        // contractAddress: '-',
      },
    ));

export const getColumnHeaders = columns =>
  columns
    .map((column) => {
      if (!column.id) {
        return {
          [column.name]: column.displayName,
          type: {
            name: 'static',
          },
        };
      }
      return {
        [column.id]: column.displayName,
        type: {
          name: column.type.name,
        },
      };
    })
    .reduce((result, curr) => {
      const header = Object.keys(curr)[0];
      return {
        ...result,
        [header]: curr,
      };
    }, {});

export const getFunctionsParameters = (id, functionDictionary) =>
  Object.keys(functionDictionary[id].parameters).reduce((result, prop) => {
    const header = functionDictionary[id].parameters[prop].displayName;
    return {
      ...result,
      [header]: functionDictionary[id].parameters[prop],
    };
  }, {});

export const buildFunctionName = (activity, userFunctionsDictionary) => {
  let functionDisplayName = userFunctionsDictionary[activity.workflowFunctionId].displayName;
  if (userFunctionsDictionary[activity.workflowFunctionId].displayName === '') {
    functionDisplayName = 'Create';
  }
  return functionDisplayName;
};

export const convertHexCodeToUser = (hexValue, users) =>
  users.reduce((acc, user) => user.userChainMappings.reduce((userResult, userProps) => {
    if (userProps.chainIdentifier === hexValue) {
      return { ...user };
    }
    return { ...acc };
  }, {}), {});

export const formatDetailsDashboardData = detailsDashboard =>
  Object.keys(detailsDashboard).map(item => ({
    name: item,
    value: detailsDashboard[item],
  }));

export const convertIdToUser = (id, users) =>
  users.reduce((acc, user) => {
    if (user.userID === Number(id)) {
      return { ...user };
    }
    return { ...acc };
  }, {});

export const listToMatrix = (list, elementsPerSubArray) => {
  const matrix = [];
  let i;
  let k;
  for (i = 0, k = -1; i < list.length; i += 1) {
    if (i % elementsPerSubArray === 0) {
      k += 1;
      matrix[k] = [];
    }
    matrix[k].push(list[i]);
  }
  return matrix;
};
