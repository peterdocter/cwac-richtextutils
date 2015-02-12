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

package com.commonsware.cwac.richtextutils;

import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import java.util.Stack;
import java.util.WeakHashMap;

/**
 * Generates XHTML from a Spanned, for use in persistance. Restore
 * the Spanned via SpannableStringGenerator.
 */
public class SpannedXhtmlGenerator {
  private static WeakHashMap<Class, SpanTagHandler> GLOBAL_SPAN_TAG_HANDLERS=
      new WeakHashMap<Class, SpanTagHandler>();
  private static final String[] CHUNK_SOURCES={"\n\n", "\n"};
  private static final String[] CHUNK_REPLACEMENTS={"</div><div>", "<br/>"};
  private final SpanTagRoster tagRoster;

  /**
   * Constructor, using a default SpanTagRoster.
   */
  public SpannedXhtmlGenerator() {
    this(new SpanTagRoster());
  }

  /**
   * Constructor. 'nuff said.
   *
   * @param tagRoster Rules for converting Spannables to/from XHTML
   */
  public SpannedXhtmlGenerator(SpanTagRoster tagRoster) {
    this.tagRoster=tagRoster;
  }

  /**
   * Generates XHTML from the supplied Spanned, according to the
   * rules from the SpanTagRoster provided in the constructor.
   *
   * @param src Spanned representing the rich text to convert
   * @return the XHTML generated from the Spanned
   */
  public String toXhtml(Spanned src) {
    Stack<CharacterStyle> activeSpans=new Stack<CharacterStyle>();
    StringBuilder result=new StringBuilder(src.length());

    for (int i=0;i<src.length();) {
      int nextSpanEnd=src.nextSpanTransition(i, src.length(),
                                              CharacterStyle.class);
      CharacterStyle[] spansInEffect=src.getSpans(i, nextSpanEnd,
                                                  CharacterStyle.class);

      while (!activeSpans.empty()) {
        boolean stillInEffect=false;
        CharacterStyle active=activeSpans.peek();

        for (CharacterStyle inEffect : spansInEffect) {
          if (active==inEffect) {
            stillInEffect=true;
            break;
          }
        }

        if (!stillInEffect) {
          SpanTagHandler handler=tagRoster.getSpanTagHandler(active.getClass());

          if (handler!=null) {
            result.append(handler.getEndTagForSpan(active));
          }

          activeSpans.pop();
        }
        else {
          break;
        }
      }

      for (CharacterStyle inEffect : spansInEffect) {
        if (!activeSpans.contains(inEffect)) {
          SpanTagHandler handler=tagRoster.getSpanTagHandler(inEffect.getClass());

          if (handler!=null) {
            result.append(handler.getStartTagForSpan(inEffect));
          }

          activeSpans.push(inEffect);
        }
      }

      CharSequence chunk=src.subSequence(i, nextSpanEnd);

      while (hasAny(chunk, CHUNK_SOURCES)) {
        chunk=TextUtils.replace(chunk, CHUNK_SOURCES, CHUNK_REPLACEMENTS);
      }

      result.append(chunk);
      i=nextSpanEnd;
    }

    while (!activeSpans.empty()) {
      CharacterStyle active=activeSpans.pop();
      SpanTagHandler handler=tagRoster.getSpanTagHandler(active.getClass());

      if (handler!=null) {
        result.append(handler.getEndTagForSpan(active));
      }
    }

    String baseResult=result.toString();

    if (baseResult.endsWith("</div><div>")) {
      return("<div>"+baseResult.substring(0, baseResult.length()-5));
    }
    else if (baseResult.contains("</div><div>")) {
      return("<div>"+baseResult+"</div>");
    }

    return(result.toString());
  }

  private static boolean hasAny(CharSequence input, String[] sources) {
    for (String source : sources) {
      if (TextUtils.indexOf(input, source)>=0) {
        return(true);
      }
    }

    return(false);
  }
}
