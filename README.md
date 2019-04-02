# Simple phonegap plugin to get Android installed apps with icons

Demo.

<img src="https://user-images.githubusercontent.com/1450983/55368734-1c9c2e00-54fc-11e9-9651-309c5a94399a.png" width="auto" height="400" />

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

## 3. Display base64 image in HTML

Add `img-src 'self' data:;` to your CSP declaration to allow `data:image` images, i.e.: 
```html
<!-- This is a wide open CSP declaration. To lock this down for production, see below. -->
<meta http-equiv="Content-Security-Policy" content="default-src * 'unsafe-inline' gap:; style-src 'self' 'unsafe-inline'; img-src 'self' data:; media-src *" />
```
**NOTE** Do not use wide open CSP declaration in production. @see https://cordova.apache.org/docs/en/latest/reference/cordova-plugin-whitelist/#content-security-policy

@see https://stackoverflow.com/a/8499716

