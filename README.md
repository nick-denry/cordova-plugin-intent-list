# Simple phonegap plugin to get Android installed apps with icons

## 1. Installation

cordova plugin add https://github.com/nick-denry/cordova-plugin-intent-list

## 2. Usage

```js
navigator.IntentList.getList(success, error);
```

```js
navigator.IntentList.getList(function(applist) {
    console.log(applist);    
}, function(errorMesssage) {
    console.log(errorMesage);
});
```

`applist` will contain array of JSON objects.

```json
{
  "applist": [
    {
      "label": "Chrome",
      "package": "com.android.chrome",
      "packageIcon": "iVBORw0KGgoAAAANSUhEUgAAAJAAAACQCAYAAADnRuK4AAAABHNCSVQICAgIfAhkiAAAIABJREF..."
    }
  ]
}
```
