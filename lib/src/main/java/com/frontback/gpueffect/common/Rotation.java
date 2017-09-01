/*
 * Copyright (C) 2017 Social Apps BVBA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.frontback.gpueffect.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Copyright (C) 2017 Social Apps BVBA
 * Copyright (C) 2012 CyberAgent
 *
 * Adapted from https://github.com/CyberAgent/android-gpuimage/blob/master/library/src/jp/co/cyberagent/android/gpuimage/Rotation.java
 */

@IntDef({Rotation.NONE, Rotation._90, Rotation._180, Rotation._270,
        Rotation.UPSIDE_DOWN, Rotation.UPSIDE_DOWN_90, Rotation.UPSIDE_DOWN_180, Rotation.UPSIDE_DOWN_270})
@Retention(RetentionPolicy.SOURCE)
public @interface Rotation {

  int NONE = 0;
  float ARRAY_NONE[] = {
          0f, 0f, // bottom left
          1f, 0f, // bottom right
          0f, 1f, // top left
          1f, 1f  // top right
  };
  int _90 = 90;
  float ARRAY_90[] = {
          0f, 1f, // top left
          0f, 0f, // bottom left
          1f, 1f, // top right
          1f, 0f  // bottom right
  };
  int _180 = 180;
  float ARRAY_180[] = {
          1f, 1f, // top right
          0f, 1f, // top left
          1f, 0f, // bottom right
          0f, 0f  // bottom left
  };
  int _270 = 270;
  float ARRAY_270[] = {
          1f, 0f, // bottom right
          1f, 1f, // top right
          0f, 0f, // bottom left
          0f, 1f  // top left
  };
  int UPSIDE_DOWN = -1;
  float ARRAY_UPSIDE_DOWN[] = {
          0f, 1f, // top left
          1f, 1f, // top right
          0f, 0f, // bottom left
          1f, 0f  // bottom right
  };
  int UPSIDE_DOWN_90 = -90;
  float ARRAY_UPSIDE_DOWN_90[] = {
          1f, 1f, // top right
          1f, 0f, // bottom right
          0f, 1f, // top left
          0f, 0f  // bottom left
  };
  int UPSIDE_DOWN_180 = -180;
  float ARRAY_UPSIDE_DOWN_180[] = {
          1f, 0f, // bottom right
          0f, 0f, // bottom left
          1f, 1f, // top right
          0f, 1f  // top left
  };
  int UPSIDE_DOWN_270 = -270;
  float ARRAY_UPSIDE_DOWN_270[] = {
          0f, 0f, // bottom left
          0f, 1f, // top left
          1f, 0f, // bottom right
          1f, 1f  // top right
  };
}
