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

import android.app.Fragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import com.commonsware.cwac.richtextutils.SpanTagRoster;
import com.commonsware.cwac.richtextutils.SpannableStringGenerator;
import java.util.Scanner;

public class AssetViewFragment extends Fragment {
  private static final String ARG_FILE="testFile";

  public static AssetViewFragment newInstance(String file) {
    AssetViewFragment f=new AssetViewFragment();
    Bundle args=new Bundle();

    args.putString(ARG_FILE, file);
    f.setArguments(args);

    return(f);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    String file=getArguments().getString(ARG_FILE);

    View result=inflater.inflate(R.layout.asset_frag, container, false);
    WebView wv=(WebView)result.findViewById(R.id.webView);
    AssetManager assets=getActivity().getAssets();
    TextView tv=(TextView)result.findViewById(R.id.textView);
    WebSettings settings = wv.getSettings();

    settings.setDefaultTextEncodingName("utf-8");
    tv.setMovementMethod(LinkMovementMethod.getInstance());

    try {
      String input=new Scanner(assets.open("testFiles/"+file)).useDelimiter("\\Z").next();
      SpanTagRoster tagRoster=new SpanTagRoster();
      Spanned fromInput=new SpannableStringGenerator(tagRoster).fromXhtml(input);

      tv.setText(fromInput);
      wv.loadData(input, "text/html; charset=utf-8",null);
    }
    catch (Exception e) {
      Log.e(getClass().getSimpleName(), "Exception reading in test file", e);
      tv.setText(e.getLocalizedMessage());
    }

    return(result);
  }
}
