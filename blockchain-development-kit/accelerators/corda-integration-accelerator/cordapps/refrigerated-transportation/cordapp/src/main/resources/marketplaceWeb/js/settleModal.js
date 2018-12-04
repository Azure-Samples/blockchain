"use strict";

// Similar to the IOU creation modal - see createIOUModal.js for comments.
angular.module('demoAppModule').controller('SettleModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, id) {
    const settleModal = this;

    settleModal.id = id;
    settleModal.form = {};
    settleModal.formError = false;

    settleModal.settle = () => {
        if (invalidFormInput()) {
            settleModal.formError = true;
        } else {
            settleModal.formError = false;

            const id = settleModal.id;
            const amount = settleModal.form.amount;
            const currency = settleModal.form.currency;

            $uibModalInstance.close();

            const issueIOUEndpoint =
                apiBaseURL +
                `settle-iou?id=${id}&amount=${amount}&currency=${currency}`;

            $http.get(issueIOUEndpoint).then(
                (result) => settleModal.displayMessage(result),
                (result) => settleModal.displayMessage(result)
            );
        }
    };

    settleModal.displayMessage = (message) => {
        const settleMsgModal = $uibModal.open({
            templateUrl: 'settleMsgModal.html',
            controller: 'settleMsgModalCtrl',
            controllerAs: 'settleMsgModal',
            resolve: {
                message: () => message
            }
        });

        settleMsgModal.result.then(() => {}, () => {});
    };

    settleModal.cancel = () => $uibModalInstance.dismiss();

    function invalidFormInput() {
        return isNaN(settleModal.form.amount) || (settleModal.form.currency.length != 3);
    }
});

angular.module('demoAppModule').controller('settleMsgModalCtrl', function($uibModalInstance, message) {
    const settleMsgModal = this;
    settleMsgModal.message = message.data;
});