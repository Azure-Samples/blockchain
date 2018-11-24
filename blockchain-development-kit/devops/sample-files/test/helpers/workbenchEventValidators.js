function assertContractUpdateEvent(assert) {
  assert.eventIsWorkbenchContractCreated = function (log, applicationName, workflowName, originatingAddress) {
    assert.strictEqual(log.event, "WorkbenchContractCreated");
    assert.strictEqual(log.args.applicationName, applicationName);
    assert.strictEqual(log.args.workflowName, workflowName);
    assert.strictEqual(log.args.originatingAddress, originatingAddress);
  }

  assert.eventIsWorkbenchContractUpdated = function (log, applicationName, workflowName, action, originatingAddress) {
    assert.strictEqual(log.event, "WorkbenchContractUpdated");
    assert.strictEqual(log.args.applicationName, applicationName);
    assert.strictEqual(log.args.workflowName, workflowName);
    assert.strictEqual(log.args.action, action);
    assert.strictEqual(log.args.originatingAddress, originatingAddress);
  }
}

exports = module.exports = assertContractUpdateEvent;
