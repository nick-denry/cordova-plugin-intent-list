var exec = require('cordova/exec');

exports.getList = function (success, error) {
    console.log('try to exec');
    exec(success, error, 'IntentList', 'getIntentList', []);
};
