/***
 Copyright (c) 2015 CommonsWare, LLC

 Licensed under the Apache License, Version 2.0 (the "License"); you may
 not use this file except in compliance with the License. You may obtain
 a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.commonsware.cwac.richtextutils.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;
import java.io.IOException;

public class MainActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ViewPager pager=(ViewPager)findViewById(R.id.pager);
    PagerSlidingTabStrip tabs=(PagerSlidingTabStrip)findViewById(R.id.tabs);

    try {
      pager.setAdapter(new AssetPagerAdapter(getAssets(), getFragmentManager()));
      tabs.setViewPager(pager);
    }
    catch (IOException e) {
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
      Log.e(getClass().getSimpleName(), "Exception in initialization", e);
    }
  }
}
