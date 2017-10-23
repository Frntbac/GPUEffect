# GPUEffect
[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

This library has been heavily inspired from: [CyberAgent's GPUImage](https://github.com/CyberAgent/android-gpuimage)

Through this library, the idea was to easily port GPUImage's filters but make it more flexible and remove the unneeded features.
This library will make it easier to combine effects and execute them offscreen.

# Requirements

* Android 4.0 or higher (and OpenGL ES 2.0)

# Usage

## Apply an effect on an image

Kotlin :
```kotlin
(EmbossEffect() receives lena)
    .bitmap
```

Java :
```java
new EmbossEffect()
    .receives(lena)
    .getBitmap();
```

## Inside a GLSurfaceView

Java :
```java
  private GPUEffect effect;

  @Override
  public void onCreate(Bundle savedInstance) {
    //...
    effect = new GPUEffect();
        effect.receives(BitmapFactory.decodeResource(getResources(), R.drawable.lena));
  }

  @Override
  public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    
  }

  @Override
  public void onSurfaceChanged(GL10 gl, int width, int height) {
    effect.setOutputSize(width, height);
    effect.init();
  }

  @Override
  public void onDrawFrame(GL10 gl) {
    effect.draw();
  }
```

# Apply Multiple Effect

Kotlin : 
```kotlin
(HazeEffect() + EmbossEffect())
    .receives(lena)
    .bitmap
```

Java : 
```java
new GPUMultiEffect(new HazeEffect(), new EmbossEffect())
    .receives(lena)
    .getBitmap()
```

# Gradle dependency

```groovy
repositories {
    jcenter()
}

dependencies {
    compile 'me.frontback:gpueffect:1.0.0'
}
```

# License

Copyright (C) 2017 Social Apps BVBA.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.