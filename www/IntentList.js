var exec = require('cordova/exec');

/**
* TODO: add params to configure what info to return with getList
*/
exports.getList = function (success, error) {
    exec(success, error, 'IntentList', 'getIntentList', []);
};
