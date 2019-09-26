/**
 * 
 */
var emptyStateValue = 0; //空状态

function BitStates() {}

function BitStates(longValue) {
    this();
    this.value = value;
}

function hasState(srcBitStates, targetValue) {
    return (srcBitStates & targetValue) == targetValue;
}

function dontHaveStates(srcBitStates, targetValue) {
    return !hasState(srcBitStates, targetValue);
}

function addState(srcBitStates, targetValue) {
    if (hasState(srcBitStates, targetValue)) {
        return srcBitStates;
    }
    srcBitStates = (srcBitStates | targetValue);
    return srcBitStates;
}

function removeState(srcBitStates, targetValue) {
    if (!hasState(srcBitStates, targetValue)) {
        return srcBitStates;
    }
    srcBitStates = (srcBitStates ^ targetValue);
    return srcBitStates;
}