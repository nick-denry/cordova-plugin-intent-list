var exec = require('cordova/exec');

exports.getList = function (success, error) {
    exec(success, error, 'IntentList', 'getIntentList', []);
};
