# Permission-ktx
request permission with kotlin

# usage
in activity or fragment

```
requestPermission(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) {

               //if all granted...
            }
```

