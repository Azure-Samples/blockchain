"use strict";

angular.module('demoAppModule').controller('IssueKandyBagModalCtrl', function($http, $uibModalInstance, $uibModal, apiBaseURL, peers) {
    const issueKandyBagModal = this;

    issueKandyBagModal.peers = peers;
    issueKandyBagModal.form = {};
    issueKandyBagModal.formError = false;

    /** Validate and create an IOU. */
    issueKandyBagModal.create = () => {
        if (invalidFormInput()) {
            issueKandyBagModal.formError = true;
        } else {
            issueKandyBagModal.formError = false;

            const amount = issueKandyBagModal.form.amount;
            const currency = issueKandyBagModal.form.currency;
            const party = issueKandyBagModal.form.counterparty;

            $uibModalInstance.close();

            // We define the IOU creation endpoint.
            const issueIOUEndpoint =
                apiBaseURL +
                `issue-iou?amount=${amount}&currency=${currency}&party=${party}`;

            // We hit the endpoint to create the IOU and handle success/failure responses.
            $http.put(issueIOUEndpoint).then(
                (result) => createIOUModal.displayMessage(result),
                (result) => createIOUModal.displayMessage(result)
            );
        }
    };

    /** Displays the success/failure response from attempting to create an IOU. */
    issueKandyBagModal.displayMessage = (message) => {
        const createIOUMsgModal = $uibModal.open({
            templateUrl: 'issueKandyBagModalCtrl.html',
            controller: 'issueKandyBagModalCtrl',
            controllerAs: 'issueKandyBagModal',
            resolve: {
                message: () => message
            }
        });

        // No behaviour on close / dismiss.
        createIOUMsgModal.result.then(() => {}, () => {});
    };

    /** Closes the IOU creation modal. */
    issueKandyBagModal.cancel = () => $uibModalInstance.dismiss();

    // Validates the IOU.
    function invalidFormInput() {
        return isNaN(issueKandyBagModal.form.amount) || (issueKandyBagModal.form.counterparty === undefined);
    }
});

// Controller for the success/fail modal.
angular.module('demoAppModule').controller('issueKandyBagModalCtrl', function($uibModalInstance, message) {
    const issueKandyBagModal = this;
    issueKandyBagModal.message = message.data;
});